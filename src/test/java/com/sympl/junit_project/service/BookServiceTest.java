package com.sympl.junit_project.service;

import com.sympl.junit_project.domain.BookRepository;
import com.sympl.junit_project.util.MailSender;
import com.sympl.junit_project.web.dto.BookRequestSaveDto;
import com.sympl.junit_project.web.dto.BookResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

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
}
