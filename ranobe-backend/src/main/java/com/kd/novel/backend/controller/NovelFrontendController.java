package com.kd.novel.backend.controller;

import com.kd.novel.backend.config.annotation.UserAccess;
import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.dto.*;
import com.kd.novel.backend.exception.ServiceException;
import com.kd.novel.backend.service.NovelService;
import com.kd.novel.backend.vo.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class NovelFrontendController {

    @Autowired
    private NovelService novelService;


    @PostMapping("/novel/list")
    public JsonResult getNovelList(@RequestBody @Validated NovelGetRequest entity){
        try {
            return novelService.getNovelList(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Novel List Error");
        }
    }

    @GetMapping("/novel/{id:\\d+}")
    public JsonResult getNovelById(@PathVariable Long id){
        try {
            return novelService.getNovelById(id);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Id Novel Error");
        }
    }


    @GetMapping("/novel/{novelId:\\d+}/episode")
    public JsonResult getEpisodeList(@PathVariable Long novelId){
        try {
            return novelService.getEpisodeList(novelId);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get NovelEpisode List Error");
        }
    }

    @GetMapping("/novel/{novelId:\\d+}/{episode:\\d+}")
    public JsonResult getEpisodeByEpisodeId(@PathVariable Long novelId,@PathVariable Long episode){
        try {
            return novelService.getEpisodeByEpisode(novelId,episode);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get NovelEpisode Error");
        }
    }


    @GetMapping("/novel/{novelId:\\d+}/{episode:\\d+}/chapter")
    public JsonResult getChapterList(@PathVariable Long novelId,@PathVariable Long episode){
        try {
            return novelService.getChapterList(novelId,episode);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get NovelChapter List Error");
        }
    }

    @GetMapping("/novel/{novelId:\\d+}/{episode:\\d+}/{chapter:\\d+}")
    public JsonResult getChapterByChapter(@PathVariable Long novelId,@PathVariable Long episode,@PathVariable Long chapter){
        try {
            return novelService.getChapterByChapter(novelId,episode,chapter);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Id NovelChapter Error");
        }
    }

    @GetMapping("/novel/subscribe")
    @UserAccess(message = "您需要登录才可访问")
    public JsonResult getNovelSubscribeList(NovelSubscribeGetRequest entity){
        try {
            return novelService.getNovelSubscribeList(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Novel Subscribe List Error");
        }
    }

    @PostMapping("/novel/subscribe")
    @UserAccess(message = "您需要登录才可访问")
    public JsonResult addNovelSubscribeList(@Validated @RequestBody NovelSubscribeAddOrUpdateRequest entity){
        try {
            return novelService.addNovelSubscribe(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Add Novel Subscribe List Error");
        }
    }

    @DeleteMapping("/novel/subscribe")
    @UserAccess(message = "您需要登录才可访问")
    public JsonResult deleteNovelSubscribeList(@RequestParam("novelId")Long novelId){
        try {
            return novelService.deleteNovelSubscribeList(novelId);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Delete Novel Subscribe Error");
        }
    }


    @GetMapping("/novel/comment")
    public JsonResult getNovelCommentList(@Validated NovelCommentGetRequest entity){
        try {
            return novelService.getNovelCommentList(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Novel Comment List Error");
        }
    }

    @PostMapping("/novel/comment")
    @UserAccess(message = "您需要登录才可访问")
    public JsonResult addNovelComment(@Validated @RequestBody NovelCommentAddOrUpdateRequest entity){
        try {
            return novelService.addNovelComment(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Add Novel Comment Error");
        }
    }

    @DeleteMapping("/novel/comment")
    @UserAccess(message = "您需要登录才可访问")
    public JsonResult deleteNovelComment(@RequestParam("id")Long id){
        try {
            return novelService.deleteNovelComment(id);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Delete Novel Comment Error");
        }
    }

    @PostMapping("/novel/comment/likes")
    @UserAccess(message = "您需要登录才可使用此功能")
    public JsonResult addOrDeleteNovelCommentLikes(@RequestBody @Validated NovelCommentLikesAddOrUpdateRequest entity){
        try {
            return novelService.addOrDeleteNovelCommentLikes(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "AddOrUpdate Novel Comment Likes Error");
        }
    }

    @PostMapping("/novel/comment/dislikes")
    @UserAccess(message = "您需要登录才可使用此功能")
    public JsonResult addOrDeleteNovelCommentDislikes(@RequestBody @Validated NovelCommentDislikesAddOrUpdateRequest entity){
        try {
            return novelService.addOrDeleteNovelCommentDislikes(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "AddOrUpdate Novel Comment Likes Error");
        }
    }


    @GetMapping("/novel/episode/chapter/paragraph/comment/{paragraphId:\\d+}")
    public JsonResult getNovelChapterParagraphCommentByParagraphId(@PathVariable Long paragraphId){
        try {
            return novelService.getNovelChapterParagraphCommentByParagraphId(paragraphId);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Novel Chapter Paragraph Comment Error");
        }
    }

    @PostMapping("/novel/episode/chapter/paragraph/comment/list")
    public JsonResult getNovelChapterParagraphCommentList(@Validated NovelChapterParagraphCommentGetRequest entity){
        try {
            return novelService.getNovelChapterParagraphCommentList(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Novel Chapter Paragraph Comment List Error");
        }
    }

    @PostMapping("/novel/episode/chapter/paragraph/comment")
    @UserAccess(message = "您需要登录才可访问")
    public JsonResult addNovelChapterParagraphComment(@Validated @RequestBody NovelChapterParagraphCommentAddOrUpdateRequest entity){
        try {
            return novelService.addNovelChapterParagraphComment(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Add Novel Chapter Paragraph Comment Error");
        }
    }

    @PostMapping("/novel/likes")
    @UserAccess(message = "您需要登录才可使用此功能")
    public JsonResult addOrDeleteNovelLikes(@RequestBody @Validated NovelLikesAddOrUpdateRequest entity){
        try {
            return novelService.addOrDeleteNovelLikes(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "AddOrDelete NovelLikes Error");
        }
    }

    @GetMapping("/novel/history")
    @UserAccess(message = "您需要登录才可访问")
    public JsonResult getNovelHistoryList(NovelHistoryGetRequest entity){
        try {
            return novelService.getNovelHistoryList(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Novel History List Error");
        }
    }

    @PostMapping("/novel/history")
    @UserAccess(message = "您需要登录才可访问")
    public JsonResult addOrUpdateNovelHistory(@Validated @RequestBody NovelHistoryAddOrUpdateRequest entity){
        try {
            return novelService.addOrUpdateNovelHistory(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "AddOrUpdate Novel History Error");
        }
    }

    @DeleteMapping("/novel/history")
    @UserAccess(message = "您需要登录才可访问")
    public JsonResult deleteNovelHistory(@RequestParam("novelId") Long novelId){
        try {
            return novelService.deleteNovelHistory(novelId);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Delete Novel History Error");
        }
    }

    @GetMapping("/novel/hot")
    public JsonResult getHotNovel(HotNovelGetRequest entity){
        try {
            return novelService.getHotNovel(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Rank Novel Error");
        }
    }

    @GetMapping("/report")
    @UserAccess(message = "您需要登录才使用此功能")
    public JsonResult getFeedbackList(@Validated FeedbackGetRequest entity){
        try {
            return novelService.getFeedbackList(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Feedback List Error");
        }
    }

    @PostMapping("/report")
    @UserAccess(message = "您需要登录才使用此功能")
    public JsonResult addOrUpdateFeedback(@Validated @RequestBody FeedbackAddOrUpdateRequest entity){
        try {
            return novelService.addOrUpdateFeedback(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "AddOrUpdate Feedback Error");
        }
    }

}
