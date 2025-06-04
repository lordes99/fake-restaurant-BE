package systems.lordes.server.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import systems.lordes.server.data.UserData;
import systems.lordes.server.data.UsersPageData;
import systems.lordes.server.entity.UserEntity;
import systems.lordes.server.gen.api.User;
import systems.lordes.server.mapper.UserMapper;
import systems.lordes.server.mapper.UserMapper3;
import systems.lordes.server.repository.RestaurantRepository;
import systems.lordes.server.repository.UserRepository;
//import jakarta.ws.rs.core.Response;
//import org.keycloak.admin.client.resource.UsersResource;
//import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

//    private static final String LOCATION_HEADER = "Location";
//    private final KeycloakRealm keycloakRealm;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
//    public UserService(KeycloakRealm keycloakRealm, UserRepository userRepository, OrganizationRepository organizationRepository, UserMapper userMapper) {
    public UserService(
            UserRepository userRepository,
//            RestaurantRepository organizationRepository,
//            UserMapper3 userMapper3,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
//        this.keycloakRealm = keycloakRealm;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }




//    public String createKeycloakUser(String name, String surname, String email) {
//        UsersResource usersResource = keycloakRealm.getUsersResource();
//        final List<UserRepresentation> userRepresentations = usersResource
//                    .searchByEmail(email, true);
//
//        // KC user already exists for given email
//        if(userRepresentations != null && !userRepresentations.isEmpty()) {
//            // NOTE: should never happen
//            if(userRepresentations.size() != 1) {
//                throw new RuntimeException("Multiple KC users share the same e-mail!");
//            }
//
//            return userRepresentations.get(0).getId();
//        } else {
//            final UserRepresentation userRepresentation = new UserRepresentation();
//
//            userRepresentation.setUsername(email);
//            userRepresentation.setFirstName(name);
//            userRepresentation.setLastName(surname);
//            userRepresentation.setEmail(email);
//            userRepresentation.setEnabled(true);
//            userRepresentation.setEmailVerified(true);
//
//            try(Response response = usersResource.create(userRepresentation)) {
//                if(response.getStatus() == HttpStatus.CREATED.value()) {
//                    // NOTE: retrieves the created userId without having to perform another API call to find the user by email
//                    final String location = response.getHeaderString(LOCATION_HEADER);
//
//                    return location.substring(location.lastIndexOf("/") + 1);
//                } else {
//                    throw new RuntimeException("Couldn't create KC user for email=" + email);
//                }
//            }
//        }
//    }

    @Transactional(readOnly = true)
    public List<UserData> findUsers() {
        return userMapper.toData(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UsersPageData findUsers(PageRequest pageRequest) {
        return userMapper.toData(userRepository.findAll(pageRequest));
    }

    public User getUserById(UUID id) {
        throw new RuntimeException("Not implemented yet");
    }

    public Optional<UserData> getUserByEmail(String email) {
        if (email != null) {
            return Optional.of(userMapper.toData(userRepository.findByEmail(email).orElse(null)));
        } else {
            throw new IllegalArgumentException("Email cannot be null");
        }
    }

    public User modifyUser(User user) {
        throw new RuntimeException("Not implemented yet");
    }

    public UserData createUser(User user) {
        UserEntity userEntity = userMapper.toEntity(user);



        // Codifica la password prima di salvarla
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        userEntity.setPasswordHash(hashedPassword);

        return userMapper.toData(userRepository.save(userEntity));
    }

    public boolean checkPassword(String rawPassword, String storedHash) {
        return passwordEncoder.matches(rawPassword, storedHash);
    }

}
