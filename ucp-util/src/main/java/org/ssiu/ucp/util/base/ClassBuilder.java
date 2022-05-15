package org.ssiu.ucp.util.base;

import java.util.Arrays;
import java.util.Optional;

/**
 * a class to help build class
 *
 * @author ssiu
 */
public class ClassBuilder {

    private static final String UNKNOWN_CLASS = "unknown";


    public static String buildClass(String... classNames) {
        final Optional<String> mayClassName = Arrays.stream(classNames).reduce((a, b) -> a + "." + b);
        return mayClassName.orElse(UNKNOWN_CLASS);
    }

    /**
     * build instance
     */
    public static <T> T buildInstance(String className, Class<T> clazz) throws Exception {
        final Object instance = Class.forName(className)
                .getConstructor()
                .newInstance();
        if (clazz.isInstance(instance)) {
            return clazz.cast(instance);
        } else {
            String errorMsg = String.format("instance class not equal. expected: %s, current: %s.", clazz.getName(), instance.getClass().getName());
            throw new ClassNotFoundException(errorMsg);
        }
    }
}
