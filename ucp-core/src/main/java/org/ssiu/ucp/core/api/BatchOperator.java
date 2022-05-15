package org.ssiu.ucp.core.api;

import com.typesafe.config.Config;
import org.ssiu.ucp.core.env.RuntimeEnv;

import java.util.Map;

/**
 * Operation in Batch mode
 *
 * @param <E>   Runtime Env
 * @param <IN>  IN data format
 * @param <OUT> Result data format
 */
public interface BatchOperator<E extends RuntimeEnv, IN, OUT> extends Plugin<E> {
    /**
     * Query/process a batch data
     *
     * @param inputs tables. key is tableName in engine
     * @param env    runtime context
     * @param config element config
     * @return query result
     */
    OUT batchQuery(Map<String, IN> inputs, E env, Config config) throws Exception;
}
