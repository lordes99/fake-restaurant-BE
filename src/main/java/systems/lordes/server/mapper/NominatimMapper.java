package systems.lordes.server.mapper;

import org.mapstruct.*;
import systems.lordes.server.data.NominatimAddressResponseData;
import systems.lordes.server.gen.api.Address;
import systems.lordes.server.gen.api.NominatimForwardSearchResponse;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface NominatimMapper {

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

    }
}
