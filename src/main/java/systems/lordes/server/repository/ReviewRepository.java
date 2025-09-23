package systems.lordes.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import systems.lordes.server.entity.ReviewEntity;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {
    Page<ReviewEntity> findAllByRestaurant_Id(UUID restaurantId, Pageable pageable);

    Page<ReviewEntity> findAllByOwnerUser_Id(UUID ownerUserId, Pageable pageable);

    boolean existsByIdAndOwnerUserId(UUID id, UUID ownerUserId);

}
