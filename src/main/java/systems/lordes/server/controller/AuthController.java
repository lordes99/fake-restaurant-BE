package systems.lordes.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import systems.lordes.server.data.CustomUserDetails;
import systems.lordes.server.gen.api.LoginRequest;
import systems.lordes.server.gen.api.TokenResponse;
import systems.lordes.server.gen.api.User;
import systems.lordes.server.gen.controller.PublicApi;
import systems.lordes.server.mapper.UserMapper;
import systems.lordes.server.service.JwtService;
import systems.lordes.server.service.UserService;
import systems.lordes.server.utils.ControllerUtils;

@RestController
@RequestMapping(ControllerUtils.PREFIX_API_V1)
public class AuthController implements PublicApi {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            UserService userService,
            UserMapper userMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @Override
    public ResponseEntity<TokenResponse> loginPost(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new TokenResponse().token(token));
    }

    @Override
    public ResponseEntity<User> registerPost(User user) {
        return ResponseEntity.ok(userMapper.toApi(userService.createUser(user)));
    }
}
