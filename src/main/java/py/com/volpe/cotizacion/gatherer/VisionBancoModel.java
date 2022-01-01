package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

/**
 * @author Arturo Volpe
 * @since 9/21/18
 */
public final class VisionBancoModel {

    private VisionBancoModel() {
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    static class VisionBranches {
        private List<Item> items;
    }


    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Item {
        private long id;
        private String title;
        private String urlTitle;
        private Gmap gmap;
        private List<Contact> contact;
        private List<Hour> hours;
        private String address;
        private String city;
        private List<Service> services;

    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Contact {
        private String type;
        private String number;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Gmap {
        private double latitude;
        private double longitude;

    }


    @Data
    static class Hour {
        private String days;
        private String open;
        private String close;

    }


    @Data
    public static class Service {
        private String name;
        private String icon;

    }

}
