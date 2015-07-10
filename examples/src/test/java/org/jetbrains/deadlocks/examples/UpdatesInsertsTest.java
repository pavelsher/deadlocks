package org.jetbrains.deadlocks.examples;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;

public class UpdatesInsertsTest extends BaseTestCase {
  @BeforeClass
  public static void setUp() {
    setUpClass();

    ourTrx.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        ourJdbcTemplate.update("delete from test_updates");
        for (int i = 0; i < 500; i++) {
          ourJdbcTemplate.update("insert into test_updates (id, data) values (?, ?)", i, "data" + i);
        }
      }
    });
  }

  @Test(threadPoolSize = 10, invocationCount = 10)
  public void updates_deadlock() {
    ourTrx.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Random random = new Random(System.nanoTime());
        for (int i = 0; i < 500; i++) {
          int id = random.nextInt(500);
          ourJdbcTemplate.update("update test_updates set data=? where id=?", "updated" + id, id);
        }
      }
    });
  }

  @Test(threadPoolSize = 10, invocationCount = 50)
  public void updates_no_deadlock() {
    ourTrx.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        for (int i = 0; i < 500; i++) {
          ourJdbcTemplate.update("update test_updates set data=? where id=?", "updated" + i, i);
        }
      }
    });
  }

  @Test(threadPoolSize = 10, invocationCount = 50)
  public void inserts_test() {
    ourTrx.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Random random = new Random(System.nanoTime());
        for (int i = 0; i < 500; i++) {
          int id = random.nextInt(500);
          try {
            ourJdbcTemplate.update("insert into test_updates (id, data) values (?,?)", id, "new_data_" + id);
          } catch (DuplicateKeyException e) {
            // ignore
          }
        }
      }
    });
  }
}
