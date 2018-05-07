package org.springframework.data.orient.commons.core;

import com.orientechnologies.orient.core.cache.OLocalRecordCache;
import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseListener;
import com.orientechnologies.orient.core.db.object.ODatabaseObject;
import com.orientechnologies.orient.core.dictionary.ODictionary;
import com.orientechnologies.orient.core.exception.OTransactionException;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.intent.OIntent;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.core.storage.ORecordCallback;
import com.orientechnologies.orient.core.storage.ORecordMetadata;
import com.orientechnologies.orient.core.storage.OStorage;
import com.orientechnologies.orient.core.tx.OTransaction;
import org.springframework.data.orient.commons.repository.DetachMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.Callable;

public abstract class AbstractOrientOperations<T> implements OrientOperations<T> {

  protected final OrientDatabaseFactory<T> dbf;

  protected Set<String> defaultClusters;

  protected AbstractOrientOperations(OrientDatabaseFactory<T> dbf) {
    this.dbf = dbf;
  }

  @Override
  public String getName() {
    return dbf.db().getName();
  }

  @Override
  public String getURL() {
    return dbf.db().getURL();
  }

  @Override
  public ODatabase<T> database() {
    return dbf.db();
  }

  @Override
  public Object setProperty(String name, Object value) {
    return dbf.db().setProperty(name, value);
  }

  @Override
  public Object getProperty(String name) {
    return dbf.db().getProperty(name);
  }

  @Override
  public Iterator<Map.Entry<String, Object>> getProperties() {
    return dbf.db().getProperties();
  }

  @Override
  public Object get(ODatabase.ATTRIBUTES attribute) {
    return dbf.db().get(attribute);
  }

  @Override
  public <DB extends ODatabase<T>> DB set(ODatabase.ATTRIBUTES attribute, Object value) {
    return dbf.db().set(attribute, value);
  }

  @Override
  public void registerListener(ODatabaseListener listener) {
    dbf.db().registerListener(listener);
  }

  @Override
  public void unregisterListener(ODatabaseListener listener) {
    dbf.db().unregisterListener(listener);
  }

  @Override
  public Map<ORecordHook, ORecordHook.HOOK_POSITION> getHooks() {
    return dbf.db().getHooks();
  }

  @Override
  public <DB extends ODatabase<T>> DB registerHook(ORecordHook hook) {
    return dbf.db().registerHook(hook);
  }

  @Override
  public <DB extends ODatabase<T>> DB registerHook(ORecordHook hook, ORecordHook.HOOK_POSITION position) {
    return dbf.db().registerHook(hook, position);
  }

  @Override
  public <DB extends ODatabase<T>> DB unregisterHook(ORecordHook hook) {
    return dbf.db().unregisterHook(hook);
  }

  @Override
  public void backup(OutputStream out, Map<String, Object> options, Callable<Object> callable, OCommandOutputListener listener,
      int compressionLevel, int bufferSize) throws IOException {
    dbf.db().backup(out, options, callable, listener, compressionLevel, bufferSize);
  }

  @Override
  public void restore(InputStream in, Map<String, Object> options, Callable<Object> callable, OCommandOutputListener listener)
      throws IOException {
    dbf.db().restore(in, options, callable, listener);
  }

  @Override
  public String getType() {
    return dbf.db().getType();
  }

  @Override
  public long getSize() {
    return dbf.db().getSize();
  }

  @Override
  public void freeze(boolean throwException) {
    dbf.db().freeze(throwException);
  }

  @Override
  public void freeze() {
    dbf.db().freeze();
  }

  @Override
  public void release() {
    dbf.db().release();
  }

  @Override
  public OMetadata getMetadata() {
    return dbf.db().getMetadata();
  }

  @Override
  public ORecordMetadata getRecordMetadata(ORID rid) {
    return dbf.db().getRecordMetadata(rid);
  }

  @Override
  public ODictionary<T> getDictionary() {
    return dbf.db().getDictionary();
  }

  @Override
  public boolean declareIntent(OIntent intent) {
    return dbf.db().declareIntent(intent);
  }

  @Override
  public boolean isMVCC() {
    return dbf.db().isMVCC();
  }

  @Override
  public <DB extends ODatabase<T>> DB setMVCC(boolean mvcc) {
    return dbf.db().setMVCC(mvcc);
  }

