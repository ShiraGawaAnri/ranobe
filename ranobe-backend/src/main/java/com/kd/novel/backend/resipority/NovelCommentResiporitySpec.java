package com.kd.novel.backend.resipority;

import com.kd.novel.backend.dto.NovelCommentGetRequest;
import com.kd.novel.backend.entity.NovelComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
public interface NovelCommentResiporitySpec extends JpaSpecificationExecutor {

    Page<NovelComment> findAllByCond(Pageable pageable, NovelCommentGetRequest entity, Long selfUserId);
}
