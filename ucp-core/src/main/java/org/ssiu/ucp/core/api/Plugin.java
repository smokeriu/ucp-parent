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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssiu.ucp.core.util.CheckResult;
import org.ssiu.ucp.core.env.RuntimeEnv;

import java.io.Serializable;
import java.util.List;

/**
 * A element is plugin + configuration.
 *
 * @author ssiu
 */
public interface Plugin<E extends RuntimeEnv> extends Serializable {

    Logger LOG = LoggerFactory.getLogger(Plugin.class);

    /**
     * validate config
     *
     * @return a mutable list contains all check result
     */
    default List<CheckResult> validateConf(Config config) {
        return CheckResult.singleSuccessList();
    }

    /**
     * prepare plugin
     */
    default void prepare(E env) {
        LOG.info("Default prepare");
    }

    /**
     * release plugin
     */
    default void release(E env) {
        LOG.info("Default release");
    }

}
