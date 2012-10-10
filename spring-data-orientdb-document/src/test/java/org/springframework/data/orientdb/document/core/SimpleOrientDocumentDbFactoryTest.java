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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
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
	private OrientDocumentDbFactory orientDbFactory;
	
	private static ODatabaseDocument db;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		db = new ODatabaseDocumentTx("memory:testDB");
	    if( !db.exists() )
	      db.create(); 
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		if (db.exists())
			db.drop();
	}
	
	@Test
	public void testgetDocumentDatabase() throws Exception {
		ODatabaseDocument documentDatabase = orientDbFactory.getDocumentDatabase();
		assertNotNull(documentDatabase);
		assertTrue(documentDatabase.exists());
	}
	
	@Test
	public void testgetDocumentDatabaseWithExistentName() throws Exception {
		ODatabaseDocument documentDatabase = orientDbFactory.getDocumentDatabase("memory:testDB");
		assertNotNull(documentDatabase);
		assertTrue(documentDatabase.exists());
	}
	
	@Test(expected=OConnectionException.class)
	public void testgetDocumentDatabaseWithNonExistentName() throws Exception {
		ODatabaseDocument documentDatabase = orientDbFactory.getDocumentDatabase("memory:NonExistentDB");
		assertNotNull(documentDatabase);
		assertTrue(!documentDatabase.exists());
	}
	
	@Test
	public void testgetDocumentDatabaseWithNameAndUserCredentials() throws Exception {
		ODatabaseDocument documentDatabase = orientDbFactory.getDocumentDatabase("memory:testDB", new UserCredentials("admin", "admin"));
		assertNotNull(documentDatabase);
		assertTrue(documentDatabase.exists());
	}
	

}
