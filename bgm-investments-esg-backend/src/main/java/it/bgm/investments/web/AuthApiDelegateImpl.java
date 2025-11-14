package it.bgm.investments.web;

import it.bgm.investments.api.AutenticazioneApiDelegate;
import it.bgm.investments.api.model.CurrentUserResponseModel;
import it.bgm.investments.api.model.LoginBodyModel;
import it.bgm.investments.api.model.LoginResponseModel;
import it.bgm.investments.service.AuthService;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthApiDelegateImpl extends BaseApiDelegate implements AutenticazioneApiDelegate {

    private final AuthService authService;
    private final HttpServletRequest request;

    @Override
    public ResponseEntity<CurrentUserResponseModel> getCurrentUser() {
        return ResponseEntity.ok(authService.currentUser(getJSessionId(request)));
    }

    @Override
    public ResponseEntity<LoginResponseModel> login(@Valid LoginBodyModel body) {
        try {
            return ResponseEntity.ok(authService.login(body));
        } catch (AuthException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<Void> logout() {
        authService.logout(getJSessionId(request));
        return ResponseEntity.noContent().build();
    }
}
