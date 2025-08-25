package systems.lordes.server.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import systems.lordes.server.data.RestaurantsPageData;
import systems.lordes.server.entity.RestaurantEntity;
import systems.lordes.server.entity.UserEntity;
import systems.lordes.server.gen.api.Restaurant;
import systems.lordes.server.mapper.RestaurantMapper;
import systems.lordes.server.repository.RestaurantRepository;
import systems.lordes.server.utils.RestaurantSpecifications;

import java.util.List;
import java.util.UUID;

@Service
public class RestaurantService {
    private final RestaurantMapper restaurantMapper;
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(
            RestaurantMapper restaurantMapper,
            RestaurantRepository restaurantRepository
    ) {
        this.restaurantMapper = restaurantMapper;
        this.restaurantRepository = restaurantRepository;
    }


    public UUID createRestaurant(Restaurant restaurant, List<MultipartFile> photos, UserEntity loggedUser) {
        RestaurantEntity restaurantEntity = restaurantMapper.toEntity(restaurant);

        restaurantEntity.setOwnerUser(loggedUser);

        RestaurantEntity savedRestaurant = restaurantRepository.save(restaurantEntity);


        return savedRestaurant.getId();
    }

    @Transactional(readOnly = true)
    public RestaurantsPageData findRestaurants(PageRequest pageRequest, String search) {
        Specification<RestaurantEntity> spec = RestaurantSpecifications.searchByKeyword(search);
        return restaurantMapper.toData(restaurantRepository.findAll(spec, pageRequest));
    }
}
