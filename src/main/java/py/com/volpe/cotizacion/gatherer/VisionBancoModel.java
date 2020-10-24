package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
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
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    static class VisionBranches {
        private List<Item> items;
    }


    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Contact {
        private String type;
        private String number;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
