package com.alibou.security.quiz;

import com.alibou.security.question.Question;
import com.alibou.security.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

//Relacja @ManyToMany między Quiz i Question ma sens tylko jeżeli:
//
//1. Zamierzasz współdzielić te same obiekty Question pomiędzy różnymi quizami
//– tzn. jedna i ta sama treść pytania może pojawić się w wielu quizach.
//
//2. Nie potrzebujesz dodatkowych atrybutów przy tej relacji (np. kolejność pytań, waga pytania, czas na każde pytanie).

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @CreatedBy
    @JoinColumn(
            nullable = false,
            updatable = false
    )
    private Integer createdBy;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Level level;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "quiz_question",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @JsonManagedReference
    private List<Question> questions;


    @ManyToMany
    @JoinTable(
            name = "quiz_best_users",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> mastersOfQuiz = new ArrayList<>();

    public void addMaster(User user) {
        if (!mastersOfQuiz.contains(user)) {
            mastersOfQuiz.add(user);
        }
    }

    public void removeMaster(User user) {
        mastersOfQuiz.remove(user);
    }

}
