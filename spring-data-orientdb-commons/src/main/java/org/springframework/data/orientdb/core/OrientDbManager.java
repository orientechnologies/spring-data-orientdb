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

package org.springframework.data.orientdb.core;

import org.springframework.dao.DataAccessException;
import org.springframework.data.authentication.UserCredentials;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

/**
 * 
 * @author "Forat Latif"
 *
 */

public interface OrientDbManager<DB extends ODatabaseRecord> {

	/**
	 * 
	 * @return ODatabaseRecord which represents a connection to the database,
	 * it will bind the connection to the current thread and will return the same
	 * one as long as its valid, this connection should not be used outside a transaction
	 * @throws DataAccessException
	 */
	DB getCurrentDatabase() throws DataAccessException;
	
	/**
	 * 
	 * @return ODatabaseRecord that is not bound to the current thread, the called of
	 * this method is responsable to close this connection.
	 * @throws DataAccessException
	 */
	DB getUnboundDatabase() throws DataAccessException;
	
//	ODatabaseRecord getDatabase(String dbURI) throws DataAccessException;
//	
//	ODatabaseRecord getDatabase(String dbURI, UserCredentials credentials) throws DataAccessException;
	
	void releaseDatabase(DB odb) throws DataAccessException;

	void releaseCurrentDatabase() throws DataAccessException;
	
	Boolean dropCurrentDatabase() throws DataAccessException;
	
	Boolean createCurrentDatabase() throws DataAccessException;
	
//	void releaseDatabase(String dbURI) throws DataAccessException;
//	
//	void releaseDatabase(String dbURI, UserCredentials credentials) throws DataAccessException;
	
}
