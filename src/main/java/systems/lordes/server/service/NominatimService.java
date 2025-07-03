package systems.lordes.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import systems.lordes.server.config.FakeRestaurantProperties;
import systems.lordes.server.data.NominatimPointData;
import systems.lordes.server.gen.api.Coordinate;

import java.net.URI;
import java.util.List;

@Service
@Slf4j
public class NominatimService {
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org";
    private static final String FORWARD_SEARCH = "/search";
    private static final String REVERSE_SEARCH = "/reverse";
    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public NominatimService(
            RestTemplateBuilder restTemplateBuilder,
            FakeRestaurantProperties  fakeRestaurantProperties
    ) {
        this.restTemplate = restTemplateBuilder
                .defaultHeader(HttpHeaders.USER_AGENT, "spring restaurant")
                .build();
        this.baseUrl = fakeRestaurantProperties.getNominatimUrl();
    }


    public NominatimPointData reverseGeocode(Coordinate coordinate) {
        URI url = buildUrl(coordinate);
        log.info("Reverse geocode request url: {}", url);

        return restTemplate.getForObject(url, NominatimPointData.class);
    }

    private URI buildUrl(Coordinate coordinate) {
        return UriComponentsBuilder
            .fromUriString(baseUrl + REVERSE_SEARCH)
            .queryParam("format", "json")
            .queryParam("lat", coordinate.getLatitude())
            .queryParam("lon", coordinate.getLongitude())
            .queryParam("accept-language", "it")
            .build()
            .encode()
            .toUri();
    }

    }
}
