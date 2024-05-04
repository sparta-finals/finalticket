package com.sparta.finalticket.domain.comment.repository;

import com.sparta.finalticket.domain.comment.entity.ParentComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentCommentRepository extends JpaRepository<ParentComment, Long> {
}
