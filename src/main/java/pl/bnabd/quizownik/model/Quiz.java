package pl.bnabd.quizownik.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.Duration;
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
@Table(name = "Quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User creator;

    private int position;

    @ManyToMany(mappedBy = "quizes")
    private List<Question> questions;

}
