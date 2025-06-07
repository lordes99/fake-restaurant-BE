package systems.lordes.server.data;

import lombok.Data;
import systems.lordes.server.gen.api.InstantDateTimeRO;
import systems.lordes.server.gen.api.Role;

import java.util.UUID;

@Data
public class UserData {
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private Role role;
    private String password;
    private InstantDateTimeRO createdAt;
    private InstantDateTimeRO updatedAt;
}
