package ru.hits.tusurhackathon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.tusurhackathon.security.JWTUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final JWTUtil jwtUtil;

}
