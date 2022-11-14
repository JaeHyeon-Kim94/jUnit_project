package com.sympl.junit_project.web;


import com.sympl.junit_project.service.BookService;
import com.sympl.junit_project.web.dto.request.BookRequestSaveDto;
import com.sympl.junit_project.web.dto.response.BookListResponseDto;
import com.sympl.junit_project.web.dto.response.BookResponseDto;
import com.sympl.junit_project.web.dto.response.CMRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class BookApiController {

    private final BookService service;

    //책 등록
    @PostMapping("/api/v1/book")
    public ResponseEntity<?> SaveBook(@RequestBody @Valid BookRequestSaveDto dto, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            Map<String, String> errorMap = new HashMap<>();
            for(FieldError fe : bindingResult.getFieldErrors()){
                errorMap.put(fe.getField(), fe.getDefaultMessage());
            }
            System.out.println(errorMap);

            throw new RuntimeException(errorMap.toString());
        }
        BookResponseDto resDto = service.saveBook(dto);

        CMRespDto<?> cmRespDto = CMRespDto.builder()
                .code(1)
                .msg("저장 성공")
                .body(resDto)
                .build();

        return new ResponseEntity<>(cmRespDto, HttpStatus.CREATED);    //201 = insert
    }

    //책 목록조회
    @GetMapping("/api/v1/book")
    public ResponseEntity<?> getBookList(){
        //그냥 List째로 리턴하는 것은 추천하지 않음.
        //return type이 collection이기 때문에 paging offset 등 별도로 필요한 데이터를 넣기 곤란함.
        BookListResponseDto items = service.selectBookList();

        return new ResponseEntity<>(CMRespDto.builder().code(1).msg("글 목록 보기 성공").body(items).build(), HttpStatus.OK);
    }

    //책 한권 조회
    @GetMapping("/api/v1/book/{id}")
    public ResponseEntity<?> getBook(@PathVariable Long id){
        BookResponseDto dto = service.selectBook(id);
        return new ResponseEntity<>(CMRespDto.builder().code(1).msg("책 한권 조회").body(dto).build()
                , HttpStatus.OK);
    }

    //책 삭제
    @DeleteMapping("/api/v1/book/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id){
        service.deleteBook(id);

        return new ResponseEntity<>(CMRespDto.builder().code(1).msg("책 삭제 성공").build()
                , HttpStatus.OK);
    }

    //책 수정정
    @PutMapping("/api/v1/book/{id}")                //Json으로 받기 위해선 @RequestBody 명시
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody @Valid BookRequestSaveDto dto, BindingResult br){

        if(br.hasErrors()){
            Map<String, String> errorMap = new HashMap<>();
            for(FieldError fe : br.getFieldErrors()){
                errorMap.put(fe.getField(), fe.getDefaultMessage());
            }
            System.out.println(errorMap);

            throw new RuntimeException(errorMap.toString());
        }

        BookResponseDto resDto = service.updateBook(id, dto);

        return new ResponseEntity<>(CMRespDto.builder().code(1).msg("수정 성공").body(resDto).build(),
                HttpStatus.OK);
    }
}
