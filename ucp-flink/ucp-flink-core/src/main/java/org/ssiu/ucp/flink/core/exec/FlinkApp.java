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

package org.ssiu.ucp.flink.core.exec;

import org.apache.flink.table.api.Table;
import org.ssiu.ucp.common.mode.JobLevel;
import org.ssiu.ucp.common.service.AppConfig;
import org.ssiu.ucp.core.execution.AppTrait;
import org.ssiu.ucp.core.service.TableProvider;
import org.ssiu.ucp.core.util.CheckResult;
import org.ssiu.ucp.core.workflow.AbstractFlow;
import org.ssiu.ucp.flink.core.env.FlinkRuntimeEnv;
import org.ssiu.ucp.flink.core.service.FlinkQueryHandle;
import org.ssiu.ucp.flink.core.service.FlinkTableProvider;
import org.ssiu.ucp.flink.core.workflow.FlinkFlow;

import java.util.List;

public class FlinkApp implements AppTrait {

    private FlinkRuntimeEnv env;

    private AbstractFlow workFlow;

    private AppConfig appConfig;

    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public void prepareApp() throws Exception {
        env = new FlinkRuntimeEnv();
        env.setConfig(appConfig.getJobConfig());
        env.prepare();
        final FlinkQueryHandle flinkQueryHandle = new FlinkQueryHandle();
        final TableProvider<FlinkRuntimeEnv, Table> tableProvider = new FlinkTableProvider();
        workFlow = new FlinkFlow(appConfig.getElements(), tableProvider, flinkQueryHandle, env);
        workFlow.initFlow();
    }

    @Override
    public JobLevel appLevel() {
        return appConfig.getJobConfig().getJobLevel();
    }

    @Override
    public List<CheckResult> checkApp() {
        final List<CheckResult> appResult = env.checkConfig();
        final List<CheckResult> validateFlow = workFlow.validateFlow();
        appResult.addAll(validateFlow);
        return appResult;
    }

    @Override
    public void submit() throws Exception {
        workFlow.runFlow();
    }
}
