package org.springframework.data.orient.document;

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.ORecord;
import org.springframework.data.orient.commons.core.AbstractOrientDatabaseFactory;

/**
 * A specific factory for creating OrientDocumentDatabaseFactory objects that handle {@link com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx}.
 *
 * @author Dzmitry_Naskou
 * @see com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx
 */
public class OrientDocumentDatabaseFactory extends AbstractOrientDatabaseFactory<ORecord> {

    private OPartitionedDatabasePool pool;

    /** The database. */
    private ODatabaseDocumentTx db;

    @Override
    protected void createPool() {
        pool = new OPartitionedDatabasePool(getUrl(), getUsername(), getPassword(), maxPoolSize, -1);
    }

    /* (non-Javadoc)
     * @see org.springframework.orm.orient.AbstractOrientDatabaseFactory#openDatabase()
     */
    @Override
    public ODatabaseDocumentTx openDatabase() {
        db = pool.acquire();
        return db;
    }

    /* (non-Javadoc)
     * @see org.springframework.orm.orient.AbstractOrientDatabaseFactory#db()
     */
    @Override
    public ODatabaseDocumentTx db() {
        return (ODatabaseDocumentTx) super.db();
    }

    /* (non-Javadoc)
     * @see org.springframework.orm.orient.AbstractOrientDatabaseFactory#newDatabase()
     */
    @Override
    protected ODatabaseDocumentTx newDatabase() {
        return new ODatabaseDocumentTx(getUrl());
    }
}   
