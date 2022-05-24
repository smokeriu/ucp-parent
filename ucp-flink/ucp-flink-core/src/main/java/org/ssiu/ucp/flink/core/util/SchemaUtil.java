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
import com.typesafe.config.ConfigBeanFactory;
import org.apache.flink.table.api.Schema;
import org.ssiu.ucp.flink.core.bean.SchemaInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SchemaUtil {

    public static final String SCHEMA = "schema";
    public static final String COMPLEX_SCHEMA = "complexSchema";

    public static Schema fromList(List<SchemaInfo> schemaInfos) {
        final Schema.Builder schemaBuilder = Schema.newBuilder();
        final List<String> primary = schemaInfos.stream()
                .filter(SchemaInfo::isPrimary)
                .map(SchemaInfo::getName)
                .collect(Collectors.toList());
        schemaInfos.forEach(info -> buildFromInfo(info, schemaBuilder));
        if (!primary.isEmpty()) {
            schemaBuilder.primaryKey(primary);
        }
        return schemaBuilder.build();
    }

    public static Schema fromMap(Map<String, String> map) {
        final Schema.Builder schemaBuilder = Schema.newBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            schemaBuilder.column(entry.getKey(), entry.getValue());
        }
        return schemaBuilder.build();
    }

    private static void buildFromInfo(SchemaInfo info, Schema.Builder builder) {
        if (info.isExpr()) {
            builder.columnByExpression(info.getName(), info.getInfo());
        } else if (info.isMeta()) {
            builder.columnByMetadata(info.getName(), info.getInfo());
        } else if (info.getWatermark() != null) {
            builder.columnByExpression(info.getName(), info.getInfo())
                    .watermark(info.getName(), info.getWatermark());
        } else {
            builder.column(info.getName(), info.getInfo());
        }
    }

    /**
     * build schema from config
     */
    public static Schema fromConfig(Config config) {
        if (config.hasPath(SCHEMA)) {
            final Map<String, String> map = config.getConfig(SCHEMA)
                    .root()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().unwrapped().toString()));
            return fromMap(map);
        } else {
            final List<SchemaInfo> schemaInfos = config.getConfigList(COMPLEX_SCHEMA)
                    .stream()
                    .map(infoConfig -> ConfigBeanFactory.create(infoConfig, SchemaInfo.class))
                    .collect(Collectors.toList());
            return fromList(schemaInfos);
        }
    }
}
