package com.mercu.demo240719.user;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED) // 클래스 레벨에 트랜잭션 추가
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return Lists.newArrayList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public User modify(User user) {
        return userRepository.save(user);
    }

    public void removeByName(String name) {
        userRepository.deleteByName(name);
    }
}