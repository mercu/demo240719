package com.mercu.demo240719.user;

import lombok.RequiredArgsConstructor;
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
        User user = User.builder()
                .name("tester")
                .password("testpw")
                .build();

        user = userRepository.save(user);

        User dbUser = userRepository.findById(user.getId()).get();
        log.info("dbUser: {}", dbUser);
        assertNotNull(dbUser);

        dbUser = User.builder()
                .id(user.getId())
                .name("updateUser")
                .password("updatePw")
                .build();
        userRepository.save(dbUser);

        User updateUser = userRepository.findById(user.getId()).get();
        log.info("updateUser: {}", updateUser);
        assertNotNull(updateUser);
        assertEquals("updateUser", updateUser.getName());

        userRepository.deleteById(user.getId());
        assertTrue(userRepository.findById(user.getId()).isEmpty());
    }
}
