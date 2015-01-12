package org.springframework.data.orient.commons.core;

import com.orientechnologies.orient.core.cache.OLocalRecordCache;
import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseListener;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.dictionary.ODictionary;
import com.orientechnologies.orient.core.exception.OTransactionException;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.intent.OIntent;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLQuery;
import com.orientechnologies.orient.core.storage.ORecordCallback;
import com.orientechnologies.orient.core.storage.ORecordMetadata;
import com.orientechnologies.orient.core.storage.OStorage;
import com.orientechnologies.orient.core.tx.OTransaction;
import com.orientechnologies.orient.core.version.ORecordVersion;
import org.springframework.data.orient.commons.repository.DetachMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.Callable;

public interface OrientOperations<T> {

    String getName();

    String getURL();

    ODatabase<T> database();

    Object setProperty(String name, Object value);

    Object getProperty(String name);

    Iterator<Map.Entry<String, Object>> getProperties();

    Object get(ODatabase.ATTRIBUTES attribute);

    <DB extends ODatabase<T>> DB set(ODatabase.ATTRIBUTES attribute, Object value);

    public void registerListener(ODatabaseListener listener);

    public void unregisterListener(ODatabaseListener listener);

    Map<ORecordHook, ORecordHook.HOOK_POSITION> getHooks();

    <DB extends ODatabase<T>> DB registerHook(ORecordHook hook);

    <DB extends ODatabase<T>> DB registerHook(ORecordHook hook, ORecordHook.HOOK_POSITION position);

    <DB extends ODatabase<T>> DB unregisterHook(ORecordHook hook);

    ORecordHook.RESULT callbackHooks(ORecordHook.TYPE type, OIdentifiable id);

    void backup(OutputStream out, Map<String, Object> options, Callable<Object> callable, OCommandOutputListener listener, int compressionLevel, int bufferSize) throws IOException;

    void restore(InputStream in, Map<String, Object> options, Callable<Object> callable, OCommandOutputListener listener) throws IOException;

    String getType();

    long getSize();

    void freeze(boolean throwException);

    void freeze();

    void release();

    OMetadata getMetadata();

    ORecordMetadata getRecordMetadata(ORID rid);

    ODictionary<T> getDictionary();

    boolean declareIntent(OIntent intent);


    public boolean isMVCC();

    public <DB extends ODatabase<T>> DB setMVCC(boolean mvcc);

    boolean isClosed();

    void reload();

    T reload(T entity, String fetchPlan, boolean ignoreCache);

    ODatabase.STATUS getStatus();

    <DB extends ODatabase<T>> DB setStatus(ODatabase.STATUS status);

    OTransaction getTransaction();

    ODatabase<T> begin();

    ODatabase<?> begin(OTransaction.TXTYPE type);

    ODatabase<T> begin(OTransaction tx);

    ODatabase<T> commit();

    ODatabase<T> commit(boolean force) throws OTransactionException;

    ODatabase<T> rollback();

    ODatabase<T> rollback(boolean force) throws OTransactionException;

    OLocalRecordCache getLevel2Cache();

    T newInstance();
    
    T load(ORID recordId);
    
    T load(String recordId);

    T load(T entity);

    T load(T entity, String fetchPlan);

    T load(T entity, String fetchPlan, boolean ignoreCache);

    T load(ORID recordId, String fetchPlan);

    T load(ORID recordId, String fetchPlan, boolean ignoreCache);

    T load(T entity, String fetchPlan, boolean ignoreCache, boolean loadTombstone, OStorage.LOCKING_STRATEGY lockingStrategy);

    T load(ORID recordId, String fetchPlan, boolean ignoreCache, boolean loadTombstone, OStorage.LOCKING_STRATEGY lockingStrategy);

    <S extends T> S save(S entity);
    
    <S extends T> S save(S entity, String cluster);

    <S extends T> S save(S entity, ODatabase.OPERATION_MODE mode, boolean forceCreate, ORecordCallback<? extends Number> recordCallback, ORecordCallback<ORecordVersion> recordUpdatedCallback);

    long countClass(String className);

    long countClass(Class<?> clazz);

    long count(OSQLQuery<?> query, Object... args);

    long countClusterElements(String clusterName);

    long countClusterElements(int clusterId);

    long countClusterElements(int[] clusterIds);

    long countClusterElements(int iClusterId, boolean countTombstones);

    long countClusterElements(int[] iClusterIds, boolean countTombstones);

    int getClusters();

    boolean existsCluster(String iClusterName);

    Collection<String> getClusterNames();

    ODatabase<T> delete(ORID recordId);

    ODatabase<T> delete(T entity);

    ODatabase<T> delete(ORID rid, ORecordVersion version);

    int getDefaultClusterId();

    int getDefaultClusterId(Class<?> domainClass);

    String getClusterNameById(int clusterId);

    int getClusterIdByName(String clusterName);

    int getClusterIdByName(String clusterName, Class<?> clazz);

    String getClusterNameByRid(String rid);

    List<String> getClusterNamesByClass(Class<?> clazz, boolean includeDefault);

    long getClusterRecordSizeById(int clusterId);

    long getClusterRecordSizeByName(String clusterName);

    int addCluster(String type, String clusterName, String location, String dataSegmentName, Object... params);

    int addCluster(String type, String clusterName, int requestedId, String location, String dataSegmentName, Object... params);

    int addCluster(String clusterName, Object... params);

    int addCluster(String clusterName);

    public void freezeCluster(int iClusterId, boolean throwException);

    public void freezeCluster(int iClusterId);

    public void releaseCluster(int iClusterId);

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
    String getRid(T entity);

    OSecurityUser getUser();

    void setUser(OUser user);

    <RET extends List<?>> RET detach(RET entities);

    <RET extends List<?>> RET detachAll(RET list);

    <RET> RET detach(RET entity);

    <RET> RET detachAll(RET entity);

    <RET extends List<?>> RET query(OQuery<?> query, Object... args);

    <RET extends List<?>> RET query(OQuery<?> query, DetachMode detachMode, Object... args);

    <RET> RET queryForObject(OSQLQuery<?> query, Object... args);

    <RET> RET queryForObject(OSQLQuery<?> query, DetachMode detachMode, Object... args);

    <RET extends OCommandRequest> RET command(OCommandRequest command);

    <RET> RET command(OCommandSQL command, Object... args);
    
    <RET> RET command(String sql, Object... args);
}
