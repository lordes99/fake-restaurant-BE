package systems.lordes.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import systems.lordes.server.entity.ReviewEntity;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {
    Page<ReviewEntity> findAllByRestaurant_Id(UUID restaurantId, Pageable pageable);

    @Query(
            value = "SELECT * FROM review_entities WHERE restaurant_id = :restaurantId "
                    + "ORDER BY (cardinality(up_vote_ids) * 1 + cardinality(down_vote_ids) * -0.5) DESC",
            nativeQuery = true)
    Page<ReviewEntity> findAllByRestaurantSorted(@Param("restaurantId") UUID restaurantId, Pageable pageable);

    Page<ReviewEntity> findAllByOwnerUser_Id(UUID ownerUserId, Pageable pageable);

    boolean existsByIdAndOwnerUserId(UUID id, UUID ownerUserId);

}
