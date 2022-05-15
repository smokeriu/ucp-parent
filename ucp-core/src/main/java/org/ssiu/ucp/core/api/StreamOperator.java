package org.ssiu.ucp.core.api;

import com.typesafe.config.Config;
import org.ssiu.ucp.core.env.RuntimeEnv;

import java.util.Map;

/**
 * Operation in Stream mode
 *
 * @param <E>   Runtime Env
 * @param <IN>  IN data format
 * @param <OUT> Result data format
 */
public interface StreamOperator<E extends RuntimeEnv, IN, OUT> extends Plugin<E> {
    /**
     * Query/process a stream data
     *
     * @param inputs tables. key is tableName in engine
     * @param env    runtime context
     * @param config element config
     * @return query result
     */
    OUT streamQuery(Map<String, IN> inputs, E env, Config config) throws Exception;
}
