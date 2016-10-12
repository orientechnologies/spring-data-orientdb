package org.springframework.data.orient.object;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by frank on 11/10/2016.
 */
public class OrientObjectDatabaseFactoryTest {


    @Rule
    public TestName name = new TestName();

    @Test
    public void shouldUseMaxPoolSize() throws Exception {

        final OrientObjectDatabaseFactory fc = new OrientObjectDatabaseFactory();

        fc.setUrl("memory:" + name.getMethodName());
        fc.setUsername("admin");
        fc.setPassword("admin");
        fc.setMaxPoolSize(10);
        fc.init();

        //do a query and assert on other thread
        Runnable acquirer = new Runnable() {
            @Override
            public void run() {

                //call th db 10 times in the same thread
                for (int i = 0; i < 10; i++) {

                    OObjectDatabaseTx db = fc.db();

//                    System.out.println(Thread.currentThread().getName() + " :: " + db.getUnderlying().hashCode());
                    try {
                        assertThat(db.isActiveOnCurrentThread()).isTrue();

                        List<ODocument> res = db.query(new OSQLSynchQuery<ODocument>("SELECT * FROM OUser"));

                        assertThat(res).hasSize(3);

                    } finally {

                        db.close();
                    }
                }
            }

        };

        ExecutorService ex = Executors.newCachedThreadPool();

        //spawn 20 threads
        for (int i = 0; i < 20; i++) {

            ex.submit(acquirer);
        }

        ex.awaitTermination(2, TimeUnit.SECONDS);


    }
}