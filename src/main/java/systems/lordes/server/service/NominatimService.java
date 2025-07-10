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
import systems.lordes.server.data.NominatimAddressResponseData;
import systems.lordes.server.exception.NominatimBadMappingException;
import systems.lordes.server.gen.api.Coordinate;
import systems.lordes.server.gen.api.NominatimForwardSearchRequest;

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

    public List<NominatimAddressResponseData> forwardGeocodeSearch(NominatimForwardSearchRequest nominatimForwardSearchRequest) {
        URI url = buildUrl(nominatimForwardSearchRequest);
//        log.info("Forward geocode request url: {}", url);

        try {
            return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<NominatimAddressResponseData>>() {}
            ).getBody();
        } catch (Exception e) {
            throw new NominatimBadMappingException(String.format("Error forward geocode request for url %s", url), e);
        }
    }


    public NominatimAddressResponseData reverseGeocode(Coordinate coordinate) {
        URI url = buildUrl(coordinate);
//        log.info("Reverse geocode request url: {}", url);

        try {
            return restTemplate.getForObject(url, NominatimAddressResponseData.class);
        } catch (Exception e) {
            throw new NominatimBadMappingException(String.format("Error reverse geocode request for url %s", url), e);
        }
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

    private URI buildUrl(NominatimForwardSearchRequest nominatimForwardSearchRequest) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(NOMINATIM_URL + FORWARD_SEARCH)
                .queryParam("q", nominatimForwardSearchRequest.getAddress())
                .queryParam("format", "json")
                .queryParam("accept-language", "it")
                .queryParam("addressdetails", "1")
                .queryParam("polygon_geojson", "1");

        Integer limit = nominatimForwardSearchRequest.getLimit();
        if (limit != null && limit > 0) {
            builder.queryParam("limit", limit);
        }

        return builder.build().encode().toUri();
    }
}
