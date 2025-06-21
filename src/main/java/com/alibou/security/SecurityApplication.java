package com.alibou.security;

import com.alibou.security.answer.Answer;
import com.alibou.security.answer.AnswerRequest;
import com.alibou.security.auth.AuthenticationService;
import com.alibou.security.auth.RegisterRequest;
import com.alibou.security.question.QuestionRepository;
import com.alibou.security.question.QuestionRequest;
import com.alibou.security.question.QuestionResponse;
import com.alibou.security.question.QuestionService;
import com.alibou.security.quiz.*;
import com.alibou.security.result.ResultRequest;
import com.alibou.security.result.ResultService;
import com.alibou.security.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.alibou.security.user.Role.*;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner commandLineRunner(
            AuthenticationService service,
            QuestionService questionService,
            QuizService quizService,
            QuizRepository quizRepository,
            UserRepository userRepository,
            ResultService resultService) {
        return args -> {

            var admin = RegisterRequest.builder()
                    .firstname("Admin")
                    .lastname("Adminski")
                    .username("Admineusz")
                    .email("admin@mail.com")
                    .password("password123!")
                    .role(ADMIN)
                    .build();
            // Rejestracja i zapis użytkownika z rolą ADMIN
            System.out.println("Admin token: " + service.register(admin).getAccessToken());
            // Pobieramy zapisanego użytkownika z bazy danych
            var savedAdmin = userRepository.findByEmail("admin@mail.com")
                    .orElseThrow(() -> new RuntimeException("Nie udało się zapisać administratora"));

            var manager = RegisterRequest.builder()
                    .firstname("Menadżer")
                    .lastname("Menadżerski")
                    .username("Menadżereusz1")
                    .email("manager@mail.com")
                    .password("password123!")
                    .role(MANAGER)
                    .build();
            // Rejestracja i zapis użytkownika z rolą MANAGER
            service.register(manager);
            // Pobieramy zapisanego użytkownika z bazy danych
            var savedManager = userRepository.findByEmail("manager@mail.com")
                    .orElseThrow(() -> new RuntimeException("Nie udało się zapisać managera"));

            var user = RegisterRequest.builder()
                    .firstname("Krystian")
                    .lastname("Juszczyk")
                    .username("Juhas")
                    .email("juszczyk-krystian@wp.pl")
                    .password("qwerty123!")
                    .role(USER)
                    .build();
            // Rejestracja i zapis użytkownika z rolą USER
            service.register(user);
            // Pobieramy zapisanego użytkownika z bazy danych
            var savedUser = userRepository.findByEmail("juszczyk-krystian@wp.pl")
                    .orElseThrow(() -> new RuntimeException("Nie udało się zapisać użytkownika"));

            System.out.println("Zapisano użytkowników: Admin (ID: " + savedAdmin.getId() + "), Manager (ID: " + savedManager.getId() +
                    "), User (ID: " + savedUser.getId() + ")");

            // Lists to hold all question IDs for each category
            List<Integer> allGrammarQuestionIds = new ArrayList<>();
            List<Integer> allVocabularyQuestionIds = new ArrayList<>();

            // Initialize a counter for assigning unique question IDs
            int currentQuestionId = 1;

            // --- Initial questions provided in the example (retained for consistency) ---
            QuestionRequest q1 = new QuestionRequest(
                    "Which sentence is grammatically correct?",
                    Level.Easy,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("The watercolorist knew the answer.", true),
                            new AnswerRequest("The watercolorist knowed the answer.", false),
                            new AnswerRequest("The watercolorist knowing the answer.", false),
                            new AnswerRequest("The watercolorist knows the answer yesterday.", false)
                    )
            );
            questionService.createAsSystem(q1, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest q2 = new QuestionRequest(
                    "Choose the sentence with correct punctuation.",
                    Level.Easy,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("When was America discovered?", true),
                            new AnswerRequest("When was America discovered.", false),
                            new AnswerRequest("When, was America discovered?", false),
                            new AnswerRequest("When was America discovered", false)
                    )
            );
            questionService.createAsSystem(q2, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest q3 = new QuestionRequest(
                    "Select the sentence with correct subject-verb agreement.",
                    Level.Easy,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Two plus two equals four.", true),
                            new AnswerRequest("Two plus two equal four.", false),
                            new AnswerRequest("Two plus two are four.", false),
                            new AnswerRequest("Two plus two is equal to four.", false)
                    )
            );
            questionService.createAsSystem(q3, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            // --- Generate 20 Vocabulary Questions ---
            QuestionRequest vocabQ1 = new QuestionRequest(
                    "What is a synonym for 'beautiful'?",
                    Level.Easy,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("Pretty", true),
                            new AnswerRequest("Ugly", false),
                            new AnswerRequest("Fast", false),
                            new AnswerRequest("Big", false)
                    )
            );
            questionService.createAsSystem(vocabQ1, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ2 = new QuestionRequest(
                    "What does 'ephemeral' mean?",
                    Level.Easy,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("Lasting for a short time", true),
                            new AnswerRequest("Eternal", false),
                            new AnswerRequest("Delicious", false),
                            new AnswerRequest("Loud", false)
                    )
            );
            questionService.createAsSystem(vocabQ2, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ3 = new QuestionRequest(
                    "Choose the antonym for 'hot'.",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("Cold", true),
                            new AnswerRequest("Warm", false),
                            new AnswerRequest("Wet", false),
                            new AnswerRequest("Dry", false)
                    )
            );
            questionService.createAsSystem(vocabQ3, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ4 = new QuestionRequest(
                    "What is a 'paradox'?",
                    Level.Hard,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("A seemingly self-contradictory statement that may nonetheless be true", true),
                            new AnswerRequest("A clear and obvious explanation", false),
                            new AnswerRequest("A type of plant", false),
                            new AnswerRequest("A quick movement", false)
                    )
            );
            questionService.createAsSystem(vocabQ4, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ5 = new QuestionRequest(
                    "What is a synonym for 'wise'?",
                    Level.Hard,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("Prudent", true),
                            new AnswerRequest("Foolish", false),
                            new AnswerRequest("Young", false),
                            new AnswerRequest("Old", false)
                    )
            );
            questionService.createAsSystem(vocabQ5, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ6 = new QuestionRequest(
                    "What does 'benevolent' mean?",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("Kind and generous", true),
                            new AnswerRequest("Malicious", false),
                            new AnswerRequest("Calm", false),
                            new AnswerRequest("Nervous", false)
                    )
            );
            questionService.createAsSystem(vocabQ6, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ7 = new QuestionRequest(
                    "Choose the antonym for 'happiness'.",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("Misery", true),
                            new AnswerRequest("Joy", false),
                            new AnswerRequest("Success", false),
                            new AnswerRequest("Smile", false)
                    )
            );
            questionService.createAsSystem(vocabQ7, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ8 = new QuestionRequest(
                    "What is a 'metaphor'?",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("A figure of speech in which a word or phrase is applied to an object or action to which it is not literally applicable", true),
                            new AnswerRequest("An exact description", false),
                            new AnswerRequest("A type of insect", false),
                            new AnswerRequest("A musical instrument", false)
                    )
            );
            questionService.createAsSystem(vocabQ8, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ9 = new QuestionRequest(
                    "What is a synonym for 'prolonged'?",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("Extended", true),
                            new AnswerRequest("Brief", false),
                            new AnswerRequest("Short", false),
                            new AnswerRequest("Rapid", false)
                    )
            );
            questionService.createAsSystem(vocabQ9, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ10 = new QuestionRequest(
                    "What does 'ambivalent' mean?",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("Having mixed feelings or contradictory ideas about something or someone", true),
                            new AnswerRequest("Confident", false),
                            new AnswerRequest("Decisive", false),
                            new AnswerRequest("Objective", false)
                    )
            );
            questionService.createAsSystem(vocabQ10, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ11 = new QuestionRequest(
                    "Choose the antonym for 'begin'.",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("End", true),
                            new AnswerRequest("Start", false),
                            new AnswerRequest("Continue", false),
                            new AnswerRequest("Wait", false)
                    )
            );
            questionService.createAsSystem(vocabQ11, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ12 = new QuestionRequest(
                    "What is an 'allegory'?",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("A story, poem, or picture that can be interpreted to reveal a hidden meaning, typically a moral or political one", true),
                            new AnswerRequest("A historical fact", false),
                            new AnswerRequest("A single word", false),
                            new AnswerRequest("A type of building", false)
                    )
            );
            questionService.createAsSystem(vocabQ12, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ13 = new QuestionRequest(
                    "What is a synonym for 'impatient'?",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("Restless", true),
                            new AnswerRequest("Patient", false),
                            new AnswerRequest("Calm", false),
                            new AnswerRequest("Relaxed", false)
                    )
            );
            questionService.createAsSystem(vocabQ13, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ14 = new QuestionRequest(
                    "What does 'exemplify' mean?",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("To be a typical example of", true),
                            new AnswerRequest("To hide", false),
                            new AnswerRequest("To destroy", false),
                            new AnswerRequest("To create", false)
                    )
            );
            questionService.createAsSystem(vocabQ14, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ15 = new QuestionRequest(
                    "Choose the antonym for 'easy'.",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("Difficult", true),
                            new AnswerRequest("Simple", false),
                            new AnswerRequest("Fast", false),
                            new AnswerRequest("Slow", false)
                    )
            );
            questionService.createAsSystem(vocabQ15, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ16 = new QuestionRequest(
                    "What is 'irony'?",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("The expression of one's meaning by using language that normally signifies the opposite, typically for humorous or emphatic effect", true),
                            new AnswerRequest("An expression of agreement", false),
                            new AnswerRequest("A request for help", false),
                            new AnswerRequest("A way of laughing", false)
                    )
            );
            questionService.createAsSystem(vocabQ16, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ17 = new QuestionRequest(
                    "What is a synonym for 'complex'?",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("Intricate", true),
                            new AnswerRequest("Simple", false),
                            new AnswerRequest("Clear", false),
                            new AnswerRequest("Easy", false)
                    )
            );
            questionService.createAsSystem(vocabQ17, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ18 = new QuestionRequest(
                    "What does 'resolute' mean?",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("Admirably purposeful, determined, and unwavering", true),
                            new AnswerRequest("Hesitant", false),
                            new AnswerRequest("Uncertain", false),
                            new AnswerRequest("Calm", false)
                    )
            );
            questionService.createAsSystem(vocabQ18, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ19 = new QuestionRequest(
                    "Choose the antonym for 'truth'.",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("Falsehood", true),
                            new AnswerRequest("Myth", false),
                            new AnswerRequest("Fact", false),
                            new AnswerRequest("Knowledge", false)
                    )
            );
            questionService.createAsSystem(vocabQ19, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);

            QuestionRequest vocabQ20 = new QuestionRequest(
                    "What is 'personification'?",
                    Level.Medium,
                    Category.Vocabulary,
                    List.of(
                            new AnswerRequest("The attribution of a personal nature or human characteristics to something non-human, or the representation of an abstract quality in human form", true),
                            new AnswerRequest("A description of a person", false),
                            new AnswerRequest("A form of sculpture", false),
                            new AnswerRequest("A type of dance", false)
                    )
            );
            questionService.createAsSystem(vocabQ20, currentQuestionId);
            allVocabularyQuestionIds.add(currentQuestionId++);


            // --- Generate 20 Grammar Questions ---
            QuestionRequest grammarQ1 = new QuestionRequest(
                    "Identify the noun in the sentence: \"The dog runs quickly.\"",
                    Level.Medium,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Dog", true),
                            new AnswerRequest("Runs", false),
                            new AnswerRequest("Quickly", false),
                            new AnswerRequest("The", false)
                    )
            );
            questionService.createAsSystem(grammarQ1, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ2 = new QuestionRequest(
                    "Which sentence is grammatically correct?",
                    Level.Medium,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("He went home.", true),
                            new AnswerRequest("He go home.", false),
                            new AnswerRequest("Him went home.", false),
                            new AnswerRequest("He goes home.", false)
                    )
            );
            questionService.createAsSystem(grammarQ2, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ3 = new QuestionRequest(
                    "What is the plural form of 'child'?",
                    Level.Medium,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Children", true),
                            new AnswerRequest("Childs", false),
                            new AnswerRequest("Childes", false),
                            new AnswerRequest("Child's", false)
                    )
            );
            questionService.createAsSystem(grammarQ3, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ4 = new QuestionRequest(
                    "Identify the verb in the sentence: \"The birds sing loudly.\"",
                    Level.Hard,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Sing", true),
                            new AnswerRequest("Birds", false),
                            new AnswerRequest("Loudly", false),
                            new AnswerRequest("The", false)
                    )
            );
            questionService.createAsSystem(grammarQ4, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ5 = new QuestionRequest(
                    "Which sentence contains a spelling error?",
                    Level.Hard,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("I recieved a letter.", true), // Intentional error for the question
                            new AnswerRequest("I received a letter.", false),
                            new AnswerRequest("She wrote a book.", false),
                            new AnswerRequest("They played outside.", false)
                    )
            );
            questionService.createAsSystem(grammarQ5, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ6 = new QuestionRequest(
                    "What is the past tense of 'eat'?",
                    Level.Hard,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Ate", true),
                            new AnswerRequest("Eaten", false),
                            new AnswerRequest("Eatting", false),
                            new AnswerRequest("Eats", false)
                    )
            );
            questionService.createAsSystem(grammarQ6, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ7 = new QuestionRequest(
                    "Identify the adjective in the sentence: \"She has a red dress.\"",
                    Level.Hard,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Red", true),
                            new AnswerRequest("She", false),
                            new AnswerRequest("Has", false),
                            new AnswerRequest("Dress", false)
                    )
            );
            questionService.createAsSystem(grammarQ7, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ8 = new QuestionRequest(
                    "Which of the following is a coordinating conjunction?",
                    Level.Hard,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("And", true),
                            new AnswerRequest("Because", false),
                            new AnswerRequest("Although", false),
                            new AnswerRequest("While", false)
                    )
            );
            questionService.createAsSystem(grammarQ8, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ9 = new QuestionRequest(
                    "What is the correct punctuation for a question?",
                    Level.Hard,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("?", true),
                            new AnswerRequest(".", false),
                            new AnswerRequest("!", false),
                            new AnswerRequest(",", false)
                    )
            );
            questionService.createAsSystem(grammarQ9, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ10 = new QuestionRequest(
                    "Identify the preposition in the sentence: \"The cat is under the table.\"",
                    Level.Hard,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Under", true),
                            new AnswerRequest("Cat", false),
                            new AnswerRequest("Table", false),
                            new AnswerRequest("Is", false)
                    )
            );
            questionService.createAsSystem(grammarQ10, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ11 = new QuestionRequest(
                    "Which sentence uses the present perfect tense correctly?",
                    Level.Hard,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("I have finished my homework.", true),
                            new AnswerRequest("I finished my homework yesterday.", false),
                            new AnswerRequest("I am finishing my homework now.", false),
                            new AnswerRequest("I will finish my homework.", false)
                    )
            );
            questionService.createAsSystem(grammarQ11, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ12 = new QuestionRequest(
                    "What is the comparative form of 'good'?",
                    Level.Hard,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Better", true),
                            new AnswerRequest("Gooder", false),
                            new AnswerRequest("Best", false),
                            new AnswerRequest("More good", false)
                    )
            );
            questionService.createAsSystem(grammarQ12, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ13 = new QuestionRequest(
                    "Which word is a pronoun?",
                    Level.Hard,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("She", true),
                            new AnswerRequest("Run", false),
                            new AnswerRequest("Quickly", false),
                            new AnswerRequest("Table", false)
                    )
            );
            questionService.createAsSystem(grammarQ13, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ14 = new QuestionRequest(
                    "What is the correct form of the verb 'to be' for 'they' in the present tense?",
                    Level.Hard,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Are", true),
                            new AnswerRequest("Is", false),
                            new AnswerRequest("Am", false),
                            new AnswerRequest("Be", false)
                    )
            );
            questionService.createAsSystem(grammarQ14, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ15 = new QuestionRequest(
                    "Choose the sentence with correct subject-verb agreement.",
                    Level.Hard,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("The dogs bark loudly.", true),
                            new AnswerRequest("The dog bark loudly.", false),
                            new AnswerRequest("The dogs barks loudly.", false),
                            new AnswerRequest("The dog are barking loudly.", false)
                    )
            );
            questionService.createAsSystem(grammarQ15, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ16 = new QuestionRequest(
                    "What is the superlative form of 'small'?",
                    Level.Easy,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Smallest", true),
                            new AnswerRequest("Smaller", false),
                            new AnswerRequest("More small", false),
                            new AnswerRequest("Most small", false)
                    )
            );
            questionService.createAsSystem(grammarQ16, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ17 = new QuestionRequest(
                    "Identify the direct object in the sentence: \"She bought a new car.\"",
                    Level.Easy,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Car", true),
                            new AnswerRequest("She", false),
                            new AnswerRequest("Bought", false),
                            new AnswerRequest("New", false)
                    )
            );
            questionService.createAsSystem(grammarQ17, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ18 = new QuestionRequest(
                    "Which of the following is an adverb of manner?",
                    Level.Easy,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Slowly", true),
                            new AnswerRequest("Here", false),
                            new AnswerRequest("Today", false),
                            new AnswerRequest("Very", false)
                    )
            );
            questionService.createAsSystem(grammarQ18, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ19 = new QuestionRequest(
                    "What type of sentence is this: \"What a beautiful day!\"",
                    Level.Easy,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Exclamatory", true),
                            new AnswerRequest("Declarative", false),
                            new AnswerRequest("Interrogative", false),
                            new AnswerRequest("Imperative", false)
                    )
            );
            questionService.createAsSystem(grammarQ19, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);

            QuestionRequest grammarQ20 = new QuestionRequest(
                    "Choose the correct spelling:",
                    Level.Easy,
                    Category.Grammar,
                    List.of(
                            new AnswerRequest("Accommodate", true),
                            new AnswerRequest("Acommodate", false),
                            new AnswerRequest("Accomodate", false),
                            new AnswerRequest("Acomodate", false)
                    )
            );
            questionService.createAsSystem(grammarQ20, currentQuestionId);
            allGrammarQuestionIds.add(currentQuestionId++);


            // --- Generate 19 Quizzes ---
            // We'll use indices to pick questions from our lists, looping back to the start if we run out.
            int grammarQuestionIndex = 0;
            int vocabularyQuestionIndex = 0;

            for (int i = 0; i < 19; i++) {
                Category category;
                Level level = Level.Mixed;
                List<Integer> questionIds = new ArrayList<>();

                if (i % 2 == 0) { // Even index for Grammar quizzes (10 quizzes: 0, 2, ..., 18)
                    category = Category.Grammar;
                    for (int j = 0; j < 3; j++) {
                        // Add question ID from the grammar list, looping if necessary
                        questionIds.add(allGrammarQuestionIds.get(grammarQuestionIndex));
                        grammarQuestionIndex = (grammarQuestionIndex + 1) % allGrammarQuestionIds.size();
                    }
                } else { // Odd index for Vocabulary quizzes (9 quizzes: 1, 3, ..., 17)
                    category = Category.Vocabulary;
                    for (int j = 0; j < 3; j++) {
                        // Add question ID from the vocabulary list, looping if necessary
                        questionIds.add(allVocabularyQuestionIds.get(vocabularyQuestionIndex));
                        vocabularyQuestionIndex = (vocabularyQuestionIndex + 1) % allVocabularyQuestionIds.size();
                    }
                }
                if(i % 5 == 0){
                    level = Level.Easy;
                }
                if(i % 7 == 0){
                    level = Level.Hard;
                }
                QuizRequest2 quizRequest = new QuizRequest2(
                        "Quiz " + i,
                        category,
                        questionIds,
                        level

                );
                quizService.createQuizWithQuestionsByAdmin(quizRequest);
            }

            // --- Create BIG ONE quiz with 30 questions ---
            String bigQuizName = "BIG ONE";
            List<Integer> bigQuizQuestionIds = new ArrayList<>();

            // Add 15 grammar questions to the big quiz
            for (int i = 0; i < Math.min(15, allGrammarQuestionIds.size()); i++) {
                bigQuizQuestionIds.add(allGrammarQuestionIds.get(i));
            }

            // Add 15 vocabulary questions to the big quiz
            for (int i = 0; i < Math.min(15, allVocabularyQuestionIds.size()); i++) {
                bigQuizQuestionIds.add(allVocabularyQuestionIds.get(i));
            }

            // Create the BIG ONE quiz
            QuizRequest2 bigQuizRequest = new QuizRequest2(
                    bigQuizName,
                    Category.Mixed, // Mixed category since we have both Grammar and Vocabulary questions
                    bigQuizQuestionIds,
                    Level.Mixed  // Mixed level for variety
            );
            quizService.createQuizWithQuestionsByAdmin(bigQuizRequest);

            System.out.println("Created BIG ONE quiz with " + bigQuizQuestionIds.size() + " questions!");

            // Pobieramy użytkownika z bazy danych na podstawie adresu email
            var actualUser = userRepository.findById(savedUser.getId())
                    .orElseThrow(() -> new RuntimeException("Nie można znaleźć użytkownika juszczyk-krystian@wp.pl"));

            System.out.println("AcutalUserID: " + actualUser.getId());
            // Pobieramy quiz z jawnie załadowanymi pytaniami
            var bigOneQuiz = quizService.findQuizWithQuestionsById(
                    quizRepository.findByName("BIG ONE").orElseThrow().getId()
            );

            Random random = new Random();
            int resultsCount = 90 + random.nextInt(100); // 30-60 wyników

            for (int i = 0; i < resultsCount; i++) {
                int daysAgo = random.nextInt(91); // 0-90 dni temu
                LocalDateTime finishedAt = LocalDateTime.now().minusDays(daysAgo);
                int duration = 60 + random.nextInt(600); // 1-10 minut

                // Przygotowujemy kopię ID pytań żeby można było je pomieszać
                List<Integer> questionOrder = new ArrayList<>();
                for (var question : bigOneQuiz.getQuestions()) {
                    questionOrder.add(question.getId());
                }
                Collections.shuffle(questionOrder);

                // Losowe odpowiedzi (wszystkie poprawne lub mieszane)
                List<Integer> chosenAnswers = new ArrayList<>();
                for (var question : bigOneQuiz.getQuestions()) {
                    var answers = question.getAnswers();
                    var correct = answers.stream().filter(Answer::isCorrect).findFirst().orElse(answers.get(0));
                    // 60% szans na poprawną odpowiedź
                    if (random.nextDouble() < 0.6) {
                        chosenAnswers.add(correct.getId());
                    } else {
                        chosenAnswers.add(answers.get(random.nextInt(answers.size())).getId());
                    }
                }

                ResultRequest resultRequest = new ResultRequest(
                        bigOneQuiz.getId().intValue(),
                        actualUser.getId(),
                        finishedAt,
                        (long) duration,
                        questionOrder,
                        chosenAnswers
                );
                resultService.save(resultRequest);
            }
            System.out.println("Dodano " + resultsCount + " przykładowych wyników dla quizu BIG ONE.");
        };
    }
}


