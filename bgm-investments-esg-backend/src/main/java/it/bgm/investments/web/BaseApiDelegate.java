package it.bgm.investments.web;

import jakarta.servlet.http.HttpServletRequest;

public abstract class BaseApiDelegate {
    String getJSessionId(HttpServletRequest request) {
        String id = request.getHeader("JSESSIONID");
        if (id == null) id = request.getHeader("jsessionid");
        if (id == null) id = request.getHeader("JSessionid");
        if (id == null && request.getCookies() != null) {
            for (var c : request.getCookies()) {
                if ("JSESSIONID".equalsIgnoreCase(c.getName())) {
                    id = c.getValue();
                    break;
                }
            }
        }
        return id;
    }
}
