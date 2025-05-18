package com.alibou.security.answer;

import com.alibou.security.question.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Answer")
public class Answer {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String answer;

    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @CreatedBy
    @Column(
            nullable = false,
            updatable = false
    )
    private Integer createdBy;

    @LastModifiedBy
    @Column(insertable = false)
    private Integer lastModifiedBy;

    public Answer(String answer, boolean isCorrect, Question question, Integer createdBy, Integer lastModifiedBy) {
        this.answer = answer;
        this.isCorrect = isCorrect;
        this.question = question;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
    }

    public Answer(String answer, boolean isCorrect, Question question) {
        this.answer = answer;
        this.isCorrect = isCorrect;
        this.question = question;
    }
}
