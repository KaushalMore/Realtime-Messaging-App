package com.more.chat.service.impl;

import com.more.chat.dto.Response;
import com.more.chat.dto.UserDto;
import com.more.chat.dto.UserRegistrationDto;
import com.more.chat.entity.User;
import com.more.chat.exception.ResourceAlreadyExistsException;
import com.more.chat.exception.ResourceNotFoundException;
import com.more.chat.mapper.Mapper;
import com.more.chat.repository.UserRepository;
import com.more.chat.service.FileUploadService;
import com.more.chat.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    private static final String SUCCESS_RESPONSE = "successful";
    private static final String ERROR_MESSAGE = "Error occurred during ";

    @Override
    public Response saveUser(UserRegistrationDto userDto) {
        log.debug("Saving user with email {}, username {}", userDto.getEmail(), userDto.getUsername());
        Response response = new Response();
        try {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new ResourceAlreadyExistsException(userDto.getEmail() + " Already Exists");
            }
            if (userRepository.existsByUsername(userDto.getUsername())) {
                throw new ResourceAlreadyExistsException(userDto.getUsername() + " Already Exists");
            }

            User user = new User();
            user.setEmail(userDto.getEmail());
            user.setUsername(userDto.getUsername());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setRegistrationDate(LocalDateTime.now());
            User savedUser = userRepository.save(user);

            response.setStatusCode(200);
            response.setMessage(SUCCESS_RESPONSE);
            response.setUserDto(Mapper.mapUserEntityToUserDto(savedUser));
            log.info("User with email {} saved successfully", userDto.getEmail());
        } catch (ResourceAlreadyExistsException e) {
            handleException(e, response, "Registration");
        }
        return response;
    }

    @Override
    public Response findByEmail(String email) {
        log.debug("Finding user with email {}", email);
        Response response = new Response();
        try {
            User user = getUserByEmail(email);
            UserDto userDto = Mapper.mapUserEntityToUserDto(user);
            response.setStatusCode(200);
            response.setMessage(SUCCESS_RESPONSE);
            response.setUserDto(userDto);
            log.info("User with email {} found successfully", email);
        } catch (Exception e) {
            handleException(e, response, "Find User");
        }
        return response;
    }

    @Override
    public Response findById(Long id) {
        log.debug("Finding user with id {}", id);
        Response response = new Response();
        try {
            User user = getUserById(id);
            UserDto userDto = Mapper.mapUserEntityToUserDto(user);
            response.setStatusCode(200);
            response.setMessage(SUCCESS_RESPONSE);
            response.setUserDto(userDto);
            log.info("User with id '{}' fetched successfully", id);
        } catch (Exception e) {
            handleException(e, response, "Find User");
        }
        return response;
    }

    @Override
    public Response searchUser(String query) {
        log.debug("Searching users with query {}", query);
        Response response = new Response();
        try {
            List<User> userList = userRepository.searchUser(query);
            List<UserDto> userDtoList = userList.stream().map(Mapper::mapUserEntityToUserDto).toList();
            response.setStatusCode(200);
            response.setMessage(SUCCESS_RESPONSE);
            response.setUserDtoList(userDtoList);
            log.info("Search for query {} returned {} users", query, userList.size());
        } catch (Exception e) {
            handleException(e, response, "Search Users");
        }
        return response;
    }

    @Override
    public Response allUsers() {
        log.debug("Fetching all users");
        Response response = new Response();
        try {
            List<User> userList = userRepository.findAll();
            List<UserDto> userDTOList = userList.stream().map(Mapper::mapUserEntityToUserDto).toList();
            response.setStatusCode(200);
            response.setMessage(SUCCESS_RESPONSE);
            response.setUserDtoList(userDTOList);
            log.info("Fetched all users successfully, count: {}", userList.size());
        } catch (Exception e) {
            handleException(e, response, "Get All Users");
        }
        return response;
    }

    @Override
    public Response update(String email, String username, String password, MultipartFile profilePic) {
        log.debug("Updating user with email {}", email);
        Response response = new Response();
        try {
            User user = getUserByEmail(email);
            String imageUrl = null;
            if (profilePic != null && !profilePic.isEmpty()) {
                imageUrl = fileUploadService.uploadFile(profilePic);
            }
            if (username != null && !userRepository.existsByUsername(user.getUsername())) user.setUsername(username);
            if (imageUrl != null) user.setProfilePictureUrl(imageUrl);
            if (password != null) user.setPassword(password);

            userRepository.save(user);
            UserDto userDto = Mapper.mapUserEntityToUserDto(user);
            response.setStatusCode(200);
            response.setMessage("Update User");
            response.setUserDto(userDto);
            log.info("User with email {} updated successfully", email);
        } catch (ResourceNotFoundException e) {
            handleException(e, response, "Update User");
        }
        return response;
    }

    @Override
    public Response deleteUserById(String email) {
        log.debug("Deleting user with email {}", email);
        Response response = new Response();
        try {
            User user = getUserByEmail(email);
            userRepository.deleteById(user.getId());
            response.setStatusCode(200);
            response.setMessage("Delete user");
            log.info("User with email {} deleted successfully", email);
        } catch (ResourceNotFoundException e) {
            handleException(e, response, "Delete User");
        }
        return response;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Long findUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }


    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private void handleException(Exception e, Response response, String context) {
        if (e instanceof ResourceNotFoundException) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            log.warn("Resource not found during {}: {}", context, e.getMessage());
        } else if (e instanceof ResourceAlreadyExistsException) {
            response.setStatusCode(409);
            response.setMessage(e.getMessage());
            log.warn("Resource already exists during {}: {}", context, e.getMessage());
        } else {
            response.setStatusCode(500);
            response.setMessage(ERROR_MESSAGE + context + " : " + e.getMessage());
            log.error(ERROR_MESSAGE + context, e);
        }
    }

}
