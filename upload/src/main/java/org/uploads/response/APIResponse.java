package org.uploads.response;

public class APIResponse {
    Object data;
    String message;
    String error;

    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
