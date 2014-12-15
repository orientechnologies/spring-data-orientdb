package org.springframework.data.orient.commons.core;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseInternal;

public interface OrientDatabaseFactory<DB extends ODatabase<?>> {
    /** Default minimum pool size. */
    public static final int DEFAULT_MIN_POOL_SIZE = 1;

    /** Default maximum pool size. */
    public static final int DEFAULT_MAX_POOL_SIZE = 20;

    ODatabaseInternal<?> openDatabase();

    String getUrl();

    void setUrl(String url);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    int getMinPoolSize();

    void setMinPoolSize(int minPoolSize);

    int getMaxPoolSize();

    void setMaxPoolSize(int maxPoolSize);
}
