package systems.lordes.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import systems.lordes.server.config.WorkingDayDataListConverter;
import systems.lordes.server.data.WorkingDayData;
import systems.lordes.server.entity.converter.RestaurantCharacteristicListConverter;
import systems.lordes.server.gen.api.Address;
import systems.lordes.server.gen.api.RestaurantCharacteristic;
import systems.lordes.server.gen.api.VoteType;

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

    @Column(columnDefinition = "text[]")
    private List<String> photos;

    @Convert(converter = RestaurantCharacteristicListConverter.class)
    @Column(columnDefinition = "jsonb")
    private List<RestaurantCharacteristic> characteristics;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Address address;

    @Convert(converter = WorkingDayDataListConverter.class)
    @Column(name = "working_hours", columnDefinition = "jsonb")
    private List<WorkingDayData> workingHours;

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
