package org.ssiu.ucp.util.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ssiu.ucp.util.command.CommandFactory;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;

public class CommandTest {

    private Command commandObj;

    @BeforeEach
    public void setUp() {
        commandObj = new Command();
        commandObj.setFiled1("value1");
        commandObj.setFiled2("value2");
        final HashMap<String, String> map = new HashMap<>();
        map.put("conf1", "confvalue1");
        map.put("conf2", "confvalue2");
        commandObj.setFiled3(map);
    }

    @Test
    public void test() throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        final CommandFactory commandFactory = CommandFactory.builder()
                .build();
        final String command = commandFactory.toCommand(commandObj);
        Assertions.assertEquals("--key1 value1 --key2 value2 --conf conf2=confvalue2 --conf conf1=confvalue1", command);
    }

    @Test
    public void test2() throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        final CommandFactory commandFactory = CommandFactory.builder()
                .setMapJoinSeparator("->")
                .build();
        final String command = commandFactory.toCommand(commandObj);
        Assertions.assertEquals("--key1 value1 --key2 value2 --conf conf2->confvalue2 --conf conf1->confvalue1", command);
    }
}
