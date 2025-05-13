package com.alibou.security.answer;

//import com.alibou.security.model_deprecated.Question;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnswerRequest {

    //private Long id;

    private String answer;
    private boolean isCorrect;

    //private Question question; czy potrzebne?
//
//    @CreatedBy
//    @Column(
//            nullable = false,
//            updatable = false
//    )
//    private Integer createdBy;
//
//    @LastModifiedBy
//    @Column(insertable = false)
//    private Integer lastModifiedBy;
}
