package com.bay.data;

public class Result {
    private final Object data;
    private final String message;
    private final int statusCode;
    private final boolean isSuccess;

    public Result(Object data, String message, int statusCode, boolean isSuccess) {
        this.data = data;
        if (data != null) {
            this.message = message;
            this.statusCode = statusCode;
            this.isSuccess = isSuccess;
        } else {
            this.message = message;
            this.statusCode = 400;
            this.isSuccess = false;
        }
    }

    public String getData() {
        if (data == null) return null;
        return data.toString();
    }

    public String getMessage() {
        return "\"" + message + "\"";
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
