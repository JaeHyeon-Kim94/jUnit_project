package com.sympl.junit_project.web.dto.response;

import com.sympl.junit_project.domain.Book;
import lombok.Getter;

@Getter
public class BookResponseDto {
    private Long id;
    private String title;
    private String author;

    public static BookResponseDto toDto(Book bookPs){
        BookResponseDto dto = new BookResponseDto();
        dto.id = bookPs.getId();
        dto.title = bookPs.getTitle();
        dto.author = bookPs.getAuthor();
        return dto;
    }

    @Override
    public String toString() {
        return "BookResponseDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
