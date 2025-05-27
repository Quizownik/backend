package com.alibou.security.result;

import com.alibou.security.answer.Answer;
import com.alibou.security.answer.AnswerRepository;
import com.alibou.security.quiz.QuizRepository;
import com.alibou.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor


public class ResultService {

    private final ResultRepository resultRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;

    public Result save(ResultRequest request) {
        var user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        var quiz = quizRepository.findById(request.quizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Oblicz poprawne odpowiedzi
        List<Integer> chosen = request.chosenAnswers();
        List<Integer> correctAnswers = answerRepository.findAllById(chosen).stream()
                .filter(Answer::isCorrect)
                .map(Answer::getId)
                .toList();

        int correctCount = correctAnswers.size();
        int failCount = chosen.size() - correctCount;

        user.setNumOfDoneQuizzes(user.getNumOfDoneQuizzes() + 1);
        if (failCount == 0) {
            user.setNumOfOnlyFullyCorrectQuizzes(user.getNumOfOnlyFullyCorrectQuizzes() + 1);
        }
        userRepository.save(user);

        System.out.println("idzie robota");

        Result result = new Result();
        result.setUser(user);
        result.setQuiz(quiz);
        result.setFinishedAt(request.finishedAt());
        result.setDuration(request.duration());
        result.setQuestionOrder(request.questionOrder());
        result.setChosenAnswers(request.chosenAnswers());

        return resultRepository.save(result);
    }

    public Page<ResultResponse> getAll(Pageable pageable) {
        return resultRepository.findAll(pageable)
                .map(this::toResponse);
    }

    private ResultResponse toResponse(Result result) {
        List<Integer> chosen = result.getChosenAnswers();
        List<Integer> correctAnswers = answerRepository.findAllById(chosen).stream()
                .filter(Answer::isCorrect)
                .map(Answer::getId)
                .toList();

        int correctCount = correctAnswers.size();
        int failCount = chosen.size() - correctCount;

        return new ResultResponse(
                result.getQuiz().getId(),
                result.getUser().getId(),
                result.getFinishedAt(),
                result.getDuration(),
                result.getQuestionOrder(),
                result.getChosenAnswers(),
                correctCount,
                failCount
        );
    }
}

