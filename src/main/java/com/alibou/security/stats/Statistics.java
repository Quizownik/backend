package com.alibou.security.stats;

import com.alibou.security.quiz.Level;
import com.alibou.security.quiz.Quiz;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static org.apache.commons.lang3.math.NumberUtils.max;
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "statistics")
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private Integer completedAttempts;

    public Level addScore(Long score){
        Level upcomingLevel;
        Integer valueToCompare;
        if(score >= 0.75){
            this.highScoreAttempts++;
            upcomingLevel = Level.Hard;
            valueToCompare = this.highScoreAttempts;

        }else if(score >= 0.40){
            this.mediumScoreAttempts++;
            upcomingLevel = Level.Medium;
            valueToCompare = this.mediumScoreAttempts;
        }else{
            this.lowScoreAttempts++;
            upcomingLevel = Level.Easy;
            valueToCompare = this.lowScoreAttempts;
        }
        this.completedAttempts++;



        if(this.highScoreAttempts == max(highScoreAttempts, mediumScoreAttempts, lowScoreAttempts)){
            if(this.highScoreAttempts.equals(valueToCompare)){
                return upcomingLevel;
            }
            return Level.Easy;
        }
        if(this.mediumScoreAttempts == max(highScoreAttempts, mediumScoreAttempts, lowScoreAttempts)){
            if(this.mediumScoreAttempts.equals(valueToCompare)){
                return upcomingLevel;
            }
            return Level.Medium;
        }
        if(this.lowScoreAttempts.equals(valueToCompare)){
            return upcomingLevel;
        }
        return Level.Hard;
    }

    public Statistics(Quiz quiz) {
        this.quiz = quiz;
        this.completedAttempts = 0;
        this.highScoreAttempts = 0;
        this.mediumScoreAttempts = 0;
        this.lowScoreAttempts = 0;
    }

    private Integer lowScoreAttempts;

    private Integer mediumScoreAttempts;

    private Integer highScoreAttempts;

}
