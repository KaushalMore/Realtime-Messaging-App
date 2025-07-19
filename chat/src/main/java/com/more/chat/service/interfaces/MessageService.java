package com.more.chat.service.interfaces;

import com.more.chat.dto.MessageDto;
import com.more.chat.exception.GeneralException;

import java.util.List;

public interface MessageService {

    MessageDto sendMessage(MessageDto messageDTO) throws GeneralException;

    List<MessageDto> getConversationMessages(Long conversationId) throws GeneralException;

    void markMessageAsSeen(Long messageId, Long recipientId) throws GeneralException;

    public List<MessageDto> getLatestMessages(Long userId, int conversationsLimit, int messagesPerConversation);
}
