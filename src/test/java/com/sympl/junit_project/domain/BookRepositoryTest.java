package com.sympl.junit_project.domain;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
public class BookRepositoryTest {

    @BeforeEach
    public void 데이터_준비(){
        String title = "junit";
        String author = "재현킴";
        Book book = Book.builder()
                .title(title)
                .author(author)
                .build();

        Book bookPersistence = repository.save(book);
    }//트랜잭션이 언제 종료되는가?
    //BeforeEach + Test 가 한 단위로 묶인다.


    @Autowired
    private BookRepository repository;

    @Test
    public void 책등록_테스트(){
        //given(데이터 준비)
        String title = "junit5";
        String author = "재현";
        Book book = Book.builder()
                .title(title)
                .author(author)
                .build();

        //when (테스트 실행)
        Book bookPersistence = repository.save(book);
            //bookPersistence : 영속화된 book 인스턴스

        //then (검증)
        assertEquals(title, bookPersistence.getTitle());
        assertEquals(author, bookPersistence.getAuthor());
    }//트랜잭션 종료되고 저장된 데이터를 초기화 한다. 연속적인 테스트가 필요한 경우 트랜잭션 종료되지 않게 한다.


    @Test
    public void 책_목록_보기_테스트(){
        //given
        String title = "junit";
        String author = "재현킴";

        //when
        List<Book> booksPs = repository.findAll();

        //then
        assertEquals(title, booksPs.get(0).getTitle());
        assertEquals(author, booksPs.get(0).getAuthor());
    }

    @Sql("classpath:db/tableInit.sql")
    @Test
    public void 책_한권_보기_테스트(){
        //given
        String title = "junit";
        String author = "재현킴";


        //when
        Book bookPs  = repository.findById(1L).get();

        //then
        assertEquals(title, bookPs.getTitle());
        assertEquals(author, bookPs.getAuthor());

    }

    @Sql("classpath:db/tableInit.sql")
    @Test
    public void 책_삭제_테스트(){
        //given
        Long id = 1L;

        //when
        repository.deleteById(id);

        //then
        assertFalse(repository.findById(id).isPresent());
    }
}
