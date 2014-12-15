package org.springframework.data.orient.object;

import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.annotation.OId;
import com.orientechnologies.orient.core.cache.OLocalRecordCache;
import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabase.ATTRIBUTES;
import com.orientechnologies.orient.core.db.ODatabase.OPERATION_MODE;
import com.orientechnologies.orient.core.db.ODatabase.STATUS;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.dictionary.ODictionary;
import com.orientechnologies.orient.core.entity.OEntityManager;
import com.orientechnologies.orient.core.exception.OTransactionException;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.hook.ORecordHook.HOOK_POSITION;
import com.orientechnologies.orient.core.hook.ORecordHook.RESULT;
import com.orientechnologies.orient.core.hook.ORecordHook.TYPE;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.intent.OIntent;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLQuery;
import com.orientechnologies.orient.core.storage.ORecordCallback;
import com.orientechnologies.orient.core.storage.ORecordMetadata;
import com.orientechnologies.orient.core.storage.OStorage;
import com.orientechnologies.orient.core.storage.OStorage.LOCKING_STRATEGY;
import com.orientechnologies.orient.core.tx.OTransaction;
import com.orientechnologies.orient.core.tx.OTransaction.TXTYPE;
import com.orientechnologies.orient.core.version.ORecordVersion;
import com.orientechnologies.orient.object.db.ODatabasePojoAbstract;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.enhancement.OObjectMethodFilter;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;
import com.orientechnologies.orient.object.iterator.OObjectIteratorCluster;
import com.orientechnologies.orient.object.metadata.OMetadataObject;
import org.springframework.data.orient.commons.repository.DetachMode;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

@Transactional
public class OrientObjectTemplate implements OrientObjectOperations {

    private final OrientObjectDatabaseFactory dbf;

    private Set<String> defaultClusters;

    public OrientObjectTemplate(OrientObjectDatabaseFactory dbf) {
        this.dbf = dbf;
    }

    public OObjectDatabaseTx database() {
        return dbf.db();
    }

    public void reload() {
        dbf.db().reload();
    }

    public STATUS getStatus() {
        return dbf.db().getStatus();
    }

    public <THISDB extends ODatabase<?>> THISDB setStatus(STATUS iStatus) {
        return dbf.db().setStatus(iStatus);
    }

    public String getName() {
        return dbf.db().getName();
    }

    public String getURL() {
        return dbf.db().getURL();
    }

    public OStorage getStorage() {
        return dbf.db().getStorage();
    }

    public OTransaction getTransaction() {
        return dbf.db().getTransaction();
    }

    public ODatabase<Object> begin() {
        return dbf.db().begin();
    }

    public OLocalRecordCache getLevel2Cache() {
        return dbf.db().getLocalCache();
    }

    public ODatabase<Object> begin(TXTYPE iType) {
        return dbf.db().begin(iType);
    }

    public boolean isClosed() {
        return dbf.db().isClosed();
    }

    public ODatabase<Object> begin(OTransaction iTx) {
        return dbf.db().begin(iTx);
    }

    public long countClusterElements(int iClusterId) {
        return dbf.db().countClusterElements(iClusterId);
    }

    public long countClusterElements(int[] iClusterIds) {
        return dbf.db().countClusterElements(iClusterIds);
    }

    @Override
    public long countClusterElements(String iClusterName) {
        return dbf.db().countClusterElements(iClusterName);
    }

    public <T> T newInstance(Class<T> iType) {
        return dbf.db().newInstance(iType);
    }

    public long countClusterElements(int iClusterId, boolean countTombstones) {
        return dbf.db().countClusterElements(iClusterId, countTombstones);
    }

    public <T> T newInstance(Class<T> iType, Object... iArgs) {
        return dbf.db().newInstance(iType, iArgs);
    }

    public long countClusterElements(int[] iClusterIds, boolean countTombstones) {
        return dbf.db().countClusterElements(iClusterIds, countTombstones);
    }

    public <RET> RET newInstance(String iClassName) {
        return dbf.db().newInstance(iClassName);
    }

    public void setDirty(Object iPojo) {
        dbf.db().setDirty(iPojo);
    }

