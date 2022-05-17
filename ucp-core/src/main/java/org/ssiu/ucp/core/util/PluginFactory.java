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
