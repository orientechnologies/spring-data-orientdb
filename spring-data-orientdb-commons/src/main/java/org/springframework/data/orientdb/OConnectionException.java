/*
 * Copyright (c) 2012 by the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.orientdb;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.authentication.UserCredentials;

/**
 * 
 * @author Forat Latif
 * 
 */

public class OConnectionException extends DataAccessResourceFailureException {

	private static final long serialVersionUID = -194909274330227547L;
	
	private UserCredentials credentials = UserCredentials.NO_CREDENTIALS;
	private String dbURI = null;
	
	public OConnectionException(String msg) {
		super(msg);
	}
	
	public OConnectionException(String msg, Throwable t) {
		super(msg, t);
	}
	
	public OConnectionException(String msg, String dbURI, UserCredentials credentials) {
		super(msg);
		this.dbURI = dbURI;
		this.credentials = credentials;
	}
	
	public OConnectionException(String msg, Throwable t, String dbURI, UserCredentials credentials) {
		super(msg, t);
		this.dbURI = dbURI;
		this.credentials = credentials;
	}
	
	public UserCredentials getCredentials() {
		return credentials;
	}
	
	public String getDbURI() {
		return dbURI;
	}
	

}
