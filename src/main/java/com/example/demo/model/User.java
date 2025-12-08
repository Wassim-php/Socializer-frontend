package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true) // Add this to ignore unknown properties
public class User {
    private Long id;
    private String username;
    private String password;
    private List<Comment> comments;
    private List<Post> posts;
    private Set<Post> likedPosts;
    private Set<User> followers;
    private Set<User> following;
    private int followersCount;
    private int followingCount;
    private boolean followedByCurrentUser;
    private String avatarColorHex;

    // Default constructor
    public User() {
    }

    // Constructor used by Jackson to deserialize a User from JSON
    public User(
            @JsonProperty("id") Long id,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("comments") List<Comment> comments,
            @JsonProperty("posts") List<Post> posts,
            @JsonProperty("likedPosts") Set<Post> likedPosts,
            @JsonProperty("followers") Set<User> followers,
            @JsonProperty("following") Set<User> following,
            @JsonProperty("followersCount") int followersCount,
            @JsonProperty("followingCount") int followingCount, // Fixed parameter name
            @JsonProperty("followedByCurrentUser") boolean followedByCurrentUser,
            @JsonProperty("avatarColorHex") String avatarColorHex) { // Add this parameter
        this.id = id;
        this.username = username;
        this.password = password;
        this.comments = comments;
        this.posts = posts;
        this.likedPosts = likedPosts;
        this.followers = followers;
        this.following = following;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.followedByCurrentUser = followedByCurrentUser;
        this.avatarColorHex = avatarColorHex;
    }

    // Set the avatar color in hex format
    public void setAvatarColorHex(String avatarColorHex) {
        this.avatarColorHex = avatarColorHex;
    }

    // Get avatar color hex
    public String getAvatarColorHex() {
        return avatarColorHex;
    }

    // Get user id
    public Long getId() {
        return id;
    }

    // Set user id
    public void setId(Long id) {
        this.id = id;
    }

    // Get username
    public String getUserName() {
        return username;
    }

    // Set username
    public void setUserName(String username) { // Fixed method name and parameter
        this.username = username;
    }

    // Get password
    public String getPassword() {
        return password;
    }

    // Set password
    public void setPassword(String password) {
        this.password = password;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public Set<Post> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(Set<Post> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public boolean isFollowedByCurrentUser() {
        return followedByCurrentUser;
    }

    public void setFollowedByCurrentUser(boolean followedByCurrentUser) {
        this.followedByCurrentUser = followedByCurrentUser;
    }
}