package org.springframework.data.orientdb.mock;

import org.springframework.dao.DataAccessException;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.orientdb.core.AbstractOrientDbManager;
import org.springframework.data.orientdb.core.OrientDbManager;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public class OrientDbManagerMock extends AbstractOrientDbManager<ODatabaseDocumentTx> {

	private ODatabaseDocumentTx db;
	
	public OrientDbManagerMock(ODatabaseDocumentTx db) {
		super("", UserCredentials.NO_CREDENTIALS);
		this.db = db;
	}
	
	public OrientDbManagerMock(String dbURI, UserCredentials credentials) {
		super(dbURI, credentials);
		this.db = null;
	}
	
	public ODatabaseDocumentTx getCurrentDatabase() throws DataAccessException {
		if (db != null && !db.isClosed())
			return db;
		db = ODatabaseDocumentPool.global().acquire(dbURI, credentials.getUsername(), credentials.getPassword());
		return db;
	}

	public ODatabaseDocumentTx getUnboundDatabase() throws DataAccessException {
		db = ODatabaseDocumentPool.global().acquire(dbURI, credentials.getUsername(), credentials.getPassword());
		return db;
	}
	
	@Override
	protected boolean checkType(ODatabaseRecord db) {
		return db != null && (db instanceof ODatabaseDocumentTx || db.getDatabaseOwner() instanceof ODatabaseDocumentTx);
	}
	
	// If these methods are used an exception will be thrown and we will notice and implement
	// an appropriate behavior for this mock
	@Override
	protected ODatabaseDocumentTx getThreadLocalInstance(String dbURI,
			UserCredentials credentials) {
		return null;
	}

	@Override
	protected ODatabaseDocumentTx getDatabaseFromPool(String dbURI,
			UserCredentials credentials) {
		return null;
	}
	

}
