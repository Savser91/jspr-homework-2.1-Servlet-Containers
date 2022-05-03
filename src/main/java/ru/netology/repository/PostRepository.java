package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepository {

    private final Map<Long, Post> postMap = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong();

    public PostRepository() {
        counter.set(1);
    }

    public List<Post> all() {
        return new ArrayList<>(postMap.values());
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(postMap.get(id));
    }

    public Post save(Post post) {
        long id = post.getId();
        if (id == 0) {
            id = counter.getAndIncrement();
            postMap.put(id, post);
        } else {
            if (postMap.containsKey(post.getId())) {
                postMap.replace(post.getId(), post);
            } else {
                throw new NotFoundException();
            }
        }
        return post;
    }

    public boolean removeById(long id) {
        if (postMap.containsKey(id)) {
            postMap.remove(id);
            return true;
        } else
            return false;
    }
}