package com.alibou.security.result;

import com.alibou.security.answer.Answer;
import com.alibou.security.answer.AnswerRepository;
import com.alibou.security.quiz.Level;
import com.alibou.security.quiz.Quiz;
import com.alibou.security.quiz.QuizRepository;
import com.alibou.security.stats.StatRepository;
import com.alibou.security.stats.Statistics;
import com.alibou.security.user.User;
import com.alibou.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor


public class ResultService {

    private final ResultRepository resultRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;
    private final StatRepository statRepository;

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

        Long score = (long)correctCount / chosen.size();
        handleScoringMechanism(user,quiz,score, correctCount);

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
                result.getQuiz().getName(),
                result.getFinishedAt(),
                result.getDuration(),
                result.getQuestionOrder(),
                result.getChosenAnswers(),
                correctCount,
                failCount
        );
    }
    private void handleScoringMechanism(User user, Quiz quiz, Long score, Integer correctCount) {
        var statisticsOpt = statRepository.findByQuiz(quiz);

        Level currentLevel = quiz.getLevel();

       int factor = Level.toInt(currentLevel);


        if (score < 0.4) {
            int temp = user.getScore() - correctCount *factor;
            if(temp < 0){
                temp = 0;
            }
            user.setScore(temp);
        }else{
            user.setScore(user.getScore() + correctCount * factor); //*factor
        }

        userRepository.save(user);

        //uaktualnij nowy poziom trudnosci:

        Statistics statistics = statisticsOpt.orElseGet(() -> new Statistics(quiz));

        Level newLevel = statistics.addScore(score);
        quiz.setLevel(newLevel);
        if(score> 0.98){ //osiagnal max
            quiz.addMaster(user);
        }
        quizRepository.save(quiz);

        statRepository.save(statistics);
    }

    public Page<ResultResponse> getResultsForCurrentUser(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return resultRepository.findAllByUserId(user.getId(), pageable)
                .map(this::toResponse);
    }

}

