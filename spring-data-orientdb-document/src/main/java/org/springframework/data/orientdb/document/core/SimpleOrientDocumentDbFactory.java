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

import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * 
 * @author "Forat Latif"
 *
 */

public class SimpleOrientDocumentDbFactory implements OrientDocumentDbFactory {

	private final String dbURI;
	private final UserCredentials credentials;

	public SimpleOrientDocumentDbFactory(String dbURI) {
		this(dbURI, UserCredentials.NO_CREDENTIALS);
	}
	
	
	public SimpleOrientDocumentDbFactory(String dbURI, UserCredentials credentials) {
		this.dbURI = dbURI;
		this.credentials = credentials;
	}
	
	public ODatabaseDocument getDocumentDatabase() throws DataAccessException {
		return getDocumentDatabase(this.dbURI, this.credentials);
	}
	
	public ODatabaseDocument getDocumentDatabase(String dbURI) throws DataAccessException {
		return getDocumentDatabase(dbURI, this.credentials);
	}

	/* TODO:
	 * This method is not being used for now, its just for testing purposes, we need to think about
	 * how to manage the database instances and synchronize them with spring transactions
	 */
	
	public ODatabaseDocument getDocumentDatabase(String dbURI, UserCredentials credentials)
			throws DataAccessException {

		
		ODatabaseDocumentTx db = null;
		//TODO: add anonymous authentication
		if (!credentials.hasUsername() || !credentials.hasPassword()) {
			throw new UnsupportedOperationException("Anonymous Authentication not supported yet");
		}
		else {
			try {
				db = ODatabaseDocumentPool.global().acquire(dbURI, credentials.getUsername(), credentials.getPassword());
			}
			catch (OException e) {
				throw new OConnectionException("Error Aquiring the Database", e, dbURI, credentials);
			}
		}
		
		return db;
	}

}
