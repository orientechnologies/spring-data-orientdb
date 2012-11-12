package org.springframework.data.orientdb.transaction;

import org.springframework.transaction.support.SmartTransactionObject;

public class OrientTransactionObject implements SmartTransactionObject {

	private ODatabaseHolder databaseHolder;

//	private boolean newDatabaseHolder;

	private Object transactionData;
	
	private boolean rollbackOnly;
	
	public OrientTransactionObject() {
		this.rollbackOnly = false;
	}

	public void setODatabaseRecordHolder(ODatabaseHolder databaseHolder) {
		this.databaseHolder = databaseHolder;
//		this.newDatabaseHolder = newDatabaseHolder;
	}

	public ODatabaseHolder getDatabaseHolder() {
		return this.databaseHolder;
	}

//	public boolean isNewDatabaseHolder() {
//		return this.newDatabaseHolder;
//	}

	public boolean hasTransaction() {
		return (this.databaseHolder != null && this.databaseHolder.isTransactionActive());
	}

	// TODO: For now transaction Data is always null, if its not needed this
	// method should be changed
	public void setTransactionData(Object transactionData) {
		this.transactionData = transactionData;
		this.databaseHolder.setTransactionActive(true);
	}

	public Object getTransactionData() {
		return this.transactionData;
	}

	public void setRollbackOnly() {
		this.rollbackOnly = true;
		this.databaseHolder.setRollbackOnly();
	}

	public boolean isRollbackOnly() {
		return this.rollbackOnly;
	}

	public void flush() {
		this.databaseHolder.getDatabaseObject().commit();
	}

}