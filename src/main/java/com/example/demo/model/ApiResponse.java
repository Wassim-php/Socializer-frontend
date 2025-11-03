package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {
    private String message;
    private T data;
    private boolean state;

    public ApiResponse() {}

    public ApiResponse(
            @JsonProperty("message") String message,
            @JsonProperty("data") T data,
            @JsonProperty("state") boolean state
    ) {
        this.message = message;
        this.data = data;
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public boolean isState() {
        return state;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
