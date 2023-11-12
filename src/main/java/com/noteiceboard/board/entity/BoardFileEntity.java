package com.noteiceboard.board.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "board_file_table")
public class BoardFileEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String originalFileName;

    @Column
    private String storedFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;

    //id값까지 가진 boardEntity에 파일의 원래 이름과 저장된 파일 이름을 추가로 가져와 BoardFileEntity로 만듬
    public static BoardFileEntity toBoardFileEntity(BoardEntity boardEntity, String originalFilename, String storedFileName) {
        BoardFileEntity boardFileEntity = new BoardFileEntity();
        boardFileEntity.setOriginalFileName(originalFilename);
        boardFileEntity.setStoredFileName(storedFileName);
        //board_id를 join하기 때문에 board_id값이 있어야 해서 save한 뒤에 id값이 있는 그 boardEntity를 가져옴
        //그 boardEntity를 setBoardEntity시킴.
        boardFileEntity.setBoardEntity(boardEntity);
        return boardFileEntity;
    }
}
