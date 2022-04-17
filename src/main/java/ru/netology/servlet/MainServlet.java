package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
    private PostController controller;

    @Override
    public void init() {
        final PostRepository repository = new PostRepository();
        final PostService service = new PostService(repository);
        controller = new PostController(service);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {
            final String path = req.getRequestURI();
            final String method = req.getMethod();
            // primitive routing
            if (path.equals("/api/posts/\\d+")) {
                final long id = Long.parseLong(path.substring(path.lastIndexOf("/")));
                switch (method) {
                    case ("GET") :
                        controller.getById(id, resp);
                        break;
                    case ("DELETE") :
                        controller.removeById(id, resp);
                        break;
                }
            }
            if (path.equals("/api/posts")) {
                switch (method) {
                    case ("GET") :
                        controller.all(resp);
                        break;
                    case ("POST") :
                        controller.save(req.getReader(), resp);
                        break;
                }
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}