package com.evacuation.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Enterprise Resilience DataSource Configuration.
 * Attempts primary connection to MySQL. If MySQL is unreachable or credentials
 * fail, automatically activates a fallback H2 in-memory MySQL-compatible database
 * to ensure zero downtime and uninterrupted educational simulation execution.
 */
@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/evacuation_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC}")
    private String mysqlUrl;

    @Value("${spring.datasource.username:root}")
    private String mysqlUsername;

    @Value("${spring.datasource.password:root}")
    private String mysqlPassword;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String mysqlDriverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        log.info("Checking database connectivity to primary MySQL at: {}", mysqlUrl);

        boolean mysqlAvailable = false;
        try {
            Class.forName(mysqlDriverClassName);
            DriverManager.setLoginTimeout(3); // 3 seconds timeout
            try (Connection conn = DriverManager.getConnection(mysqlUrl, mysqlUsername, mysqlPassword)) {
                if (conn != null && !conn.isClosed()) {
                    mysqlAvailable = true;
                    log.info("Successfully established connection to MySQL Database. Using primary MySQL DataSource.");
                }
            }
        } catch (Exception e) {
            log.warn("Primary MySQL connection could not be established ({}). Switching to resilient H2 in-memory engine.", e.getMessage());
        }

        if (mysqlAvailable) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(mysqlUrl);
            config.setUsername(mysqlUsername);
            config.setPassword(mysqlPassword);
            config.setDriverClassName(mysqlDriverClassName);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setPoolName("EvacuationMySQLPool");
            return new HikariDataSource(config);
        } else {
            log.info("Initializing fallback H2 embedded MySQL-compatible database for seamless zero-setup execution.");
            HikariConfig h2Config = new HikariConfig();
            h2Config.setJdbcUrl("jdbc:h2:mem:evacuation_db;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false");
            h2Config.setDriverClassName("org.h2.Driver");
            h2Config.setUsername("sa");
            h2Config.setPassword("");
            h2Config.setMaximumPoolSize(10);
            h2Config.setPoolName("EvacuationH2FallbackPool");
            return new HikariDataSource(h2Config);
        }
    }
}
