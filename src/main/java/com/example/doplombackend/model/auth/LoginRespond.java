package com.example.doplombackend.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRespond(@JsonProperty("auth-token") String authToken) {
}
