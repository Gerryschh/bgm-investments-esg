package it.bgm.investments.service.impl;

import it.bgm.investments.api.model.CurrentUserResponseModel;
import it.bgm.investments.api.model.LoginBodyModel;
import it.bgm.investments.api.model.LoginResponseModel;
import it.bgm.investments.api.model.UserSummaryModel;
import it.bgm.investments.domain.User;
import it.bgm.investments.mapper.UserMapper;
import it.bgm.investments.repo.UserRepository;
import it.bgm.investments.security.SessionStore;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private SessionStore sessions;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl service;

    @Test
    void login_successful() throws AuthException {
        LoginBodyModel body = new LoginBodyModel();
        body.setEmail("test@example.com");
        body.setPassword("secret");

        User user = new User();
        user.setId(1L);
        user.setPasswordHash("HASH");

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "HASH")).thenReturn(true);
        when(sessions.create(1L)).thenReturn("TOKEN");

        UserSummaryModel summary = new UserSummaryModel();
        summary.setRuolo(UserSummaryModel.RuoloEnum.USER);
        when(userMapper.toSummary(user)).thenReturn(summary);

        LoginResponseModel res = service.login(body);

        verify(userRepo).findByEmail("test@example.com");
        verify(sessions).create(1L);
        assertThat(res.getSessionId()).isEqualTo("TOKEN");
    }

    @Test
    void login_invalidCredentials_throwsEntityNotFound() {
        LoginBodyModel body = new LoginBodyModel();
        body.setEmail("wrong@example.com");
        body.setPassword("bad");

        when(userRepo.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.login(body));
    }

    @Test
    void currentUser_resolvesFromSession() {
        String token = "TOKEN";
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setNome("Mario Rossi");
        user.setEmail("mario.rossi@example.com");
        user.setRuolo("USER");

        when(sessions.userId(token)).thenReturn(userId);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        CurrentUserResponseModel res = service.currentUser(token);

        verify(sessions).userId(token);
        verify(userRepo).findById(userId);

        assertThat(res).isNotNull();
        assertThat(res.getId()).isEqualTo(userId);
        assertThat(res.getNome()).isEqualTo("Mario Rossi");
        assertThat(res.getEmail()).isEqualTo("mario.rossi@example.com");
        assertThat(res.getRuolo()).isEqualTo(CurrentUserResponseModel.RuoloEnum.USER);
    }

    @Test
    void logout_invalidatesSession() {
        String token = "TOKEN";

        service.logout(token);

        verify(sessions).invalidate(token);
    }
}