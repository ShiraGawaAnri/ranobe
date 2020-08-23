package com.kd.novel.backend.config.async;

import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.config.constants.PreConstants;
import com.kd.novel.backend.entity.Novel;
import com.kd.novel.backend.entity.NovelComment;
import com.kd.novel.backend.entity.NovelHistory;
import com.kd.novel.backend.entity.UserLogin;
import com.kd.novel.backend.resipority.*;
import com.kd.novel.backend.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.List;

@Component
@Async("asyncServiceExecutor")
public class AsyncTask {

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
    private NovelLikesResipority novelLikesResipority;

    @Autowired
    private NovelSubscribeResipority novelSubscribeResipority;

    @Autowired
    private NovelCommentResipority novelCommentResipority;

    @Autowired
    private NovelCommentResiporitySpec novelCommentResiporitySpec;

    @Autowired
    private NovelChapterParagraphCommentResipority novelChapterParagraphCommentResipority;

    @Autowired
    private NovelHistoryResipority novelHistoryResipority;

    @Autowired
    private NovelCommentLikesResipority novelCommentLikesResipority;

    @Autowired
    private NovelCommentDislikesResipority novelCommentDislikesResipority;

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    public void plusNovelViews(Novel novel){
        this.plusNovelViews(novel,1);
    }

    public void plusNovelViews(Novel novel,Integer count){
        int i = novel.getViews() == null ? 1 : novel.getViews() + count;
        novel.setViews(i);
        novelResipority.save(novel);
    }

    public void novelLikesCount(Novel novel){
        long count = novelLikesResipority.countByNovel(novel);
        novel.setLikesCount(count);
        novelResipority.save(novel);
    }

    public void novelSubscribesCount(Novel novel){
        long count = novelSubscribeResipority.countByNovel(novel);
        novel.setSubscribesCount(count);
        novelResipority.save(novel);
    }

    public void novelCommentsCount(Novel novel){
        long count = novelCommentResipority.countByNovel(novel);
        novel.setCommentsCount(count);
        novelResipority.save(novel);
    }

    public void novelCommentLikesCount(NovelComment novelComment){
        long count = novelCommentLikesResipority.countByNovelComment(novelComment);
        novelComment.setLikesCount(count);
        novelCommentResipority.save(novelComment);
    }

    public void novelCommentsDislikesCount(NovelComment novelComment){
        long count = novelCommentDislikesResipority.countByNovelComment(novelComment);
        novelComment.setDislikesCount(count);
        novelCommentResipority.save(novelComment);
    }


    public void novelHistoryReachMaxClear(UserLogin userLogin){
        final Integer max = 30;
        List<NovelHistory> byUserLoginOrderByLastReadTimeDesc = novelHistoryResipority.findByUserLoginOrderByLastReadTimeDesc(userLogin);
        if(byUserLoginOrderByLastReadTimeDesc.size() > max){
            for(int i = max; i < byUserLoginOrderByLastReadTimeDesc.size();i++){
                NovelHistory novelHistory = byUserLoginOrderByLastReadTimeDesc.get(i);
                novelHistoryResipority.deleteById(novelHistory.getId());
            }
        }
    }

    public void novelSubscribeNotify(Novel novel){
        List<String> notifyNovelHasNewUserIds = novelSubscribeResipority.getNotifyNovelHasNewUserIds(novel);
        notifyNovelHasNewUserIds.forEach(username->{
            String key = PreConstants.REDISUSERINFONEEDUPDATE + username;
            jedisCluster.setex(key,DefaultConstants.ONEDAYSECOND * 3,"subscribe");
        });
        novelSubscribeResipority.notifyNovelHasNew(novel);
    }
}


