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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.orientdb.OConnectionException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * 
 * @author "Forat Latif"
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SimpleOrientDocumentDbFactoryTest {

	@Autowired
	private SimpleOrientDocumentDbManager orientDbManager;
	
	private ODatabaseDocument db;
	
	@Before
	public void setUp() {
		db = new ODatabaseDocumentTx("memory:testDB");
		if(db.exists()) {
	    	db.open("admin", "admin");
	    	db.drop();
	    }
	    db.create(); 
	}
	
	@After
	public void tearDown() {
		if (db.exists())
			db.drop();
	}
	
	@Test
	public void testgetCurrentDatabase() throws Exception {
		ODatabaseDocument documentDatabase = orientDbManager.getCurrentDatabase();
		assertNotNull(documentDatabase);
		assertTrue(documentDatabase.exists());
	}
	
	@Test
	public void testgetDatabaseWithExistentName() throws Exception {
		ODatabaseDocument documentDatabase = orientDbManager.getBoundDatabase("memory:testDB", orientDbManager.getCredentials());
		assertNotNull(documentDatabase);
		assertTrue(documentDatabase.exists());
	}
	
	@Test(expected=OConnectionException.class)
	public void testgetDatabaseWithNonExistentName() throws Exception {
		orientDbManager.getBoundDatabase("memory:NonExistentDB", orientDbManager.getCredentials());
	}
	
	@Test
	public void testgetDatabaseWithNameAndUserCredentials() throws Exception {
		ODatabaseDocument documentDatabase = orientDbManager.getBoundDatabase("memory:testDB", new UserCredentials("admin", "admin"));
		assertNotNull(documentDatabase);
		assertTrue(documentDatabase.exists());
	}
	
	@Test
	public void testgetUnboundDatabase() throws Exception {
		ODatabaseDocument database = orientDbManager.getCurrentDatabase();
		assertNotNull(database);
		assertTrue(database.exists());
		
		ODatabaseDocument unboundDatabase = orientDbManager.getUnboundDatabase();
		assertNotNull(unboundDatabase);
		assertTrue(unboundDatabase.exists());
		
		assertNotSame(database, unboundDatabase);
		
		orientDbManager.releaseCurrentDatabase();
		
		assertTrue(database.isClosed());
		assertTrue(!unboundDatabase.isClosed());
		
		unboundDatabase.close();
		
	}
	
	@Test
	public void testreleaseDatabase()  {
		
		String dbUrl = "memory:NonExistentDB12345";
		db = new ODatabaseDocumentTx(dbUrl);
		assertTrue(!db.exists());
		
	    db.create();
		orientDbManager.releaseDatabase(db);

		assertTrue(db.isClosed());
		
		db = new ODatabaseDocumentTx(dbUrl);
		if (db.exists())
			db.drop();
		
	}
	

}
