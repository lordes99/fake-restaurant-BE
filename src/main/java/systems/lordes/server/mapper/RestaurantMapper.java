package systems.lordes.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import systems.lordes.server.data.RestaurantsPageData;
import systems.lordes.server.entity.RestaurantEntity;
import systems.lordes.server.gen.api.Restaurant;
import systems.lordes.server.gen.api.RestaurantsPage;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
//        uses = {UserMapper.class, ReviewMapper.class}
        uses = {UserMapper.class}
)
public interface RestaurantMapper {

    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "ownerUser.password", ignore = true)
    Restaurant toApi(RestaurantEntity restaurantEntity);

    @Mapping(target = "ownerUser.restaurants", ignore = true)
    @Mapping(target = "ownerUser.version", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "ownerUser.passwordHash", ignore = true)
    RestaurantEntity toEntity(Restaurant restaurant);

    default OffsetDateTime map(Instant instant) {
        if (instant == null) {
            return null;
        }
        return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    default Instant map(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toInstant();
    }

    List<Restaurant> toApis(List<RestaurantEntity> restaurants);

    default RestaurantsPageData toData(Page<RestaurantEntity> restaurants) {
        RestaurantsPageData restaurantsPageData = new RestaurantsPageData();
        restaurantsPageData.setRestaurants(toApis(restaurants.getContent()));
        systems.lordes.server.gen.api.Page page = new systems.lordes.server.gen.api.Page();
        page.setTotalElements(restaurants.getTotalElements());
        page.setTotalPages((long) restaurants.getTotalPages());
        page.setSize((long) restaurants.getSize());
        page.setNumber((long) restaurants.getNumber());
        restaurantsPageData.setPage(page);

        return restaurantsPageData;
    }

    @Mapping(target = "content", source = "restaurants")
    RestaurantsPage toApis(RestaurantsPageData restaurants);
}
