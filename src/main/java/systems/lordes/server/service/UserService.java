package systems.lordes.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import systems.lordes.server.data.UserData;
import systems.lordes.server.data.UserRole;
import systems.lordes.server.data.UsersPageData;
import systems.lordes.server.entity.UserEntity;
import systems.lordes.server.gen.api.User;
import systems.lordes.server.mapper.UserMapper;
import systems.lordes.server.repository.UserRepository;
import systems.lordes.server.utils.UserSpecifications;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserData> findUsers() {
        return userMapper.toData(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UsersPageData findUsers(PageRequest pageRequest, String search) {
        Specification<UserEntity> spec = UserSpecifications.searchByKeyword(search);
        return userMapper.toData(userRepository.findAll(spec, pageRequest));
    }

    public Optional<UserData> getUserById(UUID id) {
        return Optional.of(userMapper.toData(userRepository.findById(id).orElse(null)));
    }

    public Optional<UserData> getUserByEmail(String email) {
        if (email != null) {
            return Optional.of(userMapper.toData(userRepository.findByEmail(email).orElse(null)));
        } else {
            throw new IllegalArgumentException("Email cannot be null");
        }
    }

    public Optional<UserData> modifyUser(User user) {
        UserEntity userEntity = userMapper.toEntity(user);
        UserEntity existingUser = userRepository.findById(user.getId()).orElseThrow();
        mergeUserEntity(userEntity, existingUser);

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            existingUser.setPasswordHash(hashedPassword);
        }

        return Optional.of(userMapper.toData(userRepository.save(existingUser)));
    }

    public UserData createUser(User user, boolean roleChoiceAllowed) {
        UserEntity userEntity = userMapper.toEntity(user);

        if (!roleChoiceAllowed) {
            userEntity.setRole(UserRole.USER);
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        userEntity.setPasswordHash(hashedPassword);

        return userMapper.toData(userRepository.save(userEntity));
    }

    public boolean deleteUser(UUID id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void mergeUserEntity(UserEntity source, UserEntity target) {
        if (source.getName() != null) {
            target.setName(source.getName());
        }
        if (source.getSurname() != null) {
            target.setSurname(source.getSurname());
        }
        if (source.getEmail() != null) {
            target.setEmail(source.getEmail());
        }
        if (source.getRole() != null) {
            target.setRole(source.getRole());
        }
    }

}
