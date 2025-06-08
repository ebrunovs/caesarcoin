package br.edu.ifpb.pweb2.caesarcoin.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        HttpSession httpSession = request.getSession(false);

        if (httpSession != null) {
            AccountOwner user = (AccountOwner) httpSession.getAttribute("user");

            if (user != null) {
                String contextPath = request.getContextPath();
                String path = request.getRequestURI();

                // Remove the contextPath from the path
                String relativePath = path.substring(contextPath.length());

                // Check if the user is accessing a restricted path
                boolean requerAdmin = relativePath.startsWith("/accountowners") || relativePath.startsWith("/accounts");

                // If the user is accessing a restricted path, check if they are an admin
                if (requerAdmin & !user.isAdmin()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
                    return false;
                }
                
                // User is logged in and has access to the requested path
                return true;
            }
        }

        // No session or user not logged in, redirect to login page
        String baseUrl = request.getContextPath();
        response.sendRedirect(response.encodeRedirectURL(baseUrl + "/auth"));
        return false;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) throws Exception {
        // No specific action needed after completion
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) throws Exception {
        // No specific action needed after the handler has been executed
    }
}
