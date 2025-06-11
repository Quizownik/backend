package com.alibou.security.quiz;

import com.alibou.security.answer.Answer;
import com.alibou.security.answer.AnswerResponse;
import com.alibou.security.question.Question;
import com.alibou.security.question.QuestionRepository;
import com.alibou.security.question.QuestionResponse;
import com.alibou.security.result.Result;
import com.alibou.security.result.ResultRepository;
import com.alibou.security.stats.StatRepository;
import com.alibou.security.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;
    private final StatRepository statRepository;


    public List<QuizResponse> findAll() {
        return quizRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }


    @Transactional
    public void createQuizWithQuestionsByAdmin(QuizRequest2 request) {
        List<Question> questions;
        if(request.category() != Category.Mixed){
            questions = questionRepository.findByIdInAndCategory(request.questionIds(), request.category());
        }else{
            questions = questionRepository.findByIdIn(request.questionIds());
        }
        Quiz quiz = Quiz.builder()
                .questions(questions)
                .category(request.category())
                .name(request.name())
                .level(request.level())
                .build();

        for (Question q : questions) {
            q.getQuizes().add(quiz);
        }
        quizRepository.save(quiz);

    }

    @Transactional
    public String createQuizWithQuestions(QuizRequest request) {
        List<Question> questions;
        if(request.category() != Category.Mixed){
            questions = questionRepository.findByIdInAndCategory(request.questionIds(), request.category());
        }else{
            questions = questionRepository.findByIdIn(request.questionIds());
        }
        Quiz quiz = Quiz.builder()
                .questions(questions)
                .category(request.category())
                .name(request.name())
                .level(Level.Mixed)
                .build();

        for (Question q : questions) {
            q.getQuizes().add(quiz);
        }
        quizRepository.save(quiz);

        if(questions.size() != request.questionIds().size()) {
            return "Some questions had incorrect category";
        }
        return "Quiz created successfully";
    }
    @Transactional
    public void delete(Integer id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));


        for (Question question : quiz.getQuestions()) {
            question.getQuizes().remove(quiz);
        }
        quiz.getQuestions().clear();
        statRepository.deleteAllByQuiz(quiz);
        quizRepository.save(quiz);
        resultRepository.deleteAllByQuiz(quiz);
        quizRepository.deleteById(id);
    }

    public QuizResponse update(Integer id, QuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        quiz.setName(request.name());

        quiz.setCategory(request.category());

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
                        q.getLevel(),
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
                quiz.getName(),
                quiz.getCategory().toString(),
                questionResponses,
                quiz.getLevel().toString()
        );
    }

    private QuizLabelResponse toLabelResponse(Quiz quiz, User user) {
        boolean isMastered = quiz.getMastersOfQuiz().stream()
                .anyMatch(master -> master.getId().equals(user.getId()));

        return new QuizLabelResponse(
                quiz.getId(),
                quiz.getName(),
                quiz.getCategory().toString(),
                quiz.getLevel().toString(),
                isMastered,
                quiz.getQuestions().size()
        );
    }

    public Page<QuizLabelResponse> getLabelQuizzes(Category category, Pageable pageable, Principal connectedUser) {
        Page<Quiz> page;

        if (category != Category.All) {
            page = quizRepository.findAllByCategory(category, pageable);
        } else {
            page = quizRepository.findAll(pageable);
        }

        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        return page.map(quiz -> toLabelResponse(quiz, user));
    }

    public Page<QuizLabelResponse> getLabelQuizzes2(Category category, Level level, Pageable pageable, Principal connectedUser) {
        Page<Quiz> page;

        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        int userScore = user.getScore();
        if(level == Level.Default){
            if( userScore > 30 ){
                level = Level.Hard;
            }
            else if (userScore > 15){
                level = Level.Medium;
            }else{
                level = Level.Easy;
            }
        }

        if (level != Level.Mixed) {
            if (category != Category.All) {
                page = quizRepository.findAllByCategoryAndLevel(category, level, pageable);
            } else {
                page = quizRepository.findAllByLevel(level, pageable);
            }
        } else {
            if (category != Category.All) {
                page = quizRepository.findAllByCategory(category, pageable);
            } else {
                page = quizRepository.findAll(pageable);
            }
        }

        return page.map(quiz -> toLabelResponse(quiz, user));
    }

    public QuizResponse getQuiz(Integer id, Principal connectedUser) {
        Quiz quiz = quizRepository.getReferenceById(id);
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // Pobierz wszystkie pytania z quizu
        List<Question> allQuestions = quiz.getQuestions();

        // Upewnij się, że pytania są unikalne - użyj Set do eliminacji duplikatów
        Set<Integer> uniqueQuestionIds = new HashSet<>();
        List<Question> uniqueQuestions = allQuestions.stream()
                .filter(q -> uniqueQuestionIds.add(q.getId())) // dodaje do set i zwraca true jeśli element był unikalny
                .collect(Collectors.toList());

        // Znajdź poprzednie wyniki użytkownika dla tego quizu
        List<Result> userResults = resultRepository.findByUserAndQuiz(user, quiz);

        // Utwórz mapę pytań, na które użytkownik odpowiedział poprawnie
        Map<Integer, Boolean> answeredCorrectly = new HashMap<>();

        // Przeanalizuj poprzednie wyniki użytkownika
        for (Result result : userResults) {
            List<Integer> questionIds = result.getQuestionOrder();
            List<Integer> chosenAnswers = result.getChosenAnswers();

            // Dla każdej odpowiedzi sprawdź, czy była poprawna
            for (int i = 0; i < questionIds.size() && i < chosenAnswers.size(); i++) {
                Integer questionId = questionIds.get(i);
                Integer answerId = chosenAnswers.get(i);

                // Sprawdź, czy wybrana odpowiedź była poprawna
                boolean isCorrect = uniqueQuestions.stream()
                        .filter(q -> q.getId().equals(questionId))
                        .flatMap(q -> q.getAnswers().stream())
                        .filter(a -> a.getId().equals(answerId))
                        .anyMatch(Answer::isCorrect);

                // Jeśli odpowiedź była poprawna, zapisz to w mapie
                if (isCorrect) {
                    answeredCorrectly.put(questionId, true);
                } else {
                    // Jeśli na to pytanie użytkownik odpowiedział błędnie, zaznacz to
                    answeredCorrectly.putIfAbsent(questionId, false);
                }
            }
        }

        // Sortuj pytania - najpierw te bez odpowiedzi lub z błędną odpowiedzią
        List<Question> sortedQuestions = new ArrayList<>(uniqueQuestions);
        sortedQuestions.sort((q1, q2) -> {
            boolean q1Correct = answeredCorrectly.getOrDefault(q1.getId(), false);
            boolean q2Correct = answeredCorrectly.getOrDefault(q2.getId(), false);

            if (answeredCorrectly.containsKey(q1.getId()) && !answeredCorrectly.containsKey(q2.getId())) {
                // q1 ma odpowiedź, q2 nie ma - q2 ma wyższy priorytet
                return 1;
            } else if (!answeredCorrectly.containsKey(q1.getId()) && answeredCorrectly.containsKey(q2.getId())) {
                // q1 nie ma odpowiedzi, q2 ma - q1 ma wyższy priorytet
                return -1;
            } else if (q1Correct && !q2Correct) {
                // q1 ma poprawną odpowiedź, q2 ma błędną - q2 ma wyższy priorytet
                return 1;
            } else if (!q1Correct && q2Correct) {
                // q1 ma błędną odpowiedź, q2 ma poprawną - q1 ma wyższy priorytet
                return -1;
            }

            // Jeśli oba pytania mają taki sam status, mieszaj losowo
            return Math.random() > 0.5 ? 1 : -1;
        });

        // Ogranicz liczbę pytań do maksymalnie 10
        List<Question> limitedQuestions = sortedQuestions.stream()
                .limit(10)
                .collect(Collectors.toList());

        // Dla każdego pytania mieszaj kolejność odpowiedzi
        limitedQuestions.forEach(question -> question.getAnswers().sort((a, b) -> Math.random() > 0.5 ? 1 : -1));

        // Utwórz tymczasowy obiekt Quiz z ograniczoną liczbą pytań
        Quiz limitedQuiz = Quiz.builder()
                .id(quiz.getId())
                .name(quiz.getName())
                .category(quiz.getCategory())
                .level(quiz.getLevel())
                .questions(limitedQuestions)
                .build();

        return toResponse(limitedQuiz);
    }

    public QuizResponse generateQuiz(QuizGenerateRequest request) {

        String name = request.name();
        Category category = request.category();
        if(category == null) {
            category = Category.Mixed;
        }
        Level level = request.level();
        if(level == null) {
            level = Level.Easy;
        }
        Integer numberOfQuestions = request.numberOfQuestions();
        if (numberOfQuestions == null || numberOfQuestions <= 0) {
            numberOfQuestions = 10; // Default number of questions
        }
        List<Question> questions;
        if(category != Category.Mixed){
            questions = questionRepository.findRandomByCategoryAndLevel(category,  numberOfQuestions, level);
        }else{
            questions = questionRepository.findRandom(numberOfQuestions, level);
        }

        Quiz quiz = Quiz.builder()
                .questions(questions)
                .category(category)
                .level(level)
                .name(name)
                .build();

        for (Question q : questions) {
            q.getQuizes().add(quiz);
        }

        quizRepository.save(quiz);

        return toResponse(quiz);
    }
}
