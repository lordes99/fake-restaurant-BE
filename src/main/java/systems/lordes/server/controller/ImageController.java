package systems.lordes.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import systems.lordes.server.data.CustomUserDetails;
import systems.lordes.server.entity.UserEntity;
import systems.lordes.server.gen.api.Error;
import systems.lordes.server.gen.api.ImageValidationResponse;
import systems.lordes.server.gen.controller.ImageApi;
import systems.lordes.server.utils.ControllerUtils;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@RestController
@RequestMapping(ControllerUtils.PREFIX_API_V1)
public class ImageController implements ImageApi {
    @Override
    public ResponseEntity<ImageValidationResponse> imgValidateGet(String url) {
        CustomUserDetails userDetails = ControllerUtils.getPrincipalSession();
        UserEntity loggedUser = userDetails.getUser();

        if (loggedUser == null) {
            systems.lordes.server.gen.api.Error error = new Error().message("Access denied: unauthorized user");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            URL u = new URI(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.connect();

            String contentType = connection.getContentType();
            boolean isImage = contentType != null && contentType.startsWith("image/");
            ImageValidationResponse result = new ImageValidationResponse()
                    .isImage(isImage)
                    .contentType(contentType);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            systems.lordes.server.gen.api.Error error = new Error().message("Error: unchecked image validation");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

    }
}
