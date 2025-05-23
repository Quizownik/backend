package com.alibou.security.quiz;

import com.alibou.security.question.Question;
import com.alibou.security.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public void save(QuizRequest request) {
        var quiz = Quiz.builder()
//                .id(request.getId())
//                .createdBy(request.getCreatedBy())
                .position(request.getPosition())
                .build();
        quizRepository.save(quiz);
    }

    public List<Quiz> findAll() {
        return quizRepository.findAll();
    }



    public void addQuestionsToQuiz(Integer quizId, List<Integer> questionIds) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        List<Question> questionsToAdd = questionRepository.findAllById(questionIds);

        // Dodaj tylko te, których jeszcze nie ma
        for (Question q : questionsToAdd) {
            if (!quiz.getQuestions().contains(q)) {
                quiz.getQuestions().add(q);
                q.getQuizes().add(quiz); // synchronizacja dwukierunkowej relacji
            }
        }

        quizRepository.save(quiz);
    }

    public Quiz createQuizWithQuestions(int position, List<Integer> questionIds) {
        List<Question> questions = questionRepository.findAllById(questionIds);

        Quiz quiz = Quiz.builder()
                .position(position)
                .questions(questions)
                .build();

        // Dodaj quiz do każdej relacji Question, jeśli potrzebujesz synchronizacji dwukierunkowej:
        for (Question q : questions) {
            q.getQuizes().add(quiz);
        }

        return quizRepository.save(quiz);
    }
}

