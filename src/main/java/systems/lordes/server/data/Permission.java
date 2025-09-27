package systems.lordes.server.data;

import org.springframework.security.core.GrantedAuthority;

public enum Permission implements GrantedAuthority {
    VIEW()
    ;


    Permission() {
    }

    @Override
    public String getAuthority() {
        return this.name();
    }


}
