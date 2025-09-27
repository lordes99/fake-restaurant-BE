package systems.lordes.server.data;

import lombok.Data;
import systems.lordes.server.gen.api.Role;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class UserData {
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private Role role;
    private String password;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
