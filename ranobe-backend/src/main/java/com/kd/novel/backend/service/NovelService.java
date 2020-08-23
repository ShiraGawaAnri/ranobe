package com.kd.novel.backend.service;

import com.kd.novel.backend.dto.*;
import com.kd.novel.backend.dto.NovelUploadGetRequest;
import com.kd.novel.backend.vo.JsonResult;

public interface NovelService {

    JsonResult getBackendNovelArtistList();

    JsonResult getBackendNovelTags();

    JsonResult getBackendOptions();

    JsonResult getBackendNovelList(NovelGetRequest entity);

    JsonResult getBackendNovelById(Long id);

    JsonResult addBackendNovel(NovelAddOrUpdateRequest entity);

    JsonResult updateBackendNovel(NovelAddOrUpdateRequest entity);

    JsonResult deleteBackendNovel(Long id);

    JsonResult getBackendEpisodeList(Long novelId);

    JsonResult getBackendEpisodeByEpisodeId(Long episodeId);

    JsonResult addBackendEpisode(NovelEpisodeAddOrUpdateRequest entity);

    JsonResult updateBackendEpisode(NovelEpisodeAddOrUpdateRequest entity);

    JsonResult deleteBackendEpisode(Long id);

    JsonResult getBackendChapterList(Long novelId, Long episode);

    JsonResult getBackendChapterByChapter(Long novelId, Long episode, Long chapter);

    JsonResult addBackendChapter(NovelChapterAddOrUpdateRequest entity);

    JsonResult updateBackendChapter(NovelChapterAddOrUpdateRequest entity);

    JsonResult deleteBackendNovelChapter(Long id);

    JsonResult getUploadList(NovelUploadGetRequest entity);

    JsonResult getNovelList(NovelGetRequest entity);

    JsonResult getNovelById(Long id);

    JsonResult getEpisodeList(Long novelId);

    JsonResult getEpisodeByEpisode(Long novelId, Long episodeId);

    JsonResult getChapterList(Long novelId, Long episode);

    JsonResult getChapterByChapter(Long novelId, Long episode,Long chapter);

    JsonResult getNovelSubscribeList(NovelSubscribeGetRequest entity);

    JsonResult addNovelSubscribe(NovelSubscribeAddOrUpdateRequest entity);

    JsonResult deleteNovelSubscribeList(Long novelId);

    JsonResult getNovelCommentList(NovelCommentGetRequest entity);

    JsonResult addNovelComment(NovelCommentAddOrUpdateRequest entity);

    JsonResult deleteNovelComment(Long id);

    JsonResult addOrDeleteNovelCommentLikes(NovelCommentLikesAddOrUpdateRequest entity);

    JsonResult addOrDeleteNovelCommentDislikes(NovelCommentDislikesAddOrUpdateRequest entity);

    JsonResult getNovelChapterParagraphCommentByParagraphId(Long paragraphId);

    JsonResult getNovelChapterParagraphCommentList(NovelChapterParagraphCommentGetRequest entity);

    JsonResult addNovelChapterParagraphComment(NovelChapterParagraphCommentAddOrUpdateRequest entity);

    JsonResult addOrDeleteNovelLikes(NovelLikesAddOrUpdateRequest entity);

    JsonResult getNovelHistoryList(NovelHistoryGetRequest entity);

    JsonResult addOrUpdateNovelHistory(NovelHistoryAddOrUpdateRequest entity);

    JsonResult deleteNovelHistory(Long novelId);

    JsonResult getHotNovel(HotNovelGetRequest entity);

    JsonResult getFeedbackList(FeedbackGetRequest entity);

    JsonResult addOrUpdateFeedback(FeedbackAddOrUpdateRequest entity);



}
