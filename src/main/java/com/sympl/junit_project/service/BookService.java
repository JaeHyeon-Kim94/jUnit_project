package com.sympl.junit_project.service;

import com.sympl.junit_project.domain.Book;
import com.sympl.junit_project.domain.BookRepository;
import com.sympl.junit_project.web.dto.BookRequestSaveDto;
import com.sympl.junit_project.web.dto.BookResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;

    //책 등록
    @Transactional ( rollbackFor = RuntimeException.class )
    public BookResponseDto saveBook(BookRequestSaveDto dto){
        Book bookPs = repository.save(dto.toEntity());
        //db에서 넘어온 data를 controller로 바로 return하게 되면 lazyLoading 관련한 이슈 발생할 수 있기 때문에
        //BookResponseDto를 이용한다.
        //jpa.open-in-view(?)
        //영속화된 객체는 여기서 끊어낸다.
        return BookResponseDto.toDto(bookPs);
    }


    //책 목록보기
    public List<BookResponseDto> selectBookList(){
        return repository.findAll().stream()
                .map(BookResponseDto::toDto)
                .collect(Collectors.toList());
    }

    //책 한권 보기

    //책 삭제

    //책 수정
}
