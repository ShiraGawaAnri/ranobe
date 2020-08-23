package com.kd.novel.backend.controller;


import com.kd.novel.backend.config.annotation.UserAccess;
import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.dto.NovelAddOrUpdateRequest;
import com.kd.novel.backend.dto.NovelChapterAddOrUpdateRequest;
import com.kd.novel.backend.dto.NovelEpisodeAddOrUpdateRequest;
import com.kd.novel.backend.dto.NovelGetRequest;
import com.kd.novel.backend.dto.NovelUploadGetRequest;
import com.kd.novel.backend.exception.ServiceException;
import com.kd.novel.backend.service.NovelService;
import com.kd.novel.backend.vo.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/backend")
public class NovelBackendController {
    
    @Autowired
    private NovelService novelService;

    @GetMapping("/artist")
    @UserAccess
    public JsonResult getArtistList(){
        try {
            return novelService.getBackendNovelArtistList();
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Artist List Error");
        }
    }

    @GetMapping("/tag")
    @UserAccess
    public JsonResult getTagList(){
        try {
            return novelService.getBackendNovelTags();
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Tag List Error");
        }
    }

    @GetMapping("/options")
    @UserAccess
    public JsonResult getBackendOptions(){
        try {
            return novelService.getBackendOptions();
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Options Error");
        }
    }




    @PostMapping("/novel/list")
    @UserAccess({"ROLE_ADMIN"})
    public JsonResult getBackendNovelList(@RequestBody @Validated NovelGetRequest entity){
        try {
            return novelService.getBackendNovelList(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Novel List Error");
        }
    }

    @GetMapping("/novel/{id:\\d+}")
    @UserAccess
    public JsonResult getBackendNovelById(@PathVariable Long id){
        try {
            return novelService.getBackendNovelById(id);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Id Novel Error");
        }
    }

    @PostMapping("/novel")
    @UserAccess({"ROLE_BROZON_USER"})
    public JsonResult addBackendNovel(@Validated @RequestBody NovelAddOrUpdateRequest entity){
        try {
            return novelService.addBackendNovel(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Add Novel Error");
        }
    }

    @PutMapping("/novel")
    @UserAccess
    public JsonResult updateBackendNovel(@Validated @RequestBody NovelAddOrUpdateRequest entity){
        try {
            return novelService.updateBackendNovel(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Update Novel Error");
        }
    }

    @DeleteMapping("/novel")
    @UserAccess({"ROLE_ADMIN"})
    public JsonResult deleteBackendNovel(@RequestParam("id")Long id){
        try {
            return novelService.deleteBackendNovel(id);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Delete Novel Error");
        }
    }


    @GetMapping("/novel/{novelId:\\d+}/episode")
    @UserAccess
    public JsonResult getBackendEpisodeList(@PathVariable Long novelId){
        try {
            return novelService.getBackendEpisodeList(novelId);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get NovelEpisode List Error");
        }
    }

    @GetMapping("/novel/episode/{episodeId:\\d+}")
    @UserAccess
    public JsonResult getBackendEpisodeByEpisodeId(@PathVariable Long episodeId){
        try {
            return novelService.getBackendEpisodeByEpisodeId(episodeId);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get NovelEpisode Error");
        }
    }

    @PostMapping("/novel/episode")
    @UserAccess({"ROLE_BROZON_USER"})
    public JsonResult addBackendEpisode(@Validated @RequestBody NovelEpisodeAddOrUpdateRequest entity){
        try {
            return novelService.addBackendEpisode(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Add NovelEpisode Error");
        }
    }


    @PutMapping("/novel/episode")
    @UserAccess({"ROLE_BROZON_USER"})
    public JsonResult updateBackendEpisode(@Validated @RequestBody NovelEpisodeAddOrUpdateRequest entity){
        try {
            return novelService.updateBackendEpisode(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Update NovelEpisode Error");
        }
    }

    @DeleteMapping("/novel/episode")
    @UserAccess({"ROLE_ADMIN"})
    public JsonResult deleteBackendEpisode(@RequestParam("id") Long id){
        try {
            return novelService.deleteBackendEpisode(id);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Delete NovelEpisode Error");
        }
    }

    @GetMapping("/novel/{novelId:\\d+}/{episode:\\d+}/chapter")
    @UserAccess
    public JsonResult getBackendChapterList(@PathVariable Long novelId,@PathVariable Long episode){
        try {
            return novelService.getBackendChapterList(novelId,episode);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get NovelChapter List Error");
        }
    }

    @GetMapping("/novel/{novelId:\\d+}/{episode:\\d+}/{chapter:\\d+}")
    @UserAccess
    public JsonResult getBackendChapterByChapter(@PathVariable Long novelId,@PathVariable Long episode,@PathVariable Long chapter){
        try {
            return novelService.getBackendChapterByChapter(novelId,episode,chapter);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Id NovelChapter Error");
        }
    }

    @PostMapping("/novel/episode/chapter")
    @UserAccess({"ROLE_BROZON_USER"})
    public JsonResult addBackendChapter(@Validated @RequestBody NovelChapterAddOrUpdateRequest entity){
        try {
            return novelService.addBackendChapter(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Add NovelChapter Error");
        }
    }

    @PutMapping("/novel/episode/chapter")
    @UserAccess({"ROLE_BROZON_USER"})
    public JsonResult updateBackendChapter(@Validated @RequestBody NovelChapterAddOrUpdateRequest entity){
        try {
            return novelService.updateBackendChapter(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Update NovelChapter Error");
        }
    }

    @DeleteMapping("/novel/episode/chapter")
    @UserAccess({"ROLE_ADMIN"})
    public JsonResult deleteBackendNovelChapter(@RequestParam("id") Long id){
        try {
            return novelService.deleteBackendNovelChapter(id);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Delete NovelChapter Error");
        }
    }

    @GetMapping("/upload")
    @UserAccess({"ROLE_BROZON_USER"})
    public JsonResult getUploadList(NovelUploadGetRequest entity){
        try {
            return novelService.getUploadList(entity);
        } catch (ServiceException e) {
            return JsonResult.build(DefaultConstants.ERROR_SERVICE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.build(DefaultConstants.ERROR_SYSTEM, "Get Upload List Error");
        }
    }
}
