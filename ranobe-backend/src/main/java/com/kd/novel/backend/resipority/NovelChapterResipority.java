package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.NovelChapter;
import com.kd.novel.backend.entity.NovelEpisode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public interface NovelChapterResipority extends CrudRepository<NovelChapter,Long>,JpaRepository<NovelChapter,Long> {

//    @Query(value = "select new com.kd.novel.backend.dto.NovelChapterListQueryDTO(nc.id,nc.novelEpisode.episode,nc.chapter,nc.translateTitle,nc.originTitle) from NovelChapter nc where nc.novelEpisode.id = :#{#novelEpisode.id} order by nc.chapter asc")
//    List<NovelChapterListQueryDTO> findByNovelEpisodeOrderByChapter(@Param("novelEpisode") NovelEpisode novelEpisode);

    List<NovelChapter> findByNovelEpisode(NovelEpisode novelEpisode);

    Optional<NovelChapter> findByNovelEpisodeAndChapter(NovelEpisode novelEpisode, Long chapter);

    List<NovelChapter> findByNovelEpisodeOrderByChapter(NovelEpisode novelEpisode);

}
