package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Tag {
    private Long id;
    private String name;
    private List<Post> posts;
    private User user;


    // Default constructor
    public Tag() {}
    // Constructor for Tag deserialization
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
    // Get tag id
        return id;
    }

    public void setId(Long id) {
    // Set tag id
        this.id = id;
    }

    public String getName() {
    // Get tag name
        return name;
    }

    public void setName(String name) {
    // Set tag name
        this.name = name;
    }

    public List<Post> getPosts() {
    // Get posts attached to this tag
        return posts;
    }

    public void setPosts(List<Post> posts) {
    // Set posts for this tag
        this.posts = posts;
    }

    public User getUser() {
    // Get tag owner
        return user;
    }

    public void setUser(User user) {
    // Set tag owner
        this.user = user;
    }
}
