package systems.lordes.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import systems.lordes.server.entity.VoteEntity;

import java.util.UUID;

public interface VoteRepository extends JpaRepository<VoteEntity, UUID> {
}
