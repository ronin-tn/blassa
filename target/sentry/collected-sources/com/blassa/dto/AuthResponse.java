package com.blassa.dto;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.Serializable;


@Value
@RequiredArgsConstructor
public class AuthResponse implements Serializable {
    private String message;

}