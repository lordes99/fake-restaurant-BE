package systems.lordes.server.utils;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import systems.lordes.server.entity.UserEntity;

public class UserSpecifications {

    public static Specification<UserEntity> searchByKeyword(String search) {
        return (Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }

            String likeSearch = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), likeSearch),
                    cb.like(cb.lower(root.get("surname")), likeSearch),
                    cb.like(cb.lower(root.get("email")), likeSearch)
            );
        };
    }
}
