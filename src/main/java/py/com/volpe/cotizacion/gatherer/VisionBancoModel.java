package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import py.com.volpe.cotizacion.AppException;

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
        private Days days;
        private String open;
        private String close;

    }


    public enum Days {
        DOMINGO, LUNES_VIERNES, SBADO;

        @JsonValue
        public String toValue() {
            switch (this) {
                case DOMINGO:
                    return "Domingo";
                case LUNES_VIERNES:
                    return "Lunes - Viernes";
                default:
                    return "S\u00e1bado";
            }
        }

        @JsonCreator
        public static Days forValue(String value) {
            if ("Domingo".equals(value)) return DOMINGO;
            if ("Lunes - Viernes".equals(value)) return LUNES_VIERNES;
            if ("S\u00e1bado".equals(value)) return SBADO;
            throw new AppException(500, "Cannot deserialize Days" + value);
        }
    }


    @Data
    public static class Service {
        private Name name;
        private Icon icon;

    }

    public enum Icon {
        ATM, SAC, TAUSER, TIMER;

        @JsonValue
        public String toValue() {
            switch (this) {
                case ATM:
                    return "atm";
                case SAC:
                    return "sac";
                case TAUSER:
                    return "tauser";
                case TIMER:
                    return "timer";
                default:
                    return null;
            }
        }

        @JsonCreator
        public static Icon forValue(String value) {
            if ("atm".equals(value)) return ATM;
            if ("sac".equals(value)) return SAC;
            if ("tauser".equals(value)) return TAUSER;
            if ("timer".equals(value)) return TIMER;
            throw new AppException(500, "Cannot deserialize Icon: " + value);
        }
    }


    public enum Name {
        ATENCIN_A_CLIENTE, ATM, HORARIO_EXTENDIDO, TAUSER;

        @JsonValue
        public String toValue() {
            switch (this) {
                case ATENCIN_A_CLIENTE:
                    return "Atenci\u00f3n a cliente";
                case ATM:
                    return "ATM";
                case HORARIO_EXTENDIDO:
                    return "Horario extendido";
                case TAUSER:
                    return "Tauser";
                default:
                    return null;
            }
        }

        @JsonCreator
        public static Name forValue(String value) {
            if ("Atenci\u00f3n a cliente".equals(value)) return ATENCIN_A_CLIENTE;
            if ("ATM".equals(value)) return ATM;
            if ("Horario extendido".equals(value)) return HORARIO_EXTENDIDO;
            if ("Tauser".equals(value)) return TAUSER;
            throw new AppException(500, "Cannot deserialize Name" + value);
        }
    }

}
