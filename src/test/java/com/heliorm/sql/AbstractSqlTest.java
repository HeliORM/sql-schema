package com.heliorm.sql;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static java.lang.String.format;

class AbstractSqlTest {

    DataSource ds;

    public void setup() {
        ds = setupMysqlDataSource();
    }

    private static DataSource setupMysqlDataSource() {
        HikariConfig conf = new HikariConfig();
        conf.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        conf.setUsername("root");
        conf.setPassword("dev");
        return new HikariDataSource(conf);
    }


    protected Connection con() {
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new TestException(e.getMessage(), e);
        }
    }
}


