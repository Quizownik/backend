package com.alibou.security.quiz;

import com.alibou.security.question.Question;
import com.alibou.security.question.QuestionRequest;
import com.alibou.security.user.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

import java.util.List;

@Getter
@Setter
@Builder
public class QuizRequest_DTO {

//    private Integer id;
//    private User createdBy;
    private int position;
    private List<QuestionRequest> questions;

}
