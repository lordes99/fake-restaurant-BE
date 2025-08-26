package systems.lordes.server.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import systems.lordes.server.gen.api.Restaurant;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class RestaurantsPageData extends PageData {
    private List<Restaurant> restaurants;
}
