package com.example.demo.api;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class UserClient extends ApiClient{

    public UserClient() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public ApiResponse<List<User>> fetchUsers() throws IOException, InterruptedException {
        HttpRequest req = buildAuthorizedRequest("/api/users/all", "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<List<User>> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());
        return apiResponse;
    }

    public ApiResponse<User> getUserById(Long id) throws  IOException, InterruptedException {
        HttpRequest req = buildAuthorizedRequest("/api/users/" + id, "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<User> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );
        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<User> createUser(User user) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(user);
        HttpRequest req = buildAuthorizedRequest("/api/users/create", "POST", json);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<User> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );
        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<User> updateUser(Long id, User user) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(user);
        HttpRequest req = buildAuthorizedRequest("/api/users/update" + id, "PUT", json);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<User> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );
        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<Void> delete(Long id) throws IOException, InterruptedException {
        HttpRequest req = buildAuthorizedRequest("/api/users/delete" + id, "DELETE", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Void> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }
}


