package org.ssiu.ucp.util.base;

/**
 * A tuple of two elements.
 *
 * @param <T1> the type of the first element
 * @param <T2> the type of the second element
 * @author ssiu
 */
public final class Tuple2<T1, T2> {

    final private T1 e1;

    final private T2 e2;

    public Tuple2(T1 e1, T2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public T1 getE1() {
        return e1;
    }

    public T2 getE2() {
        return e2;
    }
}
