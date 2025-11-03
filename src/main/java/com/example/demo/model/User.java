package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;

public class User {
    private Long id;
    private String username;
    private String password;
    private List<Comment> comments;
    private List<Post> posts;
    private Set<Post> likedPosts;


    public User(){

    }
    public User(
            @JsonProperty("id") Long id,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("comments") List<Comment> comments,
            @JsonProperty("posts") List<Post> posts,
            @JsonProperty("likedPosts") Set<Post> likedPosts){
        this.username = username;
        this.password = password;
        this.comments = comments;
        this.posts = posts;
        this.likedPosts = likedPosts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return username;
    }

    public void setName(String Username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Comment> getComments() {return comments; }

    public void setComments(List<Comment> comments) { this.comments = comments; }

    public List<Post> getPosts() {return posts; }

    public void setPosts() {this.posts = posts; }

    public Set<Post> getLikedPosts() {return likedPosts; }

    public void setLikedPosts() {this.likedPosts = likedPosts;}
}
