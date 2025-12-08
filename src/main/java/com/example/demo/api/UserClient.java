package com.example.demo.api;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.User;
import com.example.demo.model.UserUpdate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

public class UserClient extends ApiClient {

    public UserClient() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // Fetch all users
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

    public ApiResponse<User> getUserById(Long id) throws IOException, InterruptedException {
    // Get a user by id
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
    // Create a new user
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
    // Update user by id
        String json = mapper.writeValueAsString(user);
        HttpRequest req = buildAuthorizedRequest("/api/users/update/" + id, "PUT", json);
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
    // Delete user by id
        HttpRequest req = buildAuthorizedRequest("/api/users/delete/" + id, "DELETE", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Void> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<User> getCurrentUser() throws IOException, InterruptedException {
    // Retrieve the currently authenticated user
        HttpRequest req = buildAuthorizedRequest("/api/users/getCurrentUser", "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<User> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<Void> followUser(Long targetUserId) throws IOException, InterruptedException {
    // Follow another user
        String json = String.format("{\"targetUserId\":%d}", targetUserId);
        HttpRequest req = buildAuthorizedRequest("/api/users/follow", "POST", json);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Void> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<Void> unfollowUser(Long targetUserId) throws IOException, InterruptedException {
    // Unfollow a user
        String json = String.format("{\"targetUserId\":%d}", targetUserId);
        HttpRequest req = buildAuthorizedRequest("/api/users/unfollow", "POST", json);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Void> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<List<User>> getFollowers(Long userId) throws IOException, InterruptedException {
    // Get followers for a user
        HttpRequest req = buildAuthorizedRequest("/api/users/" + userId + "/followers", "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<List<User>> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<List<User>> getFollowing(Long userId) throws IOException, InterruptedException {
    // Get users that the given user is following
        HttpRequest req = buildAuthorizedRequest("/api/users/" + userId + "/following", "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<List<User>> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<Boolean> isFollowingUser(Long targetUserId) throws IOException, InterruptedException {
    // Check if current user follows target user
        HttpRequest req = buildAuthorizedRequest("/api/users/isFollowing/" + targetUserId, "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Boolean> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<User> updateUserProfile(Long id, UserUpdate userUpdate) throws IOException, InterruptedException {
    // Update user profile; also process token refresh header if present
        String json = mapper.writeValueAsString(userUpdate);
        HttpRequest req = buildAuthorizedRequest("/api/users/update/" + id, "PUT", json);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());



        // 1. Check for the new token header
        Optional<String> newTokenHeader = resp.headers().firstValue("X-New-Auth-Token");

        if (newTokenHeader.isPresent()) {
            // 2. Update the static JWT token in the parent class
            updateJwtToken(newTokenHeader.get());
        }



        // Process the API response body
        ApiResponse<User> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }
}