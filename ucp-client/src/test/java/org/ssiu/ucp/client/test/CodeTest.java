package org.ssiu.ucp.client.test;

import org.junit.jupiter.api.Test;
import org.ssiu.ucp.client.App;

public class CodeTest {
    @Test
    public void test() throws Exception {
        System.out.println("test");
        String[] in = {"-C", "/ucp-parent/ucp-client/src/test/resources/code.conf", "-E", "Spark", "-D","local"};
        App.main(in);
    }
}
