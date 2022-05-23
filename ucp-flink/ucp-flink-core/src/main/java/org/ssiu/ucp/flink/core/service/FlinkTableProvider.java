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

package org.ssiu.ucp.flink.core.service;

import org.apache.flink.table.api.Table;
import org.ssiu.ucp.core.service.TableProvider;
import org.ssiu.ucp.flink.core.env.FlinkRuntimeEnv;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FlinkTableProvider implements TableProvider<FlinkRuntimeEnv, Table> {

    private final Map<String, Table> cache = new HashMap<>();

    @Override
    public Optional<Table> getTable(FlinkRuntimeEnv env, String name) {
        return Optional.ofNullable(cache.get(name));
    }

    @Override
    public void addTable(FlinkRuntimeEnv env, String name, Table table) {
        if (!cache.containsKey(name)) {
            env.getStreamTableEnv().createTemporaryView(name, table);
            cache.put(name, table);
        }
    }
}
