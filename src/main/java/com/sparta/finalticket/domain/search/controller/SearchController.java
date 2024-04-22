package com.sparta.finalticket.domain.search.controller;

import com.sparta.finalticket.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/games")
public class SearchController {

    private final SearchService searchService;

}
