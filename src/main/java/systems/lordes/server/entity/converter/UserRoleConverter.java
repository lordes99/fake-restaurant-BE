package systems.lordes.server.entity.converter;

import systems.lordes.server.data.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(UserRole uri) {
        if (uri == null) {
            return null;
        }
        return uri.getValue();
    }

    @Override
    public UserRole convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        return UserRole.getByValue(s);
    }

}
