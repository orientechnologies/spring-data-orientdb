package org.springframework.data.orient.object;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by frank on 12/10/2016.
 */
public class OrientObjectTemplateTest {

  @Rule
  public TestName name = new TestName();

  private OrientObjectTemplate        template;
  private OrientObjectDatabaseFactory fc;
  @Rule
  public ExternalResource database = new ExternalResource() {
    @Override
    protected void before() throws Throwable {
      fc = new OrientObjectDatabaseFactory();
      fc.setUrl("memory:" + name.getMethodName());
      fc.setUsername("admin");
      fc.setPassword("admin");
      fc.setMaxPoolSize(2);
      //post-construct annotated method for spring
      fc.init();

      template = new OrientObjectTemplate(fc);

    }

    @Override
    protected void after() {

      new ODatabaseDocumentTx("memory:" + name.getMethodName()).open("admin", "admin").drop();
    }
  };

  @Test
  public void testClassOperations() throws Exception {

    assertThat(template.countClass("OUser")).isEqualTo(3);

    assertThat(template.count("Select count(*) from Ouser")).isEqualTo(3);

    assertThat(template.existsClass("OUser")).isTrue();

  }

  @Test
  public void testClusterOperations() throws Exception {

    assertThat(template.countClusterElements("OUser")).isEqualTo(3);

    assertThat(template.existsCluster("OUser")).isTrue();

  }

}