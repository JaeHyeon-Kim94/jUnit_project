package com.sympl.junit_project.web.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CMRespDto<T> {
    private Integer code;   //1 : 성공, -1 : 실패
    private String msg;     //에러 미시지, 성공 메시지
    private T body;


    @Builder
    public CMRespDto(Integer code, String msg, T body) {
        this.code = code;
        this.msg = msg;
        this.body = body;
    }
}
