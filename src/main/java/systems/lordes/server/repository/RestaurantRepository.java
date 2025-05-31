package systems.lordes.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import systems.lordes.server.entity.RestaurantEntity;

import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, UUID> {
}
