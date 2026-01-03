package com.example.demo.api;

import com.example.demo.service.RedisTestService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/redis-test")
public class RedisTestController {

    private final RedisTestService service;

    public RedisTestController(RedisTestService service) {
        this.service = service;
    }

    @PostMapping
    public Map<String, String> set(@RequestBody SetRequest req) {
        service.set(req.key(), req.value());
        return Map.of("result", "saved");
    }

    @GetMapping
    public Map<String, Object> get(@RequestParam String key) {
     var result = new java.util.HashMap<String, Object>();
    result.put("key", key);

     var valueOpt = service.get(key);
     if (valueOpt.isPresent()) {
         result.put("value", valueOpt.get());
         result.put("cached", true);
     } else {
         result.put("cached", false);
     }
     return result;
    }

    public record SetRequest(
            @NotBlank String key,
            @NotBlank String value
    ) {}
}
