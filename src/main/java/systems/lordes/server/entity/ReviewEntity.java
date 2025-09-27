package systems.lordes.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "review_entities")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = DBConstants.GENERIC_NAMES_OR_FILENAMES)
    private String title;
    @Column(length = DBConstants.GENERIC_DESCRIPTIONS)
    private String description;

    @Column(columnDefinition = "text[]")
    private List<String> photos;

    @Column(columnDefinition = "text[]")
    private List<String> upVoteIds;

    @Column(columnDefinition = "text[]")
    private List<String> downVoteIds;

    @CreatedDate
    @Column(columnDefinition= DBConstants.COLUMN_DEFINITION_TIMESTAMPZ)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private UserEntity ownerUser;

    @Version
    private Long version;
}
