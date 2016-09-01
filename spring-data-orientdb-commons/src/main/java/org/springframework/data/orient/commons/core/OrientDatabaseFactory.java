package org.springframework.data.orient.commons.core;

import com.orientechnologies.orient.core.db.ODatabase;

public interface OrientDatabaseFactory<T> {
    /**
     * Default database username.
     */
    public static final String DEFAULT_USERNAME = "admin";

    /**
     * Default database password.
     */
    public static final String DEFAULT_PASSWORD = "admin";


    /**
     * Default maximum pool size.
     */
    public static final int DEFAULT_MAX_POOL_SIZE = 20;

    public ODatabase<T> db();

    ODatabase<T> openDatabase();

    String getUrl();

    void setUrl(String url);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    int getMaxPoolSize();

    void setMaxPoolSize(int maxPoolSize);

    void dropDatabase();
}
