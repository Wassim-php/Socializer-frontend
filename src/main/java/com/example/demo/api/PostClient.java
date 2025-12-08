package com.example.demo.api;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.Comment;
import com.example.demo.model.Post;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PostClient extends ApiClient {

    public PostClient() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // Fetch all posts
    public ApiResponse<List<Post>> fetchPosts() throws IOException, InterruptedException {
        HttpRequest req = buildAuthorizedRequest("/api/posts/all", "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<List<Post>> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;

    }

    public ApiResponse<Post> getPostById(Long id) throws IOException, InterruptedException {
    // Get a post by id
        HttpRequest req = buildAuthorizedRequest("/api/posts/" + id, "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Post> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;

    }

    public ApiResponse<Post> createPost(Post post) throws IOException, InterruptedException {
    // Create a new post
        String json = mapper.writeValueAsString(post);
        HttpRequest req = buildAuthorizedRequest("/api/posts/create", "POST", json);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Post> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<Post> updatePost(Long id, Post post) throws IOException, InterruptedException {
    // Update a post
        String json = mapper.writeValueAsString(post);
        HttpRequest req = buildAuthorizedRequest("/api/posts/update/" + id, "PUT", json);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Post> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<Void> delete(Long id) throws IOException, InterruptedException {
    // Delete a post
        HttpRequest req = buildAuthorizedRequest("/api/posts/delete/" + id, "DELETE", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Void> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<Void> likePost(Long id) throws IOException, InterruptedException {
    // Toggle like on a post
        HttpRequest req = buildAuthorizedRequest("/api/posts/likePost/" +id, "POST", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Void> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );
        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<Void> addComment(Long id, Comment comment) throws IOException, InterruptedException {
    // Add a comment to a post
        String json = mapper.writeValueAsString(comment);
        HttpRequest req = buildAuthorizedRequest("/api/posts/" + id + "/addComment","POST", json );
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Void> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );
        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<List<Post>> getAllByUserId(Long userId) throws IOException, InterruptedException {
    // Get all posts created by a user
        HttpRequest req = buildAuthorizedRequest("/api/posts/all/" + userId, "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<List<Post>> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );
        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public String uploadImage(File imageFile) throws IOException, InterruptedException {
        return uploadFile(imageFile);
    }

}
