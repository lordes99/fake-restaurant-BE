package systems.lordes.server.config;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("fake-restaurant.config")
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FakeRestaurantProperties {

    @NotNull
    private String secretJwtKey;
}
