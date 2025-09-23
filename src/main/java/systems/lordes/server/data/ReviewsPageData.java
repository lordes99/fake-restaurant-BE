package systems.lordes.server.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import systems.lordes.server.gen.api.Review;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReviewsPageData extends PageData {
    private List<Review> reviews;
}
