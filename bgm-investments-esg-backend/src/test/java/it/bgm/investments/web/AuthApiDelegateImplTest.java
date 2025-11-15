package it.bgm.investments.web;

import it.bgm.investments.api.model.CurrentUserResponseModel;
import it.bgm.investments.api.model.LoginBodyModel;
import it.bgm.investments.api.model.LoginResponseModel;
import it.bgm.investments.service.AuthService;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthApiDelegateImplTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthApiDelegateImpl delegate;

    @Test
    void getCurrentUser_usesJSessionIdFromRequest() {
        when(request.getHeader("JSESSIONID")).thenReturn("TOKEN");
        CurrentUserResponseModel model = new CurrentUserResponseModel();
        when(authService.currentUser("TOKEN")).thenReturn(model);

        ResponseEntity<CurrentUserResponseModel> res = delegate.getCurrentUser();

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isSameAs(model);
        verify(authService).currentUser("TOKEN");
    }

    @Test
    void login_returnsOkOnSuccess() throws AuthException {
        LoginBodyModel body = new LoginBodyModel();
        LoginResponseModel out = new LoginResponseModel();
        when(authService.login(body)).thenReturn(out);

        ResponseEntity<LoginResponseModel> res = delegate.login(body);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isSameAs(out);
        verify(authService).login(body);
    }

    @Test
    void logout_callsServiceAndReturns204() {
        when(request.getHeader("JSESSIONID")).thenReturn("TOKEN");

        ResponseEntity<Void> res = delegate.logout();

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(authService).logout("TOKEN");
    }
}
