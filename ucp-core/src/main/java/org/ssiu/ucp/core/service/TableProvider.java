package org.ssiu.ucp.core.service;

import java.util.Optional;

public interface TableProvider<T> {


    Optional<T> getTable(String name) ;

    void addTable(String name, T t) ;

}
