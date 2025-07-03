package systems.lordes.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import systems.lordes.server.data.NominatimPointData;
import systems.lordes.server.gen.api.Coordinate;
import systems.lordes.server.gen.api.NominatimPoint;
import systems.lordes.server.gen.controller.GeoApi;
import systems.lordes.server.mapper.NominatimMapper;
import systems.lordes.server.service.NominatimService;
import systems.lordes.server.utils.ControllerUtils;

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
    public ResponseEntity<NominatimPoint> reverseGeocodeSearch(Coordinate coordinate) {
        NominatimPointData nominatimPointData = this.nominatimService.reverseGeocode(coordinate);
        return ResponseEntity.ok(nominatimMapper.toApi(nominatimPointData));
    }
}
