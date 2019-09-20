package mandarin.utils;

public class BasicResponse<T> {
    private boolean status;
    private String message;
    private T data;

    public BasicResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public static <T> BasicResponse<T> ok() {
        return new BasicResponse<>(true, "Action performs successfully");
    }

    public static <T> BasicResponse<T> fail() {
        return new BasicResponse<>(false, "Failed to perform the specified action");
    }

    public boolean status() {
        return status;
    }

    public BasicResponse<T> status(boolean status) {
        this.status = status;
        return this;
    }

    public String message() {
        return message;
    }

    public BasicResponse<T> message(String message) {
        this.message = message;
        return this;
    }

    public T data() {
        return data;
    }

    public BasicResponse<T> data(T data) {
        this.data = data;
        return this;
    }
}
