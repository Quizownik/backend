package com.alibou.security.quiz;

import com.alibou.security.answer.AnswerResponse;
import com.alibou.security.question.Question;
import com.alibou.security.question.QuestionRepository;
import com.alibou.security.question.QuestionResponse;
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
import java.util.List;



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

    public Page<QuizLabelResponse> getLabelQuizzes2(Category category,Level level, Pageable pageable, Principal connectedUser) {
        Page<Quiz> page;

        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        int userScore = user.getScore();
        if(level == Level.Default){
            if( userScore > 30 ){
                level = Level.Hard;
            }
            else if (userScore < 15){
                level = Level.Medium;
            }else{
                level = Level.Easy;
            }

        }



        if (level != Level.Mixed) {
            if (category != Category.All) {
                page = quizRepository.findAllByCategoryAndLevel(category,level, pageable);
            }else{
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

    public QuizResponse getQuiz(Integer id) {
        Quiz quiz = quizRepository.getReferenceById(id);
        return toResponse(quiz);
    }


    public QuizResponse generateQuiz(String name, Category category, Level level, Integer numberOfQuestions) {
        List<Question> questions;
        if(category != Category.Mixed){
            questions = questionRepository.findRandomByCategory(category, numberOfQuestions);
        }else{
            questions = questionRepository.findRandom(numberOfQuestions);
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

