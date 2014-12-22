# Migration Notes

- deprecated everything in spring-data-orientdb-commons/src/main/java/com/springframework/data/orientdb/object/repository; 
  this is object related stuff and should live in it's own module
- spring-data-orientdb-commons/pom.xml contains dependency to orientdb-object; as soon as refactoring is done remove this
- moved the following classes from "orm" to "core":

    > AbstractOrientDatabaseFactory
    
    > OrientDocumentDatabaseFactory
    
    > OrientObjectDatabaseFactory
    
    > OrientTransaction
    
    > OrientTransactionManager
  
- AbstractOrientDatabaseFactory<T>.doCreatePool() should be void, because ODatabaseDocumentPool is deprecated (no common 
  interface for all pools available); let the implementation classes do this without the parent; the properties are 
  available anyway and the parent just set's the pool size
- using JUnit (instead of TestNG), because I had the configuration at hand and there was an exception with the latest 
  TestNG version 6.8.8; also has added value that no need to extend a TestNG base class; static import of Assert, shorter 
  code