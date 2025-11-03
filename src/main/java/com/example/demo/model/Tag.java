package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Tag {
    private Long id;
    private String name;
    private List<Post> posts;
    private User user;

    public Tag() {}
    public Tag(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("posts") List<Post> posts,
            @JsonProperty("user") User user
    ){
        this.id = id;
        this.name = name;
        this.posts = posts;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
