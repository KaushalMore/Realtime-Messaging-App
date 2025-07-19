package com.more.chat.mapper;

import com.more.chat.dto.ConnectionsDto;
import com.more.chat.dto.ConversationDto;
import com.more.chat.dto.UserDto;
import com.more.chat.entity.Connections;
import com.more.chat.entity.Conversation;
import com.more.chat.entity.User;
import com.more.chat.service.interfaces.UserService;
import org.springframework.lang.NonNull;

import java.util.stream.Collectors;

public class Mapper {

    private static UserService userService;

    public static UserDto mapUserEntityToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .profilePictureUrl(user.getProfilePictureUrl())
                .registrationDate(user.getRegistrationDate())
//                .connectionsDto(user.getConnections().stream().map(Mapper::mapConnectionsEntityToConnectionsDto).toList())
                .numberOfConnections(user.getConnections().stream().count())
                .build();
    }

    public static User mapUserDtoToUserEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setProfilePictureUrl(userDto.getProfilePictureUrl());
        user.setRegistrationDate(userDto.getRegistrationDate());
//        List<Connections> connections = userDto.getConnectionsDto().stream().map(Mapper::mapConnectionsDtoToConnectionEntity).collect(Collectors.toList());
//        user.setConnections(connections);
        return user;
    }

    public static ConnectionsDto mapConnectionsEntityToConnectionsDto(Connections connections) {
        return ConnectionsDto.builder()
                .id(connections.getId())
                .senderId(connections.getSender().getId())
                .recipientId(connections.getRecipient().getId())
                .status(connections.getStatus())
                .createdAt(connections.getCreatedAt())
                .recipient(mapUserEntityToUserDto(connections.getRecipient()))
                .conversationDto(mapConversationToDTO(connections.getConversation()))
                .build();
    }

    public static Connections mapConnectionsDtoToConnectionEntity(ConnectionsDto connectionDto) {
        Connections connection = new Connections();
        connection.setId(connectionDto.getId());
        User sender = userService.getUserById(connectionDto.getSenderId());
        connection.setSender(sender);
        User recipient = userService.getUserById(connectionDto.getRecipientId());
        connection.setRecipient(recipient);
        connection.setStatus(connectionDto.getStatus());
        connection.setCreatedAt(connectionDto.getCreatedAt());
        connection.setConversation(mapConversationDTOToEntity(connectionDto.getConversationDto()));
        return connection;
    }


    public static ConversationDto mapConversationToDTO(@NonNull Conversation conversation) {
        if (conversation == null) return null;
        ConversationDto dto = new ConversationDto();
        dto.setId(conversation.getId());
        dto.setGroupChat(conversation.isGroupChat());
        dto.setParticipantIds(conversation.getUsers().stream().map(User::getId).collect(Collectors.toList()));
        dto.setLastMessageTimestamp(conversation.getLastMessageTimestamp());
        return dto;
    }

    public static Conversation mapConversationDTOToEntity(@NonNull ConversationDto conversationDto) {
        if (conversationDto == null) return null;
        Conversation conversation = new Conversation();
        conversation.setId(conversationDto.getId());
        conversation.setGroupChat(conversationDto.isGroupChat());
        conversation.setUsers(conversationDto.getParticipantIds().stream().map(id -> userService.getUserById(id)).collect(Collectors.toList()));
        conversation.setLastMessageTimestamp(conversationDto.getLastMessageTimestamp());
        return conversation;
    }


//    public static MessageDto mapMessageToDTO(Message message) {
//        MessageDto dto = new MessageDto();
//        dto.setId(message.getId());
//        dto.setSenderId(message.getSender().getId());
//        dto.setContent(message.getContent());
//        dto.setContentMedia(message.getContentMedia());
//        dto.setTimestamp(message.getTimestamp());
//        dto.setConversationId(message.getConversation().getId());
//        dto.setSeen(message.isSeen());
//        return dto;
//    }

//    public static Message mapMessageDTOToEntity(MessageDto messageDto) {
//        Message message = new Message();
//        message.setId(messageDto.getId());
//        message.setSender(userService.getUserById(messageDto.getSenderId()));
//        message.setContent(messageDto.getContent());
//        message.setContentMedia(messageDto.getContentMedia());
//        message.setTimestamp(messageDto.getTimestamp());
//        message.setConversation(messageDto.getConversationId());
//        message.setSeen(messageDto.isSeen());
//        return message;
//    }
}