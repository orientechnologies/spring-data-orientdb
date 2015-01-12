package org.springframework.data.orient.document;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.ORecord;
import org.springframework.data.orient.commons.core.OrientOperations;

public interface OrientDocumentOperations extends OrientOperations<ORecord> {
    ODatabaseDocumentTx getDocumentDatabase();
}
