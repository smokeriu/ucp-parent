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

package org.ssiu.ucp.flink.connector.fake;

import com.typesafe.config.Config;
import org.apache.flink.connector.datagen.table.DataGenConnectorOptions;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.catalog.ResolvedSchema;
import org.ssiu.ucp.flink.core.api.FlinkBatchReader;
import org.ssiu.ucp.flink.core.api.FlinkSingleWriter;
import org.ssiu.ucp.flink.core.api.FlinkStreamReader;
import org.ssiu.ucp.flink.core.env.FlinkRuntimeEnv;
import org.ssiu.ucp.flink.core.util.SchemaUtil;

public class Fake extends FlinkSingleWriter implements FlinkBatchReader, FlinkStreamReader {
    private static final long serialVersionUID = 7042151700218950524L;
    private final static String SOURCE_CONNECTOR = "datagen";
    private final static String SINK_CONNECTOR = "print";

    @Override
    public Table batchRead(FlinkRuntimeEnv env, Config config) throws Exception {
        return streamRead(env, config);
    }

    @Override
    public Table streamRead(FlinkRuntimeEnv env, Config config) throws Exception {
        final Schema schema = SchemaUtil.fromConfig(config.getConfig(FakeConfig.SCHEMA));
        // TODO: dataGen option
        final TableDescriptor dataGen = TableDescriptor.forConnector(SOURCE_CONNECTOR)
                .schema(schema)
                .option(DataGenConnectorOptions.ROWS_PER_SECOND, 100L)
                .build();

        return env.getStreamTableEnv().from(dataGen);
    }

    @Override
    protected void singleWrite(Table input, FlinkRuntimeEnv env, Config config) throws Exception {
        final ResolvedSchema resolvedSchema = input.getResolvedSchema();
        final TableDescriptor sink = TableDescriptor.forConnector(SINK_CONNECTOR)
                //.schema(Schema.newBuilder().fromResolvedSchema(resolvedSchema).build())
                .build();
        input.executeInsert(sink);
    }


    private static class FakeConfig {
        private static final String SCHEMA = "schema";
    }
}
