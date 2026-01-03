package com.example.demo.api;

import com.example.demo.domain.TestEntity;
import com.example.demo.repository.TestEntityRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/db-test")
public class DbTestController {

    private final TestEntityRepository repo;

    public DbTestController(TestEntityRepository repo) {
        this.repo = repo;
    }

    // 저장
    @PostMapping
    public TestEntityResponse create(@RequestBody CreateRequest req) {
        TestEntity saved = repo.save(new TestEntity(req.name()));
        return TestEntityResponse.from(saved);
    }

    // 전체 조회
    @GetMapping
    public List<TestEntityResponse> list() {
        return repo.findAll()
                .stream()
                .map(TestEntityResponse::from)
                .toList();
    }

    public record CreateRequest(@NotBlank String name) {}

    public record TestEntityResponse(Long id, String name, String createdAt) {
        static TestEntityResponse from(TestEntity e) {
            return new TestEntityResponse(
                    e.getId(),
                    e.getName(),
                    e.getCreatedAt().toString()
            );
        }
    }
}
