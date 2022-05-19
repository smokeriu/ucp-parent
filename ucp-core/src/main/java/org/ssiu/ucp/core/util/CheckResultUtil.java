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

package org.ssiu.ucp.core.util;

import com.typesafe.config.Config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CheckResultUtil {
    /**
     * Check if the given key exists in the given Config
     *
     * @param config        Configuration to be checked
     * @param checkLocation Used to prompt for information about the location to which the configuration belongs
     * @param key           The key that needs to be included in this layer of the config
     * @return error CheckResult mutable list.
     */
    public static List<CheckResult> checkAllExists(Config config, String checkLocation, String... key) {
        return Arrays.stream(key).map(aKey -> CheckResultUtil.checkKey(config, checkLocation, aKey))
                .filter(CheckResult::isErr)
                .collect(Collectors.toList());
    }

    public static CheckResult checkKey(Config config, String checkLocation, String key) {
        if (config.hasPath(key)) {
            return CheckResult.success();
        } else {
            String msg = String.format("missing config [%s] at [%s].", key, checkLocation);
            return CheckResult.error(msg);
        }
    }
}
