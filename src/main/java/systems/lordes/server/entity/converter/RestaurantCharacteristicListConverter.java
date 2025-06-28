package systems.lordes.server.entity.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import systems.lordes.server.gen.api.RestaurantCharacteristic;

import java.util.List;

@Converter
public class RestaurantCharacteristicListConverter implements AttributeConverter<List<RestaurantCharacteristic>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<RestaurantCharacteristic> attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting characteristics list to JSON", e);
        }
    }

    @Override
    public List<RestaurantCharacteristic> convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, new TypeReference<List<RestaurantCharacteristic>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Error reading characteristics list from JSON", e);
        }
    }
}
