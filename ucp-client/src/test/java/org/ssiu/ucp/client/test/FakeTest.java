package org.ssiu.ucp.client.test;

import org.junit.jupiter.api.Test;
import org.ssiu.ucp.client.App;

public class FakeTest {

    @Test
    public void test() throws Exception {
        System.out.println("test");
        String[] in = {"-C", "/Users/liushengwei/IdeaProject/ucp-parent/ucp-client/src/test/resources/fake.conf", "-E", "Spark", "-D","local"};
        App.main(in);
    }

}
