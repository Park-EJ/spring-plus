package org.example.expert.domain.todo.repository;


import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class TodoRepositoryQueryImpl implements TodoRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        Todo result = jpaQueryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<TodoSearchResponse> searchTodos(String keyword, LocalDate startDate, LocalDate endDate, String managerNickname, Pageable pageable) {

        List<TodoSearchResponse> results = jpaQueryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.id,
                        todo.title,
                        manager.countDistinct(),
                        comment.countDistinct()
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(todo.comments, comment)
                .leftJoin(todo.user, user)
                .where(
                        keywordContains(keyword),
                        dateRange(startDate, endDate),
                        managerNicknameContains(managerNickname)
                )
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(todo.count())
                .from(todo)
                .where(
                        keywordContains(keyword),
                        dateRange(startDate, endDate),
                        managerNicknameContains(managerNickname)
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression keywordContains(String keyword) {
        return StringUtils.hasText(keyword) ? todo.title.containsIgnoreCase(keyword) : null;
    }

    private BooleanExpression dateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;

        BooleanExpression condition = null;
        if (startDateTime != null) {
            condition = todo.createdAt.goe(startDateTime);
        }
        if (endDateTime != null) {
            condition = (condition != null) ? condition.and(todo.createdAt.loe(endDateTime)) : todo.createdAt.loe(endDateTime);
        }
        return condition;
    }

    private BooleanExpression managerNicknameContains(String managerNickname) {
        return StringUtils.hasText(managerNickname) ? user.nickname.containsIgnoreCase(managerNickname) : null;
    }
}