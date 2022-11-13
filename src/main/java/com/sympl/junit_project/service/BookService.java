package com.sympl.junit_project.service;

import com.sympl.junit_project.domain.Book;
import com.sympl.junit_project.domain.BookRepository;
import com.sympl.junit_project.util.MailSender;
import com.sympl.junit_project.web.dto.BookRequestSaveDto;
import com.sympl.junit_project.web.dto.BookResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;
    private final MailSender mailSender;

    //책 등록
    @Transactional ( rollbackFor = RuntimeException.class )
    public BookResponseDto saveBook(BookRequestSaveDto dto){
        Book bookPs = repository.save(dto.toEntity());
        //db에서 넘어온 data를 controller로 바로 return하게 되면 lazyLoading 관련한 이슈 발생할 수 있기 때문에
        //BookResponseDto를 이용한다.
        //jpa.open-in-view(?)
        //영속화된 객체는 여기서 끊어낸다.

        if(bookPs != null){
            if(!mailSender.send()){
                throw new RuntimeException("메일 전송 실패");
            }
        }
        return BookResponseDto.toDto(bookPs);
    }


    //책 목록보기
    public List<BookResponseDto> selectBookList(){
        return repository.findAll().stream()
                .map(BookResponseDto::toDto)
                .collect(Collectors.toList());
    }

    //책 한권 보기
    public BookResponseDto selectBook(Long id){
        Optional<Book> bookPs = repository.findById(id);
        if(bookPs.isPresent()){
            return BookResponseDto.toDto(bookPs.get());
        }else{
            throw new RuntimeException("해당 아이디를 찾을 수 없습니다.");
        }
    }
    //책 삭제
    //@Transactional은 exception 발생시에만 rollback하게 되는데,
    //단순히 id를 못찾는 경우는 오류가 아니기 때문에 exception 발생 안함.
    //그럼에도 적어줄 필요가 있는 것이, 지금은 단순히 삭제 하나만 수행하지만,
    //로직이 복잡해질 경우 다른 메서드 실행 때문에 exception 발생시,
    //예를 들어 save를 수차례 하고 delete를 해야 할 경우에 save만 되다가 exception 발생 후
    //delete는 되지 않는 상황이 발생할 수 있기 때문에 필요한 상황을 대비해 적어주는 것이 좋다.
    @Transactional( rollbackFor = RuntimeException.class )
    public void deleteBook(Long id){
        repository.deleteById(id);
    }


    //책 수정
    @Transactional( rollbackFor = RuntimeException.class )
    public void updateBook(Long id, BookRequestSaveDto dto){
        Optional<Book> bookOp = repository.findById(id);
        if(bookOp.isPresent()){
            Book bookPs = bookOp.get();
            bookPs.update(dto.getTitle(), dto.getAuthor());
        }else{
            throw new RuntimeException("해당 아이디를 찾을 수 없습니다.");
        }
    }// entity update만 해주면 메서드 종료시 context에 의한 dirty checking으로 update 됨.
}
