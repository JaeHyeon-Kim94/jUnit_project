package com.sympl.junit_project.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;


@Getter
@NoArgsConstructor
@ToString
@Entity
public class Book {

    @Id @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    //empty string도 허용하지 않기 위해서는 @Convert 필요.
    @Column( length = 50, nullable = false )
    private String title;
    @Column( length = 20, nullable = false )
    private String author;

    @Builder
    public Book(Long id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public Book update(String title, String author){
        this.title = title;
        this.author = author;
        return this;
    }
}
