package systems.lordes.server.service;

import io.minio.errors.*;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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

        List<String> photosUrl = restaurantEntity.getPhotos();

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

    public boolean updateRestaurant(UUID id, Restaurant newRestaurant, List<MultipartFile> photos) {
        RestaurantEntity oldRestaurant = restaurantRepository.findById(id).orElseGet(null);
        if (oldRestaurant == null) {
            return false;
        }

        oldRestaurant.setName(newRestaurant.getName());
        oldRestaurant.setDescription(newRestaurant.getDescription());
        oldRestaurant.setCharacteristics(newRestaurant.getCharacteristics());
        oldRestaurant.setAddress(newRestaurant.getAddress());
        oldRestaurant.setWorkingHours(newRestaurant.getWorkingHours());
        oldRestaurant.setPhotos(newRestaurant.getPhotos());

        if (photos != null && !photos.isEmpty()) {
            addPhotos(oldRestaurant, photos);
        }

        restaurantRepository.save(oldRestaurant);
        return true;
    }

    public boolean deleteRestaurant(UUID restaurantId, UUID ownerId) {
        if (this.restaurantRepository.existsByIdAndOwnerUser_Id(restaurantId, ownerId)) {
            try {
                String bucket = storageService.getDefaultBucket();
                String path = MINIO_BUCKET_PUBLIC + "/" + MINIO_BUCKET_RESTAURANTS + "/" + restaurantId;
                this.storageService.storageDelete(bucket, path, null);
                this.restaurantRepository.deleteById(restaurantId);
            } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                     NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                     InternalException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public RestaurantsPageData findRestaurantsByOwnerId(UUID ownerId, PageRequest pageRequest) {
        return restaurantMapper.toData(restaurantRepository.findAllByOwnerUser_Id(ownerId, pageRequest));
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
