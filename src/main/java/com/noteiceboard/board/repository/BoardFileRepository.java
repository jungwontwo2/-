package com.noteiceboard.board.repository;

import com.noteiceboard.board.entity.BoardFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardFileRepository extends JpaRepository<BoardFileEntity,Long> {
}
