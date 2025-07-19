package com.more.chat.controller;

import com.more.chat.dto.Response;
import com.more.chat.jwt.JwtService;
import com.more.chat.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/profile")
    public ResponseEntity<Response> getProfile(@RequestHeader("Authorization") String auth) {
        String email = jwtService.extractUsername(auth.substring(7));
        Response response = userService.findByEmail(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/other-user-profile/{id}")
    public ResponseEntity<Response> getOtherUsersProfile(@PathVariable("id") Long id) {
        Response response = userService.findById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Response> searchUser(@RequestParam("query") String query) {
        Response response = userService.searchUser(query);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllUsers() {
        Response response = userService.allUsers();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Response> updateUser(
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "profilePic", required = false) MultipartFile profilePic,
            @RequestHeader("Authorization") String auth
    ) {
        String email = jwtService.extractUsername(auth.substring(7));
        Response response = userService.update(email, fullName, password, profilePic);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response> deleteUser(@RequestHeader("Authorization") String auth) {
        String email = jwtService.extractUsername(auth.substring(7));
        Response response = userService.deleteUserById(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
