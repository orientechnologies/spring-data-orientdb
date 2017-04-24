package org.springframework.data.orient.object;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.springframework.data.orient.commons.core.OrientOperations;

public interface OrientObjectOperations extends OrientOperations<Object> {

    OObjectDatabaseTx getObjectDatabase();
}
