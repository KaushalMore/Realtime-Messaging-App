package com.more.chat.service.interfaces;

import com.more.chat.dto.Response;
import com.more.chat.entity.Connections;

import java.util.List;

public interface ConnectionService {

    Response connectUser(Long senderId, Long recipientId);

    Response getSentConnectionRequests(Long userId);

    Response getReceivedConnectionRequests(Long userId);

    Response confirmConnection(Long recipientId, Long connectionId);

    Response deletePendingConnections(Long senderId, Long connectionId);

    Response deleteEstablishedConnection(Long userId, Long connectionId);

    List<Connections> getConnectionByIdAndStatus(Long id, String status);
}
