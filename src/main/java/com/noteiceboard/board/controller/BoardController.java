package com.noteiceboard.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BoardController {
    @GetMapping("/board/save")
    public String saveForm(){
        return "save";
    }
}
