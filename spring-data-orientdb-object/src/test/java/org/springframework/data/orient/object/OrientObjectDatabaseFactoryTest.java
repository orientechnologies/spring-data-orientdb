package org.springframework.data.orient.object;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {OrientDbObjectTestConfiguration.class})
public class OrientObjectDatabaseFactoryTest {


    @Autowired
    OrientObjectDatabaseFactory factory;

    @Rule
    public TestName name = new TestName();


    @Test
    public void shouldCountClassElements() throws Exception {

        OObjectDatabaseTx db = factory.db();

        assertThat(db.countClass("OUser")).isEqualTo(3);
    }

    @Test
    public void shouldCountClusterElements() throws Exception {

        OObjectDatabaseTx db = factory.db();

        assertThat(db.countClusterElements("OUser")).isEqualTo(3);
    }
}