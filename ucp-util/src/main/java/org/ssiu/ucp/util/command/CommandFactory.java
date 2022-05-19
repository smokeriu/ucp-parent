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

package org.ssiu.ucp.util.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssiu.ucp.util.annotation.Parameter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.beans.*;

/**
 * A tool to build command using {@link Parameter} annotation
 */
public class CommandFactory {

    private final static Logger LOG = LoggerFactory.getLogger(CommandFactory.class);

    private String mapJoinSeparator;

    private String listJoinSeparator;

    private String commandJoinSeparator;


    private CommandFactory() {
    }

    private void setMapJoinSeparator(String mapJoinSeparator) {
        this.mapJoinSeparator = mapJoinSeparator;
    }

    private void setCommandJoinSeparator(String commandJoinSeparator) {
        this.commandJoinSeparator = commandJoinSeparator;
    }

    public void setListJoinSeparator(String listJoinSeparator) {
        this.listJoinSeparator = listJoinSeparator;
    }

    private List<String> parseParameter(Object obj) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        Class<?> aClass = obj.getClass();
        Field[] allFields = aClass.getDeclaredFields();
        List<String> result = new LinkedList<>();
        for (Field field : allFields) {
            Parameter annotation = field.getAnnotation(Parameter.class);
            if (annotation == null) {
                continue;
            }
            List<String> strings = parseSingleField(field, obj);
            List<String> singleLineCommand = strings.stream()
                    .filter(str -> !str.isEmpty())
                    .map(str -> annotation.value() + commandJoinSeparator + str)
                    .collect(Collectors.toList());
            result.addAll(singleLineCommand);
        }
        return result;
    }

    public String toCommand(Object obj) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        final List<String> commandList = parseParameter(obj);
        return String.join(commandJoinSeparator, commandList);
    }

    private List<String> parseSingleField(Field field, Object obj) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        if (Map.class.isAssignableFrom(field.getType())) {
            // is map
            Map<?, ?> mapField = getByGetter(field.getName(), obj);
            return mapToList(mapField);
        } else if (Collection.class.isAssignableFrom(field.getType())) {
            // is collection. convert to single string
            final Collection<?> list = getByGetter(field.getName(), obj);
            final String fieldValue = listJoin(list);
            return Collections.singletonList(fieldValue);
        } else {
            // common use
            final Object byGetter = getByGetter(field.getName(), obj);
            String fieldValue = "";
            if (byGetter != null) {
                fieldValue = byGetter.toString();
            }
            return Collections.singletonList(fieldValue);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getByGetter(String filedName, Object obj) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        return (T) new PropertyDescriptor(filedName, obj.getClass()).getReadMethod().invoke(obj);
    }

    private String listJoin(Collection<?> list) {
        if (list == null) {
            return "";
        } else {
            final List<String> strings = list.stream()
                    .filter(Objects::nonNull)
                    .map(Objects::toString).collect(Collectors.toList());
            return String.join(listJoinSeparator, strings);
        }
    }

    private List<String> mapToList(Map<?, ?> map) {
        if (map == null) {
            return Collections.emptyList();
        } else {
            return map.entrySet().stream()
                    .map(e -> e.getKey().toString() + mapJoinSeparator + e.getValue().toString())
                    .collect(Collectors.toList());
        }
    }

    public static CommandFactoryBuilder builder() {
        return new CommandFactoryBuilder();
    }


    public static class CommandFactoryBuilder {
        private String mapJoinSeparator = "=";

        private String listJoinSeparator = ",";

        private String commandJoinSeparator = " ";

        public CommandFactoryBuilder setMapJoinSeparator(String mapJoinSeparator) {
            this.mapJoinSeparator = mapJoinSeparator;
            return this;
        }

        public CommandFactoryBuilder setListJoinSeparator(String listJoinSeparator) {
            this.listJoinSeparator = listJoinSeparator;
            return this;
        }

        public CommandFactoryBuilder setCommandJoinSeparator(String commandJoinSeparator) {
            this.commandJoinSeparator = commandJoinSeparator;
            return this;
        }

        public CommandFactory build() {
            final CommandFactory commandFactory = new CommandFactory();
            commandFactory.setCommandJoinSeparator(commandJoinSeparator);
            commandFactory.setMapJoinSeparator(mapJoinSeparator);
            commandFactory.setListJoinSeparator(listJoinSeparator);
            return commandFactory;
        }
    }

}
