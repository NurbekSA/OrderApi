package amanzat.com.persistence.model.dto;

public enum Status {
    CREATED("CREATED"),
    PAID("PAID"),
    CANCELLED("CANCELLED"),
    DELETED("DELETED"),
    ARCHIVATED("ARCHIVATED"),
    EXPIRED("EXPIRED");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Status fromValue(String value) {
        for (Status status : Status.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
