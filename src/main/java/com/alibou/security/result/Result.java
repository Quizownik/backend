package com.alibou.security.result;


import com.alibou.security.question.Question;
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

    private Duration duration;
    //list <kolejnosc pytan>
    //list <id_question_kotre zaznaczyles>
    //list <id poprawna odpowiedz>

    @OneToMany
    private List<Question> correctQuestions;
    //czy dodac zapisywanie niepoprawnych odpowiedzi?
}
