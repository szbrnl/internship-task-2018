package pl.codewise.internships;

public class Message {

    private final String userAgent;
    private final int errorCode;

    private final long time;

    public Message(String userAgent, int errorCode, long time) {
        this.userAgent = userAgent;
        this.errorCode = errorCode;
        this.time = time;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public long getTime() {
        return time;
    }
}
