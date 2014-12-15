package org.springframework.data.orient.commons.core;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLQuery;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;
import org.springframework.data.orient.commons.repository.DetachMode;

import java.util.List;

public interface OrientOperations {

    <RET> RET load(ORID recordId);
    
    <RET> RET load(String recordId);
    
    <RET> RET save(Object entity);
    
    <RET> RET save(Object entity, String cluster);
    
    Long count(OSQLQuery<?> query, Object... values);
    
    long countClass(String iClassName);
    
    long countClass(Class<?> iClass);
    
    long countClusterElements(String iClusterName);
    
    <RET extends List<?>> RET query(OQuery<?> query, Object... values);

    // TODO: move this to object module
    @Deprecated
    <RET> RET queryForObject(OSQLQuery<?> query, DetachMode detachMode, Object... values);

    // TODO: move this to object module
    @Deprecated
    <RET extends List<?>> RET query(OQuery<?> query, DetachMode detachMode, Object... values);
    
    ODatabase<Object> delete(ORecordInternal iRecord);

    ODatabaseObject delete(ORID iRID);

    ODatabaseObject delete(Object iPojo);

    // TODO: move this to object module
    @Deprecated
    <RET> OObjectIteratorClass<RET> browseClass(Class<RET> iClusterClass);

    int getDefaultClusterId(Class<?> domainClass);

    String getClusterNameById(int iClusterId);

    int getClusterIdByName(String clusterName, Class<?> aClass);

    String getClusterNameByRid(String rid);

    List<String> getClusterNamesByClass(Class<?> entityClass, boolean includeDefault);

    boolean isDefault(String clusterName);

    /**
     * Is Class registered in OrientDb
     *
     * @param clazz class to check
     * @return Is Class registered in OrientDb
     */
    boolean existsClass(Class<?> clazz);

    /**
     * Is Class registered in OrientDb
     *
     * @param className simple class name (clazz.getSimpleName())
     * @return Is Class registered in OrientDb
     */
    boolean existsClass(String className);

    /**
     * Find field annotated with {@link com.orientechnologies.orient.core.annotation.OId}
     * in entity and return it's value.
     *
     * @param entity Orient Entity
     * @return orient row ID or null if it is to found
     */
    String getRid(Object entity);
    
    void registerEntityClass(Class<?> domainClass);
    
    <RET> RET command(OCommandSQL command, Object... args);
    
    <RET> RET command(String sql, Object... args);
}
