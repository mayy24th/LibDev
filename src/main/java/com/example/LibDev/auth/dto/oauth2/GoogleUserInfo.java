package com.example.LibDev.auth.dto.oauth2;

import java.util.Map;

public class GoogleUserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getEmail() { return attributes.get("email").toString();}

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getPhone() {
        return null;
    }

}
