package org.springframework.data.orient.commons.core;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import static org.springframework.util.Assert.notNull;

/**
 * A base factory for creating {@link com.orientechnologies.orient.core.db.ODatabase} objects.
 *
 * @param <T> the type of database to handle
 * @author Dzmitry_Naskou
 */
public abstract class AbstractOrientDatabaseFactory<T> implements OrientDatabaseFactory<T> {

    /**
     * The logger.
     */
    private static Logger log = LoggerFactory.getLogger(AbstractOrientDatabaseFactory.class);

    /**
     * The username.
     */
    protected String username = DEFAULT_USERNAME;

    /**
     * The password.
     */
    protected String password = DEFAULT_PASSWORD;


    /**
     * The max pool size.
     */
    protected int maxPoolSize = DEFAULT_MAX_POOL_SIZE;

    protected Boolean autoCreate;

    protected String url;

    @PostConstruct
    public void init() {
        notNull(url);
        notNull(username);
        notNull(password);

        if (autoCreate == null) {
            autoCreate = !getUrl().startsWith("remote:");
        }

        ODatabase<?> db = newDatabase();
        createDatabase(db);
        createPool();
    }

    protected abstract void createPool();

    /**
     * Open the database.
     *
     * @return the o database complex
     */
    public abstract ODatabase<T> openDatabase();

    protected abstract ODatabaseInternal<?> newDatabase();

    public ODatabase<T> db() {
        ODatabase<T> db = openDatabase();
                log.debug("use existing db {}", db.hashCode());
        return db;
    }


    @Override
    public void dropDatabase() {
        ODatabaseInternal<?> database = newDatabase();
        database.open(username, password);
        database.drop();
    }

    protected void createDatabase(ODatabase<?> db) {
        if (autoCreate) {
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

    public Boolean getAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(Boolean autoCreate) {
        this.autoCreate = autoCreate;
    }
}
