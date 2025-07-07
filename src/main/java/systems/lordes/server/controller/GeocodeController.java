package systems.lordes.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import systems.lordes.server.data.NominatimAddressResponseData;
import systems.lordes.server.gen.api.Coordinate;
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
        List<NominatimAddressResponseData> nominatimResponses = this.nominatimService.forwardGeocodeSearch(nominatimForwardSearchRequest);
        return ResponseEntity.ok(nominatimMapper.toApi(nominatimResponses));
    }

    @Override
    public ResponseEntity<NominatimForwardSearchResponse> reverseGeocodeSearch(Coordinate coordinate) {
        NominatimAddressResponseData nominatimPointData = this.nominatimService.reverseGeocode(coordinate);
        return ResponseEntity.ok(nominatimMapper.toApi(nominatimPointData));
    }
}
