package com.kd.novel.backend.service.impl;


import com.kd.novel.backend.config.async.AsyncTask;
import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.config.constants.PreConstants;
import com.kd.novel.backend.config.enums.PlusHotScoreTimeLimitEnum;
import com.kd.novel.backend.config.enums.RankEnum;
import com.kd.novel.backend.dto.*;
import com.kd.novel.backend.entity.*;
import com.kd.novel.backend.exception.ServiceException;
import com.kd.novel.backend.resipority.*;
import com.kd.novel.backend.service.NovelService;
import com.kd.novel.backend.service.UserService;
import com.kd.novel.backend.utils.IpUtils;
import com.kd.novel.backend.utils.JwtTokenUtils;
import com.kd.novel.backend.utils.MyUtils;
import com.kd.novel.backend.vo.JsonResult;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Tuple;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NovelServiceImpl implements NovelService {

    @Autowired
    private NovelResipority novelResipority;

    @Autowired
    private NovelResiporitySpec novelResiporitySpec;

    @Autowired
    private NovelCoverResipority novelCoverResipority;

    @Autowired
    private NovelEpisodeResipority novelEpisodeResipority;

    @Autowired
    private NovelChapterResipority novelChapterResipority;

    @Autowired
    private NovelChapterParagraphResipority novelChapterParagraphResipority;

    @Autowired
    private NovelTagResipority novelTagResipority;

    @Autowired
    private NovelTagsResipority novelTagsResipority;

    @Autowired
    private NovelSubscribeResipority novelSubscribeResipority;

    @Autowired
    private NovelCommentResipority novelCommentResipority;

    @Autowired
    private NovelCommentResiporitySpec novelCommentResiporitySpec;

    @Autowired
    private NovelCommentLikesResipority novelCommentLikesResipority;

    @Autowired
    private NovelCommentDislikesResipority novelCommentDislikesResipority;

    @Autowired
    private NovelChapterParagraphCommentResipority novelChapterParagraphCommentResipority;
    
    @Autowired
    private NovelSubscribeResipority NovelSubscribeResipority;

    @Autowired
    private NovelHistoryResipority novelHistoryResipority;

    @Autowired
    private NovelLikesResipority novelLikesResipority;

    @Autowired
    private FeedbackHistoryResipority feedbackHistoryResipority;

    @Autowired
    private NovelArtistResipority novelArtistResipority;

    @Autowired
    private NovelCategoryiesResipority novelCategoryiesResipority;

    @Autowired
    private UserService userService;

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private MyUtils myUtils;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private AsyncTask asyncTask;

    @Autowired
    private HttpServletRequest request;

    private Novel getNovel(Long novelId) {
        Optional<Novel> byId = novelResipority.findById(novelId);
        if(!byId.isPresent()){
            throw new ServiceException("Novel not found");
        }
        Novel Novel = byId.get();
        Novel novel = new Novel();
        BeanUtils.copyProperties(Novel,novel);
        return novel;
    }

    private Novel getNovelDTO(Long novelId) {
        Optional<Novel> byId = novelResipority.findById(novelId);
        if(!byId.isPresent()){
            throw new ServiceException("Novel not found");
        }
        return byId.get();
    }

    private boolean hasAdminPermission(UserLogin userLogin){
        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_ROOT");
        permissions.add("ROLE_ADMIN");
        return jwtTokenUtils.hasPermission(userLogin,permissions);
    }

    private boolean hasAdminPermission(String token){
        Set<String> permissions = new HashSet<>();
        permissions.add("ROLE_ROOT");
        permissions.add("ROLE_ADMIN");
        return jwtTokenUtils.hasPermission(token,permissions);
    }

    private boolean checkUploaderOrAdmin(String token, UserLogin userLogin) {
        if(token !=null) {
            if(hasAdminPermission(token)){
                return true;
            }
            UserLogin requester = jwtTokenUtils.getUserLogin(token);
            return userLogin != null && userLogin.getId().equals(requester.getId());
        }
        return false;
    }

    private boolean checkNovelCanRead(Novel novel,String token){
        if (checkUploaderOrAdmin(token, novel.getUserLogin())) return true;
        Integer validate = novel.getValidate();
        Integer isDelete = novel.getIsDelete();
        if(validate == null || validate.equals(0)){
            throw new ServiceException("Novel Episode not found 2");
        }
        if(isDelete.equals(1)){
            throw new ServiceException("Novel Episode not found 3");
        }
        return true;
    }

    private boolean checkNovelChapterCanRead(NovelEpisode novelEpisode,String token){
        if (checkUploaderOrAdmin(token, novelEpisode.getUserLogin())) return true;
        Integer validate = novelEpisode.getValidate();
        if(validate == null || validate.equals(0)){
            throw new ServiceException("Novel Episode not found 2");
        }
        return true;
    }


    //通过在对应的@OneToMany注解上同时追加@OrderBy省去以下两个调用
    private void sortEpisode(Novel entity){
        Set<NovelEpisode> novelEpisodes = entity.getNovelEpisodes();
        LinkedHashSet<NovelEpisode> set = novelEpisodes.stream().sorted(Comparator.comparingLong(NovelEpisode::getEpisode)).collect(Collectors.toCollection(LinkedHashSet::new));
        set.forEach(this::sortChapter);
        entity.setNovelEpisodes(set);
    }

    private void sortChapter(NovelEpisode entity){
        Set<NovelChapter> novelChapters = entity.getNovelChapters();
        LinkedHashSet<NovelChapter> collect = novelChapters.stream().sorted(Comparator.comparingLong(NovelChapter::getChapter)).collect(Collectors.toCollection(LinkedHashSet::new));
        entity.setNovelChapters(collect);
    }

    private TempParagraphsEntity divideContentIntoParagraphs(String content){
        TempParagraphsEntity tempParagraphsEntity = new TempParagraphsEntity();
        List<String> paragraphs = new ArrayList<>();
        //String regEx = "(<br[^>]*?>[\\s\\S]*?</br>)|(<p[^>]*?>[\\s\\S]*?</p>)";
        String regEx="(<p[^>]*?>(.*?)</p>)|(<br[^>]*?>(.*?)</br>)";
        Pattern p = Pattern.compile(regEx,Pattern.CASE_INSENSITIVE|Pattern.COMMENTS);
        Matcher m = p.matcher(content);
        Long wordCount = 0L;
        while(m.find()){
            if(m.group() != null){
                paragraphs.add(m.group());
                if(m.group(1) != null){
                    wordCount += m.group(2).length();
                }else if(m.group(3) != null){
                    wordCount += m.group(4).length();
                }

            }
        }
        tempParagraphsEntity.setParagraphs(paragraphs);
        tempParagraphsEntity.setWordCount(wordCount);
        return tempParagraphsEntity;
    }

    @Getter
    @Setter
    class TempParagraphsEntity {
        private Long wordCount;
        private List<String> paragraphs;
    }

    @Override
    public JsonResult getBackendNovelArtistList() {
        List<NovelArtist> list = novelArtistResipority.findByIsDelete(0);
        return JsonResult.oK(list);
    }

    @Override
    public JsonResult getBackendNovelTags() {
        List<NovelTags> list = novelTagsResipority.findAll();
        return JsonResult.oK(list);
    }

    @Override
    public JsonResult getBackendOptions() {
        List<NovelArtist> artistList = novelArtistResipority.findByIsDelete(0);
        List<NovelTags> tagList = novelTagsResipority.findAll();
        List<NovelCategories> categories = novelCategoryiesResipority.findAll();
        Map<String,Object> map = new HashMap<>();
        map.put("artists",artistList);
        map.put("tags",tagList);
        map.put("categories",categories);
        return JsonResult.oK(map);
    }

    @Override
    public JsonResult getBackendNovelList(NovelGetRequest entity) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(entity.getPage(), entity.getSize(), sort);
        Page ret = novelResiporitySpec.findAllByCond(pageable, entity);
        //List<Novel> content = ret.getContent();
        //content.forEach(each-> sortEpisode(each));
        return JsonResult.oK(ret);
    }

    @Override
    public JsonResult getBackendNovelById(Long id) {
        Optional<Novel> byId = novelResipority.findById(id);
        if(!byId.isPresent()){
            throw new ServiceException("Novel not found");
        }
        Novel Novel = byId.get();
        sortEpisode(Novel);
        return JsonResult.oK(Novel);
    }

    @Transactional
    @Override
    public JsonResult addBackendNovel(NovelAddOrUpdateRequest entity) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        String originTitle = entity.getOriginTitle();
        String translateTitle = entity.getTranslateTitle();
        List<Novel> novelsByTranslateTitleOrOriginTitle = novelResipority.findByTranslateTitleOrOriginTitle(translateTitle, originTitle);
        if(novelsByTranslateTitleOrOriginTitle.size() > 0 ){
            throw new ServiceException("Duplicate Title Novel");
        }
        Novel novel = new Novel();
        Date now = new Date();
        novel.setCreatedTime(now);
        novel.setTranslateTitle(translateTitle);
        novel.setOriginTitle(translateTitle);
        novel.setIntroduction(entity.getIntroduction());
        novel.setViews(0);
        novel.setIsDelete(0);
        novel.setLocked(0);
        novel.setFinish(entity.getFinish());
        novel.setValidate(0);
        novel.setLastChapterUpdatedTime(now.getTime());
        novel.setUserLogin(userLogin);

        NovelCategories novelCategories = new NovelCategories();
        novelCategories.setId(entity.getCategoryId());
        novel.setNovelCategories(novelCategories);

        NovelArtist novelArtist = new NovelArtist();
        novelArtist.setId(entity.getArtistId());
        novel.setNovelArtist(novelArtist);

        novelResipority.save(novel);

        Set<NovelCover> covers = entity.getCovers();
        if(myUtils.SetNotEmpty(covers)){
            covers.forEach(cover->cover.setNovel(novel));
            novelCoverResipority.deleteAllByNovel(novel);
            novelCoverResipority.saveAll(covers);
        }

        Set<Long> tagIds = entity.getTagIds();
        if(myUtils.SetNotEmpty(tagIds)){
            Set<NovelTag> tags = new HashSet<>();
            tagIds.forEach(each->{
                NovelTags novelTags = new NovelTags();
                novelTags.setId(each);
                NovelTag novelTag = new NovelTag();
                novelTag.setNovel(novel);
                novelTag.setNovelTags(novelTags);
                tags.add(novelTag);
            });
            novelTagResipority.deleteAllByNovel(novel);
            novelTagResipority.saveAll(tags);
        }

        return JsonResult.oK(novel.getId());
    }

    @Transactional
    @Override
    public JsonResult updateBackendNovel(NovelAddOrUpdateRequest entity) {
        Long id = entity.getId();
        if(id == null){
            throw new ServiceException("Params(id) error");
        }
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        Novel novel = getNovel(id);
        if(!novel.getUserLogin().getId().equals(userLogin.getId()) && !hasAdminPermission(token)){
            throw new ServiceException("No Permission");
        }
        if(novel.getLocked() != null && novel.getLocked().equals(1) && !hasAdminPermission(token)){
            throw new ServiceException("Has Been Locked");
        }
        if(entity.getOriginTitle() != null){
            novel.setOriginTitle(entity.getOriginTitle());
        }
        if(entity.getTranslateTitle() != null){
            novel.setTranslateTitle(entity.getTranslateTitle());
        }
        if(entity.getIntroduction() != null){
            novel.setIntroduction(entity.getIntroduction());
        }
        if(entity.getViews() != null){
            novel.setViews(entity.getViews());
        }
        if(entity.getIsDelete() != null && hasAdminPermission(token)){
            novel.setIsDelete(entity.getIsDelete());
        }
        if(entity.getFinish() != null){
            novel.setFinish(entity.getFinish());
        }
        if(entity.getLocked() != null && hasAdminPermission(token)){
            novel.setLocked(entity.getLocked());
        }
        if(entity.getValidate() != null && hasAdminPermission(token)){
            novel.setValidate(entity.getValidate());
        }
        if(entity.getCategoryId() != null){
            novel.getNovelCategories().setId(entity.getCategoryId());
        }
        if(entity.getArtistId() != null){
            novel.getNovelArtist().setId(entity.getArtistId());
        }

        novel.setUserLogin(userLogin);
        novelResipority.save(novel);

        Set<NovelCover> covers = entity.getCovers();
        if(myUtils.SetNotEmpty(covers)){
            covers.forEach(cover->cover.setNovel(novel));
            novelCoverResipority.deleteAllByNovel(novel);
            novelCoverResipority.saveAll(covers);
        }

        Set<Long> tagIds = entity.getTagIds();
        if(myUtils.SetNotEmpty(tagIds)){
            Set<NovelTag> tags = new HashSet<>();
            tagIds.forEach(each->{
                NovelTags novelTags = new NovelTags();
                novelTags.setId(each);
                NovelTag novelTag = new NovelTag();
                novelTag.setNovel(novel);
                novelTag.setNovelTags(novelTags);
                tags.add(novelTag);
            });
            novelTagResipority.deleteAllByNovel(novel);
            novelTagResipority.saveAll(tags);
        }

        return JsonResult.oK(novel.getId());
    }

    @Transactional
    @Override
    public JsonResult deleteBackendNovel(Long id) {
        getNovel(id);
        novelResipority.deleteById(id);
        return JsonResult.oK(id);
    }

    @Override
    public JsonResult getBackendEpisodeList(Long novelId) {
        Novel novelDTO = getNovelDTO(novelId);
        List<NovelEpisode> byNovel = novelEpisodeResipority.findByNovelOrderByEpisode(novelDTO);
        return JsonResult.oK(byNovel);
    }

    @Override
    public JsonResult getBackendEpisodeByEpisodeId(Long episodeId) {
        Optional<NovelEpisode> byNovelAndAndEpisode = novelEpisodeResipority.findById(episodeId);
        if(!byNovelAndAndEpisode.isPresent()){
            throw new ServiceException("Novel Episode not found");
        }
        return JsonResult.oK(byNovelAndAndEpisode.get());
    }

    @Transactional
    @Override
    public JsonResult addBackendEpisode(NovelEpisodeAddOrUpdateRequest entity) {
        Long novelId = entity.getNovelId();
        Novel novelDTO = getNovelDTO(novelId);
        Novel novel = new Novel();
        BeanUtils.copyProperties(novelDTO,novel);
        Long episode = entity.getEpisode();
        Optional<NovelEpisode> byNovelAndAndEpisode = novelEpisodeResipority.findByNovelAndEpisode(novelDTO, episode);
        if(byNovelAndAndEpisode.isPresent()){
            throw new ServiceException("Novel Episode duplicate");
        }
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        NovelEpisode novelEpisode = new NovelEpisode();
        novelEpisode.setNovel(novel);
        novelEpisode.setEpisode(episode);
        novelEpisode.setOriginTitle(entity.getOriginTitle());
        novelEpisode.setTranslateTitle(entity.getTranslateTitle());
        novelEpisode.setUserLogin(userLogin);
        novelEpisode.setValidate(0);
        novelEpisode.setViews(0);
        novelEpisodeResipority.save(novelEpisode);
        return JsonResult.oK(novelEpisode.getId());
    }

    @Transactional
    @Override
    public JsonResult updateBackendEpisode(NovelEpisodeAddOrUpdateRequest entity) {
        Long id = entity.getId();
        if(id == null){
            throw new ServiceException("Params(id) error");
        }
        Optional<NovelEpisode> byId = novelEpisodeResipority.findById(id);
        if(!byId.isPresent()){
            throw new ServiceException("Novel Episode not found");
        }

        NovelEpisode novelEpisode = byId.get();
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        if(!novelEpisode.getUserLogin().getId().equals(userLogin.getId()) && !hasAdminPermission(token)){
            throw new ServiceException("No Permission");
        }
        Long entityEpisode = entity.getEpisode();
        if(entityEpisode != null && !entityEpisode.equals(novelEpisode.getEpisode())){
            Novel novelDTO = novelEpisode.getNovel();
            Optional<NovelEpisode> byNovelAndAndEpisode = novelEpisodeResipority.findByNovelAndEpisode(novelDTO, entityEpisode);
            if(byNovelAndAndEpisode.isPresent()){
                throw new ServiceException("Novel Episode duplicate");
            }
        }
        novelEpisode.setOriginTitle(entity.getOriginTitle());
        novelEpisode.setTranslateTitle(entity.getTranslateTitle());
        novelEpisode.setEpisode(entityEpisode);
        if(entity.getValidate() != null && hasAdminPermission(token)){
            novelEpisode.setValidate(entity.getValidate());
        }
        if(entity.getViews() != null && hasAdminPermission(token)){
            novelEpisode.setViews(entity.getViews());
        }
        novelEpisodeResipority.save(novelEpisode);
        return JsonResult.oK(novelEpisode.getId());
    }

    @Override
    public JsonResult deleteBackendEpisode(Long id) {
        novelEpisodeResipority.deleteById(id);
        return JsonResult.oK(id);
    }


    @Override
    public JsonResult getBackendChapterList(Long novelId, Long episode) {
        Novel novel = getNovel(novelId);
        Optional<NovelEpisode> allByNovelAndEpisode = novelEpisodeResipority.findByNovelAndEpisode(novel, episode);
        if(!allByNovelAndEpisode.isPresent()){
            throw new ServiceException("Episode not found");
        }
        NovelEpisode novelEpisode = allByNovelAndEpisode.get();
        //List<NovelChapterListQueryDTO> list = novelChapterResipority.findByNovelEpisodeOrderByChapter(novelEpisode);
        List<NovelChapter> list = novelChapterResipority.findByNovelEpisode(novelEpisode);
        return JsonResult.oK(list);
    }

    @Override
    public JsonResult getBackendChapterByChapter(Long novelId, Long episode, Long chapter) {
        Novel novel = getNovel(novelId);
        Optional<NovelEpisode> allByNovelAndEpisode = novelEpisodeResipority.findByNovelAndEpisode(novel, episode);
        if(!allByNovelAndEpisode.isPresent()){
            throw new ServiceException("Episode not found");
        }
        NovelEpisode novelEpisode = allByNovelAndEpisode.get();
//        List<NovelChapterListQueryDTO> list = novelChapterResipority.findByNovelEpisodeOrderByChapter(novelEpisode);
//
//        NovelChapterListQueryDTO preNc = new NovelChapterListQueryDTO();
//
//        NovelChapterListQueryDTO nextNc = new NovelChapterListQueryDTO();

        Novel Novel = new Novel();
        BeanUtils.copyProperties(Novel,novel);
        Optional<NovelChapter> byNovelAndAndChapter = novelChapterResipority.findByNovelEpisodeAndChapter(novelEpisode, chapter);
        if(!byNovelAndAndChapter.isPresent()){
            NovelChapter notFoundChapter = new NovelChapter();
            notFoundChapter.setChapter(chapter);
            return JsonResult.oK(notFoundChapter);
        }
        NovelChapter novelChapter = byNovelAndAndChapter.get();
        //触发lazy
        Set<NovelChapterParagraph> novelChapterParagraphs = novelChapter.getNovelChapterParagraphs();
        List<NovelChapterParagraph> collect = novelChapterParagraphs.stream().sorted(Comparator.comparingInt(NovelChapterParagraph::getNum)).collect(Collectors.toList());

        List<NovelChapterComment> collect1 = novelChapter.getNovelChapterComments().stream().sorted().collect(Collectors.toList());
        NovelChapterDTO novelChapterDTO = new NovelChapterDTO();
        BeanUtils.copyProperties(novelChapter,novelChapterDTO);
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        if(novelChapter.getNovelEpisode().getUserLogin() != null){
            BeanUtils.copyProperties(novelChapter.getNovelEpisode().getUserLogin(),userLoginDTO);
        }
        novelChapterDTO.setUserLoginDTO(userLoginDTO);
        return JsonResult.oK(novelChapterDTO);
    }


    @Transactional
    @Override
    public JsonResult addBackendChapter(NovelChapterAddOrUpdateRequest entity) {
        Long episodeId = entity.getEpisodeId();
        Optional<NovelEpisode> byId = novelEpisodeResipority.findById(episodeId);
        if(!byId.isPresent()){
            throw new ServiceException("Episode not found");
        }

        NovelEpisode novelEpisode = byId.get();
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        if(!novelEpisode.getUserLogin().getId().equals(userLogin.getId()) && !hasAdminPermission(token)){
            throw new ServiceException("No Permission");
        }

        Long chapter = entity.getChapter();
        Optional<NovelChapter> byNovelEpisodeAndChapter = novelChapterResipority.findByNovelEpisodeAndChapter(novelEpisode, chapter);
        if(byNovelEpisodeAndChapter.isPresent()){
            throw new ServiceException("Novel Episode Chapter duplicate");
        }

        NovelChapter novelChapter = new NovelChapter();
        novelChapter.setNovelEpisode(novelEpisode);
        if(chapter != null){
            novelChapter.setChapter(chapter);
        }
        if(entity.getOriginTitle() != null){
            novelChapter.setOriginTitle(entity.getOriginTitle());
        }
        if(entity.getTranslateTitle() != null){
            novelChapter.setTranslateTitle(entity.getTranslateTitle());
        }
        if(entity.getHeaderInfo() != null){
            novelChapter.setHeaderInfo(entity.getHeaderInfo());
        }
        if(entity.getFooterInfo() != null){
            novelChapter.setFooterInfo(entity.getFooterInfo());
        }

        novelChapterResipority.save(novelChapter);

        String originContent = entity.getOriginContent();
        String translateContent = entity.getTranslateContent();
//        if(originContent != null){
//            novelChapter.setOriginContent(originContent);
//        }
//        if(translateContent != null){
//            novelChapter.setOriginContent(translateContent);
//        }
        if(originContent != null || translateContent != null){
            List<String> p1 = new ArrayList<>();
            List<String> p2 = new ArrayList<>();
            Long wordCount = 0L;
            if(originContent != null){
                TempParagraphsEntity tempParagraphsEntity = divideContentIntoParagraphs(originContent);
                p1 = tempParagraphsEntity.getParagraphs();
                wordCount = tempParagraphsEntity.getWordCount();
            }
            if(translateContent != null){
                TempParagraphsEntity tempParagraphsEntity = divideContentIntoParagraphs(translateContent);
                p2 = tempParagraphsEntity.getParagraphs();
                wordCount = tempParagraphsEntity.getWordCount();
            }
            Integer maxPs = Math.max(p1.size(),p2.size());
            List<NovelChapterParagraph> novelChapterParagraphs = new ArrayList<>();
            for(int i = 0;i < maxPs;i++){
                NovelChapterParagraph saveEntity = new NovelChapterParagraph();
                saveEntity.setNum(i);
                if(i < p1.size()){
                    saveEntity.setOriginContent(p1.get(i));
                }
                if(i < p2.size()){
                    saveEntity.setTranslateContent(p2.get(i));
                }
                saveEntity.setNovelChapter(novelChapter);
                novelChapterParagraphs.add(saveEntity);
            }
            if(novelChapterParagraphs.size() > 0){
                novelChapterParagraphResipority.saveAll(novelChapterParagraphs);
            }
            novelChapter.setWordCount(wordCount);
            novelChapterResipority.save(novelChapter);
        }
        Novel novel = novelEpisode.getNovel();
        novel.setLastChapterUpdatedTime(new Date().getTime());
        novelResipority.save(novel);
        asyncTask.novelSubscribeNotify(novel);
        return JsonResult.oK(novelChapter.getId());
    }

    @Transactional
    @Override
    public JsonResult updateBackendChapter(NovelChapterAddOrUpdateRequest entity) {
        Long id = entity.getId();
        if(id == null){
            throw new ServiceException("Params(id) error");
        }
        Optional<NovelChapter> byId = novelChapterResipority.findById(id);
        if(!byId.isPresent()){
            throw new ServiceException("Novel Episode Chapter not found");
        }
        NovelChapter novelChapter = byId.get();
        NovelEpisode novelEpisode = novelChapter.getNovelEpisode();
        Optional<NovelChapter> byNovelEpisodeAndChapter = novelChapterResipority.findByNovelEpisodeAndChapter(novelEpisode, novelChapter.getChapter());
        if(byNovelEpisodeAndChapter.isPresent() && !byNovelEpisodeAndChapter.get().getId().equals(id)){
            throw new ServiceException("Novel Episode Chapter duplicate");
        }
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        if(!novelEpisode.getUserLogin().getId().equals(userLogin.getId()) && !hasAdminPermission(token)){
            throw new ServiceException("No Permission");
        }
        if(entity.getChapter() != null){
            novelChapter.setChapter(entity.getChapter());
        }
        if(entity.getOriginTitle() != null){
            novelChapter.setOriginTitle(entity.getOriginTitle());
        }
        if(entity.getTranslateTitle() != null){
            novelChapter.setTranslateTitle(entity.getTranslateTitle());
        }
        if(entity.getHeaderInfo() != null){
            novelChapter.setHeaderInfo(entity.getHeaderInfo());
        }
        if(entity.getFooterInfo() != null){
            novelChapter.setFooterInfo(entity.getFooterInfo());
        }

        novelChapter.setNovelEpisode(novelEpisode);
        novelChapterResipority.save(novelChapter);

        String originContent = entity.getOriginContent();
        String translateContent = entity.getTranslateContent();
//        if(entity.getOriginContent() != null){
//            novelChapter.setOriginContent(entity.getOriginContent());
//        }
//        if(entity.getOriginContent() != null){
//            novelChapter.setOriginContent(entity.getOriginContent());
//        }

        Integer allContentChange = entity.getAllContentChange();
        Integer deleteAllParagraphComment = entity.getDeleteAllParagraphComment();
        Map<Integer, String> originParagraphs = entity.getOriginParagraphs();
        Map<Integer, String> translateParagraphs = entity.getTranslateParagraphs();
        if(/*allContentChange.equals(1) && */(originContent != null || translateContent != null)){
            List<String> p1 = new ArrayList<>();
            List<String> p2 = new ArrayList<>();
            Long wordCount = 0L;
            if(originContent != null){
                TempParagraphsEntity tempParagraphsEntity = divideContentIntoParagraphs(originContent);
                p1 = tempParagraphsEntity.getParagraphs();
                wordCount = tempParagraphsEntity.getWordCount();
            }
            if(translateContent != null){
                TempParagraphsEntity tempParagraphsEntity = divideContentIntoParagraphs(translateContent);
                p2 = tempParagraphsEntity.getParagraphs();
                wordCount = tempParagraphsEntity.getWordCount();
            }
            Integer maxPs = Math.max(p1.size(),p2.size());
            List<NovelChapterParagraph> byNovelChapterEqualsAndNumLessThanEqual = novelChapterParagraphResipority.findByNovelChapter(novelChapter);


            if(deleteAllParagraphComment.equals(0) && byNovelChapterEqualsAndNumLessThanEqual.size() > maxPs){
                //Delete
                novelChapterParagraphResipority.deleteByNovelChapterEqualsAndNumGreaterThan(novelChapter,maxPs);
            }else if(deleteAllParagraphComment.equals(1)){
                novelChapterParagraphResipority.deleteByNovelChapterEqualsAndNumGreaterThan(novelChapter,-1);
                novelChapterParagraphResipority.flush();
            }
            List<NovelChapterParagraph> novelChapterParagraphs = new ArrayList<>();
            for(int i = 0;i < maxPs;i++){
                NovelChapterParagraph saveEntity = new NovelChapterParagraph();
                saveEntity.setNum(i);
                if(i < p1.size()){
                    saveEntity.setOriginContent(p1.get(i));
                }
                if(i < p2.size()){
                    saveEntity.setTranslateContent(p2.get(i));
                }
                saveEntity.setNovelChapter(novelChapter);
                if(deleteAllParagraphComment.equals(0) && i < byNovelChapterEqualsAndNumLessThanEqual.size()){
                    saveEntity.setId(byNovelChapterEqualsAndNumLessThanEqual.get(i).getId());
                }
                novelChapterParagraphs.add(saveEntity);
            }
            if(novelChapterParagraphs.size() > 0){
                novelChapterParagraphResipority.saveAll(novelChapterParagraphs);
            }
            novelChapter.setWordCount(wordCount);
            novelChapterResipority.save(novelChapter);
        }/*else if(allContentChange.equals(0) && (myUtils.MapNotEmpty(originParagraphs) || myUtils.MapNotEmpty(translateParagraphs))){
            Integer p1 = 0;
            Integer p2 = 0;

            if(myUtils.MapNotEmpty(originParagraphs)){
                p1 = originParagraphs.size();
            }
            if(myUtils.MapNotEmpty(translateParagraphs)){
                p2 = translateParagraphs.size();
            }


        }*/


        return JsonResult.oK(novelChapter.getChapter());
    }

    @Transactional
    @Override
    public JsonResult deleteBackendNovelChapter(Long id) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        Optional<NovelChapter> byId = novelChapterResipority.findById(id);
        if(!byId.isPresent()){
            throw new ServiceException("Novel Episode Chapter not found");
        }
        NovelChapter NovelChapter = byId.get();
        if(!NovelChapter.getNovelEpisode().getUserLogin().getId().equals(userLogin.getId())){
            if(!hasAdminPermission(token)){
                throw new ServiceException("No Permission");
            }
        }
        novelChapterResipority.deleteById(id);
        return JsonResult.oK(id);
    }

    @Override
    public JsonResult getUploadList(NovelUploadGetRequest entity) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(entity.getPage(), entity.getSize(), sort);
        Page ret = novelResipority.findByUserLogin(pageable,userLogin);
        return JsonResult.oK(ret);
    }


    @Override
    public JsonResult getNovelList(NovelGetRequest entity) {
        Integer sortBy = entity.getSortBy();
        Sort sort;
        switch (sortBy){
            default:
            case 0:
                sort = Sort.by(Sort.Direction.DESC, "id");
                break;
            case 1:
                sort = Sort.by(Sort.Direction.ASC, "id");
                break;
            case 2:
                sort = Sort.by(Sort.Direction.DESC, "lastChapterUpdatedTime");
                break;
            case 3:
                sort = Sort.by(Sort.Direction.ASC, "lastChapterUpdatedTime");
                break;
            case 4:
                sort = Sort.by(Sort.Direction.DESC, "views");
                break;
            case 5:
                sort = Sort.by(Sort.Direction.ASC, "views");
                break;
            case 6:
                sort = Sort.by(Sort.Direction.DESC, "subscribesCount");
                break;
            case 7:
                sort = Sort.by(Sort.Direction.ASC, "subscribesCount");
                break;
            case 8:
                sort = Sort.by(Sort.Direction.DESC, "likesCount");
                break;
            case 9:
                sort = Sort.by(Sort.Direction.ASC, "likesCount");
                break;
            case 10:
                sort = Sort.by(Sort.Direction.DESC, "commentsCount");
                break;
            case 11:
                sort = Sort.by(Sort.Direction.ASC, "commentsCount");
                break;
        }
        Pageable pageable = PageRequest.of(entity.getPage(), entity.getSize(), sort);
        Page<Novel> ret = novelResiporitySpec.findAllByCondValidate(pageable, entity);
        ret.getContent().forEach(e1->{

        });
        return JsonResult.oK(ret);
    }

    @Override
    public JsonResult getNovelById(Long id) {
        Optional<Novel> byId = novelResipority.findById(id);
        if(!byId.isPresent()){
            throw new ServiceException("Novel not found");
        }
        Novel novel = byId.get();
        String token = JwtTokenUtils.getTokenFromHeader(request);
        checkNovelCanRead(novel,token);
        plusHotScore(novel.getId(),PlusHotScoreTimeLimitEnum.NOVEL);
        return JsonResult.oK(novel);
    }

    @Override
    public JsonResult getEpisodeList(Long novelId) {
        Novel novelDTO = getNovelDTO(novelId);
        List<NovelEpisode> byNovel = novelEpisodeResipority.findByNovelAndValidateOrderByEpisode(novelDTO,1);
        return JsonResult.oK(byNovel);
    }

    @Override
    public JsonResult getEpisodeByEpisode(Long novelId, Long episode) {
        Novel novelDTO = getNovelDTO(novelId);
        Optional<NovelEpisode> byNovelAndAndEpisode = novelEpisodeResipority.findByNovelAndEpisodeAndValidate(novelDTO,episode,1);
        if(!byNovelAndAndEpisode.isPresent()){
            throw new ServiceException("Novel Episode not found");
        }
        NovelEpisode NovelEpisode = byNovelAndAndEpisode.get();
        //UserLogin userLogin = NovelEpisode.getUserLogin();
        return JsonResult.oK(NovelEpisode);
    }


    @Override
    public JsonResult getChapterList(Long novelId, Long episode) {
        Novel novel = getNovel(novelId);
        String token = JwtTokenUtils.getTokenFromHeader(request);
        checkNovelCanRead(novel,token);
        //Optional<NovelEpisode> allByNovelAndEpisode = novelEpisodeResipority.findByNovelAndEpisodeAndValidate(novel, episode,1);
        Optional<NovelEpisode> allByNovelAndEpisode = novelEpisodeResipority.findByNovelAndEpisode(novel, episode);
        if(!allByNovelAndEpisode.isPresent()){
            throw new ServiceException("Episode not found");
        }
        NovelEpisode novelEpisode = allByNovelAndEpisode.get();
        checkNovelChapterCanRead(novelEpisode,token);
        List<NovelChapter> list = novelChapterResipority.findByNovelEpisodeOrderByChapter(novelEpisode);
        return JsonResult.oK(list);
    }

    @Override
    public JsonResult getChapterByChapter(Long novelId, Long episode, Long chapter) {
        Novel novel = getNovel(novelId);

        //Optional<NovelEpisode> allByNovelAndEpisode = novelEpisodeResipority.findByNovelAndEpisodeAndValidate(novel, episode,1);
        Optional<NovelEpisode> allByNovelAndEpisode = novelEpisodeResipority.findByNovelAndEpisode(novel, episode);
        if(!allByNovelAndEpisode.isPresent()){
            throw new ServiceException("Episode not found");
        }

        NovelEpisode novelEpisode = allByNovelAndEpisode.get();
        String token = JwtTokenUtils.getTokenFromHeader(request);
        checkNovelChapterCanRead(novelEpisode,token);
        Optional<NovelChapter> byNovelAndAndChapter = novelChapterResipority.findByNovelEpisodeAndChapter(novelEpisode, chapter);
        if(!byNovelAndAndChapter.isPresent()){
            NovelChapter notFoundChapter = new NovelChapter();
            notFoundChapter.setChapter(chapter);
            return JsonResult.oK(notFoundChapter);
        }
        NovelChapter novelChapter = byNovelAndAndChapter.get();
        //触发lazy
        Set<NovelChapterParagraph> novelChapterParagraphs = novelChapter.getNovelChapterParagraphs();
        novelChapterParagraphs.forEach(each->{
            Set<NovelChapterParagraphComment> novelChapterParagraphComments = each.getNovelChapterParagraphComments();
            novelChapterParagraphComments.forEach(comment->{
                Long id = comment.getId();
            });
        });
        List<NovelChapterComment> collect1 = novelChapter.getNovelChapterComments().stream().sorted().collect(Collectors.toList());
        //添加额外信息
        NovelChapterDTO novelChapterDTO = new NovelChapterDTO();
        BeanUtils.copyProperties(novelChapter,novelChapterDTO);
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        if(novelChapter.getNovelEpisode().getUserLogin() != null){
            BeanUtils.copyProperties(novelChapter.getNovelEpisode().getUserLogin(),userLoginDTO);
        }
        novelChapterDTO.setUserLoginDTO(userLoginDTO);

        asyncTask.plusNovelViews(novel);
        plusHotScore(novel.getId(),0.2,PlusHotScoreTimeLimitEnum.CHAPTER);
        return JsonResult.oK(novelChapterDTO);
    }

    @Override
    public JsonResult getNovelSubscribeList(NovelSubscribeGetRequest entity) {
        Integer sortBy = entity.getSortBy();
        Sort sort;
        switch (sortBy){
            default:
            case 0:
                sort = Sort.by(Sort.Direction.DESC, "id");
                break;
            case 1:
                sort = Sort.by(Sort.Direction.ASC, "id");
                break;
            case 2:
                sort = Sort.by(Sort.Direction.DESC, "novel.lastChapterUpdatedTime");
                break;
            case 3:
                sort = Sort.by(Sort.Direction.ASC, "novel.lastChapterUpdatedTime");
                break;
            case 4:
                sort = Sort.by(Sort.Direction.DESC, "subscribeTime");
                break;
            case 5:
                sort = Sort.by(Sort.Direction.ASC, "subscribeTime");
                break;
        }
        Pageable pageable = PageRequest.of(entity.getPage(), entity.getSize(), sort);
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        Page ret = NovelSubscribeResipority.findByUserLogin(pageable, userLogin);
        return JsonResult.oK(ret);
    }

    @Transactional
    @Override
    public JsonResult addNovelSubscribe(NovelSubscribeAddOrUpdateRequest entity) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        Long novelId = entity.getNovelId();
        Novel novel = getNovel(novelId);
        Optional<NovelSubscribe> allByNovelAndUserLogin = novelSubscribeResipority.findByNovelAndUserLogin(novel, userLogin);
        if(allByNovelAndUserLogin.isPresent()){
            return JsonResult.oK();
        }
        NovelSubscribe novelSubscribe = new NovelSubscribe();
        novelSubscribe.setNovel(novel);
        novelSubscribe.setUserLogin(userLogin);
        novelSubscribe.setSubscribeTime(new Date().getTime());
        novelSubscribe.setHasNew(0);
        novelSubscribeResipority.save(novelSubscribe);
        //asyncTask.novelSubscribesCount(novel);
        long count = novelSubscribeResipority.countByNovel(novel);
        novel.setSubscribesCount(count);
        novelResipority.save(novel);
        symbolUserNeedUpdate(userLogin);
        return JsonResult.oK(novelSubscribe.getId());
    }

    @Transactional
    @Override
    public JsonResult deleteNovelSubscribeList(Long novelId) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        Novel novel = getNovel(novelId);
        Optional<NovelSubscribe> allByNovelAndUserLogin = novelSubscribeResipority.findByNovelAndUserLogin(novel, userLogin);
        if(!allByNovelAndUserLogin.isPresent()){
            return JsonResult.oK();
        }
        Long id = allByNovelAndUserLogin.get().getId();
        novelSubscribeResipority.deleteById(id);
        long count = novelSubscribeResipority.countByNovel(novel);
        novel.setSubscribesCount(count);
        novelResipority.save(novel);
        symbolUserNeedUpdate(userLogin);
        return JsonResult.oK(id);
    }

    @Override
    public JsonResult getNovelCommentList(NovelCommentGetRequest entity) {
        Integer sortBy = entity.getSortBy();
        Sort sort;
        switch (sortBy){
            default:
            case 0:
                sort = Sort.by(Sort.Direction.DESC, "id");
                break;
            case 1:
                sort = Sort.by(Sort.Direction.ASC, "id");
                break;
        }
        Pageable pageable = PageRequest.of(entity.getPage(), entity.getSize(), sort);
        String token = JwtTokenUtils.getTokenFromHeader(request);
        Long selfUserId = null;
        UserLogin userLogin;
        if(token != null){
            userLogin = jwtTokenUtils.getUserLogin(token);
            selfUserId = userLogin.getId();
        }
        Page<NovelComment> ret = novelCommentResiporitySpec.findAllByCond(pageable, entity , selfUserId);
        if(token !=null){
            userLogin = jwtTokenUtils.getUserLogin(token);
            if(selfUserId != null){
                UserLogin finalUserLogin = userLogin;
                ret.getContent().forEach(comment->{
                    comment.setLiked(novelCommentLikesResipority.findByNovelCommentAndUserLogin(comment, finalUserLogin).isPresent());
                    comment.setDisliked(novelCommentDislikesResipority.findByNovelCommentAndUserLogin(comment, finalUserLogin).isPresent());
                });
            }
        }
        return JsonResult.oK(ret);
    }

    @Transactional
    @Override
    public JsonResult addNovelComment(NovelCommentAddOrUpdateRequest entity) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        UserLoginDTO userInfo = jwtTokenUtils.getUserInfo(token);
        Novel novel = getNovel(entity.getNovelId());
        NovelComment novelComment = new NovelComment();
        novelComment.setUserLogin(userLogin);
        novelComment.setNovel(novel);
        novelComment.setCreatedTime(new Date());
        novelComment.setIsDelete(0);
        novelComment.setPriority(0);
        novelComment.setMes(entity.getMes());
        novelComment.setTempUserComment(userInfo.getRoles().size() <= 1 ? 1 : 0);
        novelCommentResipority.save(novelComment);
        novelResipority.save(novel);
        //asyncTask.novelCommentsCount(novelComment.getNovel());
        long count = novelCommentResipority.countByNovel(novel);
        novel.setCommentsCount(count);
        novelResipority.save(novel);
        plusHotScore(novel.getId(),3.0,PlusHotScoreTimeLimitEnum.COMMENT);
        return JsonResult.oK(novelComment.getId());
    }

    @Transactional
    @Override
    public JsonResult deleteNovelComment(Long id) {
        Optional<NovelComment> byId = novelCommentResipority.findById(id);
        if(!byId.isPresent()){
            throw new ServiceException("Novel Comment Not found");
        }
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        NovelComment novelComment = byId.get();
        UserLogin commentUserLogin = novelComment.getUserLogin();
        if(commentUserLogin == null || !commentUserLogin.getId().equals(userLogin.getId())){
            if(!hasAdminPermission(token)){
                throw new ServiceException("No Permission");
            }
        }
        novelComment.setIsDelete(1);
        novelCommentResipority.save(novelComment);
        Novel novel = novelComment.getNovel();
        //asyncTask.novelCommentsCount(novelComment.getNovel());
        long count = novelCommentResipority.countByNovel(novel);
        novel.setCommentsCount(count);
        novelResipority.save(novel);
        return JsonResult.oK(novelComment.getId());
    }

    @Transactional
    @Override
    public JsonResult addOrDeleteNovelCommentLikes(NovelCommentLikesAddOrUpdateRequest entity) {
        Optional<NovelComment> byId = novelCommentResipority.findById(entity.getId());
        if(!byId.isPresent()){
            throw new ServiceException("Novel Comment Not found");
        }
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        NovelComment novelComment = byId.get();
        Integer value = entity.getValue();
        Optional<NovelCommentLikes> byNovelCommentAndUserLogin = novelCommentLikesResipority.findByNovelCommentAndUserLogin(novelComment, userLogin);
        if(byNovelCommentAndUserLogin.isPresent()){
            //throw new ServiceException("Novel Not found");
            NovelCommentLikes novelCommentLikes = byNovelCommentAndUserLogin.get();
            Long id = novelCommentLikes.getId();
            if(value.equals(0)){
                novelCommentLikesResipority.deleteById(id);
                //asyncTask.novelCommentLikesCount(novelComment);
                long count = novelCommentLikesResipority.countByNovelComment(novelComment);
                novelComment.setLikesCount(count);
                novelCommentResipority.save(novelComment);
                symbolUserNeedUpdate(userLogin);
                return JsonResult.oK(id);
            }else{
                return JsonResult.oK();
            }
        }else{
            if(value.equals(1)){
                NovelCommentLikes novelCommentLikes = new NovelCommentLikes();
                novelCommentLikes.setNovelComment(novelComment);
                novelCommentLikes.setUserLogin(userLogin);
                novelCommentLikesResipority.save(novelCommentLikes);
                //asyncTask.novelCommentLikesCount(novelComment);
                long count = novelCommentLikesResipority.countByNovelComment(novelComment);
                novelComment.setLikesCount(count);
                novelCommentResipority.save(novelComment);
                symbolUserNeedUpdate(userLogin);
                return JsonResult.oK(novelCommentLikes.getId());
            }
        }
        return JsonResult.oK();
    }

    @Override
    public JsonResult addOrDeleteNovelCommentDislikes(NovelCommentDislikesAddOrUpdateRequest entity) {
        Optional<NovelComment> byId = novelCommentResipority.findById(entity.getId());
        if(!byId.isPresent()){
            throw new ServiceException("Novel Comment Not found");
        }
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        NovelComment novelComment = byId.get();
        Integer value = entity.getValue();
        Optional<NovelCommentDislikes> byNovelCommentAndUserLogin = novelCommentDislikesResipority.findByNovelCommentAndUserLogin(novelComment, userLogin);
        if(byNovelCommentAndUserLogin.isPresent()){
            //throw new ServiceException("Novel Not found");
            NovelCommentDislikes novelCommentDislikes = byNovelCommentAndUserLogin.get();
            Long id = novelCommentDislikes.getId();
            if(value.equals(0)){
                novelCommentDislikesResipority.deleteById(id);
                //asyncTask.novelCommentsDislikesCount(novelComment);
                long count = novelCommentDislikesResipority.countByNovelComment(novelComment);
                novelComment.setDislikesCount(count);
                novelCommentResipority.save(novelComment);
                symbolUserNeedUpdate(userLogin);
                return JsonResult.oK(id);
            }else{
                return JsonResult.oK();
            }
        }else{
            if(value.equals(1)){
                NovelCommentDislikes novelCommentDislikes = new NovelCommentDislikes();
                novelCommentDislikes.setNovelComment(novelComment);
                novelCommentDislikes.setUserLogin(userLogin);
                novelCommentDislikesResipority.save(novelCommentDislikes);
                //asyncTask.novelCommentsDislikesCount(novelComment);
                long count = novelCommentDislikesResipority.countByNovelComment(novelComment);
                novelComment.setDislikesCount(count);
                novelCommentResipority.save(novelComment);
                symbolUserNeedUpdate(userLogin);
                return JsonResult.oK(novelCommentDislikes.getId());
            }
        }
        return JsonResult.oK();
    }

    @Override
    public JsonResult getNovelChapterParagraphCommentByParagraphId(Long paragraphId) {
        Optional<NovelChapterParagraph> byId = novelChapterParagraphResipority.findById(paragraphId);
        if(!byId.isPresent()){
            throw new ServiceException("Novel Chapter Paragraph Not found");
        }
        NovelChapterParagraph novelChapterParagraph = byId.get();
        List<NovelChapterParagraphComment> list = novelChapterParagraphCommentResipority.findByNovelChapterParagraph(novelChapterParagraph);
        return JsonResult.oK(list);
    }

    @Override
    public JsonResult getNovelChapterParagraphCommentList(NovelChapterParagraphCommentGetRequest entity) {
        List<Long> paragraphIds = entity.getParagraphIds();
        List<NovelChapterParagraphComment> byId = novelChapterParagraphCommentResipority.findByIdIn(paragraphIds);
        return JsonResult.oK(byId);
    }

    @Transactional
    @Override
    public JsonResult addNovelChapterParagraphComment(NovelChapterParagraphCommentAddOrUpdateRequest entity) {
        Long paragraphId = entity.getParagraphId();
        Optional<NovelChapterParagraph> byId = novelChapterParagraphResipority.findById(paragraphId);
        if(!byId.isPresent()){
            throw new ServiceException("Novel Episode Chapter Paragraph Not found");
        }
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        NovelChapterParagraph novelChapterParagraph = byId.get();
        NovelChapterParagraphComment novelChapterParagraphComment = new NovelChapterParagraphComment();
        novelChapterParagraphComment.setCreatedTime(new Date());
        novelChapterParagraphComment.setMes(entity.getMes());
        novelChapterParagraphComment.setNovelChapterParagraph(novelChapterParagraph);
        novelChapterParagraphComment.setUserLogin(userLogin);
        novelChapterParagraphCommentResipority.save(novelChapterParagraphComment);
        try{
            Long id = novelChapterParagraph.getNovelChapter().getNovelEpisode().getNovel().getId();
            plusHotScore(id,2.0,PlusHotScoreTimeLimitEnum.COMMENT);
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResult.oK(novelChapterParagraphComment.getId());
    }

    @Transactional
    @Override
    public JsonResult addOrDeleteNovelLikes(NovelLikesAddOrUpdateRequest entity) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);

        Long novelId = entity.getNovelId();
        Novel novel = getNovel(novelId);
        Integer value = entity.getValue();
        Optional<NovelLikes> byNovelAndUserLogin = novelLikesResipority.findByNovelAndUserLogin(novel, userLogin);
        if(byNovelAndUserLogin.isPresent()){
            //throw new ServiceException("Novel Not found");
            NovelLikes novelLikes = byNovelAndUserLogin.get();
            Long id = novelLikes.getId();
            if(value.equals(0)){
                novelLikesResipority.deleteById(id);
//                asyncTask.novelLikesCount(novel);
                long count = novelLikesResipority.countByNovel(novel);
                novel.setLikesCount(count);
                novelResipority.save(novel);
                symbolUserNeedUpdate(userLogin);
                return JsonResult.oK(id);
            }else{
                return JsonResult.oK();
            }
        }else{
            if(value.equals(1)){
                NovelLikes novelLikes = new NovelLikes();
                novelLikes.setNovel(novel);
                novelLikes.setUserLogin(userLogin);
                novelLikesResipority.save(novelLikes);
                //asyncTask.novelLikesCount(novel);
                long count = novelLikesResipority.countByNovel(novel);
                novel.setLikesCount(count);
                novelResipority.save(novel);
                symbolUserNeedUpdate(userLogin);
                return JsonResult.oK(novelLikes.getId());
            }
        }
        return JsonResult.oK();
    }



    @Override
    public JsonResult getNovelHistoryList(NovelHistoryGetRequest entity) {
        Sort sort = Sort.by(Sort.Direction.DESC, "lastReadTime");
        Pageable pageable = PageRequest.of(entity.getPage(), entity.getSize(), sort);
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        Page<NovelHistory> byUserLogin = novelHistoryResipority.findByUserLogin(pageable, userLogin);
        return JsonResult.oK(byUserLogin);
    }

    @Transactional
    @Override
    public JsonResult addOrUpdateNovelHistory(NovelHistoryAddOrUpdateRequest entity) {
        Long novelId = entity.getNovelId();
        Long episode = entity.getEpisode();
        Long chapter = entity.getChapter();
        Novel novel = getNovelDTO(novelId);
        Optional<NovelEpisode> byNovelAndEpisode = novelEpisodeResipority.findByNovelAndEpisode(novel, episode);
        if(!byNovelAndEpisode.isPresent()){
            throw new ServiceException("Novel Episode Not found");
        }
        NovelEpisode NovelEpisode = byNovelAndEpisode.get();
        Optional<NovelChapter> byNovelEpisodeAndChapter = novelChapterResipority.findByNovelEpisodeAndChapter(NovelEpisode, chapter);
        if(!byNovelEpisodeAndChapter.isPresent()){
            throw new ServiceException("Novel Episode Chapter Not found");
        }
        NovelChapter NovelChapter = byNovelEpisodeAndChapter.get();
        NovelChapter novelChapter = new NovelChapter();
        BeanUtils.copyProperties(NovelChapter,novelChapter);
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        Optional<NovelHistory> byNovelChapterAndUserLogin = novelHistoryResipority.findByNovelChapterAndUserLogin(novelChapter, userLogin);
        NovelHistory novelHistory = new NovelHistory();
        byNovelChapterAndUserLogin.ifPresent(temp -> novelHistory.setId(temp.getId()));
        novelHistory.setReadWordCount(entity.getReadWordCount());
        novelHistory.setReadPage(entity.getReadPage());
        novelHistory.setReadParagraph(entity.getReadParagraph());
        novelHistory.setNovelChapter(novelChapter);
        novelHistory.setUserLogin(userLogin);
        novelHistory.setLastReadTime(new Date().getTime());
        novelHistoryResipority.save(novelHistory);
        asyncTask.novelHistoryReachMaxClear(userLogin);
        symbolUserNeedUpdate(userLogin);
        return JsonResult.oK(novelHistory.getId());
    }

    @Transactional
    @Override
    public JsonResult deleteNovelHistory(Long novelId) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        Novel novel = new Novel();
        novel.setId(novelId);
        Optional<NovelHistory> byId = novelHistoryResipority.findByNovelAndUserLogin(novel,userLogin);
        if(!byId.isPresent()){
            return JsonResult.oK();
        }
        Long id = byId.get().getId();
        novelHistoryResipority.deleteById(id);
        symbolUserNeedUpdate(userLogin);
        return JsonResult.oK(id);
    }

    @Override
    public JsonResult getHotNovel(HotNovelGetRequest entity) {
        LocalDateTime dateTime = LocalDateTime.parse("2020-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime now = LocalDateTime.now();

        Long startTime = entity.getStartTime() <= 0 ? dateTime.toEpochSecond(ZoneOffset.of("+8")) : entity.getStartTime();
        Long endTime = entity.getEndTime()  <= 0 ? now.toEpochSecond(ZoneOffset.of("+8")) : entity.getEndTime();

        Instant startInstant = new Date(startTime).toInstant();
        Instant endInstant = new Date(endTime).toInstant();
        Instant nowInstant = now.toInstant(ZoneOffset.of("+8"));
        if(startInstant.isAfter(nowInstant) || startInstant.isAfter(endInstant)){
            return JsonResult.oK();
        }
        if(endInstant.isAfter(nowInstant)){
            endInstant = nowInstant;
        }
        if(startInstant.plus(30, ChronoUnit.DAYS).isBefore(endInstant)){
            return JsonResult.build(DefaultConstants.ERROR_SERVIE_ERRORTEXT,"Too Long");
        }
        LocalDate startDate = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault()).toLocalDate();
        LocalDate temp = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault()).toLocalDate();
