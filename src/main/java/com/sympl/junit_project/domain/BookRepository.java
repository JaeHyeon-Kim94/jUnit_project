package com.sympl.junit_project.domain;

import org.springframework.data.jpa.repository.JpaRepository;


//JpaRepository 상속중이기 때문에 @Repository 생략 가능
public interface BookRepository extends JpaRepository<Book, Long> {

}
