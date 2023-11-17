package com.noteiceboard.board.service;

import com.noteiceboard.board.dto.CommentDTO;
import com.noteiceboard.board.entity.BoardEntity;
import com.noteiceboard.board.entity.CommentEntity;
import com.noteiceboard.board.repository.BoardRepository;
import com.noteiceboard.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    //댓글의 내용을 저장하기 위한 함수
    //detail.html에서 commentDTO에 들어갈 내용들을 가져와서
    //postmapping할 때 @ModelAttribute를 통해 CommentDTO에 값을 넣어서 전달시킴
    public Long save(CommentDTO commentDTO){
        //댓글을 저장하기 위해서는 댓글과 관련된 부분이 필요한데 그것이 commentDTO로 받음
        //또 다음은 해당 게시글의 id를 알아야 함

        //commentDTO에도 게시글의 id를 저장하는 부분이 있어서 getBoardId를 통해 BoardEntity를 옵셔널로 가져옴
        //없을 경우도 대비한 경우임 ㅇㅇ
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(commentDTO.getBoardId());
        //만약 게시글이 존재한다면?
        if(optionalBoardEntity.isPresent()){
            //OptionalBoardEntity에서 get을 통해서 BoardEntity를 가져옴
            BoardEntity boardEntity = optionalBoardEntity.get();
            //DTO와 Entity를 짬뽕시켜서 CommentEntity로 바꿔줌
            CommentEntity commentEntity = CommentEntity.toSaveEntity(commentDTO,boardEntity);
            //Long을 반환하기로 했으니 .getId를 해서 return
            return commentRepository.save(commentEntity).getId();
        }
        else{
            return null;
        }
    }


    public List<CommentDTO> findAll(Long boardId) {
        //select * from comment_table where board_id=? order by desc;
        BoardEntity boardEntity = boardRepository.findById(boardId).get();
        List<CommentEntity> commentEntityList = commentRepository.findAllByBoardEntityOrderByIdDesc(boardEntity);
        //엔티티리스트를 DTO리스트로 바꿈
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (CommentEntity commentEntity : commentEntityList) {
            CommentDTO commentDTO = CommentDTO.toCommentDTO(commentEntity, boardId);
            //System.out.println("commentDTO = " + commentDTO.getCommentWriter());
            //System.out.println("commentDTO = " + commentDTO.getCommentContents());
            //System.out.println("commentDTO = " + commentDTO.getCommentCreatedTime());

            commentDTOList.add(commentDTO);
        }
        return commentDTOList;
    }
}
