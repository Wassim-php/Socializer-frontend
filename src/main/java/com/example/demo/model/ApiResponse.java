package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {
    private String message;
    private T data;
    private boolean state;


    // Default constructor
    public ApiResponse() {}

    // Constructor used by Jackson to deserialize API responses
    public ApiResponse(
            @JsonProperty("message") String message,
            @JsonProperty("data") T data,
            @JsonProperty("state") boolean state
    ) {
        this.message = message;
        this.data = data;
        this.state = state;
    }

    // Get response message from API
    public String getMessage() {
        return message;
    }

    // Get data payload
    public T getData() {
        return data;
    }

    // Get success state
    public boolean isState() {
        return state;
    }

    // Set response message
    public void setMessage(String message) {
        this.message = message;
    }

    // Set data payload
    public void setData(T data) {
        this.data = data;
    }

    // Set success state
    public void setState(boolean state) {
        this.state = state;
    }
}
