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
import org.ssiu.ucp.core.config.OperatorConfig;
import org.ssiu.ucp.flink.core.env.FlinkRuntimeEnv;

import java.util.Map;

/**
 * A operator with two input table.
 * <p>
 * We provide a twoInputQuery because in Flink, the Batch and Stream implementations are mostly the same.
 *
 * @implNote If there is a real need to implement them separately, the user just needs to override twoInputBatchQuery for Batch mode.
 * @apiNote class impl this class should provide config {@link org.ssiu.ucp.core.config.OperatorConfig}.
 */
public abstract class FlinkTwoInputOperator implements FlinkStreamOperator, FlinkBatchOperator {
    @Override
    public Table batchQuery(Map<String, Table> inputs, FlinkRuntimeEnv env, Config config) throws Exception {
        Table left = inputs.get(OperatorConfig.LEFT);
        Table right = inputs.get(OperatorConfig.RIGHT);
        return twoInputQuery(left, right, env, config);
    }

    @Override
    public Table streamQuery(Map<String, Table> inputs, FlinkRuntimeEnv env, Config config) throws Exception {
        Table left = inputs.get(OperatorConfig.LEFT);
        Table right = inputs.get(OperatorConfig.RIGHT);
        return twoInputBatchQuery(left, right, env, config);
    }

    /**
     * A batch two input query method.
     * <p>
     * By default, it just call {@link org.ssiu.ucp.flink.core.api.FlinkTwoInputOperator#twoInputQuery}. If you need a special use for Batch mode, override this method
     *
     * @param left   left table
     * @param right  right table
     * @param env    flink env
     * @param config config for this element
     * @return result table
     */
    protected Table twoInputBatchQuery(Table left, Table right, FlinkRuntimeEnv env, Config config) throws Exception {
        return twoInputQuery(left, right, env, config);
    }

    /**
     * A common two input query method.
     * <p>
     * By default, both Batch and Stream modes will call it.
     *
     * @param left   left table
     * @param right  right table
     * @param env    flink env
     * @param config config for this element
     * @return result table
     */
    abstract protected Table twoInputQuery(Table left, Table right, FlinkRuntimeEnv env, Config config) throws Exception;

}
