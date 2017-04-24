package org.springframework.data.orient.commons.core;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.tx.OTransaction;

/**
 * The specific Orient Transaction.
 *
 * @author Dzmitry_Naskou
 */
public class OrientTransaction {

    /**
     * The orient tx object.
     */
    private OTransaction tx;

    /**
     * The database.
     */
    private ODatabase<?> database;

    public OTransaction getTx() {
        return tx;
    }

    public void setTx(OTransaction tx) {
        this.tx = tx;
    }

    public ODatabase<?> getDatabase() {
        return database;
    }

    public void setDatabase(ODatabase<?> database) {
        this.database = database;
    }
}

