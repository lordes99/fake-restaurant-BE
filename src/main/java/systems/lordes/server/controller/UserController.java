package systems.lordes.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import systems.lordes.server.gen.api.Error;
import systems.lordes.server.gen.api.Role;
import systems.lordes.server.gen.api.User;
import systems.lordes.server.gen.api.UsersPage;
import systems.lordes.server.gen.controller.UserApi;
import systems.lordes.server.mapper.UserMapper;
import systems.lordes.server.service.UserService;
import systems.lordes.server.utils.ControllerUtils;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ControllerUtils.PREFIX_API_V1)
public class UserController implements UserApi {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserController(
            UserService userService,
            UserMapper userMapper
    ) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public ResponseEntity<UsersPage> usersGet(Integer page, Integer size, String search) {
        User loggedUser = userMapper.toApi(ControllerUtils.getPrincipalSession());
        if (loggedUser == null || !Role.ADMIN.equals(loggedUser.getRole())) {
            Error error = new Error().message("Access denied: unauthorized user");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        UsersPage users;
        if (page == null || size == null) {
            Error error = new Error().message("Filter Error: page or size is null");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        PageRequest pageRequest = ControllerUtils.pageOf(page - 1, size);
        users = userMapper.toApis(userService.findUsers(pageRequest, search));
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<User> userIdGet(UUID id) {
        User loggedUser = userMapper.toApi(ControllerUtils.getPrincipalSession());
        if (loggedUser != null && loggedUser.getId().equals(id)) {
            return ResponseEntity.ok(loggedUser);
        } else if (loggedUser != null && Role.ADMIN.equals(loggedUser.getRole())) {
            return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(userMapper.toApi(user)))
                .orElseGet(() -> {
                    Error error = new Error().message("Not found: user not found");
                    return (ResponseEntity) ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
        } else {
            Error error = new Error().message("Access denied: unauthorized user");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @Override
    public ResponseEntity<User> userIdPut(UUID id, User user) {
        User loggedUser = userMapper.toApi(ControllerUtils.getPrincipalSession());
        if (loggedUser != null && (loggedUser.getId().equals(id) || Role.ADMIN.equals(loggedUser.getRole()))) {
            return userService.modifyUser(user)
                .map(userData -> ResponseEntity.ok(userMapper.toApi(userData)))
                .orElseGet(() -> {
                    Error error = new Error().message("Not found: user not found");
                    return (ResponseEntity) ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
                });
        } else {
            Error error = new Error().message("Access denied: unauthorized user");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @Override
    public ResponseEntity<UUID> usersPost(User user) {
        User loggedUser = userMapper.toApi(ControllerUtils.getPrincipalSession());
        if (loggedUser == null || !Role.ADMIN.equals(loggedUser.getRole())) {
            Error error = new Error().message("Access denied: unauthorized user");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        return ResponseEntity.ok(
            userMapper.toApi(
                userService.createUser(user, true)
            ).getId()
        );
    }
}
