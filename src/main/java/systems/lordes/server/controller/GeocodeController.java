package systems.lordes.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import systems.lordes.server.data.NominatimAddressResponseData;
import systems.lordes.server.exception.NominatimBadMappingException;
import systems.lordes.server.gen.api.Coordinate;
import systems.lordes.server.gen.api.Error;
import systems.lordes.server.gen.api.NominatimForwardSearchRequest;
import systems.lordes.server.gen.api.NominatimForwardSearchResponse;
import systems.lordes.server.gen.controller.GeoApi;
import systems.lordes.server.mapper.NominatimMapper;
import systems.lordes.server.service.NominatimService;
import systems.lordes.server.utils.ControllerUtils;

import java.util.List;

@RestController
@RequestMapping(ControllerUtils.PREFIX_API_V1)
public class GeocodeController implements GeoApi {
    private final NominatimService nominatimService;
    private final NominatimMapper nominatimMapper;

    @Autowired
    public GeocodeController(
            NominatimService nominatimService,
            NominatimMapper nominatimMapper
    ) {
        this.nominatimService = nominatimService;
        this.nominatimMapper = nominatimMapper;
    }

    @Override
    public ResponseEntity<List<NominatimForwardSearchResponse>> forwardGeocodeSearch(NominatimForwardSearchRequest nominatimForwardSearchRequest) {
        List<NominatimAddressResponseData> nominatimResponses;
        try {
            nominatimResponses = this.nominatimService.forwardGeocodeSearch(nominatimForwardSearchRequest);
        } catch (NominatimBadMappingException e) {
            systems.lordes.server.gen.api.Error error = new Error().message("Geocode: failed forward search mapping");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        if (nominatimResponses == null) {
            systems.lordes.server.gen.api.Error error = new Error().message("Geocode: failed forward search");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        return ResponseEntity.ok(nominatimMapper.toApi(nominatimResponses));
    }

    @Override
    public ResponseEntity<NominatimForwardSearchResponse> reverseGeocodeSearch(Coordinate coordinate) {
        NominatimAddressResponseData nominatimPointData;

        try {
            nominatimPointData = this.nominatimService.reverseGeocode(coordinate);
        } catch (NominatimBadMappingException e) {
            systems.lordes.server.gen.api.Error error = new Error().message("Geocode: failed backward search mapping");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        if (nominatimPointData == null) {
            systems.lordes.server.gen.api.Error error = new Error().message("Geocode: failed backward search");
            return (ResponseEntity) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        return ResponseEntity.ok(nominatimMapper.toApi(nominatimPointData));
    }
}
