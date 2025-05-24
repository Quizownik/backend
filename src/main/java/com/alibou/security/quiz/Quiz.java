package com.alibou.security.quiz;

import com.alibou.security.question.Question;
import com.alibou.security.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    private Integer id;

    private String name;

    @CreatedBy
    @JoinColumn(
            nullable = false,
            updatable = false
    )
    private Integer createdBy;

    @Enumerated(EnumType.STRING)
    private Category category;

    private int position;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "quiz_question",
            joinColumns = @JoinColumn(name = "quiz_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @JsonManagedReference
    private List<Question> questions;

}
// dla quizu: get by name -> contains("fraza")
// result: z pagingiem najwyzsze wyniki
// getUserDetails -> liczba ukonczonych quziow
// Question - > edit
//

//controller tylko do quiz, ktory podaje