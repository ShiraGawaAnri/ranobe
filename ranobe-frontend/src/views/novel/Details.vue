<template>
  <div id="novel" v-if="novel" v-cloak>
    <a-layout style="background: white;">
      <a-layout-header class="novel-info">
        <Breadcrumb :breadcrumb="breadcrumb" />
        <a-row>
          <a-col style="padding: 6px 6px;" :span="18" :xl="6">
            <div class="novel-cover" :style="{width: '100%',height: '100%', padding: '10px 10px'}">
              <div class="novel-cover-border">
                <template v-if="swiperCovers.length > 0">
                  <a-carousel
                    autoplay
                    effect="fade"
                    :dots="true"
                    :autoplaySpeed="Math.max(6000/swiperCovers.length,1500)"
                    :speed="1000"
                  >
                    <img
                      v-for="(cover,index) in swiperCovers"
                      :key="index"
                      alt="Novel Cover"
                      :src="cover.url"
                      style="width: 100%;height: 100%;padding: 10px 10px;"
                    />
                  </a-carousel>
                </template>
                <a-empty v-else />
                <!-- <img
                  slot="cover"
                  alt="Novel Cover"
                  :src="(!isValNullUndefined(novel.novelCovers) && novel.novelCovers.length > 0) ? novel.novelCovers[0].url:''"
                  style="width: 100%;height: 100%;padding: 10px 10px;"
                />-->
              </div>
            </div>
          </a-col>
          <a-col style="padding: 6px 6px 6px 24px;min-width: 500px;" :span="18">
            <div class="novel-statics">
              <a-icon type="eye" />
              <span>{{` ${novel.views}`}}</span>
              <a-icon type="mail" style="margin-left:40px;" />
              <span>{{` ${novel.subscribesCount || 0}`}}</span>
              <a-icon type="message" style="margin-left:40px;" />
              <span>{{` ${novel.commentsCount || 0}`}}</span>
              <!-- <a-row type="flex" justify="start">
                <a-col :span="4" class="novel-statics-views">
                  <a-icon type="eye" />
                  <span>{{` ${novel.views}`}}</span>
                </a-col>
                <a-col :span="4" class="novel-statics-subscribes">
                  <a-icon type="mail" />
                  <span>{{` ${novel.subscribesCount}`}}</span>
                </a-col>
                <a-col :span="4" class="novel-statics-comments">
                  <a-icon type="message" />
                  <span>{{` ${novel.commentsCount}`}}</span>
                </a-col>
              </a-row>-->
            </div>
            <br />
            <div class="title">
              <span class="title-text">{{ traditionlize(novel.translateTitle) }}</span>
            </div>
            <br />
            <div class="show-prop title">
              <span v-trans class="prop-text">原名:</span>
              <span class="prop-val">{{novel.originTitle}}</span>
            </div>
            <div class="show-prop artist">
              <span v-trans class="prop-text">原著:</span>
              <span class="prop-val" style="padding: 0 0;">
                <a-button
                  type="link"
                  size="large"
                  @click="searchByArtitst"
                >{{`${traditionlize(novel.novelArtist.translateName)} ( ${novel.novelArtist.originName} )`}}</a-button>
              </span>
            </div>
            <div class="show-prop finish set-height">
              <span v-trans class="prop-text">连载状态:</span>
              <span class="prop-val">
                <a-tag v-trans color="green" v-if="novel.finish === 1">已完结</a-tag>
                <a-tag v-trans color="blue" v-else-if="novel.finish === 0">连载中</a-tag>
                <a-tag v-trans color="cyan" v-else>未知</a-tag>
              </span>
            </div>
            <div class="show-prop statics set-height">
              <span v-trans class="prop-text">连载统计:</span>
              <span
                class="prop-val"
              >{{traditionlize(`当前 全 ${novel.epCount} 卷 | 共 ${novel.chapterCount} 话 | 合计 ${toThousandNumber(novel.totalChar)}字`)}}</span>
            </div>
            <div class="show-prop last-update set-height">
              <span v-trans class="prop-text">最终更新日期:</span>
              <span class="prop-val">{{`${parseTime(novel.lastChapterUpdatedTime)}`}}</span>
            </div>
            <div class="show-prop last-title set-height">
              <span v-trans class="prop-text">最终标题:</span>
              <span class="prop-val">{{traditionlize(`${novel.shortTitle}`)}}</span>
            </div>
            <div class="show-prop tags set-height">
              <span v-trans class="prop-text">标签:</span>
              <span class="prop-val">
                <a-tag
                  color="orange"
                  v-for="tag in novel.novelTags"
                  :key="tag.id"
                >{{traditionlize(tag.novelTags.nameCn)}}</a-tag>
              </span>
            </div>
            <hr />
            <div class="novel-actions">
              <div class="action-subscribe">
                <a-tooltip :title="traditionlize(subscribed ? '取消订阅':'订阅小说')">
                  <a-button
                    :loading="subscribedLoading"
                    type="raise"
                    icon="mail"
                    :class="{'active':subscribed}"
                    @click="($event)=>subscribeNovel($event)"
                  >{{traditionlize(`${subscribed ? '已订阅':'订阅'}(${novel.subscribesCount || 0})`)}}</a-button>
                </a-tooltip>
              </div>
              <div class="action-like">
                <a-tooltip :title="traditionlize(liked ? '取消喜爱':'喜爱小说')">
                  <a-button
                    :loading="likedLoading"
                    type="raise"
                    icon="like"
                    :class="{'active':liked}"
                    @click="($event)=>likeNovel($event)"
                  >{{traditionlize(`${liked ? '已喜爱':'喜爱'}(${novel.likesCount || 0})`)}}</a-button>
                </a-tooltip>
              </div>
              <div class="action-report">
                <a-button
                  type="raise"
                  icon="like"
                  @click="($event)=>openNovelReport($event)"
                >{{traditionlize(`反馈`)}}</a-button>
              </div>
            </div>
          </a-col>
        </a-row>
        <br />
      </a-layout-header>
      <a-layout-content style>
        <a-layout style="margin-top:30px;">
          <a-layout-content style>
            <div v-trans class="novel-introduction">{{novel.introduction}}</div>
            <div class="novel-episode-list">
              <a-collapse
                v-model="activeKey"
                expand-icon-position="left"
                v-for="(episode,index) in novel.novelEpisodes"
                :key="index"
              >
                <template #expandIcon="props">
                  <a-icon type="caret-right" :rotate="props.isActive ? 90 : 0" />
                </template>
                <a-collapse-panel :key="'n'+index">
                  <div slot="header">
                    <span>{{traditionlize(headerEpText(episode))}}</span>
                  </div>
                  <a-list
                    size="small"
                    bordered
                    :data-source="episode.novelChapters"
                    class="novel-episode-chapter-list"
                  >
                    <a-list-item
                      slot="renderItem"
                      slot-scope="chapter"
                      @click="($event)=>toNovelChapter(episode,chapter)"
                      class="novel-episode-chapter-list-item"
                      style="padding-top:4px;padding-bottom:4px;"
                    >
                      <a-list-item-meta :description="chapter.originTitle">
                        <a
                          v-trans
                          slot="title"
                          :href="getNovelChapter(episode,chapter)"
                        >{{chapter.translateTitle}}</a>
                      </a-list-item-meta>
                      <div>{{`${!isValNullUndefined(chapter.wordCount) ? toThousandNumber(chapter.wordCount) +' 字' : ''} `}}</div>
                    </a-list-item>
                    <!-- <div slot="header">Header</div>
                    <div slot="footer">Footer</div>-->
                  </a-list>
                </a-collapse-panel>
              </a-collapse>
            </div>
          </a-layout-content>
          <a-layout-sider
            style="background:white;padding: 6px 6px;"
            width="300"
            class="recommand-side"
          >
            <HotList :size="5" />
          </a-layout-sider>
        </a-layout>
      </a-layout-content>
      <a-layout-footer class="footer-comment">
        <div class="comments-count">
          <strong>{{`${commentsCount} ${traditionlize('个评论')}`}}</strong>
        </div>
        <div class="comment-box">
          <a-tabs v-if="!isMobile" @change="commentSortTabChange" v-model="commentSortBy">
            <a-tab-pane key="1" :tab="traditionlize('按欢乐排序')"></a-tab-pane>
            <a-tab-pane key="0" :tab="traditionlize('按时间排序')"></a-tab-pane>
            <div slot="tabBarExtraContent" style="margin-top:8px;text-align:right;">
              <a-pagination
                size="small"
                :total="totalComments"
                v-if="totalComments > 0"
                v-model="commentQuery.currentPage"
                :page-size.sync="commentQuery.pageSize"
                show-quick-jumper
                :default-current="commentQuery.currentPage"
                @change="commentPageChange"
                :show-total="total => `Total ${totalCommentPages} pages`"
              />
            </div>
          </a-tabs>
          <a-tabs v-else @change="commentSortTabChange" v-model="commentSortBy">
            <a-tab-pane key="1" :tab="traditionlize('按欢乐排序')"></a-tab-pane>
            <a-tab-pane key="0" :tab="traditionlize('按时间排序')"></a-tab-pane>
          </a-tabs>
          <a-comment>
            <a-avatar v-if="avatar" slot="avatar" :size="64" :src="avatar" alt="YourAvatar" />
            <div slot="content">
              <a-row type="flex" justify="space-around">
                <a-col :span="16">
                  <a-textarea
                    v-if="logined"
                    class="comment-input-textarea"
                    :rows="4"
                    :value="comment.mes"
                    @change="handleMesChange"
                    :placeholder="traditionlize('发条友善的评论')"
                  />
                  <div v-else style="background-color:#f4f5f7;height: 65px;">
                    <div style="text-align:center;margin:auto auto;">
                      <span>请先进行</span>
                      <a-button type="link" size="small" @click="($event)=>GoTo('/account?type=login')">登录</a-button>
                      <span>或</span>
                      <a-button type="link" size="small" @click="($event)=>handleAccountModal(true)">注册</a-button>
                      <span>才可以发表评论</span>
                    </div>
                  </div>
                </a-col>
                <a-col :span="4">
                  <a-button
                    :disabled="!logined"
                    html-type="submit"
                    :loading="comment.submitting"
                    type="primary"
                    @click="handleSubmitComment"
                    :style="{'width':(comment.submitting?'85px':'65px'),'height':'65px','white-space': 'pre-wrap'}"
                  >{{traditionlize('发表评论')}}</a-button>
                </a-col>
              </a-row>
            </div>
          </a-comment>
          <hr />
          <a-spin :spinning="commentSpinning" tip="Loading...">
            <a-list class="comment-list" item-layout="horizontal" :data-source="comments">
              <a-list-item slot="renderItem" slot-scope="item">
                <a-comment
                  :author="item.userLogin ? item.userLogin.nickname : ''"
                  :avatar="item.userLogin ? item.userLogin.avatar : ''"
                >
                  <template slot="actions">
                    <span v-for="(action,index) in item.actions" :key="index">{{ action }}</span>
                  </template>
                  <p class="comment-text" slot="content">{{ traditionlize(item.mes) }}</p>
                  <div class="comment-content-buttom">
                    <a-tooltip :title="item.datetime.format('YYYY-MM-DD HH:mm:ss')">
                      <span>{{ item.datetime.fromNow() }}</span>
                    </a-tooltip>
                    <span class="comment-basic-like">
                      <a-tooltip :title="traditionlize('欢乐')">
                        <a-icon
                          type="like"
                          :theme="item.liked ? 'twoTone' : 'outlined'"
                          two-tone-color="#eb2f96"
                          :loading="commentLikedLoading"
                          @click="($event)=>likeComment(item)"
                        />
                      </a-tooltip>
                      <span
                        style="margin-left:0px;padding: 0 8px;cursor: auto;"
                      >{{ `(${item.likesCount || 0})` }}</span>
                    </span>
                    <span class="comment-basic-dislike">
                      <a-tooltip :title="traditionlize('不欢乐')">
                        <a-icon
                          type="dislike"
                          :theme="item.disliked  ? 'twoTone' : 'outlined'"
                          two-tone-color="#8A2BE2"
                          :loading="commentDislikedLoading"
                          @click="($event)=>dislikeComment(item)"
                        />
                      </a-tooltip>
                      <span
                        style="margin-left:0px;padding: 0 8px;cursor: auto;"
                      >{{ `(${item.dislikesCount || 0})` }}</span>
                    </span>
                    <span class="comment-basic-reply-to" v-if="false">
                      <a-button type="link">{{traditionlize('回覆')}}</a-button>
                    </span>
                    <span class="comment-basic-report-to">
                      <a-button
                        type="link"
                        @click="($event)=>openCommentReport(item)"
                      >{{traditionlize('反馈')}}</a-button>
                    </span>
                  </div>
                </a-comment>
              </a-list-item>
            </a-list>
          </a-spin>
          <div class="pagination" style="margin-top:64px;margin-bottom:32px;text-align:left;">
            <a-pagination
              v-if="totalComments > 0"
              v-model="commentQuery.currentPage"
              :page-size.sync="commentQuery.pageSize"
              show-quick-jumper
              :default-current="commentQuery.currentPage"
              :total="totalComments"
              @change="commentPageChange"
              :item-render="itemRender"
            />
          </div>
        </div>
      </a-layout-footer>
    </a-layout>
    <Report
      :visible="reportModalVisible"
      :postReportData="postReportData"
      @handleCancel="handleCancel"
    />
    <AccountModal :visible="visible" @handleAccountModal="handleAccountModal" :status="'login'" />
  </div>
