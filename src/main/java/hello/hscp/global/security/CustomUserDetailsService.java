// src/main/java/hello/hscp/global/security/CustomUserDetailsService.java
package hello.hscp.global.security;

import hello.hscp.domain.account.entity.User;
import hello.hscp.domain.account.repository.UserRepository;
import hello.hscp.global.exception.ApiException;
import hello.hscp.global.exception.ErrorCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // username = userId 문자열
    @Override
    public UserDetails loadUserByUsername(String username) {
        Long userId;
        try {
            userId = Long.valueOf(username);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "Invalid userId");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        return new SecurityUser(user.getId(), user.getRole());
    }
}
