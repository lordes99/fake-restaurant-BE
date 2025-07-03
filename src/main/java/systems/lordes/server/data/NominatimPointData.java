package systems.lordes.server.data;

import lombok.Data;

@Data
public class NominatimPointData {
    private String display_name;
    private AddressData address;
    private double lat;
    private double lon;

    @Data
    public class AddressData {
        private String road;
        private String town;
        private String county;
        private String country;
        private String postcode;
    }
}
