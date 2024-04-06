package org.example.common.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class JwtUserData {

    private final UUID id;

    private final String login;

    private final String name;

}
