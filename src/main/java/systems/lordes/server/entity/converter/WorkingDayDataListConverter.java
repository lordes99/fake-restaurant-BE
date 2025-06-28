package systems.lordes.server.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import systems.lordes.server.data.WorkingDayData;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkingDayDataListConverter implements AttributeConverter<List<WorkingDayData>, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<WorkingDayData> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Could not serialize working hours list to JSON", e);
        }
    }

    @Override
    public List<WorkingDayData> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Could not deserialize working hours JSON to list", e);
        }
    }
}
