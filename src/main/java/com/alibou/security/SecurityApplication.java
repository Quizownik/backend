package com.alibou.security;

import com.alibou.security.answer.AnswerRequest;
import com.alibou.security.auth.AuthenticationService;
import com.alibou.security.auth.RegisterRequest;
import com.alibou.security.question.QuestionRepository;
import com.alibou.security.question.QuestionRequest;
import com.alibou.security.question.QuestionResponse;
import com.alibou.security.question.QuestionService;
import com.alibou.security.quiz.Category;
import com.alibou.security.quiz.QuizRequest;
import com.alibou.security.quiz.QuizService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

import static com.alibou.security.user.Role.*;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(
            AuthenticationService service,
            QuestionService questionService,
            QuizService quizService) {
        return args -> {

            var admin = RegisterRequest.builder()
                    .firstname("Admin")
                    .lastname("Adminski")
                    .username("Admineusz")
                    .email("admin@mail.com")
                    .password("password")
                    .role(ADMIN)
                    .build();
            System.out.println("Admin token: " + service.register(admin).getAccessToken());

            var manager = RegisterRequest.builder()
                    .firstname("Menadżer")
                    .lastname("Menadżerski")
                    .username("Menadżereusz1")
                    .email("manager@mail.com")
                    .password("password")
                    .role(MANAGER)
                    .build();
            System.out.println("Manager token: " + service.register(manager).getAccessToken());

            var user = RegisterRequest.builder()
                    .firstname("Krystian")
                    .lastname("Juszczyk")
                    .username("Juhas")
                    .email("juszczyk-krystian@wp.pl")
                    .password("qwerty123!")
                    .role(USER)
                    .build();
            System.out.println("User token: " + service.register(user).getAccessToken());

            QuestionRequest q1 = new QuestionRequest(
                    "Czy Akwarelista wiedział?",
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Tak", true),
                            new AnswerRequest("Nie", false),
                            new AnswerRequest("Nie wiem", false),
                            new AnswerRequest("Wolałbym nie mówić", false)
                    )
            );

            QuestionRequest q2 = new QuestionRequest(
                    "Kiedy odkryto Amerykę?",
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("1492", true),
                            new AnswerRequest("1776", false),
                            new AnswerRequest("1918", false),
                            new AnswerRequest("2000", false)
                    )
            );

            QuestionRequest q3 = new QuestionRequest(
                    "Jaki jest wynik 2+2?",
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("4", true),
                            new AnswerRequest("3", false),
                            new AnswerRequest("5", false),
                            new AnswerRequest("22", false)
                    )
            );

            questionService.createAsSystem(q1, 1);
            questionService.createAsSystem(q2, 1);
            questionService.createAsSystem(q3, 1);

            for (int i = 0; i < 39; i++){
                Category category = i % 2 == 0 ? Category.Grammar : Category.Vocabulary;
                QuizRequest quizRequest = new QuizRequest(
                        i,
                        "Quiz " + i,
                        category,
                        List.of(1, 2, 3)
                );
                quizService.createQuizWithQuestions(quizRequest);
            }

        };
    }
}
