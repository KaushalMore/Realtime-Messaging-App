package com.more.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    private int statusCode;
    private String message;

    private String accessToken;
    private String refreshToken;

    private UserDto userDto;
    private List<UserDto> userDtoList;

    private ConnectionsDto connectionsDto;
    private List<ConnectionsDto> connectionsDtoList;

}
