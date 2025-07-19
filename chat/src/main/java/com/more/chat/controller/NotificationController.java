package com.more.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public void sendNotification(@RequestParam Long recipientId, @RequestParam String messageContent, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            messagingTemplate.convertAndSend("/topic/notifications/" + recipientId, messageContent);
        }
    }
}
