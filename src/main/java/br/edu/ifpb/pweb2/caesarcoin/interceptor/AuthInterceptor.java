package br.edu.ifpb.pweb2.caesarcoin.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import br.edu.ifpb.pweb2.caesarcoin.model.AccountOwner;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            AccountOwner user = (AccountOwner) httpSession.getAttribute("user");

            if (user != null) {
                String contextPath = request.getContextPath(); 
                String path = request.getRequestURI();

                // Remove o contextPath do path
                String relativePath = path.substring(contextPath.length());

                // Verifica se a URL acessada começa com "/correntistas" ou "/contas"
                boolean requiresAdmin = relativePath.startsWith("/accountowner") || relativePath.startsWith("/contas");

                // Se exige admin, verifica se o usuário é admin
                if (requiresAdmin && !user.isAdmin()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
                    return false;
                }

                // Está logado e tem permissão
                return true;
            }
        }

        // Sem sessão ou usuário não autenticado
        String baseUrl = request.getContextPath();
        response.sendRedirect(response.encodeRedirectURL(baseUrl + "/auth"));
        return false;
    }

    @Override 
    public void afterCompletion(HttpServletRequest request,
    HttpServletResponse response, Object handler, Exception ex)
        throws Exception{}
    
    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {}
}
