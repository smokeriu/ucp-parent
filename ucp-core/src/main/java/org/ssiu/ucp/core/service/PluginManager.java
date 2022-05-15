package org.ssiu.ucp.core.service;

import com.typesafe.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ssiu.ucp.common.api.Element;
import org.ssiu.ucp.common.mode.ElementType;
import org.ssiu.ucp.core.api.*;
import org.ssiu.ucp.core.config.BasicConfig;
import org.ssiu.ucp.core.config.CustomConfig;
import org.ssiu.ucp.core.env.RuntimeEnv;
import org.ssiu.ucp.core.util.CheckResult;
import org.ssiu.ucp.core.util.PluginFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginManager<E extends RuntimeEnv> {

    private static final Logger LOG = LogManager.getLogger(PluginManager.class);


    public PluginManager() {
    }

    /**
     * all operator。className -> Plugin instance
     */
    private Map<String, Plugin<E>> allPlugins;


    public void init() {
        LOG.info("init plugin manager");
        // build all plugin
        LOG.info("build all plugin by SPI");
        allPlugins = new HashMap<>();
        final Map<String, Plugin<E>> pluginMap = PluginFactory.buildPluginInstanceMap();
        allPlugins.putAll(pluginMap);
        LOG.info("end build all plugin by SPI");
        LOG.info("pluginList: {}", allPlugins.keySet());
    }

    /**
     * Check for unfinished instantiated plugins
     *
     * @return a mutable list contains all check result
     */
    public List<CheckResult> checkPlugins(Collection<Element> dtoList, boolean isStreaming) {
        LOG.debug("Check if the required Plugin is fully loaded");
        final Set<String> classNameSet = dtoList.stream().map(this::findDtoClassName).collect(Collectors.toSet());
        final Set<String> simpleNameSet = allPlugins.values().stream().map(plugin -> plugin.getClass().getSimpleName()).collect(Collectors.toSet());
        classNameSet.removeAll(simpleNameSet);
        if (!classNameSet.isEmpty()) {
            classNameSet.forEach(name -> LOG.error("class not initialize: {}", name));
            // When there are plugins that are not loaded properly, we do not need to perform subsequent validation
            return Stream.of(CheckResult.error("Some plugins are not initialized")).collect(Collectors.toList());
        }

        LOG.debug("Check if conf meets the requirements of plugin");
        return validatePlugins(dtoList, isStreaming);
    }

    /**
     * validate all plugins by given elemtns
     *
     * @param elements    elements need run after validate
     * @param isStreaming is a stream env
     * @return a mutable list contains all check result
     */
    private List<CheckResult> validatePlugins(Collection<Element> elements, boolean isStreaming) {
        LOG.debug("start validate all plugins");
        return elements.stream().flatMap(element -> {
            Plugin<E> plugin = getPlugin(element);
            Config elementConfig = element.getConfig();
            List<CheckResult> checkResults = validatePlugin(plugin, elementConfig);
            CheckResult checkResultImpl = validatePluginImpl(plugin, elementConfig.getEnum(ElementType.class, BasicConfig.ELEMENT_TYPE), isStreaming);
            checkResults.add(checkResultImpl);
            return checkResults.stream();
        }).collect(Collectors.toList());
    }

    /**
     * Check if the configuration meets the running conditions of the plugin
     *
     * @return a mutable list contains all check result
     */
    private List<CheckResult> validatePlugin(Plugin<E> plugin, Config elementConfig) {
        return plugin.validateConf(elementConfig);
    }

    /**
     * Check that the plugin implements the required operational requirements
     *
     * @param plugin validate plugin
     * @return check result
     */
    private CheckResult validatePluginImpl(Plugin<E> plugin, ElementType elementType, boolean isStreaming) {
        @SuppressWarnings("unchecked")
        Class<? extends Plugin<E>> pluginClass = (Class<? extends Plugin<E>>) plugin.getClass();
        boolean assignableFrom;
        switch (elementType) {
            case Operator:
                assignableFrom = isStreaming ?
                        StreamOperator.class.isAssignableFrom(pluginClass) :
                        BatchOperator.class.isAssignableFrom(pluginClass);
                break;
            case Reader:
                assignableFrom = isStreaming ?
                        StreamReader.class.isAssignableFrom(pluginClass) :
                        BatchReader.class.isAssignableFrom(pluginClass);
                break;
            case Writer:
                assignableFrom = isStreaming ?
                        StreamWriter.class.isAssignableFrom(pluginClass) :
                        BatchWriter.class.isAssignableFrom(pluginClass);
                break;
            default:
                LOG.error("Unknown/Unrealized elementType of Plugin: {}. Need: {}", elementType, ElementType.values());
                return CheckResult.error("unknown/unrealized elementType of BatchPlugin: " + elementType);
        }
        if (!assignableFrom) {
            LOG.error("{} required implementations of {}'s {} mode.", plugin.toString(), isStreaming ? "stream" : "batch", elementType);
            return CheckResult.error("code is not implemented: " + plugin);
        }
        return CheckResult.success();

    }

    /**
     * Find the class name corresponding to the element and the plugin
     */
    private String findDtoClassName(Element element) {
        String className;
        final Config config = element.getConfig();
        if (config.hasPath(BasicConfig.CUSTOM)) {
            // user custom operator/connector
            final Config customConfig = config.getConfig(BasicConfig.CUSTOM);
            className = customConfig.getString(CustomConfig.CLASS_NAME);
        } else {
            className = config.getString(BasicConfig.PLUGIN_TYPE);
        }
        return className;
    }


    /**
     * Get the custom plugin complete className: packageName.className
     */
    private String getCustomCompleteClassName(Element element) {
        final Config customConfig = element.getConfig().getConfig(BasicConfig.CUSTOM);
        final String packageName = customConfig.getString(CustomConfig.PACKAGE_NAME);
        final String className = customConfig.getString(CustomConfig.CLASS_NAME);
        return String.join(".", packageName, className);
    }

    /**
     * Get ClassName. Not included PackageName
     */
    private String getClassName(Element element) {
        final String completeClassName = getCustomCompleteClassName(element);
        return completeClassName.substring(completeClassName.lastIndexOf('.') + 1);
    }

    /**
     * Add custom plugin and init it
     */
    public void addCustomPlugins(Collection<Element> elements) throws Exception {
        LOG.info("Add custom plugins from elementList");
        for (Element element : elements) {
            if (element.getConfig().hasPath(BasicConfig.CUSTOM)) {
                addCustomPlugin(element);
            }
        }
    }

    public void addCustomPlugin(Element element) throws Exception {
        LOG.info("Add custom plugin from element: {}", element.toString());
        final String className = getCustomCompleteClassName(element);
        final Plugin<E> customPlugin = addCustomPlugin(className);
        LOG.info("Success add custom plugin from element: {}", element.toString());
        final Plugin<E> mayExists = allPlugins.put(getClassName(element), customPlugin);
        if (mayExists != null) {
            LOG.warn("Custom plug-ins with the same name as the built-in plug-ins will remove the built-in plug-ins with the same name");
        }
    }

    /**
     * Build plugin by className
     */
    private Plugin<E> addCustomPlugin(String className) throws Exception {
        LOG.info("Build plugin by className: {}", className);
        return PluginFactory.buildCustomPluginInstance(className);
    }

    /**
     * Do prepare
     */
    public void preparePlugins(E env) {
        LOG.info("start prepare all plugins");
        allPlugins.values().forEach(plugin -> preparePlugin(plugin, env));
        LOG.info("end prepare all plugins");
    }

    /**
     * Do release
     */
    public void releasePlugins(E env) {
        LOG.info("start release all plugins");
        allPlugins.values().forEach(plugin -> releasePlugin(plugin, env));
        LOG.info("end release all plugins");
    }

    /**
     * Do plugin release
     */
    private void releasePlugin(Plugin<E> plugin, E env) {
        plugin.release(env);
    }

    /**
     * Do plugin prepare
     */
    private void preparePlugin(Plugin<E> plugin, E env) {
        plugin.prepare(env);
    }

    public Map<String, Plugin<E>> getAllPlugins() {
        return allPlugins;
    }

    public Plugin<E> getPlugin(Element dto) {
        final String pluginName = findDtoClassName(dto);
        return getPluginByName(pluginName);
    }

    public Plugin<E> getPluginByName(String name) {
        return allPlugins.get(name);
    }
}
