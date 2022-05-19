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

package org.ssiu.ucp.flink.core.env;

import org.ssiu.ucp.common.config.JobConfig;
import org.ssiu.ucp.core.env.RuntimeEnv;
import org.ssiu.ucp.core.util.CheckResult;

import java.util.List;

public class FlinkRuntimeEnv implements RuntimeEnv {

    private JobConfig jobConfig;

    @Override
    public void setConfig(JobConfig config) {
        this.jobConfig = config;
    }

    @Override
    public JobConfig getConfig() {
        return this.jobConfig;
    }

    @Override
    public List<CheckResult> checkConfig() {
        return null;
    }

    @Override
    public void prepare() throws Exception {

    }

    @Override
    public boolean isStreaming() {
        switch (jobConfig.getJobMode()) {
            case Batch:
                return false;
            default:
                return true;
        }
    }
}
