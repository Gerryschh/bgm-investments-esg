package it.bgm.investments.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final SessionStore sessions;

    public Long userId(String jsessionId) {
        return sessions.userId(jsessionId);
    }
}
