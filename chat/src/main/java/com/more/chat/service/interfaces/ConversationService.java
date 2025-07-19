package com.more.chat.service.interfaces;

import com.more.chat.dto.ConversationDto;
import com.more.chat.entity.Conversation;
import com.more.chat.exception.GeneralException;

import java.util.List;

public interface ConversationService {

    Conversation createConversation(Long userId1, Long userId2, boolean isGroupChat) throws GeneralException;

//    List<ConversationDto> getConversationsByUserId(Long userId) throws GeneralException;
    List<ConversationDto> getConversationsByUserId(String email) throws GeneralException;
}
