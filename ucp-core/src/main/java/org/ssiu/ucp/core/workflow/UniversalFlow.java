package org.ssiu.ucp.core.workflow;

import com.typesafe.config.Config;
import org.ssiu.ucp.common.api.Element;
import org.ssiu.ucp.common.exception.UnknownUcpTypeException;
import org.ssiu.ucp.common.mode.ElementType;
import org.ssiu.ucp.core.api.*;
import org.ssiu.ucp.core.config.BasicConfig;
import org.ssiu.ucp.core.env.RuntimeEnv;
import org.ssiu.ucp.core.service.PluginManager;
import org.ssiu.ucp.core.service.TableProvider;
import org.ssiu.ucp.core.util.CheckResult;
import org.ssiu.ucp.util.base.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * universal job flow
 *
 * @param <E> Runtime environment of each engine
 * @author ssiu
 */
public class UniversalFlow<E extends RuntimeEnv, T> extends AbstractFlow {


    /**
     * element map. element name -> element instance.
     */
    private Map<String, Element> elementDTOMap;

    /**
     * element list
     */
    private final List<Element> elementList;

    /**
     * Runtime environment of each engine
     */
    private final E env;

    /**
     * Provide and cache intermediate results
     */
    private final TableProvider<T> tableProvider;

    /**
     * Initialize and manage the required plug-ins
     */
    private PluginManager<E> pluginManager;

    public UniversalFlow(List<Element> elementList,
                         TableProvider<T> tableProvider,
                         E env) {
        this.elementList = elementList;
        this.env = env;
        this.tableProvider = tableProvider;
    }

    @Override
    public List<CheckResult> validateFlow() {
        return pluginManager.checkPlugins(elementList, env.isStreaming());
    }

    @Override
    public void initFlow() throws Exception {
        pluginManager = new PluginManager<>();
        elementDTOMap = initElementMap();
        // init plugin manager
        pluginManager.init();
        pluginManager.addCustomPlugins(elementList);
        pluginManager.preparePlugins(env);
    }

    private Map<String, Element> initElementMap() {
        return elementList.stream()
                .map(element -> new Tuple2<>(element.getName(), element))
                .collect(Collectors.toMap(Tuple2::getE1, Tuple2::getE2));
    }

    /**
     * Get all Writers and use them as the starting point for the run
     *
     * @throws Exception Exception from runElement
     */
    @Override
    public void runFlow() throws Exception {
        final List<Element> writers = getWriters();
        for (Element writer : writers) {
            runElement(writer);
        }
    }

    /**
     * When running an Element, we need to know if its dependencies have been completed.
     * <p>
     * The run logic is built through this lineage. Run its corresponding plugin when necessary
     *
     * @param element Element to run
     * @return result
     * @throws Exception from runPlugin
     */
    private T runElement(Element element) throws Exception {
        final List<String> parentNames = element.getParentNames();
        final Map<String, T> parentCache = new HashMap<>(parentNames.size());
        for (String parentName : parentNames) {
            final Optional<T> table = tableProvider.getTable(parentName);
            if (!table.isPresent()) {
                final T t = runElement(elementDTOMap.get(parentName));
                tableProvider.addTable(parentName, t);
                parentCache.put(parentName, t);
            } else {
                parentCache.put(parentName, table.get());
            }
        }
        return runPlugin(parentCache, env, element);
    }

    /**
     * Get the Plugin through pluginManager and run it.
     *
     * @param parentCache Input data required for Plugin
     * @param env         Runtime Environment
     * @param element     Configuration information required to allow this plugin
     * @return Running results
     * @throws Exception from run Plugin
     */
    private T runPlugin(Map<String, T> parentCache, E env, Element element) throws Exception {
        final Plugin<E> plugin = pluginManager.getPlugin(element);
        if (env.isStreaming()) {
            return runStreamPlugin(parentCache, env, plugin, element.getConfig());
        } else {
            return runBatchPlugin(parentCache, env, plugin, element.getConfig());
        }
    }

    /**
     * Run batch plugin if env is not streaming
     *
     * @param parentCache   Input data required for Plugin
     * @param env           Runtime Environment
     * @param plugin        plugin to run
     * @param elementConfig runtime config
     * @return result. May null if is a writer
     * @throws Exception               from runPlugin
     * @throws UnknownUcpTypeException from switch ElementType
     */
    private T runBatchPlugin(Map<String, T> parentCache, E env, Plugin<E> plugin, Config elementConfig) throws Exception {
        final ElementType elementType = elementConfig.getEnum(ElementType.class, BasicConfig.ELEMENT_TYPE);
        switch (elementType) {
            case Reader:
                return ((BatchReader<E, T>) plugin).batchRead(env, elementConfig);
            case Writer:
                ((BatchWriter<E, T>) plugin).batchWrite(parentCache, env, elementConfig);
                return null;
            case Operator:
                return ((BatchOperator<E, T, T>) plugin).batchQuery(parentCache, env, elementConfig);
            default:
                throw new UnknownUcpTypeException("unknown elementType");
        }
    }

    /**
     * Run stream plugin if env isStreaming
     *
     * @param parentCache   Input data required for Plugin
     * @param env           Runtime Environment
     * @param plugin        plugin to run
     * @param elementConfig runtime config
     * @return result. May null if is a writer
     * @throws Exception               from runPlugin
     * @throws UnknownUcpTypeException from switch ElementType
     */
    private T runStreamPlugin(Map<String, T> parentCache, E env, Plugin<E> plugin, Config elementConfig) throws Exception {
        final ElementType elementType = elementConfig.getEnum(ElementType.class, BasicConfig.ELEMENT_TYPE);
        switch (elementType) {
            case Reader:
                return ((StreamReader<E, T>) plugin).streamRead(env, elementConfig);
            case Writer:
                ((StreamWriter<E, T>) plugin).streamWrite(parentCache, env, elementConfig);
                return null;
            case Operator:
                return ((StreamOperator<E, T, T>) plugin).streamQuery(parentCache, env, elementConfig);
            default:
                throw new UnknownUcpTypeException("unknown elementType");
        }
    }

    /**
     * We extract all Writers. This is the starting point of the run
     *
     * @return All Writers
     */
    private List<Element> getWriters() {
        return elementList.stream()
                .filter(dto -> {
                    // is writer
                    final ElementType elementType = dto.getConfig().getEnum(ElementType.class, BasicConfig.ELEMENT_TYPE);
                    return ElementType.Writer.equals(elementType);
                }).collect(Collectors.toList());
    }

    @Override
    public void releaseFlow() {
        pluginManager.releasePlugins(env);
    }
}
