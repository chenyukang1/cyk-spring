/*
 * Copyright (c) 2015-2025，千寻位置网络有限公司版权所有。
 *
 * 时空智能 共创数字中国（厘米级定位 | 毫米级感知 | 纳秒级授时）
 */
package com.cyk.spring.jdbc.tx;

import com.cyk.spring.ioc.context.AnnotationConfigApplicationContext;
import com.cyk.spring.jdbc.JdbcTemplate;
import com.cyk.spring.jdbc.JdbcTestApplication;
import com.cyk.spring.jdbc.JdbcTestBase;
import com.cyk.spring.jdbc.exception.TransactionException;
import com.cyk.spring.jdbc.tx.pojo.Address;
import com.cyk.spring.jdbc.tx.pojo.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class TxTest
 *
 * @author yukang.chen
 * @date 2025/6/20
 */
public class JdbcTransactionTest extends JdbcTestBase {

    @Test
    public void testTx() {
        try (var ctx = new AnnotationConfigApplicationContext(JdbcTestApplication.class, createPropertyResolver())) {
            JdbcTemplate jdbcTemplate = ctx.getBean(JdbcTemplate.class);
            jdbcTemplate.update(CREATE_USER);
            jdbcTemplate.update(CREATE_ADDRESS);

            UserService userService = ctx.getBean(UserService.class);
            AddressService addressService = ctx.getBean(AddressService.class);
            // proxied:
            assertNotSame(UserService.class, userService.getClass());
            assertNotSame(AddressService.class, addressService.getClass());
            // proxy object is not inject:
            assertNull(userService.addressService);
            assertNull(addressService.userService);

            // insert user:
            User bob = userService.createUser("Bob", 12);
            assertEquals(1, bob.id);

            // insert addresses:
            Address addr1 = new Address(bob.id, "Broadway, New York", 10012);
            Address addr2 = new Address(bob.id, "Fifth Avenue, New York", 10080);
            // NOTE user not exist for addr3:
            Address addr3 = new Address(bob.id + 1, "Ocean Drive, Miami, Florida", 33411);
            assertThrows(TransactionException.class, () -> {
                addressService.addAddress(addr1, addr2, addr3);
            });

            // ALL address should not inserted:
            assertTrue(addressService.getAddresses(bob.id).isEmpty());

            // insert addr1, addr2 for Bob only:
            addressService.addAddress(addr1, addr2);
            assertEquals(2, addressService.getAddresses(bob.id).size());

            // now delete bob will cause rollback:
            assertThrows(TransactionException.class, () -> {
                userService.deleteUser(bob);
            });

            // bob and his addresses still exist:
            assertEquals("Bob", userService.getUser(1).name);
            assertEquals(2, addressService.getAddresses(bob.id).size());
        }
        // re-open db and query:
        try (var ctx = new AnnotationConfigApplicationContext(JdbcTestApplication.class, createPropertyResolver())) {
            AddressService addressService = ctx.getBean(AddressService.class);
            List<Address> addressesOfBob = addressService.getAddresses(1);
            assertEquals(2, addressesOfBob.size());
        }
    }
}
