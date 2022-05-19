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

package org.ssiu.ucp.core.api;

import com.typesafe.config.Config;
import org.ssiu.ucp.core.env.RuntimeEnv;

import java.util.Map;

/**
 * Operation in Batch mode
 *
 * @param <E>   Runtime Env
 * @param <IN>  IN data format
 * @param <OUT> Result data format
 */
public interface BatchOperator<E extends RuntimeEnv, IN, OUT> extends Plugin<E> {
    /**
     * Query/process a batch data
     *
     * @param inputs tables. key is tableName in engine
     * @param env    runtime context
     * @param config element config
     * @return query result
     */
    OUT batchQuery(Map<String, IN> inputs, E env, Config config) throws Exception;
}
