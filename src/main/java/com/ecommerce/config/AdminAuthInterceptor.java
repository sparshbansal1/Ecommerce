package com.ecommerce.config;

import com.ecommerce.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Protects all /admin/** pages.
 *
 * - Not logged in  -> redirect to /login
 * - Logged in but not an admin -> redirect to home (/)
 * - Admin -> allowed through
 */
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        if (!"ADMIN".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }

        return true;
    }
}