</template>


<script>
import store from "@/store";
import moment from "moment";
import isMobile from "is-mobile";
import { mapGetters } from "vuex";
import { message } from "ant-design-vue";
import {
  getNovelById,
  getNovelComment,
  addNovelComment,
  getNovelUserinfo,
  novelLikes,
  novelCommentLikes,
  novelCommentDislikes,
  addNovelSubscribe,
  deleteNovelSubscribe,
  addReport
} from "@/api/novel";
import {
  parseTime,
  toThousandNumber,
  isValNullUndefined,
  removeClass
} from "@/utils";
import waves from "@/directive/waves";
import traditionlize from "@/utils/translate";
import HotList from "./components/HotList";
import Report from "./components/Report";
import Breadcrumb from "@/components/Breadcrumb";
import AccountModal from "@/views/novel/components/AccountModal";
import { commonOptions } from "@/components/mixins/common";

const breadcrumb = [
  {
    href: "/",
    text: "主页"
  },
  {
    href: "/novel/main",
    text: "列表"
  },
  {
    href: undefined,
    text: "详细"
  }
];

export default {
  name: "NovelDetails",
  directives: { waves },
  components: { HotList, Breadcrumb, Report, AccountModal },
  mixins: [commonOptions],
  created() {},
  async mounted() {
    const _self = this;
    const { novelId } = _self.$route.params;
    if (isValNullUndefined(novelId)) {
      _self.$router.go(-1);
      return;
    }
    _self.novelId = parseInt(novelId);
    _self.getNovelById();
    _self.getNovelComment();
  },
  data() {
    return {
      visible: false,
      index: 0,
      swiperCovers: [],
      // novel: {
      //   novelCovers: [],
      //   novelArtist: {
      //     translateName: "",
      //     originName: ""
      //   }
      // },
      novel: undefined,
      novelId: undefined,
      commentsCount: 0,
      commentSpinning: false,
      totalComments: 0,
      totalCommentPages: 0,
      activeKey: "",
      commentSortBy: "1",
      comments: [],
      commentQuery: {
        currentPage: 1,
        pageSize: 12
      },
      comment: {
        mes: undefined,
        submitting: false
      },
      postReportData: {},
      // reportRules: {
      //   reportReasonNum: [
      //     {
      //       required: true,
      //       message: traditionlize("请选择以上的一个理由"),
      //       trigger: "blur"
      //     }
      //   ],
      //   reportMes: [
      //     {
      //       max: 250,
      //       message: traditionlize("反馈内容最多不可超过250字"),
      //       trigger: "change"
      //     }
      //   ]
      // },
      //subscribed: true,
      //liked: true,
      likedLoading: false,
      subscribedLoading: false,
      reportedLoading: false,
      commentLikedLoading: false,
      commentDislikedLoading: false,
      reportModalVisible: false,
      moment,
      traditionlize,
      parseTime,
      isValNullUndefined,
      toThousandNumber,
      breadcrumb
    };
  },
  computed: {
    ...mapGetters([
      "settings",
      "avatar",
      "token",
      "roles",
      "likes",
      "subscribes"
    ]),
    isMobile() {
      return isMobile();
    },
    logined() {
      const _self = this;
      return _self.token && Array.isArray(_self.roles) && _self.roles.length;
    },
    liked() {
      const _self = this;
      if (
        !_self.logined ||
        !Array.isArray(_self.likes) ||
        _self.likes.length === 0
      )
        return false;
      return (
        _self.likes.find(like => like.novel.id === _self.novelId) !== undefined
      );
    },
    subscribed() {
      const _self = this;
      if (
        !_self.logined ||
        !Array.isArray(_self.subscribes) ||
        _self.subscribes.length === 0
      )
        return false;
      return (
        _self.subscribes.find(
          subscribe => subscribe.novel.id === _self.novelId
        ) !== undefined
      );
    }
  },
  methods: {
    async getNovelById() {
      const _self = this;
      try {
        const { data } = await getNovelById(_self.novelId);
        const novel = data;
        if (!isValNullUndefined(novel)) {
          let currentNovel = novel;
          currentNovel.epCount = !isValNullUndefined(novel.novelEpisodes)
            ? novel.novelEpisodes.length
            : 0;
          currentNovel.chapterCount = !isValNullUndefined(novel.novelEpisodes)
            ? novel.novelEpisodes.reduce((a, b) => {
                return (
                  a +
                  (!isValNullUndefined(b.novelChapters)
                    ? b.novelChapters.length
                    : 0)
                );
              }, 0)
            : 0;
          const lastEp = [...novel.novelEpisodes].pop();
          currentNovel.shortTitle =
            currentNovel.chapterCount > 0
              ? `${
                  !isValNullUndefined(lastEp.novelChapters) &&
                  lastEp.novelChapters.length > 0
                    ? [...lastEp.novelChapters].pop().translateTitle
                    : lastEp.translateTitle
                }`
              : currentNovel.translateTitle;
          currentNovel.totalChar = !isValNullUndefined(novel.novelEpisodes)
            ? novel.novelEpisodes.reduce((a, b) => {
                return (
                  a +
                  (!isValNullUndefined(b.novelChapters)
                    ? b.novelChapters.reduce((nc1, nc2) => {
                        return nc1 + (nc2.wordCount || 0);
                      }, 0)
                    : 0)
                );
              }, 0)
            : 0;
          _self.novel = currentNovel;
          _self.swiperCovers = _self.novel.novelCovers;
        }
      } catch (error) {
        console.log(error);
      }
    },
    async getNovelComment() {
      const _self = this;
      try {
        let query = Object.assign({}, _self.commentQuery);
        query.page = _self.commentQuery.currentPage - 1;
        query.size = _self.commentQuery.pageSize;
        query.novelId = _self.novelId;
        _self.commentSpinning = true;
        const { data } = await getNovelComment(query);
        _self.comments = content;
        const content = Array.isArray(data.content)
          ? data.content.map(comment => {
              let entity = comment;
              entity.datetime = moment(comment.createdTime);
              return entity;
            })
          : [];
        _self.comments = content;
        _self.totalComments = data.totalElements;
        _self.totalCommentPages = data.totalPages;
        _self.commentsCount = data.totalElements;
      } catch (error) {
        console.log(error);
      }
      _self.commentSpinning = false;
    },
    searchByArtitst() {
      const _self = this;
      _self.$router.push({
        path: `/novel/main?artistId=${_self.novel.novelArtist.id}`
      });
    },
    headerEpText(episode) {
      let str1 = `${episode.translateTitle}`;
      let str2 = "";
      let str3 = "";
      if (
        !isValNullUndefined(episode.originTitle) &&
        episode.originTitle !== "" &&
        episode.originTitle !== episode.translateTitle
      ) {
        str2 = ` ( ${episode.originTitle} )`;
      }
      str3 = ` 共 ${
        !isValNullUndefined(episode.novelChapters)
          ? episode.novelChapters.length
          : 0
      } 话`;
      return str1.concat(str2).concat(str3);
    },
    toNovelChapter(episode, chapter) {
      const _self = this;
      _self.$router.push({
        path: `/novel/${_self.novel.id}/episode/${episode.episode}/chapter/${chapter.chapter}`
      });
    },
    getNovelChapter(episode, chapter) {
      const _self = this;
      let hash = `/novel/${_self.novel.id}/episode/${episode.episode}/chapter/${chapter.chapter}`;
      return hash;
    },
    async subscribeNovel(event) {
      const _self = this;
      let dom = event.toElement;
      dom.blur();
      _self.subscribedLoading = true;
      try {
        let postData = {};
        postData.novelId = _self.novelId;
        if (!_self.subscribed) {
          await addNovelSubscribe(postData);
          _self.novel.subscribesCount = ++_self.novel.subscribesCount || 1;
        } else {
          await deleteNovelSubscribe(postData);
          _self.novel.subscribesCount = --_self.novel.subscribesCount || 0;
        }
        await store.dispatch("user/getInfo");
      } catch (error) {
        console.log(error);
      }
      _self.subscribedLoading = false;
    },
    async likeNovel(event) {
      const _self = this;
      let dom = event.toElement;
      dom.blur();
      _self.likedLoading = true;
      try {
        let postData = {};
        postData.novelId = _self.novelId;
        postData.value = _self.liked ? 0 : 1;
        const { data } = await novelLikes(postData);
        if (_self.liked) {
          _self.novel.likesCount = --_self.novel.likesCount || 0;
        } else {
          _self.novel.likesCount = ++_self.novel.likesCount || 1;
        }
        await store.dispatch("user/getInfo");
      } catch (error) {
        console.log(error);
      }
      _self.likedLoading = false;
    },
    async likeComment(comment) {
      const _self = this;
      _self.commentLikedLoading = true;
      try {
        let postData = {};
        postData.id = comment.id;
        postData.value = comment.liked ? 0 : 1;
        const { data } = await novelCommentLikes(postData);
        if (comment.liked) {
          comment.likesCount--;
        } else {
          comment.likesCount++;
        }
        comment.liked = !comment.liked;
      } catch (error) {
        console.log(error);
      }
      _self.commentLikedLoading = false;
    },
    async dislikeComment(comment) {
      const _self = this;
      _self.commentDislikedLoading = true;
      try {
        let postData = {};
        postData.id = comment.id;
        postData.value = comment.disliked ? 0 : 1;
        const { data } = await novelCommentDislikes(postData);
        if (comment.disliked) {
          comment.dislikesCount--;
        } else {
          comment.dislikesCount++;
        }
        comment.disliked = !comment.disliked;
      } catch (error) {
        console.log(error);
      }
      _self.commentDislikedLoading = false;
    },
    handleMesChange(event) {
      const _self = this;
      _self.comment.mes = event.target.value;
    },
    async handleSubmitComment() {
      const _self = this;
      if (!_self.logined) {
        message.warning(traditionlize("请登录后再使用此功能"));
        _self.handleAccountModal(true);
        return;
      }
      let comment = Object.assign({}, _self.comment);
      comment.novelId = _self.novelId;
      _self.comment.submitting = true;
      try {
        const resp1 = await addNovelComment(comment);
        message.success(
          traditionlize("评论成功,请互相尊重意见,建立友善的对话!")
        );
        _self.commentSortBy = "0";
        _self.commentQuery.sortBy = Number(_self.commentSortBy);
        _self.commentQuery.currentPage = 1;
        await _self.getNovelComment();
        _self.comment.mes = undefined;
      } catch (error) {
        console.log(error);
      }
      _self.comment.submitting = false;
    },
    commentPageChange(current) {
      const _self = this;
      _self.commentQuery.currentPage = current;
      _self.getNovelComment();
    },
    commentSortTabChange() {
      const _self = this;
      _self.commentQuery.sortBy = Number(_self.commentSortBy);
      _self.commentQuery.currentPage = 1;
      _self.getNovelComment();
    },
    itemRender(current, type, originalElement) {
      if (type === "prev") {
        return <a v-trans>上一页</a>;
      } else if (type === "next") {
        return <a v-trans>下一页</a>;
      }
      return originalElement;
    },
    openNovelReport($event) {
      const _self = this;
      $event.toElement.blur();
      if (!_self.logined) {
        message.warning(traditionlize("请登录后再使用此功能"));
        _self.handleAccountModal(true);
        return;
      }
      _self.postReportData.reportData = _self.novel;
      _self.reportModalVisible = true;
    },
    openCommentReport(comment) {
      const _self = this;
      if (!_self.logined) {
        message.warning(traditionlize("请登录后再使用此功能"));
        _self.handleAccountModal(true);
        return;
      }
      _self.postReportData.reportData = comment;
      _self.reportModalVisible = true;
    },
    handleCancel() {
      const _self = this;
      _self.reportModalVisible = false;
    },
    handleAccountModal(val) {
      const _self = this;
      _self.visible = val;
    },
    GoTo(path){
      const _self = this;
      _self.$router.push({path});
    }
  }
};
</script>
<style lang="less">
@import "~@/styles/list-cus1.less";
@import "~@/styles/breadcrumb-cus1.less";
</style>
<style lang="less" scoped>
@import "~@/styles/selection.less";

