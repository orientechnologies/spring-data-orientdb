package org.springframework.data.orientdb.core;

import org.springframework.dao.DataAccessException;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.orientdb.OConnectionException;

import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public abstract class AbstractOrientDbManager<DB extends ODatabaseRecord> implements OrientDbManager<DB> {

	protected final String dbURI;
	protected final UserCredentials credentials;
	
	public static ThreadLocal<ODatabaseRecord> THREAD_LOCAL_INSTANCE = new ThreadLocal<ODatabaseRecord>();
	
	public String getDbURI() {
		return dbURI;
	}

	public UserCredentials getCredentials() {
		return credentials;
	}

	public AbstractOrientDbManager(String dbURI, UserCredentials credentials) {
		this.dbURI = dbURI;
		this.credentials = credentials;
	}
	
	public DB getUnboundDatabase() throws DataAccessException {
		return getDatabaseFromPool(getDbURI(), getCredentials());
	};

	
	public DB getCurrentDatabase() throws DataAccessException {
		return getBoundDatabase(getDbURI(), getCredentials());
	}
	
	// TODO: make this method protected and move the unit tests to this module
	// if necessary
	public DB getBoundDatabase(String dbURI, UserCredentials credentials)
			throws DataAccessException {

		DB db = getThreadLocalInstance(dbURI, credentials);
		
		// The database is valid and is open if its not null, so just return it
		if (db != null)
			return db;
		
		db = getDatabaseFromPool(dbURI, credentials);
		
		THREAD_LOCAL_INSTANCE.set(db);
		
		return db;
	}
	
	
	
	public void releaseCurrentDatabase () throws DataAccessException {
		releaseBoundDatabase(this.dbURI, this.credentials);
	}
	
	protected void releaseBoundDatabase (String dbURI, UserCredentials credentials) throws DataAccessException {
		ODatabaseRecord db = getThreadLocalInstance(dbURI, credentials);
		releaseDatabase(db);
		THREAD_LOCAL_INSTANCE.set(null);
	}
	
	public void releaseDatabase (ODatabaseRecord db) throws DataAccessException {
		try {
			if (db != null)
				db.close();
		}
		catch (OException e) {
			throw new OConnectionException("Error Closing the Database", e, dbURI, credentials);
		}
	}
	
	public Boolean createCurrentDatabase() {
		ODatabaseRecord db = getCurrentDatabase();
		if (!db.exists()) {
			db.create();
			return true;
		}
		throw new IllegalStateException ("Cannot create database, it already exists");
	}
	
	public Boolean dropCurrentDatabase() {
		ODatabaseRecord db = getCurrentDatabase();
		if (db.exists()) {
			db.drop();
			return true;
		}
		throw new IllegalStateException ("Cannot drop database, it doesnt exist");
	}
	
	/*
	 * Gets a database from the current thread local, if it exists and its valid 
	 * 
	 */
	
	protected DB getThreadLocalInstance(String dbURI, UserCredentials credentials) {
//		ODatabaseRecord db = ODatabaseRecordThreadLocal.INSTANCE.get();
		ODatabaseRecord db = THREAD_LOCAL_INSTANCE.get();
		if (checkType(db)) {
			if (!db.isClosed() && db.getStorage().getURL().equals(dbURI)) {
				if ( db.getUser() == null || (db.getUser() != null 
					&& db.getUser().getName().equals(credentials.getUsername())
					&& db.getUser().checkPassword(credentials.getPassword()))) {
					
					return (DB) db;
				}
			}
					
		}
		return null;
	}
	
	protected abstract DB getDatabaseFromPool(String dbURI, UserCredentials credentials);
	
	protected abstract boolean checkType (ODatabaseRecord db);
	
}	
