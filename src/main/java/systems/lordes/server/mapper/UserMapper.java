package systems.lordes.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import systems.lordes.server.data.CustomUserDetails;
import systems.lordes.server.data.UserData;
import systems.lordes.server.data.UsersPageData;
import systems.lordes.server.entity.UserEntity;
import systems.lordes.server.gen.api.User;
import systems.lordes.server.gen.api.UsersPage;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    User toApi(UserData user);

    @Mapping(target = "email", source = "username")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toApi(CustomUserDetails user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "surname", source = "surname")
    @Mapping(target = "email", source = "email")
//    @Mapping(target = "createdAt", source = "createdAt")
//    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserData toData(UserEntity user);

//    @Mapping(target = "id", source = "id")
//    @Mapping(target = "name", source = "name")
//    @Mapping(target = "surname", source = "surname")
//    @Mapping(target = "email", source = "email")

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "restaurants", ignore = true)
    @Mapping(target = "version", ignore = true)
//    @Mapping(target = "createdAt", source = "createdAt")
//    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(User user);

//    List<UserEntity> toEntities(List<User> users);
    List<UserData> toData(List<UserEntity> users);
    List<User> toApis(List<UserData> users);

    default UsersPageData toData(Page<UserEntity> users) {
        UsersPageData usersPageData = new UsersPageData();
        usersPageData.setUsers(toData(users.getContent()));
        systems.lordes.server.gen.api.Page page = new systems.lordes.server.gen.api.Page();
        page.setTotalElements(users.getTotalElements());
        page.setTotalPages((long) users.getTotalPages());
        page.setSize((long) users.getSize());
        page.setNumber((long) users.getNumber());
        usersPageData.setPage(page);

        return usersPageData;
    }
    @Mapping(target = "content", source = "users")
    UsersPage toApis(UsersPageData users);
}
