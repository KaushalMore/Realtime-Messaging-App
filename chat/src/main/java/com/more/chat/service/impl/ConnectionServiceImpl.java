package com.more.chat.service.impl;

import com.more.chat.dto.Response;
import com.more.chat.entity.Connections;
import com.more.chat.entity.Conversation;
import com.more.chat.entity.User;
import com.more.chat.exception.GeneralException;
import com.more.chat.exception.ResourceNotFoundException;
import com.more.chat.mapper.Mapper;
import com.more.chat.repository.ConnectionsRepository;
import com.more.chat.repository.UserRepository;
import com.more.chat.service.interfaces.ConnectionService;
import com.more.chat.service.interfaces.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionsRepository connectionsRepository;
    private final ConversationService conversationService;
    private final UserRepository userRepository;

    private static final String PENDING_STATUS = "PENDING";
    private static final String CONFIRMED_STATUS = "CONFIRMED";

    private static final String SUCCESS_RESPONSE = "Success";

    @Override
    public Response connectUser(Long senderId, Long recipientId) {
        log.debug("Attempting to connect user {} to user {}", senderId, recipientId);
        Response response = new Response();
        try {
            User sender = getUserById(senderId);
            User recipient = getUserById(recipientId);

            Connections connection = new Connections();
            connection.setSender(sender);
            connection.setRecipient(recipient);
            connection.setCreatedAt(LocalDateTime.now());
            connection.setStatus(PENDING_STATUS);
            connectionsRepository.save(connection);

            response.setStatusCode(200);
            response.setMessage(SUCCESS_RESPONSE);
            log.info("Connection request sent from user {} to user {}", senderId, recipientId);
        } catch (Exception e) {
            handleException(e, response);
        }
        return response;
    }

    @Override
    public Response getSentConnectionRequests(Long userId) {
        log.debug("Attempting to fetch sent connection requests for user with id {}", userId);
        Response response = new Response();
        try {
            getUserById(userId);
            List<Connections> connections = connectionsRepository.findBySenderIdAndStatus(userId, PENDING_STATUS);
            response.setStatusCode(200);
            response.setMessage(SUCCESS_RESPONSE);
            response.setConnectionsDtoList(connections.stream().map(Mapper::mapConnectionsEntityToConnectionsDto).toList());
            log.info("Fetched all sent connection request for user with id {}", userId);
        } catch (Exception e) {
            handleException(e, response);
        }
        return response;
    }

    @Override
    public Response getReceivedConnectionRequests(Long userId) {
        log.debug("Attempting to fetch received connection request for user with id {}", userId);
        Response response = new Response();
        try {
            getUserById(userId);
            List<Connections> connections = connectionsRepository.findByRecipientIdAndStatus(userId, PENDING_STATUS);
            response.setStatusCode(200);
            response.setMessage(SUCCESS_RESPONSE);
            response.setConnectionsDtoList(
                    connections.stream().map(Mapper::mapConnectionsEntityToConnectionsDto).toList());
            log.info("Fetched all received connection request for user with id {}", userId);
        } catch (Exception e) {
            handleException(e, response);
        }
        return response;
    }

    @Override
    public Response confirmConnection(Long recipientId, Long connectionId) {
        log.debug("Attempting to confirm connection with id {} by user {}", connectionId, recipientId);
        Response response = new Response();
        try {
            getUserById(recipientId);
            Connections connection = getConnectionById(connectionId);
            if (!connection.getRecipient().getId().equals(recipientId)) {
                throw new GeneralException("Only recipient can confirm connection");
            }
            connection.setStatus(CONFIRMED_STATUS);
            connectionsRepository.save(connection);

            // Create conversation
            Conversation conversation = conversationService.createConversation(connection.getSender().getId(), recipientId, false);

            // Add conversation to connection
            connection.setConversation(conversation);
            connectionsRepository.save(connection);

            response.setStatusCode(200);
            response.setMessage(SUCCESS_RESPONSE);
            response.setConnectionsDto(Mapper.mapConnectionsEntityToConnectionsDto(connection));
            log.info("Confirmed connection with id {} by user {}", connectionId, recipientId);
        } catch (Exception e) {
            handleException(e, response);
        }
        return response;
    }

    @Override
    public Response deletePendingConnections(Long userId, Long connectionId) {
        log.debug("Attempting to delete connection request for connection {} by user {}", connectionId, userId);
        Response response = new Response();
        try {
            getUserById(userId);
            Connections connection = getConnectionById(connectionId);
            if (!connection.getStatus().equals(PENDING_STATUS)) {
                throw new GeneralException("Connection Confirmed");
            }
            connectionsRepository.delete(connection);
            response.setStatusCode(200);
            response.setMessage(SUCCESS_RESPONSE);
            log.info("Successfully deleted connection request with id {} by user {}", connectionId, userId);
        } catch (Exception e) {
            handleException(e, response);
        }
        return response;
    }

    @Override
    public Response deleteEstablishedConnection(Long userId, Long connectionId) {
        log.debug("Attempting to delete existing connection {} by user {}", connectionId, userId);
        Response response = new Response();
        try {
            getUserById(userId);
            Connections connection = getConnectionById(connectionId);
            if (connection == null || connection.getSender() == null || connection.getRecipient() == null) {
                throw new GeneralException("Connection or user details are missing");
            }
            if (!connection.getStatus().equals(CONFIRMED_STATUS)) {
                throw new GeneralException("Connection not confirmed");
            }
            if (!connection.getSender().getId().equals(userId) && !connection.getRecipient().getId().equals(userId)) {
                throw new GeneralException("You cannot delete connection");
            }
            connectionsRepository.delete(connection);
            response.setStatusCode(200);
            response.setMessage(SUCCESS_RESPONSE);
            log.info("Successfully deleted connection {} by user {}", connectionId, userId);
        } catch (Exception e) {
            handleException(e, response);
        }
        return response;
    }

    @Override
    public List<Connections> getConnectionByIdAndStatus(Long id, String status) {
        log.debug("Attempting to fetch connection with id {} and status {}", id, status);
        List<Connections> connections = connectionsRepository.findByUserIdAndStatus(id, status);
        log.info("Fetched all connection with id {} and status {}", id, status);
        return connections;
    }


    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));
    }

    private Connections getConnectionById(Long connectionId) {
        return connectionsRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection", "id", connectionId.toString()));
    }

    private void handleException(Exception e, Response response) {
        if (e instanceof ResourceNotFoundException) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            log.warn("Resource not found: {}", e.getMessage());
        } else if (e instanceof GeneralException) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
            log.warn("Exception : {}", e.getMessage());
        } else {
            response.setStatusCode(500);
            response.setMessage("Error sending connection request to user : " + e.getMessage());
            log.warn("Internal server Exception: {}", e.getMessage());
        }
    }

}
