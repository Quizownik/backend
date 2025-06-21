package com.alibou.security.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByEmail(String email);
  List<User> findTop10ByOrderByNumOfDoneQuizzesDescUsernameAsc();
  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}
