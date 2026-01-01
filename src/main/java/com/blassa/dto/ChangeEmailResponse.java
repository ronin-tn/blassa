package com.blassa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangeEmailResponse {
    private Profile profile;
    private String accessToken;
}
