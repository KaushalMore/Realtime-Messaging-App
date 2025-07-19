package com.more.chat.service.interfaces;

import com.more.chat.dto.Response;
import com.more.chat.dto.UserRegistrationDto;
import com.more.chat.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Response saveUser(UserRegistrationDto userDto);

    Response findByEmail(String email);

    Response findById(Long id);

    Response searchUser(String query);

    Response allUsers();

    Response update(String email, String fullName, String password, MultipartFile profilePic);

    Response deleteUserById(String email);

    User getUserById(Long id);

    Long findUserIdByEmail(String email) ;

}