  @Override
  public boolean isClosed() {
    return dbf.db().isClosed();
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
  public void reload() {
    dbf.db().reload();
  }

  @Override
  public T reload(T entity, String fetchPlan, boolean ignoreCache) {
    return dbf.db().reload(entity, fetchPlan, ignoreCache);
  }

  @Override
  public ODatabase.STATUS getStatus() {
    return dbf.db().getStatus();
  }

  @Override
  public <DB extends ODatabase<T>> DB setStatus(ODatabase.STATUS status) {
    return dbf.db().setStatus(status);
  }

  @Override
  public OTransaction getTransaction() {
    return dbf.db().getTransaction();
  }

  @Override
  public ODatabase<T> begin() {
    return dbf.db().begin();
  }

  @Override
  public ODatabase<T> begin(OTransaction.TXTYPE type) {
    return dbf.db().begin(type);
  }

  @Override
  public ODatabase<T> begin(OTransaction tx) {
    return dbf.db().begin(tx);
  }

  @Override
  public ODatabase<T> commit() {
    return dbf.db().commit();
  }

  @Override
  public ODatabase<T> commit(boolean force) throws OTransactionException {
    return dbf.db().commit(force);
  }

  @Override
  public ODatabase<T> rollback() {
    return dbf.db().rollback();
  }

  @Override
  public ODatabase<T> rollback(boolean force) throws OTransactionException {
    return dbf.db().rollback(force);
  }

  @Override
  public OLocalRecordCache getLevel2Cache() {
    return dbf.db().getLocalCache();
  }

  @Override
  public T newInstance() {
    return dbf.db().newInstance();
  }

  @Override
  public T load(ORID recordId) {
    return dbf.db().load(recordId);
  }

  @Override
  public T load(String recordId) {
    return load(new ORecordId(recordId));
  }

  public T load(T entity) {
    return dbf.db().load(entity);
  }

  @Override
  public T load(T entity, String fetchPlan) {
    return dbf.db().load(entity, fetchPlan);
  }

  @Override
  public T load(T entity, String fetchPlan, boolean ignoreCache) {
    return dbf.db().load(entity, fetchPlan, ignoreCache);
  }

  @Override
  public T load(ORID recordId, String fetchPlan) {
    return dbf.db().load(recordId, fetchPlan);
  }

  @Override
  public T load(ORID recordId, String fetchPlan, boolean ignoreCache) {
    return dbf.db().load(recordId, fetchPlan, ignoreCache);
  }

  @Override
  public T load(T entity, String fetchPlan, boolean ignoreCache, boolean loadTombstone, OStorage.LOCKING_STRATEGY lockingStrategy) {
    return dbf.db().load(entity, fetchPlan, ignoreCache, loadTombstone, lockingStrategy);
  }

  @Override
  public T load(ORID recordId, String fetchPlan, boolean ignoreCache, boolean loadTombstone,
      OStorage.LOCKING_STRATEGY lockingStrategy) {
    return dbf.db().load(recordId, fetchPlan, ignoreCache, loadTombstone, lockingStrategy);
  }

  @Override
  public <S extends T> S save(S entity) {
    return dbf.db().save(entity);
  }

  @Override
  public <S extends T> S save(S entity, String cluster) {
    return dbf.db().save(entity, cluster);
  }

  @Override
  public <S extends T> S save(S entity, ODatabase.OPERATION_MODE mode, boolean forceCreate,
      ORecordCallback<? extends Number> recordCallback, ORecordCallback<Integer> recordUpdatedCallback) {
    return dbf.db().save(entity, mode, forceCreate, recordCallback, recordUpdatedCallback);
  }

  @Override
  public long countClass(Class<?> clazz) {
    return count("select count(*) from " + clazz.getSimpleName());
  }

  @Override
  public long countClass(String className) {
    return count("select count(*) from " + className);
  }

  @Override
  public long count(String query, Object... args) {
    OResultSet rs = dbf.db().query(query, args);
    if (rs.hasNext()) {
      Object result = rs.next().getProperty("count(*)");
      if (result instanceof Number) {
        return ((Number) result).longValue();
      }
    }
    return 0;
  }

  @Override
  public long countClusterElements(String clusterName) {
    return dbf.db().countClusterElements(clusterName);
  }

  @Override
  public long countClusterElements(int clusterId) {
    return dbf.db().countClusterElements(clusterId);
  }

  @Override
  public long countClusterElements(int[] clusterIds) {
    return dbf.db().countClusterElements(clusterIds);
  }

  @Override
  public long countClusterElements(int iClusterId, boolean countTombstones) {
    return dbf.db().countClusterElements(iClusterId, countTombstones);
  }

  @Override
  public long countClusterElements(int[] iClusterIds, boolean countTombstones) {
    return dbf.db().countClusterElements(iClusterIds, countTombstones);
  }

  @Override
  public int getClusters() {
    return dbf.db().getClusters();
  }

  @Override
  public boolean existsCluster(String clusterName) {
    return dbf.db().existsCluster(clusterName);
  }

  @Override
  public Collection<String> getClusterNames() {
    return dbf.db().getClusterNames();
  }

  @Override
  public int getClusterIdByName(String clusterName, Class<?> clazz) {
    OClass oClass = dbf.db().getMetadata().getSchema().getClass(clazz);
    for (int clusterId : oClass.getClusterIds()) {
      if (getClusterNameById(clusterId).equals(clusterName)) {
        return clusterId;
      }
    }

    throw new IllegalArgumentException("Cluster " + clusterName + " not found");
  }

  @Override
  public String getClusterNameByRid(String rid) {
    return getClusterNameById(new ORecordId(rid).getClusterId());
  }

  @Override
  public List<String> getClusterNamesByClass(Class<?> clazz, boolean showDefault) {
    int[] clusterIds = dbf.db().getMetadata().getSchema().getClass(clazz).getClusterIds();
    int defaultCluster = getDefaultClusterId(clazz);

    List<String> clusters = new ArrayList<>(clusterIds.length);
    for (int clusterId : clusterIds) {
      if (showDefault || clusterId != defaultCluster) {
        clusters.add(getClusterNameById(clusterId));
      }
    }

    return clusters;
  }

  @Override
  public int getDefaultClusterId(Class<?> domainClass) {
    return dbf.db().getMetadata().getSchema().getClass(domainClass).getDefaultClusterId();
  }

  @Override
  public long getClusterRecordSizeById(int clusterId) {
    return dbf.db().getClusterRecordSizeById(clusterId);
  }

  @Override
  public long getClusterRecordSizeByName(String clusterName) {
    return dbf.db().getClusterRecordSizeByName(clusterName);
  }

  @Override
  public int addCluster(String type, String clusterName, String location, String dataSegmentName, Object... params) {
    return dbf.db().addCluster(type, clusterName, location, dataSegmentName, params);
  }

  @Override
  public int addCluster(String type, String clusterName, int requestedId, String location, String dataSegmentName,
      Object... params) {
    return dbf.db().addCluster(type, clusterName, requestedId, location, dataSegmentName, params);
  }

  @Override
  public int addCluster(String clusterName, Object... params) {
    return dbf.db().addCluster(clusterName, params);
  }

  @Override
  public int addCluster(String clusterName) {
    return dbf.db().addCluster(clusterName);
  }

  @Override
  public ODatabase<T> delete(ORID recordId) {
    return dbf.db().delete(recordId);
  }

  @Override
  public ODatabase<T> delete(T entity) {
    return dbf.db().delete(entity);
  }

  @Override
  public ODatabase<T> delete(ORID rid, int version) {
    return dbf.db().delete(rid, version);
  }

  @Override
  public int getDefaultClusterId() {
    return dbf.db().getDefaultClusterId();
  }

  @Override
  public String getClusterNameById(int clusterId) {
    return dbf.db().getClusterNameById(clusterId);
  }

  @Override
  public int getClusterIdByName(String clusterName) {
    return dbf.db().getClusterIdByName(clusterName);
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
  public OSecurityUser getUser() {
    return dbf.db().getUser();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <RET extends List<?>> RET detach(RET entities) {
    List<Object> result = new ArrayList<>(entities.size());

    for (Object entity : entities) {
      result.add(detach(entity));
    }

    return (RET) result;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <RET extends List<?>> RET detachAll(RET entities) {
    List<Object> result = new ArrayList<>(entities.size());

    for (Object entity : entities) {
      result.add(detachAll(entity));
    }

    return (RET) result;
  }

  @Override
  public <RET extends List<?>> RET query(String query, Object... args) {
    return ((ODatabaseObject) dbf.db()).objectQuery(query, args);
  }

  @Override
  public <RET extends List<?>> RET query(String query, DetachMode detachMode, Object... args) {
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

  public <RET> RET queryForObject(String query, Object... args) {
    List<?> objects = query(query, args);

    if (objects.size() > 0)
      return (RET) objects.get(0);

    return null;
  }

  public <RET> RET queryForObject(String query, DetachMode detachMode, Object... args) {
    RET result = queryForObject(query, args);

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
  public <RET> RET command(String sql, Object... args) {
    return ((ODatabaseObject) dbf.db()).objectCommand(sql, args);
  }

  public boolean equals(Object other) {
    return dbf.db().equals(other);
  }

  public String toString() {
    return dbf.db().toString();
  }
}
