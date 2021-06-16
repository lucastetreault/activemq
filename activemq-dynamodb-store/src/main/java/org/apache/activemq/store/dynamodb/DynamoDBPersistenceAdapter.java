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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;
import javax.swing.plaf.nimbus.State;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.apache.activemq.ActiveMQMessageAudit;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.Locker;
import org.apache.activemq.broker.scheduler.JobSchedulerStore;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.ProducerId;
import org.apache.activemq.openwire.OpenWireFormat;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.store.TransactionStore;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.FactoryFinder;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.LongSequenceGenerator;
import org.apache.activemq.util.ServiceStopper;
import org.apache.activemq.wireformat.WireFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

/**
 * A {@link PersistenceAdapter} implementation using DynamoDB for persistence
 * storage.
 * <p>
 * This persistence adapter will correctly remember prepared XA transactions,
 * but it will not keep track of local transaction commits so that operations
 * performed against the Message store are done as a single unit of work.
 *
 * @org.apache.xbean.XBean element="dynamoDBPersistenceAdapter"
 */
public class DynamoDBPersistenceAdapter implements PersistenceAdapter {

    private Statements statements;


    @Override
    public Set<ActiveMQDestination> getDestinations() {
        return null;
    }

    @Override
    public MessageStore createQueueMessageStore(ActiveMQQueue destination) throws IOException {
        return null;
    }

    @Override
    public TopicMessageStore createTopicMessageStore(ActiveMQTopic destination) throws IOException {
        return null;
    }

    @Override
    public JobSchedulerStore createJobSchedulerStore() throws IOException, UnsupportedOperationException {
        return null;
    }

    @Override
    public void removeQueueMessageStore(ActiveMQQueue destination) {

    }

    @Override
    public void removeTopicMessageStore(ActiveMQTopic destination) {

    }

    @Override
    public TransactionStore createTransactionStore() throws IOException {
        return null;
    }

    @Override
    public void beginTransaction(ConnectionContext context) throws IOException {

    }

    @Override
    public void commitTransaction(ConnectionContext context) throws IOException {

    }

    @Override
    public void rollbackTransaction(ConnectionContext context) throws IOException {

    }

    @Override
    public long getLastMessageBrokerSequenceId() throws IOException {
        return 0;
    }

    @Override
    public void deleteAllMessages() throws IOException {

    }

    @Override
    public void setUsageManager(SystemUsage usageManager) {

    }

    @Override
    public void setBrokerName(String brokerName) {

    }

    @Override
    public void setDirectory(File dir) {

    }

    @Override
    public File getDirectory() {
        return null;
    }

    @Override
    public void checkpoint(boolean cleanup) throws IOException {

    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public long getLastProducerSequenceId(ProducerId id) throws IOException {
        return 0;
    }

    @Override
    public void allowIOResumption() {

    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }

    public Statements getStatements() {
        if (statements == null) {
            statements = new Statements();
        }
        return statements;
    }

    public void setStatements(Statements statements) {
        this.statements = statements;
    }

    // Needed for the AmazonDynamoDBLockClient which does not yet support the V2 SDK
    public AmazonDynamoDB getV1Client() {
        // TODO: Load from config - default to local ddb for now!
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("access_key_id", "secret_key_id");

        return AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }

    public DynamoDbClient getClient() {
        // TODO: Load from config - default to local ddb for now!
        return DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:8000"))
                // The region is meaningless for local DynamoDb but required for client builder validation
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("dummy-key", "dummy-secret")))
                .build();
    }
}
