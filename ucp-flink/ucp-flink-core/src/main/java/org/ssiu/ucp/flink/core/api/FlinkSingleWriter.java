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

package org.ssiu.ucp.flink.core.api;

import com.typesafe.config.Config;
import org.apache.flink.table.api.Table;
import org.ssiu.ucp.flink.core.env.FlinkRuntimeEnv;
import org.ssiu.ucp.flink.core.util.MapUtil;

import java.util.Map;

/**
 * A writer with only one input table.
 * <p>
 * We provide a singleWrite because in Flink, the Batch and Stream implementations are mostly the same.
 *
 * @implNote If there is a real need to implement them separately, the user just needs to override singleBatchWrite for Batch mode.
 */
public abstract class FlinkSingleWriter implements FlinkStreamWriter, FlinkBatchWriter {
    @Override
    public void streamWrite(Map<String, Table> inputs, FlinkRuntimeEnv env, Config config) throws Exception {
        Table anyTable = MapUtil.getAnyValue(inputs);
        singleWrite(anyTable, env, config);
    }


    @Override
    public void batchWrite(Map<String, Table> inputs, FlinkRuntimeEnv env, Config config) throws Exception {
        Table anyTable = MapUtil.getAnyValue(inputs);
        singleBatchWrite(anyTable, env, config);
    }

    /**
     * A batch single wirte method.
     * <p>
     * By default, it just call {@link org.ssiu.ucp.flink.core.api.FlinkSingleWriter#singleWrite}. If you need a special use for Batch mode, override this method
     *
     * @param input  a single table
     * @param env    flink env
     * @param config config for this element
     */
    protected void singleBatchWrite(Table input, FlinkRuntimeEnv env, Config config) throws Exception {
        singleWrite(input, env, config);
    }


    /**
     * A common Single write method.
     * <p>
     * By default, both Batch and Stream modes will call it.
     *
     * @param input  a single table
     * @param env    flink env
     * @param config config for this element
     */
    abstract protected void singleWrite(Table input, FlinkRuntimeEnv env, Config config) throws Exception;
}
