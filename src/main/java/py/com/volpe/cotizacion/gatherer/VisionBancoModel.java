package py.com.volpe.cotizacion.gatherer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.IOException;
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

    public static class VisionBranches {
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
    public static class Hour {
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
                case SBADO:
                    return "S\u00e1bado";
            }
            return null;
        }

        @JsonCreator
        public static Days forValue(String value) throws IOException {
            if (value.equals("Domingo")) return DOMINGO;
            if (value.equals("Lunes - Viernes")) return LUNES_VIERNES;
            if (value.equals("S\u00e1bado")) return SBADO;
            throw new IOException("Cannot deserialize Days");
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
            }
            return null;
        }

        @JsonCreator
        public static Icon forValue(String value) throws IOException {
            if (value.equals("atm")) return ATM;
            if (value.equals("sac")) return SAC;
            if (value.equals("tauser")) return TAUSER;
            if (value.equals("timer")) return TIMER;
            throw new IOException("Cannot deserialize Icon");
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
            }
            return null;
        }

        @JsonCreator
        public static Name forValue(String value) throws IOException {
            if (value.equals("Atenci\u00f3n a cliente")) return ATENCIN_A_CLIENTE;
            if (value.equals("ATM")) return ATM;
            if (value.equals("Horario extendido")) return HORARIO_EXTENDIDO;
            if (value.equals("Tauser")) return TAUSER;
            throw new IOException("Cannot deserialize Name");
        }
    }

}
