package org.springframework.data.orient.object;

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.springframework.data.orient.commons.core.AbstractOrientDatabaseFactory;

/**
 * A specific factory for creating OrientObjectDatabase objects that handle {@link com.orientechnologies.orient.object.db.OObjectDatabaseTx}.
 *
 * @author Dzmitry_Naskou
 * @see com.orientechnologies.orient.object.db.OObjectDatabaseTx
 */
public class OrientObjectDatabaseFactory extends AbstractOrientDatabaseFactory<Object> {

    private OPartitionedDatabasePool pool;

    /**
     * The database.
     */
    private OObjectDatabaseTx db;

    @Override
    protected void createPool() {
        pool = new OPartitionedDatabasePool(getUrl(), getUsername(), getPassword());
    }

    @Override
    public OObjectDatabaseTx openDatabase() {
        db = new OObjectDatabaseTx(pool.acquire());
        return db;
    }

    @Override
    public OObjectDatabaseTx db() {
        return (OObjectDatabaseTx) super.db();
    }

    @Override
    protected OObjectDatabaseTx newDatabase() {
        return new OObjectDatabaseTx(getUrl());
    }
}
