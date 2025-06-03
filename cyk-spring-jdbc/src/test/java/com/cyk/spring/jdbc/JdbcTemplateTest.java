package com.cyk.spring.jdbc;

import com.cyk.spring.ioc.context.AnnotationConfigApplicationContext;
import com.cyk.spring.jdbc.pojo.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The class JdbcTemplateTest
 *
 * @author yukang.chen
 * @date 2025/5/30
 */
public class JdbcTemplateTest extends JdbcTestBase {

    @Test
    public void test_JdbcTemplate() {
        try (var context = new AnnotationConfigApplicationContext(createPropertyResolver(), JdbcTestApplication.class)) {
            var jdbcTemplate = context.getBean(JdbcTemplate.class);
            jdbcTemplate.update(CREATE_USER);
            jdbcTemplate.update(CREATE_ADDRESS);
            // insert user:
            int userId1 = jdbcTemplate.updateAndReturnGeneratedKey(INSERT_USER, "Bob", 12).intValue();
            int userId2 = jdbcTemplate.updateAndReturnGeneratedKey(INSERT_USER, "Alice", null).intValue();
            assertEquals(1, userId1);
            assertEquals(2, userId2);
            // query user:
            User bob = jdbcTemplate.queryForObject(SELECT_USER, User.class, userId1);
            User alice = jdbcTemplate.queryForObject(SELECT_USER, User.class, userId2);
            assertEquals(1, bob.id);
            assertEquals("Bob", bob.name);
            assertEquals(12, bob.theAge);
            assertEquals(2, alice.id);
            assertEquals("Alice", alice.name);
            assertNull(alice.theAge);
            // query name:
            assertEquals("Bob", jdbcTemplate.queryForObject(SELECT_USER_NAME, String.class, userId1));
            assertEquals(12, jdbcTemplate.queryForObject(SELECT_USER_AGE, int.class, userId1));
            // update user:
            int n1 = jdbcTemplate.update(UPDATE_USER, "Bob Jones", 18, bob.id);
            assertEquals(1, n1);
            // delete user:
            int n2 = jdbcTemplate.update(DELETE_USER, alice.id);
            assertEquals(1, n2);
            // query list
            jdbcTemplate.update(INSERT_USER, "Daniel", 18);
            jdbcTemplate.update(INSERT_USER, "David", 24);
            List<User> users = jdbcTemplate.queryForList(SELECT_ALL, User.class);
            assertEquals(3, users.size());
            List<String> names = jdbcTemplate.queryForList(SELECT_ALL_NAME, String.class);
            assertEquals(3, names.size());
        }
    }
}
