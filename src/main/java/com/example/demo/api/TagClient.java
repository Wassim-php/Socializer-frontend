package com.example.demo.api;

import com.example.demo.model.ApiResponse;
import com.example.demo.model.Tag;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class TagClient extends ApiClient {

    public TagClient() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public ApiResponse<List<Tag>> fetchTags() throws IOException, InterruptedException {
        HttpRequest req = buildAuthorizedRequest("/api/tags/all", "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<List<Tag>> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}

        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());
        return apiResponse;
    }

    public ApiResponse<Tag> getTagById(Long id) throws IOException, InterruptedException {
        HttpRequest req = buildAuthorizedRequest("/api/tags/" + id, "GET", null);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Tag> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());
        return apiResponse;
    }

    public ApiResponse<Tag> createTag(Tag tag) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(tag);
        HttpRequest req = buildAuthorizedRequest("/api/tags/create", "POST", json);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Tag> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());
        return apiResponse;
    }

    public ApiResponse<Tag> updateTag(Long id, Tag tag) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(tag);
        HttpRequest req = buildAuthorizedRequest("/api/tags/update/" + id, "PUT", json);
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        ApiResponse<Tag> apiResponse = mapper.readValue(
                resp.body(),
                new TypeReference<>() {}
        );

        System.out.println("Status: " + resp.statusCode());
        System.out.println("Body: " + resp.body());
        return apiResponse;

    }

    public ApiResponse<Void> delete(Long id) throws IOException, InterruptedException {
        HttpRequest req = buildAuthorizedRequest("/api/tags/delete/" + id, "DELETE", null);
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
