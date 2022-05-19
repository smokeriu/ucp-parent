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

package org.ssiu.ucp.core.test;

import org.junit.jupiter.api.Test;
import org.ssiu.ucp.core.api.Plugin;
import org.ssiu.ucp.core.env.RuntimeEnv;
import org.ssiu.ucp.core.util.PluginFactory;

import java.util.Map;

public class PluginFactoryTest {

    @Test
    public void SPITest() {
        final Map<String, Plugin<RuntimeEnv>> pluginMap = PluginFactory.buildPluginInstanceMap();
        pluginMap.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
        });
    }

    @Test
    public void CustomClassTest() throws Exception {
        String className = "org.ssiu.ucp.test.app.CustomConnector";
        final Plugin<RuntimeEnv> runtimeEnvPlugin = PluginFactory.buildCustomPluginInstance(className);
        System.out.println(runtimeEnvPlugin);
    }
}
