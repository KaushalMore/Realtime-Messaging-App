package com.more.chat.service.interfaces;

import com.more.chat.dto.LoginRequest;
import com.more.chat.dto.Response;

public interface AuthService {

    Response verify(LoginRequest loginRequest);

    void disconnectUser(String email);
}
