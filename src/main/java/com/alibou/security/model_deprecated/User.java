package com.alibou.security.model_deprecated;//package com.alibou.security.model_deprecated;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.With;
////import com.alibou.security.
//import com.alibou.security.enums.UserRole;
//
//import java.util.List;
//
//@Data
//@With
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name = "UserQ")
//public class User {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long id;
//
//    @Enumerated(EnumType.STRING)
//    private UserRole role;
//
//    private String username;
//
//    private String password;
//
//    @OneToMany(mappedBy = "creator")
//    private List<Quiz> quizzes;
//
//}
