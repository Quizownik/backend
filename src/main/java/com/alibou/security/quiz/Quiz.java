package com.alibou.security.quiz;

import com.alibou.security.question.Question;
import com.alibou.security.user.User;
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

    @ManyToOne
    @CreatedBy
    @JoinColumn(
            nullable = false,
            updatable = false
    )
    private User createdBy;

    private int position;

    @ManyToMany(mappedBy = "quizes")
    private List<Question> questions;

}
//controller tylko do quiz, ktory podaje