//
//        List<Set<Tuple>> ranks = new ArrayList<>();
//        while (!temp.isAfter(endDate)){
//            String rankName = MessageFormat.format("{0}:{1}{2}{3}",RankEnum.NovelView.getName(),temp.getYear(),temp.getMonthValue(),temp.getDayOfMonth());
//            Set<Tuple> tuples = jedisCluster.zrangeWithScores(rankName, 0, 100);
//            ranks.add(tuples);
//            temp = temp.plusDays(1);
//        }
//        Set<Set<Tuple>> unionRank = new LinkedHashSet<>();
//        List<Novel> ret = new ArrayList<>();
//        ranks.forEach(each->{
//            each.forEach(tuple -> {
//                String element = tuple.getElement();
//                double score = tuple.getScore();
//                System.out.println(element);
//                unionRank
//            });
//        });
//        return JsonResult.oK(ranks);
        String uuid = UUID.randomUUID().toString();
        List<String> rankNames = new ArrayList<>();
        while (!temp.isAfter(endDate)){
            String rankName = "{" + RankEnum.NovelView.getName() + "}" + temp.getYear() + temp.getMonthValue() + temp.getDayOfMonth();
            rankNames.add(rankName);
            temp = temp.plusDays(1);
        }
        String[] sets = new String[rankNames.size()];
        rankNames.toArray(sets);
        String unionTempKey = "{" + RankEnum.NovelView.getName()  + "}"+ uuid;
        jedisCluster.zunionstore(unionTempKey,sets);
        Set<Tuple> tuples = jedisCluster.zrangeWithScores(unionTempKey, 0, 100);
        jedisCluster.del(unionTempKey);
        List<RankNovelDTO> list = new ArrayList<>();
        List<Long> ids = tuples.stream().map(tuple -> Long.valueOf(tuple.getElement())).collect(Collectors.toList());
        if(ids.size() > 0){
            List<Novel> byIdIn = novelResipority.findByIdIn(ids);
            list = byIdIn.stream().map(each->{
                Tuple tempTuple = tuples.stream().filter(tuple -> Long.valueOf(tuple.getElement()).equals(each.getId())).findFirst().get();
                RankNovelDTO rankNovelDTO = new RankNovelDTO();
                rankNovelDTO.setNovel(each);
                rankNovelDTO.setScore(tempTuple.getScore());
                return rankNovelDTO;
            }).collect(Collectors.toList());
        }
        list.sort((prev,next)-> next.getScore() >= prev.getScore() ? 0 : -1);
        Integer page = entity.getPage();
        Integer size = entity.getSize();
        Integer fromIndex = page * size;
        Integer toIndex = (page + 1) * size;
        if(fromIndex >= list.size()){
            list = new ArrayList<>();
        }else{
            toIndex = toIndex >= list.size() ? list.size() : toIndex;
            list.subList(fromIndex,toIndex);
        }
        PageImpl<RankNovelDTO> rankNovelDTOS = new PageImpl<>(list);
        return JsonResult.oK(rankNovelDTOS);
    }

    @Override
    public JsonResult getFeedbackList(FeedbackGetRequest entity) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdTime");
        Pageable pageable = PageRequest.of(entity.getPage(), entity.getSize(), sort);
        Page<FeedbackHistory> page = feedbackHistoryResipority.findByUserLogin(pageable, userLogin);
        return JsonResult.oK(page);
    }

    @Transactional
    @Override
    public JsonResult addOrUpdateFeedback(FeedbackAddOrUpdateRequest entity) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        FeedbackHistory feedbackHistory = new FeedbackHistory();
        Long id = entity.getId();
        if(id != null){
            Optional<FeedbackHistory> byId = feedbackHistoryResipority.findById(id);
            if(byId.isPresent()){
                FeedbackHistory temp = byId.get();
                if(temp.getUserLogin() != null && temp.getUserLogin().getId().equals(userLogin.getId())){
                    feedbackHistory = temp;
                }
            }
        }else{
            long count = feedbackHistoryResipority.countByUserLoginAndStatus(userLogin, 0);
            Integer max = 10;
            if(count >= max){
                throw new ServiceException(MessageFormat.format("个人同时反馈列表上限不能超过{0}条,请等待管理员结束反馈",max));
            }
        }
        feedbackHistory.setReportData(entity.getReportData());
        feedbackHistory.setReportMes(entity.getReportMes());
        feedbackHistory.setReportReason(entity.getReportReason());
        feedbackHistory.setReportReasonNum(entity.getReportReasonNum());
        feedbackHistory.setReportUrl(entity.getReportUrl());
        feedbackHistory.setUserLogin(userLogin);
        feedbackHistoryResipority.save(feedbackHistory);
        return JsonResult.oK(feedbackHistory.getId());
    }


    private void plusHotScore(Novel novel){
        this.plusHotScore(novel.getId(),PlusHotScoreTimeLimitEnum.DEFAULT);
    }

    private void plusHotScore(Long novelId,PlusHotScoreTimeLimitEnum plusHotScoreTimeLimitEnum){
        this.plusHotScore(novelId,1.0,plusHotScoreTimeLimitEnum);
    }

    private void plusHotScore(Novel novel,Double score,PlusHotScoreTimeLimitEnum plusHotScoreTimeLimitEnum){
        this.plusHotScore(novel.getId(),score,plusHotScoreTimeLimitEnum);
    }

    private void plusHotScore(Long novelId, Double score, PlusHotScoreTimeLimitEnum plusHotScoreTimeLimitEnum){
        String novelSym = novelId + "";
        String key;
        String token = JwtTokenUtils.getTokenFromHeader(request);
        String name = plusHotScoreTimeLimitEnum.getName();
        Long second = plusHotScoreTimeLimitEnum.getSecond();
        if(StringUtils.isNotEmpty(token)){
            UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
            String username = userLogin.getUsername();
            key = PreConstants.PHSPRE + username + "_" + name + "" + novelSym;
        }else{
            String ipAddr = IpUtils.getIpAddr(request);
            key = PreConstants.PHSPRE + ipAddr + "_" + name + "" + novelSym;
        }
        if(jedisCluster.get(key) != null){
            return;
        }
        LocalDateTime now = LocalDateTime.now();
//        String rankName = RankEnum.NovelView.getName() + now.getYear() + now.getMonthValue() + now.getDayOfMonth();
        // Cluster Error?
        String rankName = "{" + RankEnum.NovelView.getName() + "}" + now.getYear() + now.getMonthValue() + now.getDayOfMonth();
        Double zscore = jedisCluster.zscore(rankName, novelSym);
        if(zscore == null){
            jedisCluster.zadd(rankName,0,novelSym);
        }
        jedisCluster.zincrby(rankName,score,novelSym);
        jedisCluster.setex(key,second.intValue(),"1");
    }

    private void symbolUserNeedUpdate(UserLogin userLogin){
        String key = PreConstants.REDISUSERINFONEEDUPDATE + userLogin.getUsername();
        jedisCluster.setex(key,DefaultConstants.ONEDAYSECOND * 3,"subscribe");
    }
}
