package com.kd.novel.backend.resipority;

import com.kd.novel.backend.dto.NovelCommentGetRequest;
import com.kd.novel.backend.entity.*;
import com.kd.novel.backend.overrideInterface.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
public class NovelCommentResiporitySpecImpl implements NovelCommentResiporitySpec{


    @Autowired
    private NovelCommentResipority novelCommentResipority;

    @Autowired
    private NovelCommentLikesResipority novelCommentLikesResipority;

    @Autowired
    private NovelCommentDislikesResipority novelCommentDislikesResipority;

    @SuppressWarnings("unchecked")
    @Override
    public Page<NovelComment> findAllByCond(Pageable pageable, NovelCommentGetRequest entity,Long selfUserId) {
        Spec<NovelComment> specification = (predicates, root, cb) -> {
            Long novelId = entity.getNovelId();
            Join<NovelComment,Novel> join = root.join("novel",JoinType.LEFT);
            predicates.add(cb.and(cb.equal(join.get("id"),novelId)));

            predicates.add(cb.and(cb.equal(root.get("isDelete"),0)));

            if(selfUserId != null){
                Join<NovelComment,UserLogin> nuJoin = root.join("userLogin",JoinType.LEFT);
                predicates.add(cb.or(cb.equal(root.get("tempUserComment"),0),cb.equal(nuJoin.get("id"),selfUserId)));
            }else{
                predicates.add(cb.and(cb.equal(root.get("tempUserComment"),0)));
            }
        };
        return novelCommentResipority.findAll(specification, pageable);
    }

    @Override
    public Optional findOne(Specification spec) {
        return Optional.empty();
    }

    @Override
    public List findAll(Specification spec) {
        return null;
    }

    @Override
    public Page findAll(Specification spec, Pageable pageable) {
        return null;
    }

    @Override
    public List findAll(Specification spec, Sort sort) {
        return null;
    }

    @Override
    public long count(Specification spec) {
        return 0;
    }
}
