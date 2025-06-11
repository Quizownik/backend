package com.alibou.security.result;

import com.alibou.security.answer.Answer;
import com.alibou.security.answer.AnswerRepository;
import com.alibou.security.quiz.Level;
import com.alibou.security.quiz.Quiz;
import com.alibou.security.quiz.Category;
import com.alibou.security.quiz.QuizRepository;
import com.alibou.security.stats.StatRepository;
import com.alibou.security.stats.Statistics;
import com.alibou.security.user.User;
import com.alibou.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        double score = ((double)correctCount / (double)chosen.size());

        // Sprawdź, czy użytkownik jest już masterem quizu
        if (!user.getMasteredQuizzes().contains(quiz)) {
            handleScoringMechanism(user, quiz, score, correctCount);
        }

        Result result = new Result();
        result.setUser(user);
        result.setQuiz(quiz);
        result.setFinishedAt(request.finishedAt());
        result.setDuration(request.duration());
        result.setQuestionOrder(request.questionOrder());
        result.setChosenAnswers(request.chosenAnswers());
        result.setScore(score);
        result.setCategory(quiz.getCategory());

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
    private void handleScoringMechanism(User user, Quiz quiz, double score, Integer correctCount) {
        var statisticsOpt = statRepository.findByQuiz(quiz);

        Level currentLevel = quiz.getLevel();

       int factor = Level.toInt(currentLevel);

        if (score < 0.4) {
            int temp =  (user.getScore() - (int)(factor*(1-score)*2));
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

    public List<ResultPlotResponse> getUserPlots(Principal connectedUser, Category c) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        LocalDateTime now = LocalDateTime.now();
        List<ResultPlotResponse> summaries = new ArrayList<>();
        List<Category> categories = new ArrayList<>();

        if(c == Category.All){
            categories.add(Category.Grammar);
            categories.add(Category.Vocabulary);
            categories.add(Category.Mixed);
        }else  {
            categories.add(c);
        }

        for (Category category : categories) {
            List<Result> results = resultRepository.getResultsByUserAndCategory(user, category);

            double avgScore = results.stream()
                    .mapToDouble(Result::getScore)
                    .average()
                    .orElse(0.0);

            long inLast7Days = 0;
            long in7to14Days = 0;
            long in14to21Days = 0;

            for (Result result : results) {
                LocalDateTime finishedAt = result.getFinishedAt();
                if (finishedAt == null) continue;

                if (!finishedAt.isBefore(now.minusDays(7))) {
                    inLast7Days++;
                } else if (!finishedAt.isBefore(now.minusDays(14))) {
                    in7to14Days++;
                } else if (!finishedAt.isBefore(now.minusDays(21))) {
                    in14to21Days++;
                }
            }

            ResultPlotResponse summary = new ResultPlotResponse(
                    category.name(),
                    avgScore,
                    inLast7Days,
                    in7to14Days,
                    in14to21Days
            );

            summaries.add(summary);
        }
        return summaries;
    }

    public List<QuizStatsResponse> getQuizStats() {
        List<Quiz> quizzes = quizRepository.findAll(); // lub filtrowane po aktywności
        LocalDateTime now = LocalDateTime.now();

        List<QuizStatsResponse> stats = new ArrayList<>();

        for (Quiz quiz : quizzes) {
            List<Result> results = resultRepository.findByQuiz(quiz);

            List<Double> scores = results.stream()
                    .map(Result::getScore)
                    .sorted()
                    .collect(Collectors.toList());

            double median = calculateMedian(scores);
            long total = scores.size();

            long last7 = 0, days7to14 = 0, days14to21 = 0;

            for (Result result : results) {
                LocalDateTime finished = result.getFinishedAt();
                if (finished == null) continue;

                if (!finished.isBefore(now.minusDays(7))) {
                    last7++;
                } else if (!finished.isBefore(now.minusDays(14))) {
                    days7to14++;
                } else if (!finished.isBefore(now.minusDays(21))) {
                    days14to21++;
                }
            }

            stats.add(new QuizStatsResponse(
                    quiz.getId(),
                    quiz.getName(),
                    median,
                    total,
                    last7,
                    days7to14,
                    days14to21
            ));
        }

        return stats;
    }

    private double calculateMedian(List<Double> sortedScores) {
        int size = sortedScores.size();
        if (size == 0) return 0.0;

        if (size % 2 == 1) {
            return sortedScores.get(size / 2);
        } else {
            return (sortedScores.get(size / 2 - 1) + sortedScores.get(size / 2)) / 2.0;
        }
    }



}

