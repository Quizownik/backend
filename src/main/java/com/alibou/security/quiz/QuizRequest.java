package com.alibou.security.quiz;

import com.alibou.security.question.QuestionRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class QuizRequest {

//    private Integer id;
//    private User createdBy;
    private int position;
    private List<QuestionRequest> questions;

}
