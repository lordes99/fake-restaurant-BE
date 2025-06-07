package systems.lordes.server.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import systems.lordes.server.data.CustomUserDetails;

public class ControllerUtils {

    public final static String PREFIX_API_V1 = "/api/v1";
    private static final Integer FIRST_PAGE = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 20;
    private static final Integer MAX_PAGE_SIZE = 50;

    public static CustomUserDetails getPrincipalSession() {
        return (CustomUserDetails) getPrincipal().getPrincipal();
    }

    public static Authentication getPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static PageRequest pageOf(Integer page, Integer size) {
        if (page == null) {
            page = FIRST_PAGE;
        } else if (page < FIRST_PAGE) {
            throw new IllegalArgumentException("page must be greater than or equal to 0");
        }
        if (size == null) {
            size = DEFAULT_PAGE_SIZE;
        } else if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        } if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than or equal to 0");
        }

        return PageRequest.of(page, size);
    }
}
