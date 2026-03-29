package com.example.demo.api;

import com.example.demo.service.ChatQueryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/chat")
public class ChatQueryController {

    private final ChatQueryService chatQueryService;

    public ChatQueryController(ChatQueryService chatQueryService) {
        this.chatQueryService = chatQueryService;
    }

    @GetMapping("/query")
    public ChatQueryService.ChatResponse query(
            @RequestParam @Size(min = 1, max = 200) String q,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit
    ) {
        return chatQueryService.handle(q, limit);
    }
}