    public int getClusters() {
        return dbf.db().getClusters();
    }

    public boolean existsCluster(String iClusterName) {
        return dbf.db().existsCluster(iClusterName);
    }

    public Collection<String> getClusterNames() {
        return dbf.db().getClusterNames();
    }

    public OMetadataObject getMetadata() {
        return dbf.db().getMetadata();
    }

    public void unsetDirty(Object iPojo) {
        dbf.db().unsetDirty(iPojo);
    }

    public <RET> RET newInstance(String iClassName, Object iEnclosingClass,
                                 Object... iArgs) {
        return dbf.db().newInstance(iClassName, iEnclosingClass, iArgs);
    }

    public int getClusterIdByName(String iClusterName) {
        return dbf.db().getClusterIdByName(iClusterName);
    }

    public void setInternal(ATTRIBUTES attribute, Object iValue) {
        dbf.db().setInternal(attribute, iValue);
    }

    @Override
    public String getClusterNameById(int iClusterId) {
        return dbf.db().getClusterNameById(iClusterId);
    }

    @Override
    public int getClusterIdByName(String clusterName, Class<?> aClass) {
        OClass oClass = dbf.db().getMetadata().getSchema().getClass(aClass);
        for(int clusterId : oClass.getClusterIds()){
            if(getClusterNameById(clusterId).equals(clusterName)){
                return clusterId;
            }
        }
        
        throw new OException("Cluster " + clusterName + " not found");
    }

    @Override
    public String getClusterNameByRid(String rid) {
        return getClusterNameById(new ORecordId(rid).getClusterId());
    }

    public long getClusterRecordSizeById(int iClusterId) {
        return dbf.db().getClusterRecordSizeById(iClusterId);
    }

    public long getClusterRecordSizeByName(String iClusterName) {
        return dbf.db().getClusterRecordSizeByName(iClusterName);
    }

    public int addCluster(String iType, String iClusterName, String iLocation, String iDataSegmentName, Object... iParameters) {
        return dbf.db().addCluster(iType, iClusterName, iLocation, iDataSegmentName, iParameters);
    }

    public int addCluster(String iType, String iClusterName, int iRequestedId, String iLocation, String iDataSegmentName, Object... iParameters) {
        return dbf.db().addCluster(iType, iClusterName, iRequestedId, iLocation, iDataSegmentName, iParameters);
    }

    public <RET> RET newInstance(String iClassName, Object iEnclosingClass, ODocument iDocument, Object... iArgs) {
        return dbf.db().newInstance(iClassName, iEnclosingClass, iDocument, iArgs);
    }

    public OSecurityUser getUser() {
        return dbf.db().getUser();
    }

    public int addCluster(String iClusterName, Object... iParameters) {
        return dbf.db().addCluster(iClusterName, iParameters);
    }

    public void setUser(OUser user) {
        dbf.db().setUser(user);
    }

    public <RET extends OCommandRequest> RET command(OCommandRequest iCommand) {
        return dbf.db().command(iCommand);
    }

    public int addCluster(String iClusterName) {
        return dbf.db().addCluster(iClusterName);
    }

    @Override
    public <RET extends List<?>> RET query(OQuery<?> iCommand, Object... iArgs) {
        return dbf.db().query(iCommand, iArgs);
    }

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

    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detach(RET list) {
        final OObjectDatabaseTx db = dbf.db();
        List<Object> pojos = new ArrayList<Object>(list.size());

        for (Object object : list) {
            pojos.add(db.detach(object, true));
        }

        return (RET) pojos;
    }

    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detachAll(RET list) {
        final OObjectDatabaseTx db = dbf.db();
        List<Object> pojos = new ArrayList<Object>(list.size());

        for (Object object : list) {
            pojos.add(db.detachAll(object, true));
        }

        return (RET) pojos;
    }

    @Override
    public <RET> OObjectIteratorClass<RET> browseClass(Class<RET> iClusterClass) {
        return dbf.db().browseClass(iClusterClass);
    }

    public int getDefaultClusterId() {
        return dbf.db().getDefaultClusterId();
    }

