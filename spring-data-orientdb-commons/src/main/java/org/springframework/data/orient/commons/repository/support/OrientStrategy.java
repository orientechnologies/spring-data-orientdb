package org.springframework.data.orient.commons.repository.support;


public interface OrientStrategy<T> {

    <S extends T> S save(S entity);
    
    long count();
    
    String getSource();
}
