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
            //다중 첨부 파일
            //파일이 있으면 파일이 있는 entity로 바꿔줌
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            //엔티티로 바꾼 것을 save함
            BoardEntity savedEntity = boardRepository.save(boardEntity);
            //id를 가져옴(join에 쓰이기 때문에)
            Long savedId = savedEntity.getId();
            //id로 boardEntity를 찾아옴
            BoardEntity board = boardRepository.findById(savedId).get();
            //매개변수로 받은 boardDTO에서 반복문을 통해서 boardFile을 가져옴
            for( MultipartFile boardFile: boardDTO.getBoardFile()){
                //originalFilename,sotredFileName을 가져옴
                String originalFilename = boardFile.getOriginalFilename();//2
                String storedFileName = System.currentTimeMillis() + "_" + originalFilename;//3
                //저장경로 설정
                String savePath = "C:/notice_board/" + storedFileName;//4
                //저장경로에 파일을 저장시킴
                boardFile.transferTo(new File(savePath));//5
                //boardDTO를 BoardFileEntity로 변환시킴
                //BoardFileEntity에는 외래키로 가져온 BoardEntity의 id, 파일이름, 원래 파일 이름, 저장된 파일 이름이 등록된다.
                BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFilename, storedFileName);
                //파일저장소에 저장함
                boardFileRepository.save(boardFileEntity);//7
            }
            //MultipartFile boardFile = boardDTO.getBoardFile();//1


            //BoardDTO를 가져왔는데 해당 BoardDTO를 첨부파일 있는 boardEntity로 바꿔줘야함.
            //그래서 toSaveFileEntity를 통해서 boardDTO를 boardEntity로 바꿔줌

            //가져온 board에 원래 파일이름(originalFilename), 저장된 파일 이름(storedFileName)을 가져옴
            //그래서 파일이 있는 객체인 BoardFileEntity로 바꿔줌.



        }

    }
    @Transactional
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

    @Transactional
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

    public Page<BoardDTO> paging(Pageable pageable,String orderCriteria){
        System.out.println(pageable.getPageNumber());
        int page= pageable.getPageNumber()-1;
        //System.out.println("page = " + page);
        int pageLimit=3;

        Page<BoardEntity> boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, orderCriteria)));
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
