package org.ssiu.ucp.core.api;

import com.typesafe.config.Config;
import org.ssiu.ucp.core.env.RuntimeEnv;

import java.util.Map;

/**
 * Writer in Batch mode
 *
 * @param <E>  Runtime Env
 * @param <IN> IN data format
 */
public interface BatchWriter<E extends RuntimeEnv, IN> extends Plugin<E> {
    /**
     * Write data to external storages
     *
     * @param inputs data. key is data name in engine
     * @param env    runtime context
     * @param config element config
     */
    void batchWrite(Map<String, IN> inputs, E env, Config config) throws Exception;
}
