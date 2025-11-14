package it.bgm.investments.service;

import it.bgm.investments.api.model.CurrentUserResponseModel;
import it.bgm.investments.api.model.LoginBodyModel;
import it.bgm.investments.api.model.LoginResponseModel;
import jakarta.security.auth.message.AuthException;

public interface AuthService {
    LoginResponseModel login(LoginBodyModel body) throws AuthException;

    CurrentUserResponseModel currentUser(String jSessionId);

    void logout(String jSessionId);
}