    public int getDefaultClusterId(Class<?> domainClass) {
        return dbf.db().getMetadata().getSchema().getClass(domainClass).getDefaultClusterId();
    }

    public <RET> OObjectIteratorClass<RET> browseClass(Class<RET> iClusterClass, boolean iPolymorphic) {
        return dbf.db().browseClass(iClusterClass, iPolymorphic);
    }

    public boolean declareIntent(OIntent iIntent) {
        return dbf.db().declareIntent(iIntent);
    }

    @Override
    public ODatabaseObject delete(ORecordInternal iRecord) {
        return dbf.db().delete(iRecord);
    }

    public <RET> OObjectIteratorClass<RET> browseClass(String iClassName) {
        return dbf.db().browseClass(iClassName);
    }

    public ODatabaseInternal<?> getDatabaseOwner() {
        return dbf.db().getDatabaseOwner();
    }

    public <RET> OObjectIteratorClass<RET> browseClass(String iClassName, boolean iPolymorphic) {
        return dbf.db().browseClass(iClassName, iPolymorphic);
    }

    public ODatabaseInternal<?> setDatabaseOwner(ODatabaseInternal<?> iOwner) {
        return dbf.db().setDatabaseOwner(iOwner);
    }

    public boolean equals(Object iOther) {
        return dbf.db().equals(iOther);
    }

    public <DBTYPE extends ODatabase<?>> DBTYPE registerHook(ORecordHook iHookImpl) {
        return dbf.db().registerHook(iHookImpl);
    }

    public <RET> OObjectIteratorCluster<RET> browseCluster(String iClusterName) {
        return dbf.db().browseCluster(iClusterName);
    }

    public String toString() {
        return dbf.db().toString();
    }

    public Object setProperty(String iName, Object iValue) {
        return dbf.db().setProperty(iName, iValue);
    }

    public <DBTYPE extends ODatabase<?>> DBTYPE registerHook(ORecordHook iHookImpl, HOOK_POSITION iPosition) {
        return dbf.db().registerHook(iHookImpl, iPosition);
    }

    public Object getProperty(String iName) {
        return dbf.db().getProperty(iName);
    }

    public RESULT callbackHooks(TYPE iType, OIdentifiable iObject) {
        return dbf.db().callbackHooks(iType, iObject);
    }

    public Iterator<Entry<String, Object>> getProperties() {
        return dbf.db().getProperties();
    }

    public <RET> RET load(Object iPojo) {
        return dbf.db().load(iPojo);
    }

    public <RET> RET reload(Object iPojo) {
        return dbf.db().reload(iPojo);
    }

    public Object get(ATTRIBUTES iAttribute) {
        return dbf.db().get(iAttribute);
    }

    public Map<ORecordHook, HOOK_POSITION> getHooks() {
        return dbf.db().getHooks();
    }

    public <RET> RET reload(Object iPojo, boolean iIgnoreCache) {
        return dbf.db().reload(iPojo, iIgnoreCache);
    }

    public <THISDB extends ODatabase<?>> THISDB set(ATTRIBUTES attribute, Object iValue) {
        return dbf.db().set(attribute, iValue);
    }

    public <DBTYPE extends ODatabase<?>> DBTYPE unregisterHook(ORecordHook iHookImpl) {
        return dbf.db().unregisterHook(iHookImpl);
    }

    public <RET> RET reload(Object iPojo, String iFetchPlan, boolean iIgnoreCache) {
        return dbf.db().reload(iPojo, iFetchPlan, iIgnoreCache);
    }

    public void registerListener(ODatabaseListener iListener) {
        dbf.db().registerListener(iListener);
    }

    public boolean isMVCC() {
        return dbf.db().isMVCC();
    }

    public <DBTYPE extends ODatabase<?>> DBTYPE setMVCC(boolean iMvcc) {
        return dbf.db().setMVCC(iMvcc);
    }

    public void unregisterListener(ODatabaseListener iListener) {
        dbf.db().unregisterListener(iListener);
    }

    public <V> V callInLock(Callable<V> iCallable, boolean iExclusiveLock) {
        return dbf.db().callInLock(iCallable, iExclusiveLock);
    }

