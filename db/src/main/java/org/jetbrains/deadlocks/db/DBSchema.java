package org.jetbrains.deadlocks.db;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class DBSchema {
  private final JdbcTemplate myJdbcTemplate;
  private final TransactionTemplate myTransactionTemplate;

  public DBSchema(BasicDataSource dataSource, DataSourceTransactionManager transactionManager) {
    myJdbcTemplate = new JdbcTemplate(dataSource);
    myTransactionTemplate = new TransactionTemplate(transactionManager);
  }

  public void create() {
    myJdbcTemplate.execute("drop table if exists test_updates");
    myJdbcTemplate.execute("create table test_updates (id int, data varchar(256), primary key(id))");
  }

  public JdbcTemplate getJdbcTemplate() {
    return myJdbcTemplate;
  }

  public TransactionTemplate getTransactionTemplate() {
    return myTransactionTemplate;
  }
}
