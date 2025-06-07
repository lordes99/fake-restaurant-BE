package systems.lordes.server.data;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum UserRole {
    ADMIN(0, false, "ADMIN"),
    USER(100, false, "USER")
    ;

    private final int hierarchy; // Lower is more important
    private final boolean internal; // Internal are not visible in API and assignable
    private final String value;
    private final Set<Permission> permissions;

    UserRole(int hierarchy, boolean internal, String value, Permission... permissions) {
        this(hierarchy, internal, value, List.of(), Arrays.asList(permissions));
    }

    UserRole(int hierarchy, boolean internal, String value, UserRole extendSource, Permission... permissionArray) {
        this(hierarchy, internal, value, List.of(extendSource), Arrays.asList(permissionArray));
    }

    public static UserRole getByValue(String value) {
        for (UserRole role : values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Can't find role with value: " + value);
    }

    UserRole(int hierarchy, boolean internal, String value, List<UserRole> extendSource, List<Permission> permissions) {
        this.hierarchy = hierarchy;
        this.internal = internal;
        this.value = value;
        Set<Permission> permissionSet = extendSource.stream()
                .flatMap(e -> e.getPermissions().stream())
                .collect(Collectors.toSet());

        permissionSet.addAll(permissions);
        this.permissions = permissionSet;
    }

    public boolean isMoreOrEqualPrivilegedThan(UserRole role) {
        return hierarchy <= role.hierarchy;
    }

}
