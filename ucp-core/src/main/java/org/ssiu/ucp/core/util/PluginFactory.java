package org.ssiu.ucp.core.util;

import org.ssiu.ucp.core.api.Plugin;
import org.ssiu.ucp.core.env.RuntimeEnv;
import org.ssiu.ucp.util.base.ClassBuilder;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
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
        return load.stream()
                .map(provider -> (Plugin<E>) provider.get())
                .collect(Collectors.toList());
    }
}
