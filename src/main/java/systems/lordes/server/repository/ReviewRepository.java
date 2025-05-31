package systems.lordes.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import systems.lordes.server.entity.ReviewEntity;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {
}
