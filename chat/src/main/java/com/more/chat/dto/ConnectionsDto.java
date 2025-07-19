package com.more.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectionsDto {

    private Long id;

    private Long senderId;

    private Long recipientId;

    private String status;

    private LocalDateTime createdAt;

    private UserDto recipient;

    private ConversationDto conversationDto;

}
