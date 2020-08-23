package com.kd.novel.backend.overrideInterface;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public interface Spec<T> extends Specification<T> {

    @SuppressWarnings("NullableProblems")
    @Override
    default Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder){
        List<Predicate> predicateList = new ArrayList<>();
        addPredicate(predicateList,root,criteriaBuilder);
        query.distinct(true);
        return query.where(predicateList.toArray(new Predicate[0])).getRestriction();
    }

    void addPredicate(List<Predicate> predicates,Root<T> root,CriteriaBuilder criteriaBuilder);
}
