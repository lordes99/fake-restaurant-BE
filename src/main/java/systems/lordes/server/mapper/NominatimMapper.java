package systems.lordes.server.mapper;

import org.mapstruct.*;
import systems.lordes.server.data.NominatimPointData;
import systems.lordes.server.gen.api.Address;
import systems.lordes.server.gen.api.NominatimPoint;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface NominatimMapper {

    NominatimPoint toApi(NominatimPointData nominatimPointData);

    @Mapping(target = "country", source = "country")
    @Mapping(target = "city", source = "town")
    @Mapping(target = "street", source = "road")
    @Mapping(target = "postalCode", source = "postcode")
    @Mapping(target = "latitude", ignore = true)
    @Mapping(target = "longitude", ignore = true)
    @Mapping(target = "displayName", ignore = true)
    Address toApi(NominatimPointData.AddressData addressData);

    @AfterMapping
    default void afterMapping(NominatimPointData source, @MappingTarget NominatimPoint target) {
        double lat = source.getLat();
        double lon = source.getLon();
        Address address = target.getAddress();
        if (address != null) {
            address
                .latitude(lat)
                .longitude(lon)
                .displayName(source.getDisplay_name());
            if (address.getCity() == null) {
                address.city(source.getAddress().getCounty());
            }
        }

    }
}
