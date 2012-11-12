package org.springframework.data.orientdb.transaction;

import org.springframework.data.orientdb.core.OrientDbManager;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.exception.OTransactionException;

public class OrientTransactionManager extends AbstractPlatformTransactionManager
implements ResourceTransactionManager  {

	private OrientDbManager dbManager;
	
	public OrientDbManager getDbManager() {
		return dbManager;
	}

	public OrientTransactionManager(OrientDbManager dbManager) {
		this.dbManager = dbManager;
	}
	
	public Object getResourceFactory() {
		return getDbManager();
	}

	@Override
	protected boolean isExistingTransaction(Object transaction) {
//		return ((OrientTransactionObject) transaction).hasTransaction();
		
		// TODO: Transaction propagation not supported.
		// The behavior for nested transaction is dependent on orientDB for now.
		return false;
	}
	
	@Override
	protected Object doGetTransaction() throws TransactionException {
		OrientTransactionObject txObject = new OrientTransactionObject();
		return txObject;
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition)
			throws TransactionException {
		
		OrientTransactionObject txObject = (OrientTransactionObject) transaction;
		
		ODatabaseRecord db = getDbManager().getCurrentDatabase();
		
		try {
			txObject.setODatabaseRecordHolder(new ODatabaseHolder(db));
			
			// Sets TransactionActive = true in the Database Holder
			txObject.setTransactionData(null);
			
			// Bind the DatabaseHolder to the thread.
			TransactionSynchronizationManager.bindResource(getDbManager(), 
					txObject.getDatabaseHolder());
			txObject.getDatabaseHolder().setSynchronizedWithTransaction(true);
			
			txObject.getDatabaseHolder().getDatabaseObject().begin();
		}
		catch (Exception e) {
			closeDatabaseConnectionAfterFailedBegin(txObject);
			throw new CannotCreateTransactionException("Could open a Transaction with Object: " + txObject, e);
		}
	}
	
	protected void closeDatabaseConnectionAfterFailedBegin(OrientTransactionObject txObject) {
			ODatabaseRecord db = txObject.getDatabaseHolder().getDatabaseObject();
			try {
				if (db.getTransaction().isActive()) {
					db.rollback();
				}
			}
			catch (Throwable ex) {
				logger.debug("Could not rollback OTransaction after failed transaction begin", ex);
			}
			finally {
				getDbManager().releaseDatabase(db);
			}
	}
	

	@Override
	protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
		OrientTransactionObject txObject = (OrientTransactionObject) status.getTransaction();
		if (status.isDebug()) {
			logger.debug("Committing transaction on DB [" +
					txObject.getDatabaseHolder().getDatabaseObject() + "]");
		}
		try {
			ODatabaseRecord db = txObject.getDatabaseHolder().getDatabaseObject();
			db.commit();
		}
		catch (OTransactionException ex) {
			throw new TransactionSystemException("Could not commit OrientDB transaction", ex);
		}
		catch (RuntimeException ex) {
			// TODO: Translate exception if necessary
			throw ex;
		}
		
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status)
			throws TransactionException {
		OrientTransactionObject txObject = (OrientTransactionObject) status.getTransaction();
		if (status.isDebug()) {
			logger.debug("Rolling back OrientDB transaction on DB [" +
					txObject.getDatabaseHolder().getDatabaseObject() + "]");
		}
		try {
			ODatabaseRecord db = txObject.getDatabaseHolder().getDatabaseObject();
			db.rollback();
		}
		catch (OTransactionException ex) {
			throw new TransactionSystemException("Could not commit OrientDB transaction", ex);
		}
		catch (RuntimeException ex) {
			// TODO: Translate exception if necessary
			throw ex;
		}
	}
	
	@Override
	protected void doCleanupAfterCompletion(Object transaction) {
		OrientTransactionObject txObject = (OrientTransactionObject) transaction;

		// Remove the DatabaseHolder from the thread.
		TransactionSynchronizationManager.unbindResource(getDbManager());
		txObject.getDatabaseHolder().clear();

		ODatabaseRecord db = txObject.getDatabaseHolder().getDatabaseObject();
		getDbManager().releaseDatabase(db);
	}
	
}
