package systems.lordes.server.utils;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import systems.lordes.server.entity.RestaurantEntity;
import systems.lordes.server.gen.api.RestaurantCharacteristic;

import java.util.ArrayList;
import java.util.List;

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

    public static Specification<RestaurantEntity> hasCharacteristics(List<RestaurantCharacteristic> types) {
        return (root, query, criteriaBuilder) -> {
            if (types == null || types.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            for (RestaurantCharacteristic type : types) {
                predicates.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.function(
                                        "jsonb_exists",
                                        Boolean.class,
                                        root.get("characteristics"),
                                        criteriaBuilder.literal(type.toString())
                                ),
                                true
                        )
                );
            }

//            in AND
//            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

            // in OR
             return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }


}
