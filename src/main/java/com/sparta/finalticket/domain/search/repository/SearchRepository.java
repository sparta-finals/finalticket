package com.sparta.finalticket.domain.search.repository;

import com.sparta.finalticket.domain.search.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<Search, Long> {
}
