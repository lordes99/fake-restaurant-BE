package systems.lordes.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import systems.lordes.server.data.ReviewsPageData;
import systems.lordes.server.entity.ReviewEntity;
import systems.lordes.server.gen.api.Review;
import systems.lordes.server.gen.api.ReviewsPage;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {UserMapper.class}
)
public interface ReviewMapper {

    @Mapping(target = "ownerUser.password", ignore = true)
    @Mapping(target = "restaurantId", ignore = true) //ToDo: da rimuovere
    Review toApi(ReviewEntity reviewEntity);

    @Mapping(target = "ownerUser.restaurants", ignore = true)
    @Mapping(target = "ownerUser.version", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "ownerUser.passwordHash", ignore = true)
    @Mapping(target = "restaurant", ignore = true) //ToDo: da rimuovere
    ReviewEntity toEntity(Review review);

    List<Review> toApis(List<ReviewEntity> reviewEntities);

    default ReviewsPageData toData(Page<ReviewEntity> reviewEntities) {
        ReviewsPageData reviewsPageData = new ReviewsPageData();
        reviewsPageData.setReviews(toApis(reviewEntities.getContent()));
        systems.lordes.server.gen.api.Page page = new systems.lordes.server.gen.api.Page();
        page.setTotalElements(reviewEntities.getTotalElements());
        page.setTotalPages((long) reviewEntities.getTotalPages());
        page.setSize((long) reviewEntities.getSize());
        page.setNumber((long) reviewEntities.getNumber());
        reviewsPageData.setPage(page);

        return reviewsPageData;
    }

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

    @Mapping(target = "content", source = "reviews")
    ReviewsPage toApis(ReviewsPageData reviews);
}