    public ODatabasePojoAbstract<Object> setRetainObjects(boolean iValue) {
        return dbf.db().setRetainObjects(iValue);
    }

    public <RET> RET load(Object iPojo, String iFetchPlan) {
        return dbf.db().load(iPojo, iFetchPlan);
    }

    public void attach(Object iPojo) {
        dbf.db().attach(iPojo);
    }

    public ORecordMetadata getRecordMetadata(ORID rid) {
        return dbf.db().getRecordMetadata(rid);
    }

    public <RET> RET attachAndSave(Object iPojo) {
        return dbf.db().attachAndSave(iPojo);
    }

    public boolean isRetainObjects() {
        return dbf.db().isRetainObjects();
    }

    public long getSize() {
        return dbf.db().getSize();
    }

    public <RET> RET detach(Object iPojo) {
        return dbf.db().detach(iPojo);
    }

    public void freeze(boolean throwException) {
        dbf.db().freeze(throwException);
    }

    public void freeze() {
        dbf.db().freeze();
    }

    public void release() {
        dbf.db().release();
    }

    public void freezeCluster(int iClusterId, boolean throwException) {
        dbf.db().freezeCluster(iClusterId, throwException);
    }

    public <RET> RET detach(Object iPojo, boolean returnNonProxiedInstance) {
        return dbf.db().detach(iPojo, returnNonProxiedInstance);
    }

    public void freezeCluster(int iClusterId) {
        dbf.db().freezeCluster(iClusterId);
    }

    public void releaseCluster(int iClusterId) {
        dbf.db().releaseCluster(iClusterId);
    }

    public boolean existsUserObjectByRID(ORID iRID) {
        return dbf.db().existsUserObjectByRID(iRID);
    }

    public ODocument getRecordById(ORID iRecordId) {
        return dbf.db().getRecordById(iRecordId);
    }

    @Override
    public <RET> RET detachAll(Object iPojo, boolean returnNonProxiedInstance) {
        return dbf.db().detachAll(iPojo, returnNonProxiedInstance);
    }

    public boolean isManaged(Object iEntity) {
        return dbf.db().isManaged(iEntity);
    }

    public Object getUserObjectByRecord(OIdentifiable iRecord, String iFetchPlan) {
        return dbf.db().getUserObjectByRecord(iRecord, iFetchPlan);
    }

    public <RET> RET load(Object iPojo, String iFetchPlan, boolean iIgnoreCache) {
        return dbf.db().load(iPojo, iFetchPlan, iIgnoreCache);
    }

    @Override
    public <RET> RET load(ORID recordId) {
        return dbf.db().load(recordId);
    }

    @Override
    public final <RET> RET load(String recordId) {
        return load(new ORecordId(recordId));
    }

    public <RET> RET load(ORID iRecordId, String iFetchPlan) {
        return dbf.db().load(iRecordId, iFetchPlan);
    }

    public <RET> RET load(ORID iRecordId, String iFetchPlan, boolean iIgnoreCache) {
        return dbf.db().load(iRecordId, iFetchPlan, iIgnoreCache);
    }

    @Override
    public <RET> RET save(Object iContent) {
        return dbf.db().save(iContent);
    }

