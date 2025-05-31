package systems.lordes.server.entity;

import lombok.*;
import systems.lordes.server.data.UserRole;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_entities", indexes = {
//    @Index(name = "idx_userentities_sso", columnList = "ssoSubjectId", unique = true),
    @Index(name = "idx_userentities_email", columnList = "email", unique = true),
//    @Index(name = "idx_userentities_organization", columnList = "organizationId"),
})
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = DBConstants.GENERIC_NAMES_OR_FILENAMES)
    private String name;
    @Column(length = DBConstants.GENERIC_NAMES_OR_FILENAMES)
    private String surname;
    @Column(length = DBConstants.GENERIC_NAMES_OR_FILENAMES)
    private String email;

    @Column(length = 255, nullable = false)
    private String passwordHash;

    @CreatedDate
    @Column(columnDefinition= DBConstants.COLUMN_DEFINITION_TIMESTAMPZ)
    private Instant createdAt;
    @LastModifiedDate
    @Column(columnDefinition= DBConstants.COLUMN_DEFINITION_TIMESTAMPZ)
    private Instant updatedAt;

    @OneToMany(mappedBy = "ownerUser")
    private List<RestaurantEntity> restaurants;

    @Version
    private Long version;

}
