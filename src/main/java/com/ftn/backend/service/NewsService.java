package com.ftn.backend.service;

import com.ftn.backend.dtos.*;
import com.ftn.backend.exception.auth.AuthenticationException;
import com.ftn.backend.exception.business.ResourceNotFoundException;
import com.ftn.backend.model.News;
import com.ftn.backend.model.NewsComment;
import com.ftn.backend.model.User;
import com.ftn.backend.repository.NewsCommentRepository;
import com.ftn.backend.repository.NewsRepository;
import com.ftn.backend.repository.UserRepository;
import com.ftn.backend.utils.JpaQueryFilters;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsCommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<PageDto<NewsDto>> getAllNews(Map<String, String> params) {
        JpaQueryFilters<News> filters = new JpaQueryFilters<>(params, News.class);
        Page<News> page = newsRepository.findAll(filters.getSpecification(), filters.getPageable());

        List<NewsDto> data = page.stream().map(this::mapToDto).toList();

        return ResponseEntity.ok(PageDto.<NewsDto>builder()
                .data(data)
                .total(page.getTotalElements())
                .build());
    }

    @Transactional(readOnly = true)
    public SingleResultDto<NewsDto> getNewsById(Long id) {
        News news = newsRepository
                .findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found"));

        List<NewsComment> allComments = commentRepository.findByNewsIdAndDeletedAtIsNull(id);
        List<CommentDto> commentTree = buildCommentTree(allComments);

        NewsDto newsDto = mapToDto(news);
        newsDto.setComments(commentTree);

        return new SingleResultDto<>(newsDto);
    }

    @Transactional
    public NewsDto createNews(CreateNewsDto request, String email) {
        User author = resolveUser(email);

        News news = News.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .author(author)
                .build();

        News saved = newsRepository.save(news);
        return mapToDto(saved);
    }

    @Transactional
    public CommentDto addComment(Long newsId, CreateCommentDto request, String email) {
        News news = newsRepository
                .findByIdAndDeletedAtIsNull(newsId)
                .orElseThrow(() -> new ResourceNotFoundException("News not found"));

        User author = resolveUser(email);

        NewsComment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository
                    .findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

            if (parentComment.getNews().getId() != newsId) {
                throw new IllegalArgumentException("Parent comment does not belong to this news post");
            }
        }

        NewsComment comment = NewsComment.builder()
                .content(request.getContent())
                .fileUrl(request.getFileUrl())
                .news(news)
                .author(author)
                .parentComment(parentComment)
                .build();

        NewsComment saved = commentRepository.save(comment);
        return mapToCommentDto(saved);
    }

    private NewsDto mapToDto(News news) {
        return NewsDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .description(news.getDescription())
                .category(news.getCategory())
                .date(news.getDate())
                .author(new AuthorDto(
                        news.getAuthor().getId(),
                        news.getAuthor().getFirstName(),
                        news.getAuthor().getLastName()))
                .createdAt(news.getCreatedAt())
                .build();
    }

    private CommentDto mapToCommentDto(NewsComment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .fileUrl(comment.getFileUrl())
                .author(new AuthorDto(
                        comment.getAuthor().getId(),
                        comment.getAuthor().getFirstName(),
                        comment.getAuthor().getLastName()))
                .createdAt(comment.getCreatedAt())
                .replies(new ArrayList<>())
                .build();
    }

    private User resolveUser(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email).orElseGet(() -> userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new AuthenticationException("No users in database")));
    }

    private List<CommentDto> buildCommentTree(List<NewsComment> allComments) {
        Map<Long, CommentDto> dtoById = new HashMap<>();
        List<CommentDto> roots = new ArrayList<>();
        Map<Long, List<CommentDto>> repliesByParent = new HashMap<>();

        for (NewsComment comment : allComments) {
            CommentDto dto = mapToCommentDto(comment);
            dtoById.put(comment.getId(), dto);

            Long parentId = comment.getParentComment() != null
                    ? comment.getParentComment().getId()
                    : null;
            if (parentId != null) {
                repliesByParent
                        .computeIfAbsent(parentId, k -> new ArrayList<>())
                        .add(dto);
            } else {
                roots.add(dto);
            }
        }

        for (Map.Entry<Long, List<CommentDto>> entry : repliesByParent.entrySet()) {
            CommentDto parent = dtoById.get(entry.getKey());
            if (parent != null) {
                parent.setReplies(entry.getValue());
            }
        }

        return roots;
    }
}
