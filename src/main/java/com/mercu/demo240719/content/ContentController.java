package com.mercu.demo240719.content;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Content Management")
public class ContentController {
    private final ContentService contentService;

    @GetMapping
    public ResponseEntity<List<Content>> getAllContents() {
        List<Content> contents = contentService.findAll();
        return new ResponseEntity<>(contents, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Content> get(@PathVariable Long id) {
        Content content = contentService.find(id);
        if (content != null) {
            return new ResponseEntity<>(content, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Content> create(@RequestBody Content content) {
        Content createdContent = contentService.create(content);
        return new ResponseEntity<>(createdContent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Content> update(@PathVariable Long id, @RequestBody Content content) {
        Content existingContent = contentService.find(id);
        if (existingContent != null) {
            content.setId(existingContent.getId());  // 기존 id 유지
            Content updatedContent = contentService.modify(content);
            return new ResponseEntity<>(updatedContent, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Content existingContent = contentService.find(id);
        if (existingContent != null) {
            contentService.remove(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}