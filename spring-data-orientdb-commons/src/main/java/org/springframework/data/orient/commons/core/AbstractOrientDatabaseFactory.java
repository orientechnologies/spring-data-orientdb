package org.springframework.data.orient.commons.core;

import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * A base factory for creating {@link com.orientechnologies.orient.core.db.ODatabase} objects.
 *
 * @author Dzmitry_Naskou
 * @param <T> the type of database to handle
 */
public abstract class AbstractOrientDatabaseFactory<T extends ODatabaseInternal<?>> implements OrientDatabaseFactory<T> {

    /** The logger. */
    private static Logger log = LoggerFactory.getLogger(AbstractOrientDatabaseFactory.class);

    /** The username. */
    protected String username;

    /** The password. */
    protected String password;

    /** The min pool size. */
    protected int minPoolSize = DEFAULT_MIN_POOL_SIZE;

    /** The max pool size. */
    protected int maxPoolSize = DEFAULT_MAX_POOL_SIZE;

    protected String url;
    
    @PostConstruct
    public void init() {
        Assert.notNull(url);
        Assert.notNull(username);
        Assert.notNull(password);

        ODatabaseInternal<?> db = newDatabase();
        createDatabase(db);
        createPool();
    }

    protected abstract void createPool();

    /**
     * Open the database.
     *
     * @return the o database complex
     */
    public abstract ODatabaseInternal<?> openDatabase();

    protected abstract ODatabaseInternal<?> newDatabase();

    public ODatabaseInternal<?> db() {
        ODatabaseInternal<?> db;
        if(!ODatabaseRecordThreadLocal.INSTANCE.isDefined()) {
            db = openDatabase();
            log.debug("acquire db from pool {}", db.hashCode());
        } else {
            db = ODatabaseRecordThreadLocal.INSTANCE.get().getDatabaseOwner();
            log.debug("use existing db {}", db.hashCode());
        }

        return db;
    }

    protected void createDatabase(ODatabaseInternal<?> db) {
        if (!getUrl().startsWith("remote:")) {
            if (!db.exists()) {
                db.create();
                db.close();
            }
        }
    }

    /**
     * Gets the database url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the database url.
     *
     * @param url the new url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the min pool size.
     *
     * @return the min pool size
     */
    public int getMinPoolSize() {
        return minPoolSize;
    }

    /**
     * Sets the min pool size.
     *
     * @param minPoolSize the new min pool size
     */
    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    /**
     * Gets the max pool size.
     *
     * @return the max pool size
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Sets the max pool size.
     *
     * @param maxPoolSize the new max pool size
     */
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }
}
