package org.springframework.data.orientdb.transaction;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.orientdb.core.OrientDbManager;
import org.springframework.data.orientdb.mock.OrientDbManagerMock;
import org.springframework.transaction.support.DefaultTransactionStatus;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.tx.OTransaction;

public class OrientTransactionManagerTest {

	private OrientDbManager factory;
	
	private ODatabaseRecord db;
	
	private OrientTransactionManager transactionManager;
	
	@Before
	public void setUp() throws Exception {
		db = new ODatabaseDocumentTx("memory:testDB");
		if(db.exists()) {
	    	db.open("admin", "admin");
	    	db.drop();
	    }
		db.create();
//		factory = Mockito.mock(AbstractOrientDbFactory.class, Mockito.CALLS_REAL_METHODS);
//		when(factory.getDatabase()).thenReturn(db);
		factory = new OrientDbManagerMock((ODatabaseDocumentTx) db);
		transactionManager = new OrientTransactionManager(factory);

	}

	@After
	public void tearDown() throws Exception {
		if (!db.isClosed()) {
			if (db.getTransaction().isActive()) 
				db.rollback();
			if(db.exists()) 
		    	db.drop();
			db.close();
		}
		
	}

	@Test
	public void testDoGetTransaction() {
		Object txObject = transactionManager.doGetTransaction();
		assertTrue(txObject instanceof OrientTransactionObject);
	}

	@Test
	public void testDoBegin() {
		OrientTransactionObject txObject = (OrientTransactionObject) transactionManager.doGetTransaction();
		transactionManager.doBegin(txObject, null);
		
		assertTrue(txObject.getDatabaseHolder().isTransactionActive());
		assertTrue(txObject.getDatabaseHolder().isSynchronizedWithTransaction());
		
		assertTrue(txObject.getDatabaseHolder().getDatabaseObject().getTransaction().isActive());
	}

	@Test
	public void testCloseDatabaseConnectionAfterFailedBegin() {
		OrientTransactionObject txObject = (OrientTransactionObject) transactionManager.doGetTransaction();
		transactionManager.doBegin(txObject, null);
		
		ODatabaseRecord db = txObject.getDatabaseHolder().getDatabaseObject();
		OTransaction tx = db.getTransaction();
		assertTrue(tx.isActive());
		transactionManager.closeDatabaseConnectionAfterFailedBegin(txObject);

		assertTrue(!tx.isActive());
		assertTrue(db.isClosed());
	}

	@Test
	public void testDoCommit() {
		OrientTransactionObject txObject = (OrientTransactionObject) transactionManager.doGetTransaction();
		
		// Creates the class before beginning the transaction
		db.getMetadata().getSchema().createClass("Test");
		
		transactionManager.doBegin(txObject, null);

		ODatabaseDocumentTx db = (ODatabaseDocumentTx) txObject.getDatabaseHolder().getDatabaseObject();
		OTransaction tx = db.getTransaction();
		
		ODocument doc = new ODocument("Test");
		doc.field("testkey", "testvalue");
		db.save(doc);
		
		doc = new ODocument("Test");
		doc.field("testkey", "testvalue2");
		db.save(doc);
		
		DefaultTransactionStatus status = Mockito.mock(DefaultTransactionStatus.class);
		when(status.getTransaction()).thenReturn(txObject);
		when(status.isDebug()).thenReturn(true);
		
		transactionManager.doCommit(status);
		
		assertTrue(!tx.isActive());

		db.rollback();
		
		assertTrue (db.countClass("Test") == 2);
		
		for (ODocument document : db.browseClass("Test")) {
			  assertTrue(document.field( "testkey" ).toString().startsWith("testvalue"));
		}
		
	}

	@Test
	public void testDoRollback() {
		OrientTransactionObject txObject = (OrientTransactionObject) transactionManager.doGetTransaction();
		
		// Creates the class before beginning the transaction
		db.getMetadata().getSchema().createClass("Test");
		
		transactionManager.doBegin(txObject, null);

		ODatabaseDocumentTx db = (ODatabaseDocumentTx) txObject.getDatabaseHolder().getDatabaseObject();
		OTransaction tx = db.getTransaction();
		
		ODocument doc = new ODocument("Test");
		doc.field("testkey", "testvalue");
		db.save(doc);
		
		doc = new ODocument("Test");
		doc.field("testkey", "testvalue2");
		db.save(doc);
		
		DefaultTransactionStatus status = Mockito.mock(DefaultTransactionStatus.class);
		when(status.getTransaction()).thenReturn(txObject);
		when(status.isDebug()).thenReturn(true);
		
		transactionManager.doRollback(status);
		
		assertTrue(!tx.isActive());

		assertTrue (db.countClass("Test") == 0);
	}

	@Test
	public void testDoCleanupAfterCompletionObject() {
		OrientTransactionObject txObject = (OrientTransactionObject) transactionManager.doGetTransaction();
		transactionManager.doBegin(txObject, null);
		
		ODatabaseRecord db = txObject.getDatabaseHolder().getDatabaseObject();
		OTransaction tx = db.getTransaction();
		assertTrue(tx.isActive());
		
		DefaultTransactionStatus status = Mockito.mock(DefaultTransactionStatus.class);
		when(status.getTransaction()).thenReturn(txObject);
		when(status.isDebug()).thenReturn(true);
		
		transactionManager.doRollback(status);
		
		transactionManager.doCleanupAfterCompletion(txObject);

		assertTrue(!tx.isActive());
		assertTrue(!txObject.getDatabaseHolder().isSynchronizedWithTransaction());
		assertTrue(!txObject.getDatabaseHolder().isTransactionActive());
		assertTrue(db.isClosed());
	}

}