// #novel {
//   font-family: "Noto Serif SC", "Noto Serif TC",
//     "Noto Serif", "Microsoft YaHei", -apple-system, BlinkMacSystemFont,
//     "Segoe UI", "PingFang SC", "Hiragino Sans GB", "Helvetica Neue", Helvetica,
//     Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
// }
.novel-info.ant-layout-header {
  //background: rgb(200, 176, 255);
  background: whitesmoke;
  height: fit-content;
  line-height: 20px;
}
.novel-cover {
  text-align: center;
}
.novel-cover-border {
  border: 2px solid rgba(150, 149, 149, 0.507);
  border-radius: 10px;
}
.title-text {
  font-size: 40px;
  letter-spacing: 1px;
}
.show-prop {
  font-size: 16px;

  &.set-height {
    height: 40px;
    line-height: 35px;
  }

  .prop-text {
    font-weight: bold;
  }
  .prop-val {
    padding-left: 5px;
  }
}

.novel-actions {
  margin-top: 16px;

  [class^="action-"] {
    display: inline;
  }
  [class^="action-"]:nth-child(n + 2) {
    margin-left: 24px;
  }
  .action-subscribe {
    .ant-btn-raise {
      color: white;
      background: #ff4d4f;
      border-color: #ff4d4f;
    }
  }
  .action-like {
    .ant-btn-raise {
      color: white;
      background: rgb(48, 154, 253);
      border-color: rgb(48, 154, 253);
    }
  }
  .action-report {
    .ant-btn-raise {
      color: white;
      background: rgb(27, 27, 27);
      border-color: rgb(27, 27, 27);
    }
  }
}

