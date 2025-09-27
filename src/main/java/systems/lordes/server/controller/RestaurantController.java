package systems.lordes.server.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import systems.lordes.server.data.CustomUserDetails;
import systems.lordes.server.data.UserRole;
import systems.lordes.server.entity.UserEntity;
import systems.lordes.server.gen.api.Error;
import systems.lordes.server.gen.api.Restaurant;
import systems.lordes.server.gen.api.RestaurantsPage;
import systems.lordes.server.gen.controller.RestaurantApi;
import systems.lordes.server.mapper.RestaurantMapper;
import systems.lordes.server.service.RestaurantService;
import systems.lordes.server.utils.ControllerUtils;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ControllerUtils.PREFIX_API_V1)
public class RestaurantController implements RestaurantApi {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;


    @Autowired
    public RestaurantController(
            RestaurantService restaurantService,
            RestaurantMapper restaurantMapper
    ) {
        this.restaurantService = restaurantService;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    public ResponseEntity<Void> restaurantIdDelete(UUID id) {
        UserEntity loggedUser = ControllerUtils.getPrincipalSession().getUser();
        if (restaurantService.deleteRestaurant(id, loggedUser.getId())) {
            return ResponseEntity.noContent().build();
        } else {
            Error error = new Error().message("Not found: restaurant not found");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @Override
    public ResponseEntity<Restaurant> restaurantIdGet(UUID id) {
        return restaurantService.findRestaurant(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                Error error = new Error().message("Not found: restaurant not found");
                return (ResponseEntity) ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            });
    }

    @Override
    public ResponseEntity<UUID> restaurantIdPut(UUID id, Restaurant restaurant, List<MultipartFile> photos) {
        CustomUserDetails userDetails = ControllerUtils.getPrincipalSession();
        UserEntity loggedUser = userDetails.getUser();

        if (loggedUser == null) {
            systems.lordes.server.gen.api.Error error = new Error().message("Access denied: unauthorized user");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        Restaurant existing = restaurantService.findRestaurant(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant with id " + id + " not found"));

        if (!existing.getOwnerUser().getId().equals(loggedUser.getId())) {
            systems.lordes.server.gen.api.Error error = new Error().message("Access denied: unauthorized user");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        if (restaurantService.updateRestaurant(id, restaurant, photos)) {
            return ResponseEntity.ok(id);
        }

        systems.lordes.server.gen.api.Error error = new Error().message("Not Update: restaurant not updated");
        return (ResponseEntity) ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(error);
    }

    @Override
    public ResponseEntity<RestaurantsPage> restaurantsGet(Integer page, Integer size, String search) {

        RestaurantsPage restaurants;
        if (page == null || size == null) {
            Error error = new Error().message("Filter Error: page or size is null");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        PageRequest pageRequest = ControllerUtils.pageOf(page - 1, size);
        restaurants = restaurantMapper.toApis(restaurantService.findRestaurants(pageRequest, search));
        return ResponseEntity.ok(restaurants);
    }

    @Override
    public ResponseEntity<UUID> restaurantsPost(Restaurant restaurant, List<MultipartFile> photos) {
        CustomUserDetails userDetails = ControllerUtils.getPrincipalSession();
        UserEntity loggedUser = userDetails.getUser();

        if (loggedUser == null) {
            systems.lordes.server.gen.api.Error error = new Error().message("Access denied: unauthorized user");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        UUID restaurantId = restaurantService.createRestaurant(restaurant, photos, loggedUser);

        return ResponseEntity.ok(restaurantId);
    }

    @Override
    public ResponseEntity<RestaurantsPage> userIdRestaurantsGet(UUID userId, Integer page, Integer size) {
        UserEntity loggedUser = ControllerUtils.getPrincipalSession().getUser();
        if (loggedUser != null && (loggedUser.getId().equals(userId) || UserRole.ADMIN.equals(loggedUser.getRole()))) {
            RestaurantsPage restaurants;
            if (page == null || size == null) {
                systems.lordes.server.gen.api.Error error = new Error().message("Filter Error: page or size is null");
                return (ResponseEntity) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            PageRequest pageRequest = ControllerUtils.pageOf(page - 1, size);
            restaurants = restaurantMapper.toApis(restaurantService.findRestaurantsByOwnerId(userId, pageRequest));

            return ResponseEntity.ok(restaurants);
        } else {
            Error error = new Error().message("Access denied: unauthorized user");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}
