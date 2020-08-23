package com.kd.novel.backend.resipority;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.dto.NovelGetRequest;
import com.kd.novel.backend.entity.*;
import com.kd.novel.backend.overrideInterface.Spec;
import com.kd.novel.backend.utils.MyUtils;
import com.kd.novel.backend.vo.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NovelResiporitySpecImpl implements NovelResiporitySpec{


    @Autowired
    private NovelResipority novelResipority;

    @Autowired
    private NovelArtistResipority novelArtistResipority;
    

    @Autowired
    private MyUtils myUtils;

    @SuppressWarnings("unchecked")
    @Override
    public Page<Novel> findAllByCond(Pageable pageable, NovelGetRequest entity) {
        Spec<Novel> specification = (predicates, root, cb) -> {
            Long id = entity.getId();
            Long artistId = entity.getArtistId();
            String artistName = entity.getArtistName();
            Long categoryId = entity.getCategoryId();
            List<Long> tagIds = entity.getTagIds();
            String title = entity.getTitle();
            Integer isDelete = entity.getIsDelete();
            Integer validate = entity.getValidate();


            predicates.add(cb.and());

            if(id != null){
                predicates.add(cb.and(cb.equal(root.get("id"),id)));
            }
            if(artistId != null){
                Join<Novel,NovelArtist> join = root.join("novelArtist",JoinType.LEFT);
                predicates.add(cb.and(cb.equal(join.get("id"),artistId)));
            }else if(StringUtils.isNotEmpty(artistName)){
//                List<NovelArtist> list1 = novelArtistResipority.findByTranslateNameLikeOrOriginNameLike(artistName, artistName);
//                List<NovelArtist> list2 = novelArtistResipority.findByTranslateNameLikeOrOriginNameLike(artistName+"%", artistName+"%");
                List<NovelArtist> list3 = novelArtistResipority.findByTranslateNameLikeOrOriginNameLike("%"+artistName+"%", "%"+artistName+"%");
//                list1.addAll(list2);
//                list1.addAll(list3);
                List<Long> ids = list3.stream().map(NovelArtist::getId).distinct().collect(Collectors.toList());
                Join<Novel,NovelArtist> join = root.join("novelArtist",JoinType.LEFT);
                predicates.add(cb.and(join.get("id").in(ids)));
            }
            if(categoryId != null){
                Join<Novel,NovelCategories> join = root.join("novelCategories",JoinType.LEFT);
                predicates.add(cb.and(cb.equal(join.get("id"),categoryId)));
            }
            if(StringUtils.isNotEmpty(title)){
                String val = "%"+title+"%";
                predicates.add(cb.or(cb.like(root.get("originTitle"),val),cb.like(root.get("translateTitle"),val)));
            }
            if(isDelete != null){
                predicates.add(cb.and(cb.equal(root.get("isDelete"),isDelete)));
            }
            if(validate != null){
                predicates.add(cb.and(cb.equal(root.get("validate"),validate)));
            }
        };
        return novelResipority.findAll(specification,pageable);
    }

    @Override
    public Page<Novel> findAllByCondValidate(Pageable pageable, NovelGetRequest entity) {
        Spec<Novel> specification = (predicates, root, cb) -> {
            Long id = entity.getId();
            Long artistId = entity.getArtistId();
            Long categoryId = entity.getCategoryId();
            List<Long> tagIds = entity.getTagIds();
            Integer finish = entity.getFinish();
            String title = entity.getTitle();


            LocalDateTime dateTime = LocalDateTime.parse("2018-12-31 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime now = LocalDateTime.now();

            Boolean timeCondition = true;
            Long startTime = entity.getStartTime();
            Long endTime = entity.getEndTime();
            if(startTime == null || endTime == null || startTime.equals(0L) || endTime.equals(0L)){
                timeCondition = false;
            }else{
                startTime = startTime < 0 ? dateTime.toEpochSecond(ZoneOffset.of("+8")) : startTime;
                endTime = endTime < 0 ? now.toEpochSecond(ZoneOffset.of("+8")) : endTime;
                Instant startInstant = new Date(startTime).toInstant();
                Instant endInstant = new Date(endTime).toInstant();
                Instant nowInstant = now.toInstant(ZoneOffset.of("+8"));


                if(startInstant.isAfter(nowInstant) || startInstant.isAfter(endInstant)){
                    timeCondition = false;
                }
                if(endInstant.isAfter(nowInstant)){
                    endInstant = nowInstant;
                }
//                if(startInstant.plus(30, ChronoUnit.DAYS).isBefore(endInstant)){
//                    timeCondition = false;
//                }
            }

            List<Long> novelIds = entity.getNovelIds();

            predicates.add(cb.and(cb.equal(root.get("isDelete"),0)));
            predicates.add(cb.and(cb.equal(root.get("validate"),1)));

            if(id != null){
                predicates.add(cb.and(cb.equal(root.get("id"),id)));
            }else if(novelIds != null){
                predicates.add(cb.and(root.get("id").in(novelIds)));
            }
            if(artistId != null){
                Join<Novel,NovelArtist> join = root.join("novelArtist",JoinType.LEFT);
                predicates.add(cb.and(cb.equal(join.get("id"),artistId)));
            }
            if(categoryId != null){
                Join<Novel,NovelCategories> join = root.join("novelCategories",JoinType.LEFT);
                predicates.add(cb.and(cb.equal(join.get("id"),categoryId)));
            }
            if(StringUtils.isNotEmpty(title)){
                String val = "%"+title+"%";
                predicates.add(cb.or(cb.like(root.get("originTitle"),val),cb.like(root.get("translateTitle"),val)));
            }
            if(finish != null){
                predicates.add(cb.and(cb.equal(root.get("finish"),finish)));
            }
            if(myUtils.ArrayNotEmpty(tagIds)){
                Join<Novel,NovelTag> novelTag = root.join("novelTags",JoinType.LEFT);
                Join<NovelTag, NovelTags> novelTags = novelTag.join("novelTags", JoinType.LEFT);
                predicates.add(cb.and(novelTags.get("id").in(tagIds)));
            }
            if(timeCondition){
                predicates.add(cb.and(cb.between(root.get("lastChapterUpdatedTime"),startTime,endTime)));
            }
            Join<Novel,NovelEpisode> episodeListDTOJoin = root.join("novelEpisodes",JoinType.LEFT);
            predicates.add(cb.and(cb.equal(episodeListDTOJoin.get("validate"),1)));
        };
        return novelResipority.findAll(specification,pageable);
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
