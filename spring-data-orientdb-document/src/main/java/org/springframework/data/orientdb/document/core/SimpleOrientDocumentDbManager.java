/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.orientdb.document.core;

import org.springframework.dao.DataAccessException;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.orientdb.OConnectionException;
import org.springframework.data.orientdb.core.AbstractOrientDbManager;

import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

/**
 * 
 * This class is responsable for creating and managing ODatabaseDocument instances,
 * its a connection manager, it uses orientDB's pool to share instances with other
 * threads 
 * 
 * @author "Forat Latif"
 *
 */

public class SimpleOrientDocumentDbManager extends AbstractOrientDbManager<ODatabaseDocumentTx> implements OrientDocumentDbManager {


	public SimpleOrientDocumentDbManager(String dbURI) {
		this(dbURI, UserCredentials.NO_CREDENTIALS);
	}
	
	public SimpleOrientDocumentDbManager(String dbURI, UserCredentials credentials) {
		super(dbURI, credentials);
	}
	
	protected ODatabaseDocumentTx getDatabaseFromPool(String dbURI, UserCredentials credentials) {
		
		//TODO: add anonymous authentication
		if (!credentials.hasUsername() || !credentials.hasPassword()) {
			throw new UnsupportedOperationException("Anonymous Authentication not supported yet");
		}
		
		ODatabaseDocumentTx db = null;
		try {
			db = ODatabaseDocumentPool.global().acquire(dbURI, credentials.getUsername(), credentials.getPassword());
		}
		catch (OException e) {
			throw new OConnectionException("Error Acquiring the Database", e, dbURI, credentials);
		}
		return db;
	}
	
	protected boolean checkType (ODatabaseRecord db) {
		return db != null && (db instanceof ODatabaseDocumentTx || db.getDatabaseOwner() instanceof ODatabaseDocumentTx);
	}

	/*
	 * Gets a database from the current thread local, if it exists and is valid it means that
	 * it is the one being currently used by the user and transactions are manager manually,
	 * if it exists but the url or the credentials do not match it means that the user
	 * created the database manually, so we leave it alone, there is no need to remove it
	 * from the threadlocal because this will be done by the database pool if another database
	 * is created, its the user's responsability to keep a reference to its manually created database
	 */
//	@Override
//	protected ODatabaseDocumentTx getThreadLocalInstance(String dbURI, UserCredentials credentials) {
//		ODatabaseRecord odb = ODatabaseRecordThreadLocal.INSTANCE.get();
//		if (odb != null && (odb instanceof ODatabaseDocumentTx || odb.getDatabaseOwner() instanceof ODatabaseDocumentTx)) {
//			ODatabaseDocumentTx docDB = (odb  instanceof ODatabaseDocumentTx) ?
//					(ODatabaseDocumentTx) odb : (ODatabaseDocumentTx) odb.getDatabaseOwner();
//			if (!docDB.isClosed() && docDB.getStorage().getURL().equals(dbURI)) {
//				if ( docDB.getUser() == null || (docDB.getUser() != null 
//					&& docDB.getUser().getName().equals(credentials.getUsername())
//					&& docDB.getUser().checkPassword(credentials.getPassword()))) {
//					
//					return docDB;
//				}
//			}
//					
//		}
//		return null;
//	}

}
