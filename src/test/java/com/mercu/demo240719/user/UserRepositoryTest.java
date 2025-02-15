package com.mercu.demo240719.user;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void bean() {
        log.info("userRepository : {}", userRepository);
        assertNotNull(userRepository);
    }

    @Test
    public void crud() {
        // create
        User user = User.builder()
                .name("tester")
                .password("testpw")
                .build();

        user = userRepository.save(user);

        // read
        User dbUser = userRepository.findById(user.getSeq()).get();
        log.info("dbUser: {}", dbUser);
        assertNotNull(dbUser);

        // update
        dbUser = User.builder()
                .seq(user.getSeq())
                .name("updateUser")
                .password("updatePw")
                .build();
        userRepository.save(dbUser);

        // read
        User updateUser = userRepository.findById(user.getSeq()).get();
        log.info("updateUser: {}", updateUser);
        assertNotNull(updateUser);
        assertEquals("updateUser", updateUser.getName());

        // delete
        userRepository.deleteById(user.getSeq());
        assertTrue(userRepository.findById(user.getSeq()).isEmpty());
    }
}
