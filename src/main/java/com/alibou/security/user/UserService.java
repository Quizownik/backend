package com.alibou.security.user;

import com.alibou.security.quiz.Level;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }

    public List<UserRankingResponse> getTopRankedUsers() {
        return repository.findTop10ByOrderByNumOfDoneQuizzesDescUsernameAsc()
                .stream()
                .map(user -> new UserRankingResponse(
                        user.getAlias(),
                        user.getScore()
                ))
                .toList();
    }

    public UserStatsResponse getUserStats(Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Integer userScore = user.getScore();
        Level userLevel;
        if (userScore > 30) {
            userLevel = Level.Hard;
        } else if (userScore > 15) {
            userLevel = Level.Medium;
        } else {
            userLevel = Level.Easy;
        }
        return new UserStatsResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getAppUsername(),
                user.getEmail(),
                user.getRole(),
                userLevel,
                user.getCreatedDate(),
                user.getNumOfDoneQuizzes(),
                user.getNumOfOnlyFullyCorrectQuizzes(),
                user.getScore(),
                user.getId()
        );
    }
}

