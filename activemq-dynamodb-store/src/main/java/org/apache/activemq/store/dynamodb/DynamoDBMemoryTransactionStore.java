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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageAck;
import org.apache.activemq.command.MessageId;
import org.apache.activemq.command.TransactionId;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.store.IndexListener;
import org.apache.activemq.store.MessageStore;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.ProxyMessageStore;
import org.apache.activemq.store.TopicMessageStore;
import org.apache.activemq.store.TransactionRecoveryListener;
import org.apache.activemq.store.TransactionStore;
import org.apache.activemq.store.memory.MemoryTransactionStore;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.DataByteArrayInputStream;

/**
 * respect 2pc prepare
 * uses local transactions to maintain prepared state
 * xid column provides transaction flag for additions and removals
 * a commit clears that context and completes the work
 * a rollback clears the flag and removes the additions
 * Essentially a prepare is an insert &| update transaction
 *  commit|rollback is an update &| remove
 */
public class DynamoDBMemoryTransactionStore implements TransactionStore {

    @Override
    public void prepare(TransactionId txid) throws IOException {

    }

    @Override
    public void commit(TransactionId txid, boolean wasPrepared, Runnable preCommit, Runnable postCommit) throws IOException {

    }

    @Override
    public void rollback(TransactionId txid) throws IOException {

    }

    @Override
    public void recover(TransactionRecoveryListener listener) throws IOException {

    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }
}
