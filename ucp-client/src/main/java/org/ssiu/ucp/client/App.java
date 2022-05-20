/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ssiu.ucp.client;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssiu.ucp.client.flink.FlinkClient;
import org.ssiu.ucp.common.mode.EngineType;
import org.ssiu.ucp.core.command.BaseAppArgs;
import org.ssiu.ucp.client.api.UcpClient;
import org.ssiu.ucp.client.spark.SparkClient;

import java.util.List;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        final UcpClient ucpClient = createClient(args);
        ucpClient.start();
    }

    private static UcpClient createClient(String[] args) throws Exception {
        final BaseAppArgs baseAppArgs = new BaseAppArgs();
        final JCommander jCommander = JCommander.newBuilder()
                .addObject(baseAppArgs)
                .acceptUnknownOptions(true)
                .args(args)
                .build();
        final EngineType engine = baseAppArgs.getEngine();
        switch (engine) {
            case Spark:
                LOG.info("Build spark client");
                return SparkClient.from(baseAppArgs, args);
            case Flink:
                LOG.info("Build Flink client");
                final List<String> unknownOptions = jCommander.getUnknownOptions();
                return FlinkClient.from(unknownOptions, baseAppArgs);
            default:
                throw new Exception("not support");
        }
    }
}