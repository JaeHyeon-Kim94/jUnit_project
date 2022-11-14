package com.sympl.junit_project.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.sympl.junit_project.domain.Book;
import com.sympl.junit_project.domain.BookRepository;
import com.sympl.junit_project.service.BookService;
import com.sympl.junit_project.web.dto.request.BookRequestSaveDto;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

//통합 테스트
//컨트롤러만 단위테스트 하려면 서비스 mocking.
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")  //dev모드. application-dev.yml 바라본다.
                        //배포할 환경이 Linux라면 Linux 테스트 서버에서 통합 테스트해보아야 한다. 여기서 profile은 dev
                        //테스트 하고 실패하면 다시 수정, 성공하면 jar로 빌드.
                        //빌드 후 실제 배포할 서버에 jar를 담고, java -jar 에서 실행 옵션에 prod를 바라보도록 한다.
public class BookApiControllerTest {


    //@SpringBootTest든 @WebMvcTest든 생성자 주입 전략 사용하게 되면 에러 발생
    //msg : variable service not initialized in the default constructor
//    private final BookService service;
    //The TestContext framework does not instrument the manner
    // in which a test instance is instantiated.
    // Thus the use of @Autowired or @Inject for constructors
    // has no effect for test classes
//    @Autowired  //생성자 DI 전략 불가능.
//    private BookService service;

    @Autowired
    private BookRepository repository;

    @Autowired
    private TestRestTemplate rt;

    private static ObjectMapper om;
    private static HttpHeaders headers;
    @BeforeAll
    public static void init(){
        om = new ObjectMapper();
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void 책_저장_테스트() throws JsonProcessingException {
        //given
        //여기선 client에서 넘어오는 데이터를 가지고 테스트를 수행하는 것이기 때문에
        //BookRequestSaveDto를 데이터로 준비하는 것이 아니라
        //JSON을 준비하도록 한다.
        //DispatcherServlet이라는 font controller에서 JSON을 받으면
        // 이를 BookSaveRequestDto로 변환해주는 것이기 때문에.(@RequestBody -> RequestmappingHandlerAdapter, HttpMessageConverter)
        BookRequestSaveDto bookRequestSaveDto = new BookRequestSaveDto();
        bookRequestSaveDto.setTitle("책 저장 테스트_제목");
        bookRequestSaveDto.setAuthor("책 저장 테스트_저자");

        String body = om.writeValueAsString(bookRequestSaveDto);

        //when
        //generic 타입을 ?로 해버리면 String이 아니라 Object로 받을 것이기 때문에 String으로 지정한다.
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = rt.exchange("/api/v1/book", HttpMethod.POST, request, String.class);

        //then
        DocumentContext dc = JsonPath.parse(response.getBody());
        //JSONPath 참고.
        //$는 루트 노드. JSONPath의 모든 표현식은 이것으로 시작됨.
        String title = dc.read("$.body.title");
        String author = dc.read("$.body.author");

        assertThat(title).isEqualTo(bookRequestSaveDto.getTitle());
        assertThat(author).isEqualTo(bookRequestSaveDto.getAuthor());
    }

    @Test
    public void 책_목록_보기_테스트(){
        //given
        BookRequestSaveDto saveDto = new BookRequestSaveDto();
        saveDto.setTitle("책_목록_보기_테스트_제목");
        saveDto.setAuthor("책_목록_보기_테스트_저자");
        Book bookForSave = saveDto.toEntity();
        Book savedBook = repository.save(bookForSave);

        //when
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = rt.exchange("/api/v1/book", HttpMethod.GET, request, String.class);


        //then
        DocumentContext dc = JsonPath.parse(response.getBody());

        Integer code = dc.read("$.code");
        String title =  dc.read("$.body.items[0].title");
        String author = dc.read("$.body.items[0].author");

        assertThat(code).isEqualTo(1);
        assertThat(title).isEqualTo(saveDto.getTitle());
        assertThat(author).isEqualTo(saveDto.getAuthor());
    }
    @Sql("classpath:db/tableInit.sql")
    @Test
    public void 책_한건_보기_테스트(){
        //given
        BookRequestSaveDto saveDto = new BookRequestSaveDto();
        saveDto.setTitle("책_한건_보기_테스트_제목");
        saveDto.setAuthor("책_한건_보기_테스트_저자");
        Book bookForSave = saveDto.toEntity();
        Book savedBook = repository.save(bookForSave);
        Long id = 1L;

        //when
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = rt.exchange("/api/v1/book/"+id, HttpMethod.GET, request, String.class);

        //then
        DocumentContext dc = JsonPath.parse(response.getBody());

        Integer code = dc.read("$.code");
        String title = dc.read("$.body.title");
        String author = dc.read("$.body.author");

        assertThat(code).isEqualTo(1);
        assertThat(title).isEqualTo(saveDto.getTitle());
        assertThat(author).isEqualTo(saveDto.getAuthor());

    }

    @Sql("classpath:db/tableInit.sql")
    @Test
    public void 책_삭제하기_테스트(){
        //given
        BookRequestSaveDto saveDto = new BookRequestSaveDto();
        saveDto.setTitle("책_삭제_테스트_제목");
        saveDto.setAuthor("책_삭제_테스트_저자");
        Book bookForSave = saveDto.toEntity();
        Book savedBook = repository.save(bookForSave);
        Long id = 1L;

        //when
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = rt.exchange("/api/v1/book/"+id, HttpMethod.DELETE, request, String.class);


        //then
        DocumentContext dc = JsonPath.parse(response.getBody());
        Integer code = dc.read("$.code");
        assertThat(code).isEqualTo(1);



    }

    @Sql("classpath:db/tableInit.sql")
    @Test
    public void 책_수정하기_테스트() throws JsonProcessingException {
        //given
        BookRequestSaveDto saveDto = new BookRequestSaveDto();
        saveDto.setTitle("책_수정전_테스트_제목");
        saveDto.setAuthor("책_수정전_테스트_저자");
        Book bookForSave = saveDto.toEntity();
        Book savedBook = repository.save(bookForSave);
        Long id = 1L;

        savedBook.update("책_수정후_테스트_제목", "책_수정후_테스트_저자");


        String body = om.writeValueAsString(savedBook);
        //when
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = rt.exchange("/api/v1/book/"+id, HttpMethod.PUT, request, String.class);

        //then
        DocumentContext dc = JsonPath.parse(response.getBody());

        Integer code = dc.read("$.code");
        String title = dc.read("$.body.title");
        String author = dc.read("$.body.author");

        assertThat(code).isEqualTo(1);
        assertThat(title).isEqualTo(savedBook.getTitle());
        assertThat(author).isEqualTo(savedBook.getAuthor());

    }

}
