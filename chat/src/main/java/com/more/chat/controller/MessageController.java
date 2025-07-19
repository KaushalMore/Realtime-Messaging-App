package com.more.chat.controller;

import com.more.chat.dto.MessageDto;
import com.more.chat.exception.GeneralException;
import com.more.chat.service.interfaces.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public MessageDto sendMessage(MessageDto messageDTO) throws GeneralException {
        return messageService.sendMessage(messageDTO);
    }
}
