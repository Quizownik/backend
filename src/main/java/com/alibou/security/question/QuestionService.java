package com.alibou.security.question;

import com.alibou.security.answer.Answer;
import com.alibou.security.answer.AnswerRepository;
import com.alibou.security.answer.AnswerRequest;
import com.alibou.security.answer.AnswerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    // Używane w POST (tworzenie pytania)
    public void save(QuestionRequest request) {
        // Pobierz ID aktualnie zalogowanego użytkownika
        Integer currentUserId = getCurrentUserId();

        Question question = Question.builder()
                .question(request.getQuestion())
                .createdBy(currentUserId)
                .build();

        Question savedQuestion = questionRepository.save(question);

        List<Answer> answers = request.getAnswers().stream()
                .map(answerReq -> Answer.builder()
                        .answer(answerReq.getAnswer())
                        .isCorrect(answerReq.isCorrect())
                        .question(savedQuestion)
                        .build())
                .toList();

        answerRepository.saveAll(answers);
        savedQuestion.setAnswers(answers);
    }


    // Używane w GET (pojedynczy obiekt)
    public Optional<QuestionResponse> findById(Integer id) {
        return questionRepository.findById(id)
                .map(this::toResponseDto);
    }

    // Używane w GET (wszystkie pytania)
    public List<QuestionResponse> findAll() {
        return questionRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    // Mapowanie do DTO używanego przy GET
    public QuestionResponse toResponseDto(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .answers(
                        question.getAnswers().stream()
                                .map(answer -> AnswerResponse.builder()
                                        .id(answer.getId())
                                        .answer(answer.getAnswer())
                                        .isCorrect(answer.isCorrect())
                                        .build())
                                .toList()
                )
                .build();
    }

    // (opcjonalnie) mapowanie do użycia przy edycji
    public QuestionRequest toRequestDto(Question question) {
        return QuestionRequest.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .answers(
                        question.getAnswers().stream()
                                .map(answer -> AnswerRequest.builder()
                                        .answer(answer.getAnswer())
                                        .isCorrect(answer.isCorrect())
                                        .build())
                                .toList()
                )
                .build();
    }

    private Integer getCurrentUserId() {
        var authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Użytkownik niezalogowany");
        }

        // Zakładamy, że principal to UserDetails z polem ID
        var principal = authentication.getPrincipal();

        if (principal instanceof com.alibou.security.user.User user) {
            return user.getId(); // <-- zakładając, że masz metodę getId()
        }

        throw new IllegalStateException("Nieprawidłowy typ użytkownika w kontekście");
    }

}