package com.noteiceboard.board.controller;

import com.noteiceboard.board.dto.BoardDTO;
import com.noteiceboard.board.dto.CommentDTO;
import com.noteiceboard.board.service.BoardService;
import com.noteiceboard.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/save")
    public String saveForm() {
        return "save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        System.out.println("boardDTO = " + boardDTO);
        boardService.save(boardDTO);
        return "index";
    }

    @GetMapping("/")
    public String findAll(Model model) {
        //db에서 전체 개시글 데이터를 가져와서 list.html에 보여준다.
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);
        return "list";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) {
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        //댓글목록 가져오기
        List<CommentDTO> commentDTOList = commentService.findAll(id);
        model.addAttribute("commentList",commentDTOList);
        model.addAttribute("board", boardDTO);
        return "detail";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model) {
        BoardDTO board = boardService.update(boardDTO);
        model.addAttribute("board", board);
        return "detail";
    }

    @GetMapping("update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO);
        return "update";
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable Long id){
        boardService.delete(id);
        return "redirect:/board";
    }

    //글 전체 조회(페이징)
    //   /board/paging?orderby={orderCriteria}
    @GetMapping("/paging")
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model,@RequestParam(required = false,defaultValue = "id",value = "orderby") String orderCriteria) {
//        pageable.getPageNumber();
        Page<BoardDTO> boardList = boardService.paging(pageable,orderCriteria);
        //System.out.println("boardList = " + boardList.toList());
        //System.out.println("boardList = " + boardList.getNumber());
        //System.out.println(boardList.getPageable());
        //System.out.println(boardList.getNumberOfElements());
        //return boardList;
        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        // page 갯수 20개
        // 현재 사용자가 3페이지
        // 1 2 3
        // 현재 사용자가 7페이지
        // 7 8 9
        // 보여지는 페이지 갯수 3개
        // 총 페이지 갯수 8개

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("orderCriteria",orderCriteria);
        //System.out.println("startPage = " + startPage);
        //System.out.println("endPage = " + endPage);
        return "paging";
    }
//    @PostMapping("/comment")
//    public String comment(@ModelAttribute("comment") String comment){
//        CommentEntity commentEntity = new CommentEntity();
//        commentEntity.setComment(comment);
//
//    }
}
