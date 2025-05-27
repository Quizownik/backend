package com.alibou.security;

import com.alibou.security.answer.Answer;
import com.alibou.security.answer.AnswerRepository;
import com.alibou.security.answer.AnswerRequest;
import com.alibou.security.auth.AuthenticationService;
import com.alibou.security.auth.RegisterRequest;
import com.alibou.security.question.QuestionRepository;
import com.alibou.security.question.QuestionRequest;
import com.alibou.security.question.QuestionService;
import com.alibou.security.quiz.Category;
import com.alibou.security.quiz.QuizRequest;
import com.alibou.security.quiz.QuizService;
import com.alibou.security.user.Role;
import com.fasterxml.jackson.databind.JsonSerializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

import static com.alibou.security.user.Role.ADMIN;
import static com.alibou.security.user.Role.MANAGER;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
		System.out.println("Admin token : eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBtYWlsLmNvbSIsImlhdCI6MTc0NzY2MTU0MSwiZXhwIjoxNzQ3NzQ3OTQxfQ.HqB3_uTVH18RbjLtFIqf3TVxcmE54XQCz7D-n-LADkw");
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service,
			QuestionService questionService,

			QuizService quizService) {
			return args -> {
				QuestionRepository qr;
			var admin = RegisterRequest.builder()
					.firstname("Admin")
					.lastname("Admin")
					.email("admin@mail.com")
					.password("password")
					.role(ADMIN)
					.build();
			System.out.println("Admin token: " + service.register(admin).getAccessToken());

			var manager = RegisterRequest.builder()
					.firstname("Admin")
					.lastname("Admin")
					.email("manager@mail.com")
					.password("password")
					.role(MANAGER)
					.build();
			System.out.println("Manager token: " + service.register(manager).getAccessToken());

				QuestionRequest q1 = QuestionRequest.builder()
						.question("Czy Akwarelista wiedział?")
						.category(Category.Grammar)
						.answers(List.of(
								new AnswerRequest("Tak", true),
								new AnswerRequest("Nie", false),
								new AnswerRequest("Nie wiem", false),
								new AnswerRequest("Wolałbym nie mówić", false)
						))
						.build();



				var q2 = QuestionRequest.builder()
						.question("Kiedy odkryto Amerykę?")
						.category(Category.Grammar)
						.answers(List.of(
								new AnswerRequest("1492", true),
								new AnswerRequest("1776", false),
								new AnswerRequest("1918", false),
								new AnswerRequest("2000", false)
						))
						.build();

				var q3 = QuestionRequest.builder()
						.question("Jaki jest wynik 2+2?")
						.category(Category.Grammar)
						.answers(List.of(
								new AnswerRequest("4", true),
								new AnswerRequest("3", false),
								new AnswerRequest("5", false),
								new AnswerRequest("22", false)
						))
						.build();
				questionService.createAsSystem(q1,1);
				questionService.createAsSystem(q2,1);
				questionService.createAsSystem(q3,1);

//				QuizRequest qu1 = QuizRequest.builder()
//						.position(101)
//						.name("Quiz testowy1")
//						.category(Category.Grammar)
//						.questionIds(List.of(1,2))
//						.build();
//
//				QuizRequest qu2 = QuizRequest.builder()
//						.position(101)
//						.name("Quiz testowy2")
//						.category(Category.Grammar)
//						.questionIds(List.of(3,2))
//						.build();
//
//				quizService.createQuizWithQuestions(qu1);
//				quizService.createQuizWithQuestions(qu2);
			};
	}
}
