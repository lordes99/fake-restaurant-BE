package systems.lordes.server.service;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import systems.lordes.server.data.ReviewsPageData;
import systems.lordes.server.entity.RestaurantEntity;
import systems.lordes.server.entity.ReviewEntity;
import systems.lordes.server.entity.UserEntity;
import systems.lordes.server.gen.api.Review;
import systems.lordes.server.gen.api.VoteType;
import systems.lordes.server.mapper.ReviewMapper;
import systems.lordes.server.repository.ReviewRepository;

import java.util.UUID;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final EntityManager entityManager;

    public ReviewService(
            ReviewRepository reviewRepository,
            ReviewMapper reviewMapper,
            EntityManager entityManager
    ) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.entityManager = entityManager;
    }

    public UUID createReview(UUID restaurantId, Review review, UserEntity loggedUser) {
        ReviewEntity reviewEntity = reviewMapper.toEntity(review);
        RestaurantEntity restaurant = entityManager.getReference(RestaurantEntity.class, restaurantId);

        reviewEntity.setOwnerUser(loggedUser);
        reviewEntity.setRestaurant(restaurant);

        return reviewRepository.save(reviewEntity).getId();
    }

    @Transactional(readOnly = true)
    public ReviewsPageData findReviewsByRestaurantId(UUID restaurantId, PageRequest pageRequest) {
        return reviewMapper.toData(reviewRepository.findAllByRestaurant_Id(restaurantId, pageRequest));
    }

    @Transactional(readOnly = true)
    public ReviewsPageData findReviewsByOwnerId(UUID ownerId, PageRequest pageRequest) {
        return reviewMapper.toData(reviewRepository.findAllByOwnerUser_Id(ownerId, pageRequest));
    }

    public boolean deleteReview(UUID reviewId, UUID ownerId) {
        if (this.reviewRepository.existsByIdAndOwnerUserId(reviewId, ownerId)) {
            this.reviewRepository.deleteById(reviewId);
            return true;
        }
        return false;
    }

    public boolean updateVoteReview(UUID reviewId, UUID ownerId, VoteType voteType) {
        ReviewEntity reviewEntity = this.reviewRepository.findById(reviewId).orElseGet(null);
        String ownerIdString = ownerId.toString();

        if (reviewEntity != null) {
            if (VoteType.UP.equals(voteType)) {
                reviewEntity.getDownVoteIds().remove(ownerIdString);
                reviewEntity.getUpVoteIds().add(ownerIdString);
            } else if (VoteType.DOWN.equals(voteType)) {
                reviewEntity.getUpVoteIds().remove(ownerIdString);
                reviewEntity.getDownVoteIds().add(ownerIdString);
            }

            this.reviewRepository.save(reviewEntity);
            return true;
        }
        return false;
    }
}
