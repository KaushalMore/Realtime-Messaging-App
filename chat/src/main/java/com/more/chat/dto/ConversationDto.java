package com.more.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDto {

    private Long id;

    private boolean isGroupChat;

    private List<Long> participantIds;

    private LocalDateTime lastMessageTimestamp;

}
