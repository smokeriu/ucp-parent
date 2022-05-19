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

package org.ssiu.ucp.common.test;

import org.junit.jupiter.api.Test;
import org.ssiu.ucp.common.api.Element;
import org.ssiu.ucp.common.config.JobConfig;
import org.ssiu.ucp.common.service.AppConfig;

import java.util.List;

public class ConfigTest {

    @Test
    public void fromString() {
        String config = "{ \"job\":{ \"jobLevel\":\"Dev\", \"jobMode\":\"Batch\" }}";
        final AppConfig appConfig = AppConfig.fromString(config);
        testConfigBuilder(appConfig);
    }

    @Test
    public void fromName() {
        String filePath = "test";
        final AppConfig appConfig = AppConfig.fromName(filePath);
        testConfigBuilder(appConfig);
    }

    private void testConfigBuilder(AppConfig appConfig) {
        final JobConfig jobConfig = appConfig.getJobConfig();
        System.out.println(jobConfig);
    }


}
