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

package org.ssiu.ucp.client.flink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssiu.ucp.client.api.UcpClient;
import org.ssiu.ucp.client.util.PathFinder;
import org.ssiu.ucp.common.config.ClientConfig;
import org.ssiu.ucp.common.mode.EngineType;
import org.ssiu.ucp.common.service.AppConfig;
import org.ssiu.ucp.core.command.BaseAppArgs;

import java.util.List;

/**
 * Now, we use unknownOptions(from Jcommander) to build flink command.
 * <p>
 * In flink, we need to provide a fat jar.
 */
public class FlinkClient implements UcpClient {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkClient.class);

    private final BaseAppArgs appArgs;

    private AppConfig appConfig;

    private FlinkClient(BaseAppArgs appArgs) {
        this.appArgs = appArgs;
    }

    private FlinkSubmitCommand flinkSubmitCommand;

    public static UcpClient from(List<String> flinkOptions, BaseAppArgs appArgs) {
        final FlinkClient flinkClient = new FlinkClient(appArgs);
        flinkClient.initClient();
        flinkClient.flinkSubmitCommand.setFlinkOptions(flinkOptions);
        return flinkClient;
    }

    private void initClient() {
        appConfig = AppConfig.fromPath(appArgs.getConfigFile());
        flinkSubmitCommand = new FlinkSubmitCommand();
    }

    private String buildCommand() throws Exception {
        final ClientConfig clientConfig = appConfig.getClientConfig();
        // In Flink, set [flink run / flink run-application]
        final String submitPrefix = clientConfig.getSubmitPrefix();
        flinkSubmitCommand.setShellPrefix(submitPrefix);
        final String appPath = PathFinder.findApp(EngineType.Flink).toAbsolutePath().toString();
        flinkSubmitCommand.setAppPath(appPath);
        flinkSubmitCommand.setAppArgs(new String[]{"--config", appArgs.getConfigFile()});
        return flinkSubmitCommand.toCommand();
    }


    @Override
    public int start() throws Exception {
        final String command = buildCommand();
        LOG.info("spark start command:\n{}", command);
        //return ProcessRunner.runLocal(command);
        return 0;
    }
}
