package com.kd.novel.backend.resipority;

import com.kd.novel.backend.dto.NovelGetRequest;
import com.kd.novel.backend.entity.Novel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface NovelResiporitySpec extends JpaSpecificationExecutor {

    Page<Novel> findAllByCond(Pageable pageable, NovelGetRequest entity);

    Page<Novel> findAllByCondValidate(Pageable pageable, NovelGetRequest entity);

}
