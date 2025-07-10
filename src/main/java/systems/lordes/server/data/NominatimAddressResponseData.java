package systems.lordes.server.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NominatimAddressResponseData {
    @JsonProperty("place_id")
    private long placeId;

    private String licence;

    @JsonProperty("osm_type")
    private String osmType;

    @JsonProperty("osm_id")
    private long osmId;

    private double lat;
    private double lon;

    @JsonProperty("class")
    private String clazz;

    private String type;

    @JsonProperty("place_rank")
    private int placeRank;

    private double importance;

    private String addresstype;

    private String name;

    @JsonProperty("display_name")
    private String displayName;

    private List<Double> boundingbox;

    @JsonProperty("geojson")
    private GeoJsonData geoJson;

    private AddressData address;

    @Data
    public static class AddressData {
        private String road;
        private String town;
        private String county;
        private String country;
        private String postcode;
    }

    @Data
    public static class GeoJsonData {
        private String type;
        private Object coordinates;
    }
}
