package org.springframework.data.orientdb.transaction;

import static org.junit.Assert.assertTrue;

import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class TransactionalMethodsContainer {

	private ODatabaseDocumentTx db;
	
	public TransactionalMethodsContainer(ODatabaseDocumentTx db) {
		this.db = db;
	}
	
	public TransactionalMethodsContainer() {
	}
	
	public void setDb(ODatabaseDocumentTx db) {
		this.db = db;
	}
	
	@Transactional
	public void commitAutomatically(String className) {
		assertTrue(db.getTransaction().isActive());
		
		ODocument doc = new ODocument(className);
		doc.field("test", "test");
		db.save(doc);
		
	}
	
	@Transactional
	public void rollbackOnError(String className) {
		assertTrue(db.getTransaction().isActive());
		ODocument doc = new ODocument(className);
		doc.field("test", "test");
		db.save(doc);
		
		throw new RuntimeException();
		
	}
	
}
