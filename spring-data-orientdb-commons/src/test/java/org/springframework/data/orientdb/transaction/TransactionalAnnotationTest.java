package org.springframework.data.orientdb.transaction;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.orientdb.core.OrientDbManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;


/**
 * 
 * @author "Forat Latif"
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
//@TransactionConfiguration(transactionManager="orientTransactionManager", defaultRollback=false)
//@Transactional
public class TransactionalAnnotationTest {
	
	@Autowired
	private OrientDbManager dbManager;
	
	private ODatabaseDocumentTx db;
	
	@Autowired
	private TransactionalMethodsContainer txMethodsContainer;
	
	@Before
	public void setUp() {
		db = new ODatabaseDocumentTx("memory:testDB");
		if(db.exists()) {
	    	db.open("admin", "admin");
	    	db.drop();
	    }
	    db.create(); 
		db = (ODatabaseDocumentTx) dbManager.getCurrentDatabase();
		txMethodsContainer.setDb(db);
	}
	
	@After
	public void tearDown() {
		db = (ODatabaseDocumentTx) dbManager.getCurrentDatabase();
		if(db.exists()) {
	    	db.drop();
	    }
		db.close();
	}
	
	@Test
	public void commitWithAnnotationTest() {
		assertTrue(!db.getTransaction().isActive());
		db.getMetadata().getSchema().createClass("testcommit");
		txMethodsContainer.commitAutomatically("testcommit");
		assertTrue(!db.getTransaction().isActive());
		assertTrue(db.isClosed());
		
		db = (ODatabaseDocumentTx) dbManager.getCurrentDatabase();
		
		assertTrue(db.countClass("testcommit") == 1);
	}
	
	@Test
	public void rollbackWithAnnotationTest() {
		assertTrue(!db.getTransaction().isActive());
		db.getMetadata().getSchema().createClass("testrollback");
		try {
			txMethodsContainer.rollbackOnError("testrollback");
		}
		catch (Exception e) {
			
		}
		assertTrue(!db.getTransaction().isActive());
		assertTrue(db.isClosed());
		
		db = (ODatabaseDocumentTx) dbManager.getCurrentDatabase();
		
		assertTrue(db.countClass("testrollback") == 0);
	}

}
