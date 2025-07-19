package com.more.chat.service.impl;

import com.more.chat.dto.MessageDto;
import com.more.chat.entity.Conversation;
import com.more.chat.entity.Message;
import com.more.chat.entity.User;
import com.more.chat.exception.GeneralException;
import com.more.chat.repository.ConversationRepository;
import com.more.chat.repository.MessageRepository;
import com.more.chat.repository.UserRepository;
import com.more.chat.service.interfaces.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    @Override
    public MessageDto sendMessage(MessageDto messageDTO) throws GeneralException {
        User sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new GeneralException("Sender not found"));
        Conversation conversation = conversationRepository.findById(messageDTO.getConversationId())
                .orElseThrow(() -> new GeneralException("Conversation not found"));
        Message message = new Message();
        message.setSender(sender);
        message.setConversation(conversation);
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setStatus("SENT");
        message.setSeen(false);

        Message savedMessage = messageRepository.save(message);

        // Handle notification for group chat or direct message
        if (conversation.isGroupChat()) {
            sendGroupNotification(conversation.getUsers(), messageDTO.getContent(), sender.getId());
        } else {
            conversation.getUsers().stream()
                    .filter(user -> !user.getId().equals(sender.getId()))
                    .forEach(user -> sendNotificationToRecipient(user.getId(), messageDTO.getContent()));
        }

        return mapMessageToDTO(savedMessage);
    }

    @Override
    public List<MessageDto> getConversationMessages(Long conversationId) throws GeneralException {
        List<Message> messages = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new GeneralException("Conversation not found"))
                .getMessages();

        return messages.stream()
                .map(this::mapMessageToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void markMessageAsSeen(Long messageId, Long recipientId) throws GeneralException {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException("Message not found"));
        if (!message.getConversation().getUsers().stream().anyMatch(user -> user.getId().equals(recipientId))) {
            throw new GeneralException("Only the recipient can mark the message as seen");
        }
        message.setSeen(true);
        messageRepository.save(message);
    }


//    public List<MessageDto> getLatestMessages(Long userId, int connectionsLimit, int messagesPerConnection) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//        List<Conversation> conversationsByUserId = conversationRepository.findConversationsByUserId(user.getId());
//        List<Message> allMessages = new ArrayList<>();
//        for (int i = 0; i < Math.min(conversationsByUserId.size(), connectionsLimit); i++) {
//            User connection = conversationsByUserId.get(i).getUsers().stream().filter(x -> x.getId() == userId).findFirst().get();
//            List<Message> messages = messageRepository.findBySenderOrReceiverOrderByTimestampDesc(user, connection, PageRequest.of(0, messagesPerConnection));
//            allMessages.addAll(messages);
//        }
//
//        return allMessages.stream().map(MessageMapper::mapMessageEntityToMessageDto).collect(Collectors.toList());
//    }
    public List<MessageDto> getLatestMessages(Long userId, int conversationsLimit, int messagesPerConversation) {
        List<Conversation> conversations = conversationRepository.findConversationsByUserId(userId);
        List<Message> allMessages = new ArrayList<>();
        for (int i = 0; i < Math.min(conversations.size(), conversationsLimit); i++) {
            Conversation conversation = conversations.get(i);
            List<Message> messages = messageRepository.findMessagesByConversationId(conversation.getId(), PageRequest.of(0, messagesPerConversation));
            allMessages.addAll(messages);
        }
        return allMessages.stream().map(this::mapMessageToDTO).collect(Collectors.toList());
    }


    private MessageDto mapMessageToDTO(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setContent(message.getContent());
        dto.setContentMedia(message.getContentMedia());
        dto.setTimestamp(message.getTimestamp());
        dto.setConversationId(message.getConversation().getId());
        dto.setSeen(message.isSeen());
        return dto;
    }

    private void sendNotificationToRecipient(Long recipientId, String messageContent) {
        // Logic to send notification (could be an email, push notification, etc.)
    }

    private void sendGroupNotification(List<User> users, String messageContent, Long senderId) {
        users.stream()
                .filter(user -> !user.getId().equals(senderId))
                .forEach(user -> sendNotificationToRecipient(user.getId(), messageContent));
    }


}
