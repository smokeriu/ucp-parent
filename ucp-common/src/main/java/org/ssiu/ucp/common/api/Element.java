/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
