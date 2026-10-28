package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import org.junit.jupiter.api.Test;

class PostgresIT {

  @Test
  void canConnectAndQuery() throws Exception {
    try (Connection connection =
        DriverManager.getConnection("jdbc:h2:mem:postgres_it;DB_CLOSE_DELAY=-1", "sa", "")) {
      ResultSet rs = connection.createStatement().executeQuery("select 1");
      rs.next();
      assertEquals(1, rs.getInt(1));
    }
  }
}
