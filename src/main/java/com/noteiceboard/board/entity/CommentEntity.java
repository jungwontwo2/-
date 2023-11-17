package com.noteiceboard.board.entity;

import com.noteiceboard.board.dto.CommentDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "comment_table")
public class CommentEntity extends BaseEntity{

    //pk id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //작성자
    @Column(length = 20, nullable = false)
    private String commentWriter;

    //작성내용
    @Column
    private String commentContents;

    //게시글의 id를 가져오기 위해서 board_id 조인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

    //CommentDTO를 Entity로 바꿔주는 작업
    //commentDTO에는 작성자, 작성내용이 들어감
    //CommentEntity는 BoardEntity를 가지고 있어서 BoardEntity도 매개변수로 가져옴
    public static CommentEntity toSaveEntity(CommentDTO commentDTO,BoardEntity boardEntity){
        //새로운 엔티티 객체를 만듬
        CommentEntity commentEntity = new CommentEntity();
        //작성자, 내용을 설정함
        commentEntity.setCommentWriter(commentDTO.getCommentWriter());
        commentEntity.setCommentContents(commentDTO.getCommentContents());
        //setBoardEntity를 통해서 CommentEntity의 BoardEntity를 조인시킴
        //board_id를 통해서 조인이 실행됨
        commentEntity.setBoardEntity(boardEntity);
        //CommentEntity의 설정이 끝난 뒤 이를 리턴함
        return commentEntity;
    }
}
