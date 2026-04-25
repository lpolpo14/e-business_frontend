package unipi.d3fender.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import unipi.d3fender.dtos.RegisterRequest;
import unipi.d3fender.models.User;
import unipi.d3fender.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .subscriptionPlan(null)
                .build();

        userRepository.save(user);
    }
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName().equals("anonymousUser")) {
            throw new IllegalStateException("No authenticated user");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public void selectSubscription(String plan) {
        if (!plan.equals("FREE") && !plan.equals("PRO")) {
            throw new IllegalArgumentException("Invalid subscription plan");
        }

        User user = getCurrentUser();
        user.setSubscriptionPlan(plan);
        userRepository.save(user);
    }

    public boolean currentUserHasSubscription() {
        User user = getCurrentUser();
        return user.getSubscriptionPlan() != null;
    }
}