package org.springframework.data.orient.document;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLQuery;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;
import org.springframework.data.orient.commons.core.OrientOperations;
import org.springframework.data.orient.commons.repository.DetachMode;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class OrientDocumentTemplate implements OrientOperations {

    private OrientDocumentDatabaseFactory dbf;

    @Override
    public <RET> RET load(ORID recordId) {
        return null;
    }

    @Override
    public <RET> RET load(String recordId) {
        return null;
    }

    @Override
    public <RET> RET save(Object entity) {
        return null;
    }

    @Override
    public <RET> RET save(Object entity, String cluster) {
        return null;
    }

    @Override
    public Long count(OSQLQuery<?> query, Object... values) {
        return null;
    }

    @Override
    public long countClass(String iClassName) {
        return 0;
    }

    @Override
    public long countClass(Class<?> iClass) {
        return 0;
    }

    @Override
    public long countClusterElements(String iClusterName) {
        return 0;
    }

    @Override
    public <RET extends List<?>> RET query(OQuery<?> query, Object... values) {
        return null;
    }

    @Override
    public <RET> RET queryForObject(OSQLQuery<?> query, DetachMode detachMode, Object... values) {
        return null;
    }

    @Override
    public <RET extends List<?>> RET query(OQuery<?> query, DetachMode detachMode, Object... values) {
        return null;
    }

    @Override
    public ODatabase<Object> delete(ORecordInternal iRecord) {
        return null;
    }

    @Override
    public ODatabaseObject delete(ORID iRID) {
        return null;
    }

    @Override
    public ODatabaseObject delete(Object iPojo) {
        return null;
    }

    @Override
    public <RET> OObjectIteratorClass<RET> browseClass(Class<RET> iClusterClass) {
        return null;
    }

    @Override
    public int getDefaultClusterId(Class<?> domainClass) {
        return 0;
    }

    @Override
    public String getClusterNameById(int iClusterId) {
        return null;
    }

    @Override
    public int getClusterIdByName(String clusterName, Class<?> aClass) {
        return 0;
    }

    @Override
    public String getClusterNameByRid(String rid) {
        return null;
    }

    @Override
    public List<String> getClusterNamesByClass(Class<?> entityClass, boolean includeDefault) {
        return null;
    }

    @Override
    public boolean isDefault(String clusterName) {
        return false;
    }

    @Override
    public boolean existsClass(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean existsClass(String className) {
        return false;
    }

    @Override
    public String getRid(Object entity) {
        return null;
    }

    @Override
    public void registerEntityClass(Class<?> domainClass) {

    }

    @Override
    public <RET> RET command(OCommandSQL command, Object... args) {
        return null;
    }

    @Override
    public <RET> RET command(String sql, Object... args) {
        return null;
    }
}
