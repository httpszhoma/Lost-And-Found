package zhoma.controllers;

import org.springframework.web.bind.annotation.*;
import zhoma.config.ApplicationConfiguration;
import zhoma.dto.UserDto;
import zhoma.mappers.UserMapper;
import zhoma.services.UserService;

import java.util.List;

@RequestMapping("/user")
@RestController
@CrossOrigin("*")

public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @GetMapping("/allUsers")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream().map(userMapper::toDto).toList();

    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable String id) {
        return userMapper.toDto(userService.getUserById(Long.getLong(id)));


    }
    @GetMapping("/{username}")
    public UserDto getUserByUsername(@PathVariable String username) {
        return userMapper.toDto(userService.getUserByUsername(username));
    }
    @GetMapping("/me")
    public UserDto getCurrentUser() {
        return userMapper.toDto(userService.getAuthenticatedUser());

    }
    @PostMapping("/delete/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(Long.getLong(id));
    }



//
}
