package com.maxml.socialsignin.entity;

import com.maxml.socialsignin.util.SignInConstants;

/**
 * Inner entity-wrapper. You can build your entity in this functionality
 */
public class SignInUser {

    private String id;
    private String name;
    private String token;

    private SignInConstants.AccountType type;

    public SignInUser(SignInConstants.AccountType type, String id, String name, String token) {
        this.name = name;
        this.id = id;
        this.token = token;
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public SignInConstants.AccountType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "SignInUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", token='" + token + '\'' +
                ", type=" + type +
                '}';
    }
}
