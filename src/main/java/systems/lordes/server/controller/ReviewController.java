package systems.lordes.server.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import systems.lordes.server.data.CustomUserDetails;
import systems.lordes.server.data.UserRole;
import systems.lordes.server.entity.UserEntity;
import systems.lordes.server.gen.api.Error;
import systems.lordes.server.gen.api.Review;
import systems.lordes.server.gen.api.ReviewsPage;
import systems.lordes.server.gen.api.VoteRequest;
import systems.lordes.server.gen.controller.ReviewApi;
import systems.lordes.server.mapper.ReviewMapper;
import systems.lordes.server.service.ReviewService;
import systems.lordes.server.utils.ControllerUtils;

import java.util.UUID;

@RestController
@RequestMapping(ControllerUtils.PREFIX_API_V1)
public class ReviewController implements ReviewApi {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    public ReviewController(
            ReviewService reviewService,
            ReviewMapper reviewMapper
    ) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public ResponseEntity<UUID> reviewsPost(UUID restaurantId, Review review) {
        CustomUserDetails userDetails = ControllerUtils.getPrincipalSession();
        UserEntity loggedUser = userDetails.getUser();

        UUID createdReview = reviewService.createReview(restaurantId, review, loggedUser);

        return ResponseEntity.ok(createdReview);
    }

    @Override
    public ResponseEntity<ReviewsPage> userIdReviewsGet(UUID userId, Integer page, Integer size) {
        UserEntity loggedUser = ControllerUtils.getPrincipalSession().getUser();
        if (loggedUser != null && (loggedUser.getId().equals(userId) || UserRole.ADMIN.equals(loggedUser.getRole()))) {
            ReviewsPage reviews;
            if (page == null || size == null) {
                systems.lordes.server.gen.api.Error error = new Error().message("Filter Error: page or size is null");
                return (ResponseEntity) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            PageRequest pageRequest = ControllerUtils.pageOf(page - 1, size, null);
            reviews = reviewMapper.toApis(reviewService.findReviewsByOwnerId(userId, pageRequest));

            return ResponseEntity.ok(reviews);
        } else {
            Error error = new Error().message("Access denied: unauthorized user");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @Override
    public ResponseEntity<ReviewsPage> restaurantIdReviewsGet(UUID restaurantId, Integer page, Integer size) {
        ReviewsPage reviews;
        if (page == null || size == null) {
            Error error = new Error().message("Filter Error: page or size is null");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        PageRequest pageRequest = ControllerUtils.pageOf(page - 1, size, null);
        reviews = reviewMapper.toApis(reviewService.findReviewsByRestaurantId(restaurantId, pageRequest));
        return ResponseEntity.ok(reviews);
    }

    @Override
    public ResponseEntity<Void> reviewIdDelete(UUID reviewId) {
        UserEntity loggedUser = ControllerUtils.getPrincipalSession().getUser();

        if (reviewService.deleteReview(reviewId, loggedUser.getId())) {
            return ResponseEntity.noContent().build();
        } else {
            String message = String.format("Not found: review with id: %s not found", reviewId.toString());
            Error error = new Error().message(message);
            return (ResponseEntity) ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @Override
    public ResponseEntity<UUID> reviewIdPut(UUID reviewId, VoteRequest voteRequest) {
        UserEntity loggedUser = ControllerUtils.getPrincipalSession().getUser();

        if (reviewService.updateVoteReview(reviewId, loggedUser.getId(), voteRequest.getVoteType())) {
            return ResponseEntity.ok(reviewId);
        } else {
            Error error = new Error().message("Not found: restaurant not found");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
