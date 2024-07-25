package com.mercu.demo240719.user;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void bean() {
        log.info("userService : {}", userService);
        assertNotNull(userService);
    }

    @Test
    public void crud() {
        // create
        User user = User.builder()
                .name("tester")
                .password("testpw")
                .build();

        userService.create(user);

        // read
        User dbUser = userService.findByName("tester");
        log.info("dbUser: {}", dbUser);
        assertNotNull(dbUser);

        // update
        dbUser = User.builder()
                .seq(dbUser.getSeq())
                .name(dbUser.getName())
                .password("updatePw")
                .build();
        userService. modify(dbUser);

        // read
        User updateUser = userService.findByName("tester");
        log.info("updateUser: {}", updateUser);
        assertNotNull(updateUser);
        assertEquals("updatePw", updateUser.getPassword());

        // delete
        userService.removeByName("tester");
        assertNull(userService.findByName("tester"));
    }
}
