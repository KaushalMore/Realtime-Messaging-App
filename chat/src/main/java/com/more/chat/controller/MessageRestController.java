package com.more.chat.controller;

import com.more.chat.dto.MessageDto;
import com.more.chat.exception.GeneralException;
import com.more.chat.service.interfaces.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageRestController {

    private final MessageService messageService;

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageDto>> getConversationMessages(@PathVariable Long conversationId) throws GeneralException {
        List<MessageDto> messages = messageService.getConversationMessages(conversationId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/markSeen")
    public ResponseEntity<Void> markMessageAsSeen(@RequestParam Long messageId, @RequestParam Long recipientId) throws GeneralException {
        messageService.markMessageAsSeen(messageId, recipientId);
        return ResponseEntity.ok().build();
    }
}

