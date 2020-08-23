package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.Novel;
import com.kd.novel.backend.entity.NovelEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface NovelEpisodeResipority extends CrudRepository<NovelEpisode,Long>,JpaRepository<NovelEpisode,Long> {

    List<NovelEpisode> findByNovelOrderByEpisode(Novel novel);

    List<NovelEpisode> findByNovelAndValidateOrderByEpisode(Novel novel,Integer validate);

    Optional<NovelEpisode> findByNovelAndEpisodeAndValidate(Novel novel,Long episode, Integer validate);

    Optional<NovelEpisode> findByNovelAndEpisode(Novel novelListDTO, Long episode);
}
