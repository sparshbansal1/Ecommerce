package com.ecommerce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class SchemaUpdater implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            String columnType = jdbcTemplate.queryForObject(
                "SELECT DATA_TYPE FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() " +
                "AND TABLE_NAME = 'Product' " +
                "AND COLUMN_NAME = 'image_url'",
                String.class
            );

            if (!"longtext".equalsIgnoreCase(columnType)) {
                jdbcTemplate.execute("ALTER TABLE Product MODIFY COLUMN image_url LONGTEXT");
                System.out.println("Schema updated: image_url column changed to LONGTEXT");
            }
        } catch (Exception e) {
            System.out.println("Schema update skipped: " + e.getMessage());
        }
    }
}
