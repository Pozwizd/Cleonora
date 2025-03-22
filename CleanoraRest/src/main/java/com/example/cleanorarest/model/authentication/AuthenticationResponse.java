package com.example.cleanorarest.model.authentication;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrZXZlbi5jcm9va3NAeWFob28uY29tIiwiaWF0IjoxNzI2MTIyMzgxLCJleHAiOjE3MjYyMDg3ODF9.tFBDEAVBPX3MOydnDSQpyz3L038LIxGdGvUCayoXCrs")
    private String accessToken;
    private String refreshToken;
}
