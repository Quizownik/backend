package com.alibou.security.user;

import com.alibou.security.quiz.Quiz;
import com.alibou.security.token.Token;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "_user",
        uniqueConstraints = {@UniqueConstraint(name="student_email_unique", columnNames = "email")}
)
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  private String firstName;
  private String lastName;
  private String username;
  @Column( nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  private Integer score;
  @Enumerated(EnumType.STRING)
  private Role role;

  private Integer numOfDoneQuizzes;

  private Integer numOfOnlyFullyCorrectQuizzes;

  @CreatedDate
  @Column(
          nullable = false,
          updatable = false
  )
  private LocalDateTime createdDate;

  @ManyToMany(mappedBy = "mastersOfQuiz")
  private List<Quiz> masteredQuizzes;


  @OneToMany(mappedBy = "user")
  private List<Token> tokens;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  public String getAppUsername() {
    return username;
  }


  public String getAlias() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
