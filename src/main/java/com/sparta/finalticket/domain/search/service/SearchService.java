package com.sparta.finalticket.domain.search.service;

import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import com.sparta.finalticket.domain.search.entity.Search;
import com.sparta.finalticket.domain.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final GameRepository gameRepository;
    private final SearchRepository searchRepository;

    public void performSearchAndSave(Long gameId, String keyword) {
        // Retrieve the game associated with the provided gameId
        Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + gameId));

        // Save search result
        Search search = Search.builder()
            .keyword(keyword)
            .game(game)
            .build();
        searchRepository.save(search);
    }
}
