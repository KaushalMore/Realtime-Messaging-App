package com.more.chat.service.impl;

import com.more.chat.dto.ConversationDto;
import com.more.chat.entity.Conversation;
import com.more.chat.entity.User;
import com.more.chat.exception.GeneralException;
import com.more.chat.repository.ConnectionsRepository;
import com.more.chat.repository.ConversationRepository;
import com.more.chat.repository.UserRepository;
import com.more.chat.service.interfaces.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConnectionsRepository connectionsRepository;
    private final UserRepository userRepository;

    @Override
    public Conversation createConversation(Long userId1, Long userId2, boolean isGroupChat) throws GeneralException {
        if (!isGroupChat) {
            log.info("Creating conversation between users {} and {}", userId1, userId2);
            boolean isConnectionConfirmed = connectionsRepository.findBySenderIdAndRecipientIdAndStatus(userId1, userId2, "CONFIRMED").isPresent()
                    || connectionsRepository.findBySenderIdAndRecipientIdAndStatus(userId2, userId1, "CONFIRMED").isPresent();
            if (!isConnectionConfirmed) {
                throw new GeneralException("Users must have an established connection with status CONFIRM to start a conversation.");
            }
        }
        User user1 = userRepository.findById(userId1).orElseThrow(() -> new GeneralException("User not found"));
        User user2 = userRepository.findById(userId2).orElseThrow(() -> new GeneralException("User not found"));
        Conversation conversation = new Conversation();
        conversation.setGroupChat(isGroupChat);
        conversation.getUsers().add(user1);
        conversation.getUsers().add(user2);
        conversation.setLastMessageTimestamp(LocalDateTime.now());
        Conversation savedConversation = conversationRepository.save(conversation);
        log.info("Conversation between users {} and {} created successfully", userId1, userId2);
        return savedConversation;
    }

    @Override
    public List<ConversationDto> getConversationsByUserId(String email) throws GeneralException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new GeneralException("User not found"));
        List<Conversation> savedConversation = conversationRepository.findConversationsByUserId(user.getId());
        return savedConversation.stream().map(this::mapConversationToDTO).toList();
    }
//    @Override
//    public List<ConversationDto> getConversationsByUserId(Long userId) throws GeneralException {
//        User user1 = userRepository.findById(userId).orElseThrow(() -> new GeneralException("User not found"));
//        List<Conversation> savedConversation = conversationRepository.findConversationsByUserId(user1.getId());
//        return savedConversation.stream().map(this::mapConversationToDTO).toList();
//    }

    private ConversationDto mapConversationToDTO(Conversation conversation) {
        ConversationDto dto = new ConversationDto();
        dto.setId(conversation.getId());
        dto.setGroupChat(conversation.isGroupChat());
        dto.setParticipantIds(conversation.getUsers().stream().map(User::getId).collect(Collectors.toList()));
        dto.setLastMessageTimestamp(conversation.getLastMessageTimestamp());
        return dto;
    }
    
}
