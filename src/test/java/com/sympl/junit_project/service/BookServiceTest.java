package com.sympl.junit_project.service;

import com.sympl.junit_project.domain.Book;
import com.sympl.junit_project.domain.BookRepository;
import com.sympl.junit_project.util.MailSender;
import com.sympl.junit_project.web.dto.request.BookRequestSaveDto;
import com.sympl.junit_project.web.dto.response.BookListResponseDto;
import com.sympl.junit_project.web.dto.response.BookResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//서비스 레이어만 테스트하고싶은데,
//@DataJpaTest 선언하고 Repository DI 받는 방식으로 하면
//Repository 레이어가 함꼐 테스트됨.

//이러한 문제 해결 -> 가짜 환경 만들기
//가짜 환경에는 가짜 repository와 가짜 mailsender가 있을것.
//Mockito -> 가짜 객체를 보관하는 환경.

@ExtendWith(MockitoExtension.class) //가짜 환경 만들어짐.
@ActiveProfiles("dev")
public class BookServiceTest {

    @InjectMocks    //해당 클래스에서 가지고 있는 의존성(repository, mailsender)들을
                    // 가짜로 주입해서 메모리에 로드.
                    // 가짜이기 때문에 Stub 필요.
    private BookService service;
    @Mock   //가짜 환경에 담기. (익명 클래스로)
    private BookRepository repository;
    @Mock
    private MailSender mailSender;

    @Test
    public void 책_등록하기_테스트(){
        //given
        BookRequestSaveDto dto = new BookRequestSaveDto();
        dto.setTitle("junit 강의");
        dto.setAuthor("재현");
        //stub (행동 정의. 가설. 즉, repository 기능이 실행되는 것이 아니라
        // 그 실행 결과를 이것(thenReturn(~))으로 간주하겠다.)
        when(repository.save(any()))
                .thenReturn(dto.toEntity());
        when(mailSender.send())
                .thenReturn(true);
        //when
        BookResponseDto bookResponseDto = service.saveBook(dto);

        //then
        assertThat(bookResponseDto.getTitle()).isEqualTo(dto.getTitle());
        assertThat(bookResponseDto.getAuthor()).isEqualTo(dto.getAuthor());

    }

    @Test
    public void 책_목록보기_테스트(){
        //given

        //stub
        List<Book> books = Arrays.asList(
                new Book(1L, "junit1", "재현1"),
                new Book(2L, "junit2", "재현2")
        );
        when(repository.findAll()).thenReturn(books);

        //when
        BookListResponseDto dtos = service.selectBookList();

        //then
        assertThat(dtos.getItems().get(0).getTitle()).isEqualTo("junit1");
    }

    @Test
    public void 책_한권_보기_테스트(){
        //given
        Long id = 1L;
        Book book = new Book(id, "junit", "재현");
        Optional<Book> bookOp = Optional.of(book);

        //stub
        when(repository.findById(id)).thenReturn(bookOp);

        //when
        BookResponseDto dto = service.selectBook(id);

        //then
        assertThat(dto.getTitle()).isEqualTo(book.getTitle());
        assertThat(dto.getAuthor()).isEqualTo(book.getAuthor());
    }

    @Test
    public void 책_수정하기_테스트(){
        //given
        Long id = 1L;
        BookRequestSaveDto requestDto = new BookRequestSaveDto();
        requestDto.setTitle("수정된_junit강의");
        requestDto.setAuthor("수정된_재현");

        //stub
        Book book = new Book(1L, "junit강의", "재현");
        Optional<Book> bookOp = Optional.of(book);
        when(repository.findById(id)).thenReturn(bookOp);

        //when
        BookResponseDto updatedDto = service.updateBook(id, requestDto);

        //then
        assertThat(updatedDto.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(updatedDto.getAuthor()).isEqualTo(requestDto.getAuthor());
    }
}
