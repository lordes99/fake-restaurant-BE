package systems.lordes.server.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import systems.lordes.server.gen.api.*;
import systems.lordes.server.gen.controller.UserApi;
import systems.lordes.server.gen.controller.UsersApi;
import systems.lordes.server.mapper.UserMapper;
import systems.lordes.server.service.UserService;
import systems.lordes.server.utils.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ControllerUtils.PREFIX_API_V1)
public class UserController implements UsersApi, UserApi {

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
    public ResponseEntity<UsersPage> usersGet(Integer page, Integer size) {
        ControllerUtils.getPrincipal();

        UsersPage users;
        if ((page == null || page < 1) && (size == null || size < 1)) {
            List<User> usersApi = userMapper.toApis(userService.findUsers());
            users = new UsersPage().content(usersApi);
            return ResponseEntity.ok(users);
        }

        PageRequest pageRequest = ControllerUtils.pageOf(page, size);
        users = userMapper.toApis(userService.findUsers(pageRequest));
        return ResponseEntity.ok(users);
    }

//    @Override
//    public ResponseEntity<UserSession> userSessionGet() {
//        ControllerUtils.getPrincipal();
//
//        UserSession userSession = new UserSession();
////        userSession.setUser();
////        userSession.setPermissions();
//        return ResponseEntity.ok(userSession);
//    }

    @Override
    public ResponseEntity<User> userIdGet(UUID id) {
        ControllerUtils.getPrincipal();

        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<User> userIdPost(UUID id, User user) {
        ControllerUtils.getPrincipal();

        user.setId(id);
        User modified = userService.modifyUser(user);
        return ResponseEntity.ok(modified);
    }

    @Override
    public ResponseEntity<UUID> usersPost(User user) {
        ControllerUtils.getPrincipal();

//        UUID id = userService.createUser(user);
//        return ResponseEntity.ok(id);
        return null;
    }
}
