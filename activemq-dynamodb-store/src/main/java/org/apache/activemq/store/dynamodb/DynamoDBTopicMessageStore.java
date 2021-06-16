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

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.activemq.ActiveMQMessageAudit;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.SubscriptionInfo;
import org.apache.activemq.store.MessageRecoveryListener;
import org.apache.activemq.store.MessageStoreSubscriptionStatistics;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.wireformat.WireFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class DynamoDBTopicMessageStore extends DynamoDBMessageStore implements TopicMessageStore {

    public DynamoDBTopicMessageStore(ActiveMQDestination destination) {
        super(destination);
    }

    @Override
    public void acknowledge(ConnectionContext context, String clientId, String subscriptionName, MessageId messageId, MessageAck ack) throws IOException {

    }

    @Override
    public void deleteSubscription(String clientId, String subscriptionName) throws IOException {

    }

    @Override
    public void recoverSubscription(String clientId, String subscriptionName, MessageRecoveryListener listener) throws Exception {

    }

    @Override
    public void recoverNextMessages(String clientId, String subscriptionName, int maxReturned, MessageRecoveryListener listener) throws Exception {

    }

    @Override
    public void resetBatching(String clientId, String subscriptionName) {

    }

    @Override
    public int getMessageCount(String clientId, String subscriberName) throws IOException {
        return 0;
    }

    @Override
    public long getMessageSize(String clientId, String subscriberName) throws IOException {
        return 0;
    }

    @Override
    public MessageStoreSubscriptionStatistics getMessageStoreSubStatistics() {
        return null;
    }

    @Override
    public SubscriptionInfo lookupSubscription(String clientId, String subscriptionName) throws IOException {
        return null;
    }

    @Override
    public SubscriptionInfo[] getAllSubscriptions() throws IOException {
        return new SubscriptionInfo[0];
    }

    @Override
    public void addSubscription(SubscriptionInfo subscriptionInfo, boolean retroactive) throws IOException {

    }
}
