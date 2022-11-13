package com.sympl.junit_project.domain;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookRepositoryTest {

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
    }
}
