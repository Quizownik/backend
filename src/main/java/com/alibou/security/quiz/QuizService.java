package com.alibou.security.quiz;

import com.alibou.security.answer.AnswerResponse;
import com.alibou.security.question.Question;
import com.alibou.security.question.QuestionRepository;
import com.alibou.security.question.QuestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public void save(QuizRequest request) {
        var quiz = Quiz.builder()
//                .id(request.getId())
//                .createdBy(request.getCreatedBy())
                //         .position(request.getPosition())
                .build();
        quizRepository.save(quiz);
    }

    public List<QuizResponse> findAll() {
        return quizRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }


    public String createQuizWithQuestions(QuizRequest request) {

        List<Question> questions = questionRepository.findByIdInAndCategory(request.questionIds(), request.category());
        Quiz quiz = Quiz.builder()
                .position(request.position())
                .questions(questions)
                .category(request.category())
                .name(request.name())
                .build();

        // Dodaj quiz do każdej relacji Question, jeśli potrzebujesz synchronizacji dwukierunkowej:
        for (Question q : questions) {
            q.getQuizes().add(quiz);
        }
        quizRepository.save(quiz);

        if(questions.size() != request.questionIds().size()) {
            return "Some questions had incorrect category";
        }
        return "Quiz created successfully";
    }

    public void delete(Integer id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // 1. Usuń relacje między Quiz a Question
        for (Question question : quiz.getQuestions()) {
            question.getQuizes().remove(quiz); // usuń quiz z pytania
        }
        quiz.getQuestions().clear(); // usuń pytania z quiz
        quizRepository.save(quiz);
        quizRepository.deleteById(id);
    }

    public QuizResponse update(Integer id, QuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Zaktualizuj pola
        quiz.setPosition(request.position());
        quiz.setName(request.name());


        // Pytania po ID i kategorii (opcjonalnie filtruj po kategorii)
        List<Question> updatedQuestions = questionRepository.findByIdIn(request.questionIds());
        quiz.setQuestions(updatedQuestions);

        Quiz saved = quizRepository.save(quiz);
        return toResponse(saved);
    }

    private QuizResponse toResponse(Quiz quiz) {
        List<QuestionResponse> questionResponses = quiz.getQuestions().stream()
                .map(q -> new QuestionResponse(
                        q.getId(),
                        q.getQuestion(),
                        q.getCategory(),
                        q.getAnswers().stream()
                                .map(a -> new AnswerResponse(
                                        a.getId(),
                                        a.getAnswer(),
                                        a.isCorrect()
                                ))
                                .toList()
                ))
                .toList();

        return new QuizResponse(
                quiz.getId(),
                quiz.getPosition(),
                quiz.getName(),
                quiz.getCategory().toString(),
                questionResponses
        );
    }


    public Page<QuizResponse> getQuizzes(Category category, Pageable pageable) {
        Page<Quiz> page;

        if (category != null) {
            page = quizRepository.findAllByCategory(category, pageable);
        } else {
            page = quizRepository.findAll(pageable);
        }

        return page.map(this::toResponse);
    }

}

