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
import com.typesafe.config.ConfigValue;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {
    public static <V> V getAnyValue(Map<?, V> inputs) {
        return inputs.entrySet().iterator().next().getValue();
    }


    public static Map<String, ConfigValue> mapFromConfig(Config config, String name) {
        final HashMap<String, ConfigValue> map = new HashMap<>();
        if (config.hasPath(name)) {
            map.putAll(config.getConfig(name).root());
        }
        return map;
    }

    public static Map<String, String> stringMapFromConfig(Config config, String name) {
        final Map<String, ConfigValue> configValueMap = mapFromConfig(config, name);
        final HashMap<String, String> map = new HashMap<>();
        for (Map.Entry<String, ConfigValue> entry : configValueMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue().toString());
        }
        return map;
    }
}
