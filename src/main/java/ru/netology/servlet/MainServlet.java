package ru.netology.servlet;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.configuration.JavaConfiguration;
import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private PostController controller;

    @Override
    public void init() {
        final ApplicationContext context = new
                AnnotationConfigApplicationContext(JavaConfiguration.class);
        controller = context.getBean(PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final String path = req.getRequestURI();
            final String method = req.getMethod();
            final long id = getId(path);
            switch (method) {
                case ("GET") :
                    if (id > 0) {
                        controller.getById(id, resp);
                    } else
                        controller.all(resp);
                    break;
                    case ("DELETE") :
                        if (id > 0) {
                            controller.removeById(id, resp);
                        }
                        break;
                    case ("POST") :
                        if (id == -1) {
                            controller.save(req.getReader(), resp);
                        }
                        break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof NotFoundException) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    private long getId(String path) {
        return  path.matches("/api/posts/\\d+") ?
                Long.parseLong(path.substring(path.lastIndexOf("/") + 1)) : -1;
    }
}