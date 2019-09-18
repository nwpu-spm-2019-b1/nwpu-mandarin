package mandarin.utils;

public class StatusUtils<T> {

    private boolean status;
    private String message;
    private T data;

    public StatusUtils(boolean stu, String msg){
        this.status = stu;
        this.message = msg;
    }

    public static <T> StatusUtils<T> ok(){
        return new StatusUtils<>(true, "Action performs successfully");
    }

    public static <T> StatusUtils<T> fail(){
        return new StatusUtils<>(false, "Failed to perform the specified action");
    }

    public static <T> StatusUtils<T> fail(String msg){
        return new StatusUtils<>(false, msg);
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
