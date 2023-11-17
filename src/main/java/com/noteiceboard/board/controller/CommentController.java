package com.noteiceboard.board.controller;

import com.noteiceboard.board.dto.CommentDTO;
import com.noteiceboard.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/save")
    //ResponseEntity는 넘길 정보와 HttpStatus까지 설정 가능하게 해는 클래스
    //모델 어트리뷰트를 통해서 댓글의 작성자, 내용을 commentDTO에 담음
    public ResponseEntity save(@ModelAttribute CommentDTO commentDTO) {
        //댓글의 내용을 저장함
        Long saveResult = commentService.save(commentDTO);
        //저장이 성공하면 saveResult의 값이 나오기 때문에
        if(saveResult!=null){
            //댓글 작성 성공하면 댓글목록을 가져와서 리턴
            //댓글목록: 해당 게시글의 댓글 전체
            //게시글의 id를 통해서 모든 댓글을 가져와야함
            List<CommentDTO> commentDTOList = commentService.findAll(commentDTO.getBoardId());
            return new ResponseEntity<>(commentDTOList,HttpStatus.OK);
        }else{
            return new ResponseEntity<>("해당 게시글이 존재하지 않습니다.",HttpStatus.NOT_FOUND);
        }
    }
}