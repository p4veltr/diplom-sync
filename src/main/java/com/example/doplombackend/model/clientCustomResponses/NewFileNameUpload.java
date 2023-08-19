package com.example.doplombackend.model.clientCustomResponses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NewFileNameUpload(@JsonProperty("fileName") String name) {
}
