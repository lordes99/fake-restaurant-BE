package systems.lordes.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import systems.lordes.server.config.FakeRestaurantProperties;
import systems.lordes.server.data.NominatimPointData;
import systems.lordes.server.gen.api.Coordinate;

import java.util.Locale;

@Service
@Slf4j
public class NominatimService {
//    https://nominatim.org/
    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public NominatimService(
            RestTemplate restTemplate,
            FakeRestaurantProperties  fakeRestaurantProperties
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = fakeRestaurantProperties.getNominatimUrl();
    }


    public NominatimPointData reverseGeocode(Coordinate coordinate) {
        String url = String.format(
                Locale.US,//serve per mettere il punto invece della virgoal
                "%s/reverse?format=json&lat=%f&lon=%f",
                baseUrl,
                coordinate.getLatitude(),
                coordinate.getLongitude()
        );

        log.info("Reverse geocode request url: {}", url);

        return restTemplate.getForObject(url, NominatimPointData.class);
    }
}
