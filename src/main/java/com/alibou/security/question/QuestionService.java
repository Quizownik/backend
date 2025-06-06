package com.alibou.security.question;

import com.alibou.security.answer.Answer;
import com.alibou.security.answer.AnswerRepository;
import com.alibou.security.answer.AnswerResponse;
import com.alibou.security.quiz.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    public QuestionResponse create(QuestionRequest request) {
        Integer currentUserId = getCurrentUserId();

        Question question = Question.builder()
                .question(request.question())
                .category(request.category())
                .createdBy(currentUserId)
                .build();

        Question saved = questionRepository.save(question);

        List<Answer> answers = request.answers().stream()
                .map(a -> Answer.builder()
                        .answer(a.answer())
                        .isCorrect(a.isCorrect())
                        .question(saved)
                        .build())
                .toList();

        answerRepository.saveAll(answers);
        saved.setAnswers(answers);

        return toResponse(saved);
    }

    public QuestionResponse createAsSystem(QuestionRequest request, Integer userId) {
        Question question = Question.builder()
                .question(request.question())
                .category(request.category())
                .createdBy(userId)
                .lastModifiedBy(userId)
                .build();

        Question saved = questionRepository.save(question);

        List<Answer> answers = request.answers().stream()
                .map(a -> Answer.builder()
                        .answer(a.answer())
                        .isCorrect(a.isCorrect())
                        .question(saved)
                        .createdBy(userId)
                        .lastModifiedBy(userId)
                        .build())
                .toList();

        answerRepository.saveAll(answers);
        saved.setAnswers(answers);

        return toResponse(saved);
    }

    public QuestionResponse update(Integer id, QuestionRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setQuestion(request.question());
        question.setCategory(request.category());

        questionRepository.save(question);

        // Get all answers for the question
        List<Answer> existingAnswers = answerRepository.findByQuestionId(id);

        // Delete existing answers
        existingAnswers.forEach(answer -> {
            answer.setQuestion(null); // Unlink the answer from the question
            answerRepository.delete(answer); // Delete the answer
        });

        List<Answer> newAnswers = request.answers().stream()
                .map(a -> Answer.builder()
                        .answer(a.answer())
                        .isCorrect(a.isCorrect())
                        .question(question)
                        .build())
                .toList();
        answerRepository.saveAll(newAnswers);
        question.setAnswers(newAnswers);

        return toResponse(question);
    }

    public void delete(Integer id) {
        //answerRepository.deleteById(id);
        questionRepository.deleteById(id);
    }

    private QuestionResponse toResponse(Question question) {
        List<AnswerResponse> answers = question.getAnswers().stream()
                .map(a -> new AnswerResponse(a.getId(), a.getAnswer(), a.isCorrect()))
                .toList();

        return new QuestionResponse(
                question.getId(),
                question.getQuestion(),
                question.getCategory(),
                answers
        );
    }


    public Page<QuestionResponse> findAllByCategory(Category category, Pageable pageable) {
        Page<Question> page = questionRepository.findByCategory(category, pageable);
        return page.map(this::toResponse);
    }

    public Page<QuestionResponse> findAll( Pageable pageable) {
        Page<Question> page = questionRepository.findAll(pageable);
        return page.map(this::toResponse);
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
