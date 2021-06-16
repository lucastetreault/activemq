/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.store.dynamodb;

import com.amazonaws.services.dynamodbv2.AcquireLockOptions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClientOptions;
import com.amazonaws.services.dynamodbv2.CreateDynamoDBTableOptions;
import com.amazonaws.services.dynamodbv2.GetLockOptions;
import com.amazonaws.services.dynamodbv2.LockItem;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import org.apache.activemq.broker.AbstractLocker;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.util.ServiceStopper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Represents an exclusive lease on a database to avoid multiple brokers running
 * against the same logical database.
 *
 * @org.apache.xbean.XBean element="dynamo-lease-locker"
 */
public class DefaultDatabaseLocker extends AbstractLocker {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDatabaseLocker.class);

    private AmazonDynamoDBLockClient lockClient;
    private LockItem lockItem;

    protected boolean handleStartException;
    protected DynamoDBPersistenceAdapter dynamodbAdapter;
    protected int maxAllowableDiffFromDBTime = 0;
    protected String leaseHolderId;


    public void configure(PersistenceAdapter adapter) {
        if (adapter instanceof DynamoDBPersistenceAdapter) {
            this.dynamodbAdapter = (DynamoDBPersistenceAdapter) adapter;
        } else {
            this.dynamodbAdapter = new DynamoDBPersistenceAdapter();
        }
    }

    @Override
    public void preStart() {
        lockClient = new AmazonDynamoDBLockClient(
                AmazonDynamoDBLockClientOptions.builder(dynamodbAdapter.getV1Client(), dynamodbAdapter.getStatements().getFullLockTableName())
                        .withTimeUnit(TimeUnit.MILLISECONDS)
                        .withLeaseDuration(lockAcquireSleepInterval)
                        .withHeartbeatPeriod(lockable.getLockKeepAlivePeriod())
                        .withOwnerName(getLeaseHolderId())
                        .withCreateHeartbeatBackgroundThread(true)
                        .build());

        if (!lockClient.lockTableExists()) {
            createLockTable();
        }
    }

    private void createLockTable() {
        LOG.info("Creating DynamoDB lock table: {}", dynamodbAdapter.getStatements().getFullLockTableName());

        CreateDynamoDBTableOptions createDynamoDBTableOptions = CreateDynamoDBTableOptions.builder(
                dynamodbAdapter.getV1Client(),
                new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L),
                dynamodbAdapter.getStatements().getFullLockTableName()
        ).build();

        AmazonDynamoDBLockClient.createLockTableInDynamoDB(createDynamoDBTableOptions);

        DynamoDbWaiter waiter = dynamodbAdapter.getClient().waiter();

        WaiterResponse<DescribeTableResponse> waiterResponse =
                waiter.waitUntilTableExists(r -> r.tableName(dynamodbAdapter.getStatements().getFullLockTableName()));

        waiterResponse.matched().response().ifPresent(r -> LOG.info("Successfully created DynamoDB lock table: {}", dynamodbAdapter.getStatements().getFullLockTableName()));
    }

    public void doStart() throws Exception {
        if (lockAcquireSleepInterval < lockable.getLockKeepAlivePeriod()) {
            LOG.warn("LockableService keep alive period: " + lockable.getLockKeepAlivePeriod()
                    + ", which renews the lease, is greater than lockAcquireSleepInterval: " + lockAcquireSleepInterval
                    + ", the lease duration. These values will allow the lease to expire.");
        }

        LOG.info(getLeaseHolderId() + " attempting to acquire exclusive lease to become the active instance");

        while (!isStopping()) {
            try {
                final Optional<LockItem> lockItem = lockClient.tryAcquireLock(AcquireLockOptions.builder("1").build());
                if (lockItem.isPresent()) {
                    LOG.info(getLeaseHolderId() + " becoming active with lease expiry in " + lockAcquireSleepInterval + " milli(s).");
                    this.lockItem = lockItem.get();
                    break;
                } else {
                    LOG.debug(getLeaseHolderId() + " failed to acquire lease.  Sleeping for " + lockAcquireSleepInterval + " milli(s) before trying again...");
                }
            } catch (Exception e) {
                LOG.warn(getLeaseHolderId() + " lease acquire failure: " + e, e);
                if (isStopping()) {
                    throw new Exception(
                            "Cannot start broker as being asked to shut down. "
                                    + "Interrupted attempt to acquire lock: "
                                    + e, e);
                }
                if (handleStartException) {
                    throw e;
                }
            }

            LOG.debug(getLeaseHolderId() + " failed to acquire lease.  Sleeping for " + lockAcquireSleepInterval + " milli(s) before trying again...");
            TimeUnit.MILLISECONDS.sleep(lockAcquireSleepInterval);
        }
        if (isStopping()) {
            throw new RuntimeException(getLeaseHolderId() + " failing lease acquire due to stop");
        }
    }

    private void reportLeaseOwnerShipAndDuration(AmazonDynamoDBLockClient client) {
        final Optional<LockItem> lockItem = client.getLockFromDynamoDB(GetLockOptions.builder("1").build());
        lockItem.ifPresent(item -> LOG.debug(getLeaseHolderId() + " Lease held by " + item.getOwnerName() + " till " + item.getLeaseDuration()));
    }


    public void doStop(ServiceStopper stopper) {
        if (lockable.getBrokerService() != null && lockable.getBrokerService().isRestartRequested()) {
            // keep our lease for restart
            return;
        }
        releaseLease();
    }

    private void releaseLease() {
        if (lockItem != null) {
            lockClient.releaseLock(lockItem);
        }
    }

    @Override
    public boolean keepAlive() {
        if (lockItem.isExpired()) {
            LOG.warn("Lock keepAlive failed, no longer lock owner");
            return false;
        }
        return true;
    }

    public String getLeaseHolderId() {
        if (leaseHolderId == null) {
            if (lockable.getBrokerService() != null) {
                leaseHolderId = lockable.getBrokerService().getBrokerName();
            }
        }
        return leaseHolderId;
    }

    public void setLeaseHolderId(String leaseHolderId) {
        this.leaseHolderId = leaseHolderId;
    }

    public int getMaxAllowableDiffFromDBTime() {
        return maxAllowableDiffFromDBTime;
    }

    public void setMaxAllowableDiffFromDBTime(int maxAllowableDiffFromDBTime) {
        this.maxAllowableDiffFromDBTime = maxAllowableDiffFromDBTime;
    }

    public boolean isHandleStartException() {
        return handleStartException;
    }

    public void setHandleStartException(boolean handleStartException) {
        this.handleStartException = handleStartException;
    }

    @Override
    public String toString() {
        return "LeaseDatabaseLocker owner:" + leaseHolderId + ",duration:" + lockAcquireSleepInterval + ",renew:" + lockAcquireSleepInterval;
    }
}
