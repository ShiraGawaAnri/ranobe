package com.kd.novel.backend.dto;

import com.kd.novel.backend.entity.NovelChapter;
import com.kd.novel.backend.entity.NovelEpisode;
import com.kd.novel.backend.entity.UserLogin;
import lombok.Data;

@Data
public class NovelChapterDTO extends NovelChapter {

//    private NovelChapter preChapter;
////
////    private NovelChapter nextChapter;

    private UserLoginDTO userLoginDTO;
}
