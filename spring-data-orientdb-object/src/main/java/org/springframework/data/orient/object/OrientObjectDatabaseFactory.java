package org.springframework.data.orient.object;

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.orient.commons.core.AbstractOrientDatabaseFactory;

/**
 * A specific factory for creating OrientObjectDatabase objects that handle {@link com.orientechnologies.orient.object.db.OObjectDatabaseTx}.
 *
 * @author Dzmitry_Naskou
 * @see com.orientechnologies.orient.object.db.OObjectDatabaseTx
 */
public class OrientObjectDatabaseFactory extends AbstractOrientDatabaseFactory<Object> {

    private static Logger log = LoggerFactory.getLogger(AbstractOrientDatabaseFactory.class);

    private OPartitionedDatabasePool pool;

    /**
     * The database.
     */
    private OObjectDatabaseTx db;

    @Override
    protected void createPool() {
        //since max pool size was set, use it to create the partitioned pool
        int maxPartitionSize = Runtime.getRuntime().availableProcessors();
        pool = new OPartitionedDatabasePool(getUrl(), getUsername(), getPassword(),
                maxPartitionSize, maxPoolSize);
    }


    @Override
    public OObjectDatabaseTx openDatabase() {
        ODatabaseDocumentTx documentTx = pool.acquire();
        db = new OObjectDatabaseTx(documentTx);
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
