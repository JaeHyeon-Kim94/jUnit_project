package com.sympl.junit_project.web.dto;

import com.sympl.junit_project.domain.Book;
import lombok.Setter;

@Setter
public class BookRequestSaveDto {
    private String title;
    private String author;

    public Book toEntity(){
        return Book.builder()
                .title(title)
                .author(author)
                .build();
    }
}
