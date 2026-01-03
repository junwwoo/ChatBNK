package com.example.demo.api;

import com.example.demo.service.ChatQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatQueryController {

    private final ChatQueryService chatQueryService;

    public ChatQueryController(ChatQueryService chatQueryService) {
        this.chatQueryService = chatQueryService;
    }

    @GetMapping("/query")
    public ChatQueryService.ChatResponse query(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return chatQueryService.handle(q, limit);
    }
}
