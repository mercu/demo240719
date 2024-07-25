package com.mercu.demo240719.content;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED) // 클래스 레벨에 트랜잭션 추가
public class ContentService {
    private final ContentRepository contentRepository;

    @Transactional(readOnly = true)
    public List<Content> findAll() {
        return Lists.newArrayList(contentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Content find(Long id) {
        return contentRepository.findById(id).orElse(null);
    }

    public Content create(Content content) {
        return contentRepository.save(content);
    }

    public Content modify(Content content) {
        return contentRepository.save(content);
    }

    public void remove(Long id) {
        contentRepository.deleteById(id);
    }
}