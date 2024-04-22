package com.sparta.finalticket.domain.search.controller;

import com.sparta.finalticket.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/games/{gameId}")
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/search")
    public ResponseEntity<String> performSearch(@PathVariable(name = "gameId") Long gameId,
                                                @RequestParam(name = "keyword") String keyword) {
        searchService.performSearchAndSave(gameId, keyword);
        return ResponseEntity.ok("Search performed successfully.");
    }
}
