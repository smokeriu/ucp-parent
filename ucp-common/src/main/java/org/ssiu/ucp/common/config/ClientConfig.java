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

package org.ssiu.ucp.common.config;

import com.typesafe.config.Config;

public class ClientConfig {

    public static final String CLIENT_CONFIG_KEY = "env";

    /**
     * shell-command
     */
    private String submitPrefix;

    /**
     * engine env Config
     */
    private Config engineConfig;

    public String getSubmitPrefix() {
        return submitPrefix;
    }

    public void setSubmitPrefix(String submitPrefix) {
        this.submitPrefix = submitPrefix;
    }

    public Config getEngineConfig() {
        return engineConfig;
    }

    public void setEngineConfig(Config engineConfig) {
        this.engineConfig = engineConfig;
    }
}
