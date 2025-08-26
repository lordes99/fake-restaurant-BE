package systems.lordes.server.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RestaurantService {
    private static final String MINIO_BUCKET_PUBLIC = "public";
    private static final String MINIO_BUCKET_RESTAURANTS = "restaurants";
    private final RestaurantMapper restaurantMapper;
    private final RestaurantRepository restaurantRepository;
    private final StorageService storageService;

    public RestaurantService(
            RestaurantMapper restaurantMapper,
            RestaurantRepository restaurantRepository,
            StorageService storageService
    ) {
        this.restaurantMapper = restaurantMapper;
        this.restaurantRepository = restaurantRepository;
        this.storageService = storageService;
    }


    public UUID createRestaurant(Restaurant restaurant, List<MultipartFile> photos, UserEntity loggedUser) {
        RestaurantEntity restaurantEntity = restaurantMapper.toEntity(restaurant);

        restaurantEntity.setOwnerUser(loggedUser);

        RestaurantEntity savedRestaurant = restaurantRepository.save(restaurantEntity);

        addPhotos(savedRestaurant, photos);

        return restaurantRepository.save(savedRestaurant).getId();
    }

    @Transactional(readOnly = true)
    public RestaurantsPageData findRestaurants(PageRequest pageRequest, String search) {
        Specification<RestaurantEntity> spec = RestaurantSpecifications.searchByKeyword(search);
        return restaurantMapper.toData(restaurantRepository.findAll(spec, pageRequest));
    }

    public Optional<Restaurant> findRestaurant(UUID restaurantId) {
        return restaurantRepository.findByIdWithDetails(restaurantId)
                .map(restaurantMapper::toApi);
    }

    private void addPhotos(RestaurantEntity restaurantEntity, List<MultipartFile> photos) {
        if (photos == null || photos.isEmpty()) return;

        List<String> photosUrl = new ArrayList<>(photos.size());

        UUID restaurantId = restaurantEntity.getId();

        for (MultipartFile photo : photos) {
            try {
//                String filename = photo.getOriginalFilename();
                String filename = UUID.randomUUID().toString();
                String objectPath = MINIO_BUCKET_PUBLIC + "/" + MINIO_BUCKET_RESTAURANTS + "/" + restaurantId + "/" + filename;

                storageService.uploadResource(
                        toResource(photo),
                        null,
                        objectPath,
                        null,
                        MediaType.valueOf(photo.getContentType())
                );

                String url = String.format("%s/%s/%s",
                        storageService.getEndpoint(),
                        storageService.getDefaultBucket(),
                        objectPath
                );

                photosUrl.add(url);

            } catch (Exception e) {
                this.restaurantRepository.deleteById(restaurantId);
                throw new RuntimeException("Error upload photo for restaurant: " + restaurantId, e);
            }
        }

        restaurantEntity.setPhotos(photosUrl);
    }

    private Resource toResource(MultipartFile file) throws IOException {
        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
    }
}
