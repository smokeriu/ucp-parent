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

import org.ssiu.ucp.core.command.CommandArgs;

import java.util.List;

/**
 * In flink. we build command by unknown config from Jcommander
 */
public class FlinkSubmitCommand implements CommandArgs {
    private static final long serialVersionUID = -7805061783200962861L;

    private static final String SEPARATOR = " ";

    private String shellPrefix;


    private String appPath;

    private List<String> flinkOptions;

    private String[] appArgs;

    public String getShellPrefix() {
        return shellPrefix;
    }

    public void setShellPrefix(String shellPrefix) {
        this.shellPrefix = shellPrefix;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public List<String> getFlinkOptions() {
        return flinkOptions;
    }

    public void setFlinkOptions(List<String> flinkOptions) {
        this.flinkOptions = flinkOptions;
    }

    public String[] getAppArgs() {
        return appArgs;
    }

    public void setAppArgs(String[] appArgs) {
        this.appArgs = appArgs;
    }

    @Override
    public String toCommand() throws Exception {
        final String options = String.join(SEPARATOR, flinkOptions);
        final String appArgsStr = String.join(SEPARATOR, appArgs);
        return String.join(SEPARATOR, this.shellPrefix, options, appPath, appArgsStr);
    }
}
