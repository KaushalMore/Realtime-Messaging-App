package com.more.chat.controller;

import com.more.chat.dto.ConversationDto;
import com.more.chat.exception.GeneralException;
import com.more.chat.jwt.JwtService;
import com.more.chat.service.interfaces.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final JwtService jwtService;

    @GetMapping("/all")
    public ResponseEntity<List<ConversationDto>> getAllConversations(@RequestHeader("Authorization") String auth) throws GeneralException {
        String email = jwtService.extractUsername(auth.substring(7));
        List<ConversationDto> conversationsByUserId = conversationService.getConversationsByUserId(email);
        return ResponseEntity.status(200).body(conversationsByUserId);
    }

}
