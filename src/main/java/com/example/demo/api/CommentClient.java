package com.example.demo.api;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.Comment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CommentClient extends ApiClient {

    public CommentClient() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public ApiResponse<List<Comment>> fetchComments() throws IOException, InterruptedException {
        HttpRequest req = buildAuthorizedRequest("/api/comments/all", "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<List<Comment>> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<Comment> getCommentById(Long id) throws IOException, InterruptedException {
        HttpRequest req = buildAuthorizedRequest("/api/comments/" + id, "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Comment> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<Comment> createComment(Comment comment) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(comment);

        HttpRequest req = buildAuthorizedRequest("/api/comments/create", "POST", json);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Comment> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<Comment> updateComment(Long id, Comment comment) throws IOException, InterruptedException{
        String json = mapper.writeValueAsString(comment);
        HttpRequest req = buildAuthorizedRequest("/api/comments/update/" + id, "PUT", json);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Comment> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<Void> delete(Long id) throws IOException, InterruptedException{
        HttpRequest req = buildAuthorizedRequest("/api/comments/delete/"+ id, "DELETE", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Void> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }

    public ApiResponse<List<Comment>> getCommentsByPostId(Long id)  throws IOException, InterruptedException {
        HttpRequest req = buildAuthorizedRequest("/api/comments/all/" +id, "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<List<Comment>> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());

        return apiResponse;
    }
}
