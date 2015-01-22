package org.springframework.data.orient.document;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.ORecord;
import org.springframework.data.orient.commons.core.AbstractOrientOperations;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OrientDocumentTemplate extends AbstractOrientOperations<ORecord> implements OrientDocumentOperations {

    public OrientDocumentTemplate(OrientDocumentDatabaseFactory dbf) {
        super(dbf);
    }

    public ODatabaseDocumentTx getDocumentDatabase() {
        return (ODatabaseDocumentTx)dbf.db();
    }

    @Override
    public String getRid(ORecord entity) {
        return entity.getRecord().getIdentity().toString();
    }

    @Override
    public <RET> RET detach(RET entity) {
        ((ORecord)entity).detach();

        return entity;
    }

    @Override
    public <RET> RET detachAll(RET entity) {
        // TODO: is this enough?
        return detach(entity);
    }
}