.novel-introduction {
  background: rgba(206, 206, 206, 0.479);
  padding: 24px 24px;
  font: 16px bold;
  min-height: 300px;
}
.novel-episode-list {
  background: white;
  padding: 24px 24px;
  .ant-collapse-item {
    margin: 8px 0;
  }
}
.novel-episode-chapter-list {
  background-color: white;
}
.novel-episode-chapter-list-item {
  cursor: pointer;
  margin: 4px 0;
  :hover {
    background-color: #fffcec;
  }
  .ant-list-item-meta-title {
    font-weight: bold;
  }
}
.footer-comment {
  padding: 24px 0;
  background-color: white;
  width: 75%;
}
.comments-count {
  font-size: 18px;
  line-height: 24px;
  color: #222;
  margin-bottom: 20px;
  //margin-left: 20px;
}
.comment-box {
  border: 1px solid sandybrown;
  border-radius: 10px;
  padding: 0 24px;
}

.comment-content-buttom {
  color: #99a2aa;
  line-height: 26px;
  margin-left: -32px;

  span:nth-child(n + 2) {
    margin-left: 20px;
  }
}
.comment-basic-reply-to {
  :hover {
    background-color: #e5e9ef;
  }
}
.comment-basic-report-to {
  :hover {
    background-color: #e5e9ef;
  }
}
.comment-input-textarea {
  border-radius: 4px;
  color: #555;
  height: 65px;
  transition: 0.5s;
  padding: 5px 10px;
  line-height: normal;
  background-color: #f4f5f7;
  border: 1px solid #e5e9ef;
  overflow: auto;
  &:focus,
  &:hover {
    background-color: white;
  }
}
p.comment-text {
  line-height: 20px;
  padding: 2px 0;
  font-size: 14px;
  text-shadow: none;
  overflow: hidden;
  word-wrap: break-word;
  word-break: break-word;
}
@media screen and (max-width: 1165px) {
  .recommand-side {
    display: none;
  }
  .footer-comment {
    width: 100%;
  }
}
</style>