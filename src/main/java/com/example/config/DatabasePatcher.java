package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabasePatcher implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE complaints MODIFY status VARCHAR(255);");
        } catch (Exception e) {
        }

        try {
            List<String> constraints = jdbcTemplate.queryForList(
                    "SELECT CONSTRAINT_NAME FROM information_schema.check_constraints " +
                            "WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'complaints'",
                    String.class);

            for (String constraint : constraints) {
                jdbcTemplate.execute("ALTER TABLE complaints DROP CHECK " + constraint + ";");
                System.out.println("Dropped constraint: " + constraint);
            }
        } catch (Exception e) {
            System.err.println("Error dropping check constraints: " + e.getMessage());
        }
    }
}
