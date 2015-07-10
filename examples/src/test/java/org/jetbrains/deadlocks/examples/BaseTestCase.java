package org.jetbrains.deadlocks.examples;

import org.jetbrains.deadlocks.db.DBSchema;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.annotations.BeforeClass;

public abstract class BaseTestCase {
  protected static JdbcTemplate ourJdbcTemplate;
  protected static TransactionTemplate ourTrx;

  @BeforeClass
  public static void setUpClass() {
    ApplicationContext context =
        new ClassPathXmlApplicationContext(new String[] {"spring.xml"});
    DBSchema schema = context.getBean(DBSchema.class);
    schema.create();

    ourTrx = schema.getTransactionTemplate();
    ourJdbcTemplate = schema.getJdbcTemplate();
  }
}
