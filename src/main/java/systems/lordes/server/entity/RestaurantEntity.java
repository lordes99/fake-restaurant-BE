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
@Table(name = "restaurant_entities")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = DBConstants.GENERIC_NAMES_OR_FILENAMES)
    private String name;
    @Column(length = DBConstants.GENERIC_DESCRIPTIONS)
    private String description;

    @Column
    private String thumbnail;

    @Column(columnDefinition = "text[]")
    private List<String> photos;

    @Column
    private Double latitude;
    @Column
    private Double longitude;

    @CreatedDate
    @Column(columnDefinition= DBConstants.COLUMN_DEFINITION_TIMESTAMPZ)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private UserEntity ownerUser;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviews;

    @Version
    private Long version;
}
