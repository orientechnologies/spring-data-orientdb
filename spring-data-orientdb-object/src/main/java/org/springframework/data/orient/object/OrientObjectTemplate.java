package org.springframework.data.orient.object;

import com.orientechnologies.orient.core.annotation.OId;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.springframework.data.orient.commons.core.AbstractOrientOperations;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class OrientObjectTemplate extends AbstractOrientOperations<Object> implements OrientObjectOperations {

    public OrientObjectTemplate(OrientObjectDatabaseFactory dbf) {
        super(dbf);
    }

    @Override
    public OObjectDatabaseTx getObjectDatabase() {
        return (OObjectDatabaseTx)dbf.db();
    }

    @Override
    public String getRid(Object entity) {
        Class<?> clazz = entity.getClass();
        while(clazz != Object.class){
            for(Field field : clazz.getDeclaredFields()){
                OId ridAnnotation = field.getAnnotation(OId.class);
                if(ridAnnotation != null){
                    field.setAccessible(true);
                    try{
                        Object rid = field.get(entity);
                        if(rid == null) {
                            Method method = clazz.getDeclaredMethod(getterName(field.getName()));
                            rid = method.invoke(entity);
                        }
                        return  rid != null ? rid.toString() : null;
                    } catch (IllegalAccessException | IllegalArgumentException
                            | NoSuchMethodException | InvocationTargetException ex){
                        throw new RuntimeException(ex);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private String getterName(String propertyName) {
        return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1).toLowerCase();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detach(RET entities) {
        // NOTE: this re-implementation avoids unnecessary calls to getObjectDatabase()
        final OObjectDatabaseTx db = getObjectDatabase();

        List<Object> result = new ArrayList<>(entities.size());

        for (Object entity : entities) {
            result.add(db.detach(entity, true));
        }

        return (RET)result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detachAll(RET entities) {
        // NOTE: this re-implementation avoids unnecessary calls to getObjectDatabase()
        final OObjectDatabaseTx db = getObjectDatabase();

        List<Object> result = new ArrayList<>(entities.size());

        for (Object entity : entities) {
            result.add(db.detachAll(entity, true));
        }

        return (RET) result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET> RET detach(RET entity) {
        return (RET)getObjectDatabase().detach(entity, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET> RET detachAll(RET entity) {
        return (RET)getObjectDatabase().detachAll(entity, true);
    }

/**
    @Override
    public <RET extends List<?>> RET query(OQuery<?> query, DetachMode detachMode, Object... args) {
        RET result = query(query, args);

        switch (detachMode) {
            case ENTITY:
                return detach(result);
            case ALL:
                return detachAll(result);
            case NONE:
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detach(RET list) {
        final OObjectDatabaseTx db = getObjectDatabase();

        List<Object> pojos = new ArrayList<>(list.size());

        for (Object object : list) {
            pojos.add(db.detach(object, true));
        }

        return (RET) pojos;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detachAll(RET list) {
        final OObjectDatabaseTx db = getObjectDatabase();

        List<Object> pojos = new ArrayList<>(list.size());

        for (Object object : list) {
            pojos.add(db.detachAll(object, true));
        }

        return (RET) pojos;
    }

    @Override
    public <RET> OObjectIteratorClass<RET> browseClass(Class<RET> iClusterClass) {
        return getObjectDatabase().browseClass(iClusterClass);
    }

    @Override
    public <RET> OObjectIteratorClass<RET> browseClass(Class<RET> iClusterClass, boolean iPolymorphic) {
        return getObjectDatabase().browseClass(iClusterClass, iPolymorphic);
    }

    @Override
    public <RET> OObjectIteratorClass<RET> browseClass(String iClassName) {
        return getObjectDatabase().browseClass(iClassName);
    }

    @Override
    public <RET> OObjectIteratorClass<RET> browseClass(String iClassName, boolean iPolymorphic) {
        return getObjectDatabase().browseClass(iClassName, iPolymorphic);
    }

    @Override
    public ODatabaseInternal<?> getDatabaseOwner() {
        return getObjectDatabase().getDatabaseOwner();
    }

    @Override
    public ODatabaseInternal<?> setDatabaseOwner(ODatabaseInternal<?> iOwner) {
        return getObjectDatabase().setDatabaseOwner(iOwner);
    }

    @Override
    public <RET> OObjectIteratorCluster<RET> browseCluster(String iClusterName) {
        return getObjectDatabase().browseCluster(iClusterName);
    }

    @Override
    public <V> V callInLock(Callable<V> iCallable, boolean iExclusiveLock) {
        return getObjectDatabase().callInLock(iCallable, iExclusiveLock);
    }

    @Override
    public ODatabasePojoAbstract<Object> setRetainObjects(boolean iValue) {
        return getObjectDatabase().setRetainObjects(iValue);
    }

    @Override
    public void attach(Object iPojo) {
        getObjectDatabase().attach(iPojo);
    }

    @Override
    public <RET> RET attachAndSave(Object iPojo) {
        return getObjectDatabase().attachAndSave(iPojo);
    }

    @Override
    public boolean isRetainObjects() {
        return getObjectDatabase().isRetainObjects();
    }

    @Override
    public <RET> RET detach(Object iPojo) {
        return getObjectDatabase().detach(iPojo);
    }

    @Override
    public <RET> RET detach(Object iPojo, boolean returnNonProxiedInstance) {
        return getObjectDatabase().detach(iPojo, returnNonProxiedInstance);
    }

    @Override
    public boolean existsUserObjectByRID(ORID iRID) {
        return getObjectDatabase().existsUserObjectByRID(iRID);
    }

    @Override
    public ODocument getRecordById(ORID iRecordId) {
        return getObjectDatabase().getRecordById(iRecordId);
    }

    @Override
    public <RET> RET detachAll(Object iPojo, boolean returnNonProxiedInstance) {
        return getObjectDatabase().detachAll(iPojo, returnNonProxiedInstance);
    }

    @Override
    public boolean isManaged(Object iEntity) {
        return getObjectDatabase().isManaged(iEntity);
    }

    @Override
    public Object getUserObjectByRecord(OIdentifiable iRecord, String iFetchPlan) {
        return dbf.db().getUserObjectByRecord(iRecord, iFetchPlan);
    }

    @Override
    public long countClass(String className) {
        return getObjectDatabase().countClass(className);
    }

    @Override
    public long countClass(Class<?> clazz) {
        return getObjectDatabase().countClass(clazz);
    }

    @Override
    public OEntityManager getEntityManager() {
        return getObjectDatabase().getEntityManager();
    }

    @Override
    public ODatabaseDocument getUnderlying() {
        return getObjectDatabase().getUnderlying();
    }

    @Override
    public ORecordVersion getVersion(Object iPojo) {
        return getObjectDatabase().getVersion(iPojo);
    }

    @Override
    public ORID getIdentity(Object iPojo) {
        return getObjectDatabase().getIdentity(iPojo);
    }

    @Override
    public boolean isSaveOnlyDirty() {
        return getObjectDatabase().isSaveOnlyDirty();
    }

    @Override
    public void setSaveOnlyDirty(boolean saveOnlyDirty) {
        getObjectDatabase().setSaveOnlyDirty(saveOnlyDirty);
    }

    @Override
    public boolean isAutomaticSchemaGeneration() {
        return getObjectDatabase().isAutomaticSchemaGeneration();
    }

    @Override
    public void setAutomaticSchemaGeneration(boolean automaticSchemaGeneration) {
        getObjectDatabase().setAutomaticSchemaGeneration(automaticSchemaGeneration);
    }

    @Override
    public ODocument pojo2Stream(Object iPojo, ODocument iRecord) {
        return getObjectDatabase().pojo2Stream(iPojo, iRecord);
    }

    @Override
    public Object stream2pojo(ODocument iRecord, Object iPojo, String iFetchPlan) {
        return getObjectDatabase().stream2pojo(iRecord, iPojo, iFetchPlan);
    }

    @Override
    public Object stream2pojo(ODocument iRecord, Object iPojo, String iFetchPlan, boolean iReload) {
        return getObjectDatabase().stream2pojo(iRecord, iPojo, iFetchPlan, iReload);
    }

    @Override
    public boolean isLazyLoading() {
        return getObjectDatabase().isLazyLoading();
    }

    @Override
    public void setLazyLoading(boolean lazyLoading) {
        getObjectDatabase().setLazyLoading(lazyLoading);
    }

    @Override
    public void unregisterPojo(Object iObject, ODocument iRecord) {
        getObjectDatabase().unregisterPojo(iObject, iRecord);
    }

    @Override
    public void registerClassMethodFilter(Class<?> iClass, OObjectMethodFilter iMethodFilter) {
        getObjectDatabase().registerClassMethodFilter(iClass, iMethodFilter);
    }

    @Override
    public void deregisterClassMethodFilter(Class<?> iClass) {
        getObjectDatabase().deregisterClassMethodFilter(iClass);
    }

    @Override
    public <RET> RET queryForObject(OSQLQuery<?> query, Object... args) {
        List<RET> list = query(query, args);

        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public <RET> RET queryForObject(OSQLQuery<?> query, DetachMode detachMode, Object... args) {
        RET result = queryForObject(query, args);

        switch (detachMode) {
            case ENTITY:
                return getObjectDatabase().detach(result, true);
            case ALL:
                return getObjectDatabase().detachAll(result, true);
            case NONE:
        }

        return result;
    }

    @Override
    public void registerEntityClass(Class<?> domainClass) {
        try (OObjectDatabaseTx db = getObjectDatabase()) {
            db.getEntityManager().registerEntityClass(domainClass);
        }
    }
     */
}
