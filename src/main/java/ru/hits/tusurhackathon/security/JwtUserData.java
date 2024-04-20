package ru.hits.tusurhackathon.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class JwtUserData {

    private final UUID id;

}
