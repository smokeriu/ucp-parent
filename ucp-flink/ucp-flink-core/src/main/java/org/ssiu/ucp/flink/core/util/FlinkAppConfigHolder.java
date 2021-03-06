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

package org.ssiu.ucp.flink.core.util;

import com.beust.jcommander.JCommander;
import com.typesafe.config.ConfigFactory;
import org.apache.flink.api.java.utils.ParameterTool;
import org.ssiu.ucp.common.service.AppConfig;
import org.ssiu.ucp.flink.core.command.FlinkAppArgs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FlinkAppConfigHolder {
    private final FlinkAppArgs flinkAppArgs;

    public FlinkAppConfigHolder(String[] args) {
        flinkAppArgs = new FlinkAppArgs();
        JCommander.newBuilder().args(args)
                .addObject(flinkAppArgs)
                .acceptUnknownOptions(true)
                .build();
    }

    public AppConfig getAppConfig() {
        final String configFile = flinkAppArgs.getConfigFile();
        final Path path = Paths.get(configFile);
        return AppConfig.fromFile(path.toFile());
    }
}
