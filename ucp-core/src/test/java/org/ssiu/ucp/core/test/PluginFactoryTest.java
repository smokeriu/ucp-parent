package org.ssiu.ucp.core.test;

import org.junit.jupiter.api.Test;
import org.ssiu.ucp.core.api.Plugin;
import org.ssiu.ucp.core.env.RuntimeEnv;
import org.ssiu.ucp.core.util.PluginFactory;

import java.util.Map;

public class PluginFactoryTest {

    @Test
    public void SPITest() {
        final Map<String, Plugin<RuntimeEnv>> pluginMap = PluginFactory.buildPluginInstanceMap();
        pluginMap.forEach((k, v) -> {
            System.out.println(k);
            System.out.println(v);
        });
    }

    @Test
    public void CustomClassTest() throws Exception {
        String className = "org.ssiu.ucp.test.app.CustomConnector";
        final Plugin<RuntimeEnv> runtimeEnvPlugin = PluginFactory.buildCustomPluginInstance(className);
        System.out.println(runtimeEnvPlugin);
    }
}
