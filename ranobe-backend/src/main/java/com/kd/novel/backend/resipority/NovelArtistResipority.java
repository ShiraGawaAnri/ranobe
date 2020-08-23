package com.kd.novel.backend.resipority;

import com.kd.novel.backend.entity.NovelArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface NovelArtistResipority extends CrudRepository<NovelArtist,Long>,JpaRepository<NovelArtist,Long> {

    List<NovelArtist> findByTranslateNameLikeOrOriginNameLike(String translateName, String originName);

    List<NovelArtist> findByIdIn(List<Long> ids);

    List<NovelArtist> findByIsDelete(Integer isDelete);
}
