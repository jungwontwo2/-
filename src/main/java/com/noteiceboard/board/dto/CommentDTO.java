package com.noteiceboard.board.dto;

import com.noteiceboard.board.entity.CommentEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CommentDTO {
    //pk로 생성된 id값
    private Long id;

    //작성자
    private String commentWriter;

    //작성내용
    private String commentContents;

    //게시판의 id
    private Long boardId;

    //생성날짜
    private LocalDateTime commentCreatedTime;

    public static CommentDTO toCommentDTO(CommentEntity commentEntity,Long boardId) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(commentEntity.getId());
        commentDTO.setCommentWriter(commentEntity.getCommentWriter());
        commentDTO.setCommentContents(commentEntity.getCommentContents());
        commentDTO.setCommentCreatedTime(commentEntity.getCreatedTime());
        commentDTO.setBoardId(boardId);
        return commentDTO;
    }
}
