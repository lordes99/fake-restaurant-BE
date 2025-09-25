package systems.lordes.server.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import systems.lordes.server.entity.RestaurantEntity;

public class RestaurantSpecifications {

    public static Specification<RestaurantEntity> searchByKeyword(String search) {
        return (Root<RestaurantEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }

            String likeSearch = "%" + search.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), likeSearch),
                cb.like(cb.lower(root.get("description")), likeSearch)
            );
        };
    }
}