    public <RET> RET save(Object iContent, OPERATION_MODE iMode, boolean iForceCreate, ORecordCallback<? extends Number> iRecordCreatedCallback, ORecordCallback<ORecordVersion> iRecordUpdatedCallback) {
        return dbf.db().save(iContent, iMode, iForceCreate, iRecordCreatedCallback, iRecordUpdatedCallback);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.core.OrientOperations#save(java.lang.Object, java.lang.String)
     */
    @Override
    public <RET> RET save(Object iPojo, String iClusterName) {
        return dbf.db().save(iPojo, iClusterName);
    }


    public <RET> RET save(Object iPojo, String iClusterName,
                          OPERATION_MODE iMode, boolean iForceCreate,
                          ORecordCallback<? extends Number> iRecordCreatedCallback,
                          ORecordCallback<ORecordVersion> iRecordUpdatedCallback) {
        return dbf.db().save(iPojo, iClusterName, iMode, iForceCreate, iRecordCreatedCallback, iRecordUpdatedCallback);
    }

    @Override
    public ODatabaseObject delete(Object iPojo) {
        return dbf.db().delete(iPojo);
    }

    @Override
    public ODatabaseObject delete(ORID iRID) {
        return dbf.db().delete(iRID);
    }

    public ODatabaseObject delete(ORID iRID, ORecordVersion iVersion) {
        return dbf.db().delete(iRID, iVersion);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.core.OrientOperations#countClass(java.lang.String)
     */
    @Override
    public long countClass(String iClassName) {
        return dbf.db().countClass(iClassName);
    }

    /* (non-Javadoc)
     * @see org.springframework.data.orient.core.OrientOperations#countClass(java.lang.Class)
     */
    @Override
    public long countClass(Class<?> iClass) {
        return dbf.db().countClass(iClass);
    }

    public ODictionary<Object> getDictionary() {
        return dbf.db().getDictionary();
    }

    public ODatabasePojoAbstract<Object> commit() {
        return dbf.db().commit();
    }

    public ODatabasePojoAbstract<Object> rollback() {
        return dbf.db().rollback();
    }

    public OEntityManager getEntityManager() {
        return dbf.db().getEntityManager();
    }

    public ODatabaseDocument getUnderlying() {
        return dbf.db().getUnderlying();
    }

    public ORecordVersion getVersion(Object iPojo) {
        return dbf.db().getVersion(iPojo);
    }

    public ORID getIdentity(Object iPojo) {
        return dbf.db().getIdentity(iPojo);
    }

    public boolean isSaveOnlyDirty() {
        return dbf.db().isSaveOnlyDirty();
    }

    public void setSaveOnlyDirty(boolean saveOnlyDirty) {
        dbf.db().setSaveOnlyDirty(saveOnlyDirty);
    }

    public boolean isAutomaticSchemaGeneration() {
        return dbf.db().isAutomaticSchemaGeneration();
    }

    public void setAutomaticSchemaGeneration(boolean automaticSchemaGeneration) {
        dbf.db().setAutomaticSchemaGeneration(automaticSchemaGeneration);
    }

    public Object newInstance() {
        return dbf.db().newInstance();
    }

    public ODocument pojo2Stream(Object iPojo, ODocument iRecord) {
        return dbf.db().pojo2Stream(iPojo, iRecord);
    }

    public Object stream2pojo(ODocument iRecord, Object iPojo, String iFetchPlan) {
        return dbf.db().stream2pojo(iRecord, iPojo, iFetchPlan);
    }

    public Object stream2pojo(ODocument iRecord, Object iPojo, String iFetchPlan, boolean iReload) {
        return dbf.db().stream2pojo(iRecord, iPojo, iFetchPlan, iReload);
    }

    public boolean isLazyLoading() {
        return dbf.db().isLazyLoading();
    }

    public void setLazyLoading(boolean lazyLoading) {
        dbf.db().setLazyLoading(lazyLoading);
    }

    public String getType() {
        return dbf.db().getType();
    }

    public ODocument getRecordByUserObject(Object iPojo, boolean iCreateIfNotAvailable) {
        return dbf.db().getRecordByUserObject(iPojo, iCreateIfNotAvailable);
    }

    public Object getUserObjectByRecord(OIdentifiable iRecord, String iFetchPlan, boolean iCreate) {
        return dbf.db().getUserObjectByRecord(iRecord, iFetchPlan, iCreate);
    }

    public void registerUserObject(Object iObject, ORecord iRecord) {
        dbf.db().registerUserObject(iObject, iRecord);
    }

    public void registerUserObjectAfterLinkSave(ORecord iRecord) {
        dbf.db().registerUserObjectAfterLinkSave(iRecord);
    }

    public void unregisterPojo(Object iObject, ODocument iRecord) {
        dbf.db().unregisterPojo(iObject, iRecord);
    }

    public void registerClassMethodFilter(Class<?> iClass, OObjectMethodFilter iMethodFilter) {
        dbf.db().registerClassMethodFilter(iClass, iMethodFilter);
    }

    public void deregisterClassMethodFilter(Class<?> iClass) {
        dbf.db().deregisterClassMethodFilter(iClass);
    }

    public void backup(OutputStream out, Map<String, Object> options, Callable<Object> callable, OCommandOutputListener iListener, int compressionLevel, int bufferSize) throws IOException {
        dbf.db().backup(out, options, callable, iListener, compressionLevel, bufferSize);
    }

    public ODatabasePojoAbstract<Object> commit(boolean force) throws OTransactionException {
        return dbf.db().commit(force);
    }

    public <RET> RET load(Object iPojo, String iFetchPlan, boolean iIgnoreCache, boolean loadTombstone, LOCKING_STRATEGY iLockingStrategy) {
        return dbf.db().load(iPojo, iFetchPlan, iIgnoreCache, loadTombstone, iLockingStrategy);
    }

    public <RET> RET load(ORID iRecordId, String iFetchPlan, boolean iIgnoreCache, boolean loadTombstone, LOCKING_STRATEGY iLockingStrategy) {
        return dbf.db().load(iRecordId, iFetchPlan, iIgnoreCache, loadTombstone, iLockingStrategy);
    }

    public <THISDB extends ODatabase<?>> THISDB open(String iUserName, String iUserPassword) {
        return dbf.db().open(iUserName, iUserPassword);
    }

    public void restore(InputStream in, Map<String, Object> options, Callable<Object> callable, OCommandOutputListener iListener) throws IOException {
        dbf.db().restore(in, options, callable, iListener);
    }

    public ODatabasePojoAbstract<Object> rollback(boolean force) throws OTransactionException {
        return dbf.db().rollback(force);
    }

    public <RET> RET queryForObject(OSQLQuery<?> query, Object... values) {
        List<RET> list = query(query, values);

        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public <RET> RET queryForObject(OSQLQuery<?> query, DetachMode detachMode, Object... values) {
        RET result = queryForObject(query, values);

        switch (detachMode) {
            case ENTITY:
                return dbf.db().detach(result, true);
            case ALL:
                return dbf.db().detachAll(result, true);
            case NONE:
        }

        return result;
    }

    @Override
    public Long count(OSQLQuery<?> query, Object... values) {
        return ((ODocument) dbf.db().query(query, values).get(0)).field("count");
    }

    @Override
    public boolean existsClass(Class<?> clazz) {
        return existsClass(clazz.getSimpleName());
    }

    @Override
    public boolean existsClass(String className) {
        return dbf.db().getMetadata().getSchema().existsClass(className);
    }

    @Override
    public List<String> getClusterNamesByClass(Class<?> entityClass, boolean showDefault) {
        int[] clusterIds = dbf.db().getMetadata().getSchema().getClass(entityClass).getClusterIds();
        int defaultCluster = getDefaultClusterId(entityClass);

        List<String> clusters = new ArrayList<>(clusterIds.length);
        for (int clusterId : clusterIds) {
            if (showDefault || clusterId != defaultCluster) {
                clusters.add(getClusterNameById(clusterId));
            }
        }

        return clusters;
    }

    @Override
    public boolean isDefault(String clusterName) {
        loadDefaultClusters();
        return defaultClusters.contains(clusterName);
    }

    private void loadDefaultClusters() {
        if (defaultClusters == null) {
            synchronized (this) {
                if (defaultClusters == null) {
                    defaultClusters = new HashSet<>();
                    for (OClass oClass : dbf.db().getMetadata().getSchema().getClasses()) {
                        String defaultCluster = getClusterNameById(oClass.getDefaultClusterId());
                        defaultClusters.add(defaultCluster);
                    }
                }
            }
        }
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
        return "get" + propertyName.substring(0, 1).toUpperCase() +
                propertyName.substring(1).toLowerCase();
    }

    public void registerEntityClass(Class<?> domainClass) {
        try (OObjectDatabaseTx db = dbf.openDatabase()) {
            db.getEntityManager().registerEntityClass(domainClass);
        }
    }
    
    public <RET> RET command(OCommandSQL command, Object... args) {
        return dbf.db().command(command).execute(args);
    }
    
    public <RET> RET command(String sql, Object... args) {
        return command(new OCommandSQL(sql), args);
    }
}
