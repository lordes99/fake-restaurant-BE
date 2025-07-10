package systems.lordes.server.mapper;

import jakarta.validation.Valid;
import org.mapstruct.*;
import systems.lordes.server.data.NominatimAddressResponseData;
import systems.lordes.server.gen.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface NominatimMapper {

    @Mapping(target = "boundingBox", ignore = true)
    @Mapping(target = "geoJson", ignore = true)
    NominatimForwardSearchResponse toApi(NominatimAddressResponseData nominatimPointData);

    List<NominatimForwardSearchResponse> toApi(List<NominatimAddressResponseData>  nominatimPointData);

    @Mapping(target = "country", source = "country")
    @Mapping(target = "city", source = "town")
    @Mapping(target = "street", source = "road")
    @Mapping(target = "postalCode", source = "postcode")
    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    @Mapping(target = "displayName", ignore = true)
    Address toApi(NominatimAddressResponseData.AddressData addressData);

    @AfterMapping
    default void afterMapping(NominatimAddressResponseData source, @MappingTarget NominatimForwardSearchResponse target) {
        double lat = source.getLat();
        double lon = source.getLon();
        Address address = target.getAddress();
        if (address != null) {
            address
                .latitude(lat)
                .longitude(lon)
                .displayName(source.getDisplayName());
            if (address.getCity() == null) {
                address.city(source.getAddress().getCounty());
            }
        }

        mapBoundingBox(source.getBoundingbox(), target);

        mapPolygons(source.getGeoJson(), target);
    }

    private static void mapBoundingBox(List<Double> boundingBox, NominatimForwardSearchResponse target) {
        if (boundingBox != null) {
            Coordinate southWestCoordinate = new Coordinate()
                    .latitude(boundingBox.getFirst())
                    .longitude(boundingBox.get(2));

            Coordinate northEastCoordinate = new Coordinate()
                    .latitude(boundingBox.get(1))
                    .longitude(boundingBox.getLast());

            List<Coordinate> boundingBoxCoordinates = Arrays.asList(northEastCoordinate, southWestCoordinate);
            target.setBoundingBox(boundingBoxCoordinates);
        }
    }

    private static void mapPolygons(NominatimAddressResponseData.GeoJsonData geoJsonSrc, NominatimForwardSearchResponse target) {
        if (geoJsonSrc != null) {
            target.setGeoJson(new GeoJson().type(GeoJsonType.fromValue(geoJsonSrc.getType())));

            target.getGeoJson()
                    .setPolygons(
                            mapMultiPolygon((List<?>) geoJsonSrc.getCoordinates(), target.getGeoJson().getType())
                    );
        }
    }

    private static Coordinate mapCoordinates(List<?> coordinatesSrc) {
        return new Coordinate()
                .longitude((Double) coordinatesSrc.get(0))
                .latitude((Double)coordinatesSrc.get(1));
    }

    static LineString mapLineString(List<?> coordinatesSrc, @Valid GeoJsonType type) {
        if (GeoJsonType.LINE_STRING.equals(type)) {
            ArrayList<Coordinate> coordinates = new ArrayList<>();

            for (Object coordinateObj : coordinatesSrc) {
                List<?> latLng = (List<?>) coordinateObj;
                coordinates.add(mapCoordinates(latLng));
            }

            return new LineString().polygonCoordinates(coordinates);
        }

        return new LineString().polygonCoordinates(List.of(
                mapCoordinates(coordinatesSrc)
        ));
    }

    private static Polygon mapPolygon(List<?> coordinates, @Valid GeoJsonType type) {
        if (GeoJsonType.POLYGON.equals(type)) {
            ArrayList<LineString> lineStrings = new ArrayList<>();

            for (Object coordinateObj : coordinates) {
                List<?> lineString = (List<?>) coordinateObj;
                lineStrings.add(mapLineString(lineString, GeoJsonType.LINE_STRING));
            }

            return new Polygon().lineStrings(lineStrings);
        }

        return new Polygon()
                .lineStrings(List.of(
                        mapLineString(coordinates, type)
                ));
    }

    private static MultiPolygon mapMultiPolygon(List<?> coordinates, @Valid GeoJsonType type) {
        if (GeoJsonType.MULTI_POLYGON.equals(type)) {
            ArrayList<Polygon> polygonsTarget = new ArrayList<>();

            for (Object polygonObj : coordinates) {
                List<?> polygons = (List<?>) polygonObj;
                polygonsTarget.add(mapPolygon(polygons, GeoJsonType.POLYGON));
            }

            return new MultiPolygon().multiPolygons(polygonsTarget);
        }

        return new MultiPolygon()
                .multiPolygons(List.of(
                        mapPolygon(coordinates, type)
                ));
    }
}
