package com.alibou.security.result;

import com.alibou.security.quiz.Quiz;
import com.alibou.security.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Result")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime finishedAt;

    private Long duration; //w sekundach


    @ElementCollection
    @CollectionTable(name = "result_question_order", joinColumns = @JoinColumn(name = "result_id"))
    @Column(name = "question_id")
    private List<Integer> questionOrder;  // tylko ID pytań

    @ElementCollection
    @CollectionTable(name = "result_chosen_answers", joinColumns = @JoinColumn(name = "result_id"))
    @Column(name = "answer_id")
    private List<Integer> chosenAnswers;

}
