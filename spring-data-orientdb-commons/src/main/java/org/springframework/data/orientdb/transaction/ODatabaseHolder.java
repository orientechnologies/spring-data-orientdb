package org.springframework.data.orientdb.transaction;

import org.springframework.transaction.support.ResourceHolderSupport;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public class ODatabaseHolder extends ResourceHolderSupport  {

	private boolean transactionActive;
	
	private ODatabaseRecord databaseObject;
	
	public ODatabaseHolder(ODatabaseRecord databaseObject) {
		this.databaseObject = databaseObject;
		this.transactionActive = false;
	}
	
	public ODatabaseRecord getDatabaseObject() {
		return databaseObject;
	}

//	public void setDatabaseObject(ODatabaseRecord databaseObject) {
//		this.databaseObject = databaseObject;
//	}

	public void setTransactionActive(boolean isTransactionActive) {
		this.transactionActive = isTransactionActive;
	}

	public boolean isTransactionActive () {
		return this.transactionActive;
	}
	
	@Override
	public void clear() {
		super.clear();
		this.transactionActive = false;
	}
	
}
