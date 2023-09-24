package com.techcourse.controller.annotation_controller;

import com.techcourse.controller.UserSession;
import com.techcourse.domain.User;
import com.techcourse.repository.InMemoryUserRepository;
import context.org.springframework.stereotype.Controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.org.springframework.web.bind.annotation.RequestMapping;
import web.org.springframework.web.bind.annotation.RequestMethod;
import webmvc.org.springframework.web.servlet.ModelAndView;
import webmvc.org.springframework.web.servlet.view.JspView;

@Controller
public class LoginAnnotationController {

    private static final Logger log = LoggerFactory.getLogger(LoginAnnotationController.class);

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView executePostLogin(final HttpServletRequest req, final HttpServletResponse res) {
        if (UserSession.isLoggedIn(req.getSession())) {
            final String viewName = "redirect:/index.jsp";

            return new ModelAndView(new JspView(viewName));
        }

        final String viewName = InMemoryUserRepository.findByAccount(req.getParameter("account"))
                                                     .map(user -> {
                                                         log.info("User : {}", user);
                                                         return login(req, user);
                                                     })
                                                     .orElse("redirect:/401.jsp");

        return new ModelAndView(new JspView(viewName));
    }

    private String login(final HttpServletRequest request, final User user) {
        if (user.checkPassword(request.getParameter("password"))) {
            final var session = request.getSession();
            session.setAttribute(UserSession.SESSION_KEY, user);

            return "redirect:/index.jsp";
        }

        return "redirect:/401.jsp";
    }

    @RequestMapping(value = "/login/view", method = RequestMethod.GET)
    public ModelAndView executeGetLoginView(final HttpServletRequest req, final HttpServletResponse res) {
        final String viewName = UserSession.getUserFrom(req.getSession())
                                    .map(user -> {
                                        log.info("logged in {}", user.getAccount());
                                        return "redirect:/index.jsp";
                                    })
                                    .orElse("/login.jsp");

        return new ModelAndView(new JspView(viewName));
    }
}