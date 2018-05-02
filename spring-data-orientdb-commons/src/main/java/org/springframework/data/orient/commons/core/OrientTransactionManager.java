package org.springframework.data.orient.commons.core;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * {@link org.springframework.transaction.PlatformTransactionManager} implementation
 * for OrientDB.
 *
 * @author Dzmitry_Naskou
 */
public class OrientTransactionManager extends AbstractPlatformTransactionManager implements ResourceTransactionManager {

    private static final long serialVersionUID = 1L;

    /**
     * The logger.
     */
    private static Logger log = LoggerFactory.getLogger(OrientTransactionManager.class);

    /**
     * The database factory.
     */
    private OrientDatabaseFactory<?> dbf;

    /**
     * Instantiates a new {@link OrientTransactionManager}.
     *
     * @param dbf the dbf
     */
    public OrientTransactionManager(OrientDatabaseFactory<?> dbf) {
        super();
        this.dbf = dbf;
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doGetTransaction()
     */
    @Override
    protected Object doGetTransaction() throws TransactionException {
        OrientTransaction tx = new OrientTransaction();

        ODatabase<?> db = (ODatabase<?>) TransactionSynchronizationManager.getResource(getResourceFactory());

        if (db != null) {
            tx.setDatabase(db);
            tx.setTx(db.getTransaction());
        }

        return tx;
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#isExistingTransaction(java.lang.Object)
     */
    @Override
    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
        OrientTransaction tx = (OrientTransaction) transaction;

        return tx.getTx() != null && tx.getTx().isActive();
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doBegin(java.lang.Object, org.springframework.transaction.TransactionDefinition)
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException {
        OrientTransaction tx = (OrientTransaction) transaction;

        ODatabase<?> db = tx.getDatabase();
        if (db == null || db.isClosed()) {
            db = dbf.openDatabase();
            tx.setDatabase(db);
            TransactionSynchronizationManager.bindResource(dbf, db);
        }

        log.debug("beginning transaction, db.hashCode() = {}", db.hashCode());

        db.begin();
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doCommit(org.springframework.transaction.support.DefaultTransactionStatus)
     */
    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
        OrientTransaction tx = (OrientTransaction) status.getTransaction();
        ODatabase<?> db = tx.getDatabase();

        log.debug("committing transaction, db.hashCode() = {}", db.hashCode());

        db.commit();
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doRollback(org.springframework.transaction.support.DefaultTransactionStatus)
     */
    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
        OrientTransaction tx = (OrientTransaction) status.getTransaction();
        ODatabase<?> db = tx.getDatabase();

        log.debug("rolling back transaction, db.hashCode() = {}", db.hashCode());

        db.rollback();
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doSetRollbackOnly(org.springframework.transaction.support.DefaultTransactionStatus)
     */
    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) throws TransactionException {
        status.setRollbackOnly();
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doCleanupAfterCompletion(java.lang.Object)
     */
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        OrientTransaction tx = (OrientTransaction) transaction;

        if (!tx.getDatabase().isClosed()) {
            log.debug("closing transaction, db.hashCode() = {}", tx.getDatabase().hashCode());
            tx.getDatabase().close();
        }

        TransactionSynchronizationManager.unbindResource(dbf);
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doSuspend(java.lang.Object)
     */
    @Override
    protected Object doSuspend(Object transaction) throws TransactionException {
        OrientTransaction tx = (OrientTransaction) transaction;

        return tx.getDatabase();
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#doResume(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void doResume(Object transaction, Object suspendedResources) throws TransactionException {
        OrientTransaction tx = (OrientTransaction) transaction;
        ODatabase<?> db = tx.getDatabase();

        if (!db.isClosed()) {
            db.close();
        }

        ODatabaseInternal<?> oldDb = (ODatabaseInternal<?>) suspendedResources;
        TransactionSynchronizationManager.bindResource(dbf, oldDb);
        ODatabaseRecordThreadLocal.instance().set((ODatabaseDocumentInternal) oldDb.getUnderlying());
    }

    /* (non-Javadoc)
     * @see org.springframework.transaction.support.ResourceTransactionManager#getResourceFactory()
     */
    @Override
    public Object getResourceFactory() {
        return dbf;
    }

    /**
     * Gets the database factory for the database managed by this transaction manager.
     *
     * @return the database
     */
    public OrientDatabaseFactory<?> getDatabaseFactory() {
        return dbf;
    }

    /**
     * Sets the database factory for the database managed by this transaction manager.
     *
     * @param databaseFactory the database to set
     */
    public void setDatabaseFactory(OrientDatabaseFactory<?> databaseFactory) {
        this.dbf = databaseFactory;
    }
}
