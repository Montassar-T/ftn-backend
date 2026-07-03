package com.ftn.backend.repository;

import com.ftn.backend.model.NewsComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NewsCommentRepository extends JpaRepository<NewsComment, Long>, JpaSpecificationExecutor<NewsComment> {
    List<NewsComment> findByNewsIdAndDeletedAtIsNull(Long newsId);
}
