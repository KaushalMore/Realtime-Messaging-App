package com.more.chat.controller;

import com.more.chat.dto.Response;
import com.more.chat.jwt.JwtService;
import com.more.chat.service.interfaces.ConnectionService;
import com.more.chat.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/connections")
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/connect/{recipientId}")
    public ResponseEntity<Response> connectionToUser(
            @PathVariable("recipientId") Long recipientId, @RequestHeader("Authorization") String auth
    ) {
        String email = jwtService.extractUsername(auth.substring(7));
        Long userId = userService.findUserIdByEmail(email);
        Response response = connectionService.connectUser(userId, recipientId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/confirm/{connectionId}")
    public ResponseEntity<Response> confirmConnection(
            @PathVariable("connectionId") Long connectionId, @RequestHeader("Authorization") String auth
    ) {
        String email = jwtService.extractUsername(auth.substring(7));
        Long userId = userService.findUserIdByEmail(email);
        Response response = connectionService.confirmConnection(userId, connectionId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/remove-pending/{connectionId}")
    public ResponseEntity<Response> removePendingConnection(
            @PathVariable("connectionId") Long connectionId, @RequestHeader("Authorization") String auth
    ) {
        String email = jwtService.extractUsername(auth.substring(7));
        Long userId = userService.findUserIdByEmail(email);
        Response response = connectionService.deletePendingConnections(userId, connectionId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/remove-established/{connectionId}")
    public ResponseEntity<Response> removeEstablishedConnection(
            @PathVariable("connectionId") Long connectionId, @RequestHeader("Authorization") String auth
    ) {
        String email = jwtService.extractUsername(auth.substring(7));
        Long userId = userService.findUserIdByEmail(email);
        Response response = connectionService.deleteEstablishedConnection(userId, connectionId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/sent-requests")
    public ResponseEntity<Response> getSentConnectionRequests(@RequestHeader("Authorization") String auth) {
        String email = jwtService.extractUsername(auth.substring(7));
        Long userId = userService.findUserIdByEmail(email);
        Response response = connectionService.getSentConnectionRequests(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/received-requests")
    public ResponseEntity<Response> getReceivedConnectionRequests(@RequestHeader("Authorization") String auth) {
        String email = jwtService.extractUsername(auth.substring(7));
        Long userId = userService.findUserIdByEmail(email);
        Response response = connectionService.getReceivedConnectionRequests(userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}