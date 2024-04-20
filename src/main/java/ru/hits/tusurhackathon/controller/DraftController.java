package ru.hits.tusurhackathon.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/drafts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Черновики.")
public class DraftController {
}
