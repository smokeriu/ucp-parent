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
