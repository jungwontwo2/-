package com.noteiceboard.board.dto;

import com.noteiceboard.board.entity.BoardEntity;
import com.noteiceboard.board.entity.BoardFileEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private Long id;
    private String boardWriter;
    private String boardPass;
    private String boardTitle;
    private String boardContents;
    private int boardHits;
    private LocalDateTime boardCreatedTime;
    private LocalDateTime boardUpdatedTime;

    private List<MultipartFile> boardFile;//save.html -> Controller 파일 담는 용도
    private List<String> originalFileName;//원본 파일 이름
    private List<String> storedFileName;//서버 저장용 파일 이름 //똑같은 파일의 이름이 있을수도 있으니 서버에는 다른이름으로 저장하기 위함
    private int fileAttached;//파일 첨부 여부(첨부 1, 미첨부 0)
    public BoardDTO(Long id, String boardWriter, String boardTitle, int boardHits, LocalDateTime boardCreatedTime) {
        this.id = id;
        this.boardWriter = boardWriter;
        this.boardTitle = boardTitle;
        this.boardHits = boardHits;
        this.boardCreatedTime = boardCreatedTime;
    }

    public static BoardDTO toBoardDTO(BoardEntity boardEntity){
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(boardEntity.getId());
        boardDTO.setBoardWriter(boardEntity.getBoardWriter());
        boardDTO.setBoardPass(boardEntity.getBoardPass());
        boardDTO.setBoardTitle(boardEntity.getBoardTitle());
        boardDTO.setBoardContents(boardEntity.getBoardContents());
        boardDTO.setBoardHits(boardEntity.getBoardHits());
        boardDTO.setBoardCreatedTime(boardEntity.getCreatedTime());
        boardDTO.setBoardUpdatedTime(boardEntity.getUpdatedTime());
//        if(boardEntity.getFileAttached()==0){
//            boardDTO.setFileAttached(boardEntity.getFileAttached());
//        }
//        else{
//            boardDTO.setFileAttached(boardEntity.getFileAttached());
//            boardDTO.setOriginalFileName(boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
//            boardDTO.setStoredFileName(boardEntity.getBoardFileEntityList().get(0).getStoredFileName());
//
//        }
        boardDTO.setFileAttached(boardEntity.getFileAttached());
        //파일이 저장되어있는 게시글이라면?
        if(boardEntity.getFileAttached()==1){
            //원본파일 이름과 저장된 파일 이름을 리스트로 만듬
            List<String> originalFileNameList = new ArrayList<>();
            List<String> storedFileNameList = new ArrayList<>();
            //리스트를 계속 돌면서 boardEntity안에 boardFileEntity가 리스트로 저장되어 있으니 그걸 하나씩 꺼냄
            for (BoardFileEntity boardFileEntity : boardEntity.getBoardFileEntityList()) {
                //파일들의 원래 이름과 저장된 바뀐 이름을 list에 넣어줌
                originalFileNameList.add(boardFileEntity.getOriginalFileName());
                storedFileNameList.add(boardFileEntity.getStoredFileName());
            }
            boardDTO.setOriginalFileName(originalFileNameList);
            boardDTO.setStoredFileName(storedFileNameList);

            //저장된 BoardFileEntity는 원래 파일 이름과 저장된 파일 이름을 가지고 있고
            //어떤 게시물의 것인지 알기위해 BoardDTO의 pk값인 board_id를 가지고있음
            //나중에 조회할 때 board_id를 통하여 파일들을 불러옴
        }
        return boardDTO;
    }
}
