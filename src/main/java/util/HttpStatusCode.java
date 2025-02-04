package util;

public enum HttpStatusCode {

    OK(200, "OK"),

    CREATED( 201, "Created"),
    BAD_REQUEST( 400, "Bad request"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal server error");

    private final int value;
    private final String reason;

    HttpStatusCode(int value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    public int getValue() {
        return value;
    }
}