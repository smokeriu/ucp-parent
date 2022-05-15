package org.ssiu.ucp.common.mode;

/**
 * Engine type
 *
 * @author ssiu
 */
public enum EngineType {

    /**
     * Flink engine
     */
    Flink("flink"),


    /**
     * Spark engine
     */
    Spark("spark");

    private final String simpleName;

    EngineType(String className) {
        this.simpleName = className;
    }

    public String getSimpleName() {
        return simpleName;
    }
}
