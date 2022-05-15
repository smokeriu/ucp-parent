package org.ssiu.ucp.common.api;


import com.typesafe.config.Config;
import com.typesafe.config.Optional;

import java.util.Collections;
import java.util.List;

/**
 * The element base object used for passing to project
 * element config store in{@link Config}.
 *
 * @author ssiu
 */
public class Element {

    public static final String ELEMENTS_KEY = "elements";


    /**
     * Element name. For building working relationships
     */
    private String name;

    /**
     * Element config
     */
    private Config config;

    /**
     * Element Dependent elements
     */
    @Optional
    private List<String> parentNames = Collections.emptyList();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public List<String> getParentNames() {
        return parentNames;
    }

    public void setParentNames(List<String> parentNames) {
        this.parentNames = parentNames;
    }

    @Override
    public String toString() {
        return "ElementDTO{" +
                ", name='" + name + '\'' +
                ", configuration=" + config +
                ", frontUUID=" + parentNames +
                '}';
    }

}
