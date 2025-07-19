package com.more.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String email;

    private String username;

    private String profilePictureUrl;

    private LocalDateTime registrationDate;

//    private List<ConnectionsDto> connectionsDto;
    private long numberOfConnections;

}
