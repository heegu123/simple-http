package myhttp.common.model;

public enum HttpStatus {
    CONTINUE(100, "Continue"),
    OK(200, "OK"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String reason;

    HttpStatus(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode(){
        return code;
    }

    public String getReason() {
        return reason;
    }

    public static HttpStatus fromCode(int code) {
        for (HttpStatus s : values()) {
            if (s.getCode() == code) return s;
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }
}
