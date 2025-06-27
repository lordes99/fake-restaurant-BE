package systems.lordes.server.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import systems.lordes.server.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findByEmail(String email);
    //    Page<UserEntity> findByOrganization(Pageable page, OrganizationEntity organization);

//    @Query("SELECT u FROM UserEntity u WHERE " +
//            "LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
//            "LOWER(u.surname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
//            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))")
//    Page<UserEntity> searchUsers(@Param("search") String search, Pageable pageable);
}
