package org.ssiu.ucp.core.config;

/**
 * Unless otherwise noted, all the following properties are required for a plugin
 */
public final class BasicConfig {

    /**
     * Control the plugin type. operator / connector
     */
    public final static String ELEMENT_TYPE = "elementType";

    /**
     * plugin class name, HDFS / Mysql ...
     */
    public final static String PLUGIN_TYPE = "pluginType";

    /**
     * optional
     */
    public final static String CUSTOM = "custom";
}
