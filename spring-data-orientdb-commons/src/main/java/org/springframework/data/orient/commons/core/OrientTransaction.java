package org.springframework.data.orient.commons.core;

import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.tx.OTransaction;

/**
 * The specific Orient Transaction.
 * 
 * @author Dzmitry_Naskou
 */
public class OrientTransaction {

    /** The orient tx object. */
    private OTransaction tx;

    /** The database. */
    private ODatabaseInternal<?> database;

    public OTransaction getTx() {
        return tx;
    }

    public void setTx(OTransaction tx) {
        this.tx = tx;
    }

    public ODatabaseInternal<?> getDatabase() {
        return database;
    }

    public void setDatabase(ODatabaseInternal<?> database) {
        this.database = database;
    }
}

