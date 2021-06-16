/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.store.dynamodb;

import org.apache.activemq.ActiveMQMessageAudit;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.store.AbstractMessageStore;
import org.apache.activemq.store.IndexListener;
import org.apache.activemq.store.MessageRecoveryListener;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.ByteSequenceData;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.wireformat.WireFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 */
public class DynamoDBMessageStore extends AbstractMessageStore {

    public DynamoDBMessageStore(ActiveMQDestination destination) {
        super(destination);
    }

    @Override
    public void addMessage(ConnectionContext context, Message message) throws IOException {

    }

    @Override
    public Message getMessage(MessageId identity) throws IOException {
        return null;
    }

    @Override
    public void removeMessage(ConnectionContext context, MessageAck ack) throws IOException {

    }

    @Override
    public void removeAllMessages(ConnectionContext context) throws IOException {

    }

    @Override
    public void recover(MessageRecoveryListener container) throws Exception {

    }

    @Override
    public void resetBatching() {

    }

    @Override
    public void recoverNextMessages(int maxReturned, MessageRecoveryListener listener) throws Exception {

    }
}
