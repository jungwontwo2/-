package com.noteiceboard.board.service;

import com.noteiceboard.board.dto.BoardDTO;
import com.noteiceboard.board.entity.BoardEntity;
import com.noteiceboard.board.entity.BoardFileEntity;
import com.noteiceboard.board.repository.BoardFileRepository;
import com.noteiceboard.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    public void save(BoardDTO boardDTO) throws IOException {
        if(boardDTO.getBoardFile().isEmpty()){
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity);
        }else{
            // 첨부 파일 있음.
            /*
                1. DTO에 담긴 파일을 꺼냄
                2. 파일의 이름 가져옴
                3. 서버 저장용 이름을 만듦
                // 내사진.jpg => 839798375892_내사진.jpg
                4. 저장 경로 설정
                5. 해당 경로에 파일 저장
                6. board_table에 해당 데이터 save 처리
                7. board_file_table에 해당 데이터 save 처리
             */
            MultipartFile boardFile = boardDTO.getBoardFile();//1
            String originalFilename = boardFile.getOriginalFilename();//2
            String storedFileName = System.currentTimeMillis() + "_" + originalFilename;//3
            String savePath = "C:/notice_board/" + storedFileName;//4
            boardFile.transferTo(new File(savePath));//5

            //BoardDTO를 가져왔는데 해당 BoardDTO를 첨부파일 있는 boardEntity로 바꿔줘야함.
            //그래서 toSaveFileEntity를 통해서 boardDTO를 boardEntity로 바꿔줌
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            //해당 boardEntity를 저장하고 savedId를 반환함.
            BoardEntity savedEntity = boardRepository.save(boardEntity);//5
            //BoardFileEntity는 board_id값을 통해서 외래키로 받아오기 때문에 id값이 있는 boardEntity를 가져와야함.
            //BoardEntity.getId()를 통해서 pk값이 id를 가져옴.
            Long savedId = savedEntity.getId();
            //findById(savedId)를 통해서 pk값이 있는 BoardEntity를 가져옴.
            BoardEntity board = boardRepository.findById(savedId).get();
            //가져온 board에 원래 파일이름(originalFilename), 저장된 파일 이름(storedFileName)을 가져옴
            //그래서 파일이 있는 객체인 BoardFileEntity로 바꿔줌.
            BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFilename, storedFileName);
            boardFileRepository.save(boardFileEntity);


        }

    }

    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList;
    }

    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if(optionalBoardEntity.isPresent()){
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardDTO boardDTO = BoardDTO.toBoardDTO(boardEntity);
            return boardDTO;
        }
        else {
            return null;
        }
    }

    public BoardDTO update(BoardDTO boardDTO){
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);
        return findById(boardDTO.getId());
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public Page<BoardDTO> paging(Pageable pageable){
        System.out.println(pageable.getPageNumber());
        int page= pageable.getPageNumber()-1;
        //System.out.println("page = " + page);
        int pageLimit=3;

        Page<BoardEntity> boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
//        System.out.println("boardEntities = " + boardEntities.toString());
//        System.out.println("boardEntities = " + boardEntities);
//        System.out.println("boardEntities.getContent() = " + boardEntities.getContent()); // 요청 페이지에 해당하는 글
//        System.out.println("boardEntities.getTotalElements() = " + boardEntities.getTotalElements()); // 전체 글갯수
//        System.out.println("boardEntities.getNumber() = " + boardEntities.getNumber()); // DB로 요청한 페이지 번호
//        System.out.println("boardEntities.getTotalPages() = " + boardEntities.getTotalPages()); // 전체 페이지 갯수
//        System.out.println("boardEntities.getSize() = " + boardEntities.getSize()); // 한 페이지에 보여지는 글 갯수
//        System.out.println("boardEntities.hasPrevious() = " + boardEntities.hasPrevious()); // 이전 페이지 존재 여부
//        System.out.println("boardEntities.isFirst() = " + boardEntities.isFirst()); // 첫 페이지 여부
//        System.out.println("boardEntities.isLast() = " + boardEntities.isLast()); // 마지막 페이지 여부
//        // 목록: id, writer, title, hits, createdTime
        Page<BoardDTO> boardDTOS = boardEntities.map(board -> new BoardDTO(board.getId(), board.getBoardWriter(), board.getBoardTitle(), board.getBoardHits(), board.getCreatedTime()));
        log.info(boardDTOS.toString());
        return boardDTOS;
    }
}
