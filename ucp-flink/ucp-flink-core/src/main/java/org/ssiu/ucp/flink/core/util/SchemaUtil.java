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

import com.typesafe.config.Config;
import org.apache.flink.table.api.Schema;

import java.util.Map;
import java.util.stream.Collectors;

public class SchemaUtil {

    public static Schema fromMap(Map<String, String> schemaMap) {
        final Schema.Builder schemaBuilder = Schema.newBuilder();
        for (Map.Entry<String, String> entry : schemaMap.entrySet()) {
            final String columnName = entry.getKey();
            final String columnType = entry.getValue();
            schemaBuilder.column(columnName, columnType);
        }
        return schemaBuilder.build();
    }

    public static Schema fromConfig(Config config) {
        final Map<String, String> schemaMap = config.root()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().unwrapped().toString()));
        return fromMap(schemaMap);
    }
}
