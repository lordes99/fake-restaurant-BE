package systems.lordes.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import systems.lordes.server.entity.RestaurantEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, UUID>, JpaSpecificationExecutor<RestaurantEntity> {
    @Query("""
        SELECT r FROM RestaurantEntity r
        LEFT JOIN FETCH r.ownerUser
        LEFT JOIN FETCH r.reviews
        WHERE r.id = :id
    """)
    Optional<RestaurantEntity> findByIdWithDetails(@Param("id") UUID id);

    boolean existsByIdAndOwnerUser_Id(UUID id, UUID ownerUserId);
}
