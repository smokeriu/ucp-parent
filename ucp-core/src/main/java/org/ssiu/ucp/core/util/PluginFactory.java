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

import org.ssiu.ucp.core.api.Plugin;
import org.ssiu.ucp.core.env.RuntimeEnv;
import org.ssiu.ucp.util.base.ClassBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tools for building Plugin.
 * <p>
 * Build via SPI, or specify className to build
 */
public class PluginFactory {


    @SuppressWarnings("unchecked")
    private final static Class<Plugin<?>> pluginClass = (Class<Plugin<?>>) (Class<?>) Plugin.class;


    /**
     * @return pluginName -> Plugin instance
     */
    public static <E extends RuntimeEnv> Map<String, Plugin<E>> buildPluginInstanceMap() {
        // create by spi
        final List<Plugin<E>> pluginList = buildPluginList();
        return pluginList.stream().collect(Collectors.toMap(plugin -> plugin.getClass().getSimpleName(), plugin -> plugin));
    }

    @SuppressWarnings("unchecked")
    public static <E extends RuntimeEnv> Plugin<E> buildCustomPluginInstance(String classFullName) throws Exception {
        return (Plugin<E>) ClassBuilder.buildInstance(classFullName, PluginFactory.pluginClass);
    }

    @SuppressWarnings("unchecked")
    private static <E extends RuntimeEnv> List<Plugin<E>> buildPluginList() {
        final ServiceLoader<Plugin<?>> load = ServiceLoader.load(PluginFactory.pluginClass);
        final Iterator<Plugin<?>> iterator = load.iterator();
        final List<Plugin<E>> list = new ArrayList<>();
        while (iterator.hasNext()) {
            final Plugin<E> plugin = (Plugin<E>) iterator.next();
            list.add(plugin);
        }
        return list;
    }
}
