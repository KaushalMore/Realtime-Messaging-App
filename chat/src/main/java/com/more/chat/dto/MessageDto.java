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
public class MessageDto {

    private Long id;

    private Long senderId;

    private Long conversationId;

    private String content;

    private String contentMedia;

    private LocalDateTime timestamp;

    private boolean seen;

}
