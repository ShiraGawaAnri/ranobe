<template>
  <div id="read-container">
    <div id="read-content">
      <div id="read-header">
        <div class="chapter-title">
          <strong
            :style="{fontSize:`${Number(settings.fontSize*6/5)}px`}"
          >{{ traditionlize(chapterEntity.translateTitle) }}</strong>
        </div>
        <div class="upload-info">
          <a-row type="flex" justify="space-between" v-if="uploader">
            <a-col
              :span="6"
              class="show-upload-userinfo"
              :style="{fontSize:`${Number(settings.fontSize*3/4)}px`}"
            >{{`By ${uploader.nickname}`}}</a-col>
            <a-col :span="6" class="show-upload-datetime" v-if="uploadDateTime">
              <a-icon type="clock-circle" theme="twoTone" />
              <span
                v-trans
                class="datetime-fromnow"
                :style="{fontSize:`${Number(settings.fontSize*3/4)}px`}"
              >{{`(${uploadDateTime.fromNow()})`}}</span>
              <span
                class="datetime-format"
                :style="{fontSize:`${Number(settings.fontSize*3/4)}px`}"
              >{{` ${uploadDateTime.format("YYYY-MM-DD HH:mm:ss")}`}}</span>
            </a-col>
          </a-row>
        </div>
      </div>

      <a-layout :style="{'padding':'0 0','background-color':backgroundColor,margin: 'auto auto'}">
        <a-layout-content
          id="read-content-details"
          :style="{'padding':'8px 0','background-color':backgroundColor,margin: 'auto auto'}"
        >
          <a-divider>
            <a-row class="head-divider-row" type="flex" justify="space-around">
              <a-col :span="6" v-if="preChapter && preChapter.id && preChapter.chapter">
                <a-tooltip placement="top" arrow-point-at-center>
                  <div slot="title">{{traditionlize(preChapter.translateTitle)}}</div>
                  <a-button icon="left" @click="($event)=>goChapter(preChapter,$event)"></a-button>
                </a-tooltip>
              </a-col>
              <a-col :span="6" v-if="preEpisode && preEpisode.id && preEpisode.episode">
                <a-tooltip placement="top" arrow-point-at-center>
                  <div slot="title">{{traditionlize(preEpisode.translateTitle)}}</div>
                  <a-button icon="left" @click="($event)=>goEpisode(preEpisode,$event)"></a-button>
                </a-tooltip>
              </a-col>
              <a-col :span="6">
                <a-tooltip placement="top" arrow-point-at-center>
                  <div slot="title">返回目录</div>
                  <a-button icon="rollback" @click="returnToNovelEpisode"></a-button>
                </a-tooltip>
              </a-col>
              <a-col :span="6" v-if="nextChapter && nextChapter.id && nextChapter.chapter">
                <a-tooltip placement="top" arrow-point-at-center>
                  <div slot="title">{{traditionlize(nextChapter.translateTitle)}}</div>
                  <a-button icon="right" @click="($event)=>goChapter(nextChapter,$event)"></a-button>
                </a-tooltip>
              </a-col>
              <a-col :span="6" v-else-if="nextEpisode && nextEpisode.id && nextEpisode.episode">
                <a-tooltip placement="top" arrow-point-at-center>
                  <div slot="title">{{traditionlize(nextEpisode.translateTitle)}}</div>
                  <a-button icon="right" @click="($event)=>goEpisode(nextEpisode,$event)"></a-button>
                </a-tooltip>
              </a-col>
            </a-row>
          </a-divider>
          <a-spin
            :spinning="processingContentSpinning"
            :tip="traditionlize('正在处理内容转码')"
            size="large"
          >
            <template v-if="chapterEntity.headerInfo">
              <div
                class="header-info"
                :style="{fontSize:`${settings.fontSize}px`,'color':`${fontColor}`}"
              >
                <p
                  v-for="(info,index) in chapterEntity.headerInfo"
                  :key="index"
                >{{ traditionlize(info)}}</p>
                <a-divider :dashed="true" style="border-color:black;"></a-divider>
              </div>
            </template>
            <template v-if="paragraphs && paragraphs.length > 0">
              <div class="paragraph" v-for="(item,index) in paragraphs" :key="index">
                <div
                  class="text"
                  :style="{fontSize:`${settings.fontSize}px`,'color':`${fontColor}`}"
                >
                  <div class="paragraph-content">
                    <!-- <span
                      v-trans
                      style="float:left;"
                      v-if="!settings || !settings.showOriginContent"
                    >{{`${item.translateContent}`}}</span>-->

                    <div
                      class="paragraph-comments-box"
                      style="margin-left:10px;"
                      :class="{'has-comment':hasComment(item)}"
                      @click="($event)=>openParagraphComment(item,$event)"
                    >
                      <span
                        v-if="!settings || !settings.showOriginContent"
                        v-html="item.translateContent"
                      ></span>
                      <span style="float:left;" v-else v-html="item.originContent"></span>
                      <a-badge
                        class="badge"
                        v-show="hasComment(item)"
                        :title="`吐槽${paragraphCommentsNumber(item)}个`"
                        :overflowCount="99"
                        :count="paragraphCommentsNumber(item)"
                        :number-style="{
                          backgroundColor: 'transparent',
                          color: 'rgba(0, 0, 0, 0.65)',
                          borderColor:'rgba(0, 0, 0, 0.65)',
                          boxShadow: '0 0 0 1px rgba(0, 0, 0, 0.65) inset',
                          borderRadius: '4px',
                        }"
                        style="margin-left: 12px;"
                      ></a-badge>
                    </div>
                  </div>
                </div>
              </div>
            </template>
            <template v-else>
              <br v-for="i in 20" :key="i" />
            </template>
            <template v-if="chapterEntity.footerInfo">
              <div
                class="footer-info"
                :style="{fontSize:`${settings.fontSize}px`,'color':`${fontColor}`}"
              >
                <a-divider :dashed="true" style="border-color:black;"></a-divider>
                <p v-for="(info,index) in chapterEntity.footerInfo" :key="index">{{info}}</p>
              </div>
            </template>
          </a-spin>
          <a-divider>
            <a-row class="head-divider-row" type="flex" justify="space-around">
              <a-col :span="6" v-if="preChapter && preChapter.id && preChapter.chapter">
                <a-tooltip placement="top" arrow-point-at-center>
                  <div slot="title">{{traditionlize(preChapter.translateTitle)}}</div>
                  <a-button icon="left" @click="($event)=>goChapter(preChapter,$event)"></a-button>
                </a-tooltip>
              </a-col>
              <a-col :span="6" v-if="preEpisode && preEpisode.id && preEpisode.episode">
                <a-tooltip placement="top" arrow-point-at-center>
                  <div slot="title">{{traditionlize(preEpisode.translateTitle)}}</div>
                  <a-button icon="left" @click="($event)=>goEpisode(preEpisode,$event)"></a-button>
                </a-tooltip>
              </a-col>
              <a-col :span="6">
                <a-tooltip placement="top" arrow-point-at-center>
                  <div slot="title">返回目录</div>
                  <a-button icon="rollback" @click="returnToNovelEpisode"></a-button>
                </a-tooltip>
              </a-col>
              <a-col :span="6" v-if="nextChapter && nextChapter.id && nextChapter.chapter">
                <a-tooltip placement="top" arrow-point-at-center>
                  <div slot="title">{{traditionlize(nextChapter.translateTitle)}}</div>
                  <a-button icon="right" @click="($event)=>goChapter(nextChapter,$event)"></a-button>
                </a-tooltip>
              </a-col>
              <a-col :span="6" v-else-if="nextEpisode && nextEpisode.id && nextEpisode.episode">
                <a-tooltip placement="top" arrow-point-at-center>
                  <div slot="title">{{traditionlize(nextEpisode.translateTitle)}}</div>
                  <a-button icon="right" @click="($event)=>goEpisode(nextEpisode,$event)"></a-button>
                </a-tooltip>
              </a-col>
            </a-row>
          </a-divider>
        </a-layout-content>
        <a-layout-sider
          class="recommand-side"
          width="300"
          style="background-color:#fff;margin-left:4px;"
        >
          <HotList />
        </a-layout-sider>
      </a-layout>

      <a-row type="flex" justify="space-between">
        <a-col></a-col>
        <a-col>
          <div class="side"></div>
        </a-col>
      </a-row>
      <div id="read-footer"></div>
    </div>
    <a-drawer
      placement="right"
      :closable="true"
      :visible="drawerVisible"
      @close="onDrawerClose"
      width="350"
    >
      <div slot="title">
        <div>
          <strong v-trans>吐槽一下</strong>
        </div>
        <a-comment>
          <a-avatar v-if="avatar" :size="64" slot="avatar" :src="avatar" alt="YourAvatar" />
          <div slot="content">
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
                <a-button type="link" size="small" @click="($event)=>handleAccountModal(true)">登录</a-button>
                <span>或</span>
                <a-button type="link" size="small" @click="($event)=>handleAccountModal(true)">注册</a-button>
                <span>才可以发表评论</span>
              </div>
            </div>
            <br />
            <br />
            <a-button
              :disabled="!logined"
              html-type="submit"
              :loading="comment.submitting"
              type="primary"
              @click="handleSubmitComment"
            >{{traditionlize('发表评论')}}</a-button>
            <a-row type="flex" justify="space-around">
              <a-col :span="16"></a-col>
              <a-col :span="4"></a-col>
            </a-row>
            <!-- <a-form layout="inline">
                <a-form-item></a-form-item>
                <a-form-item></a-form-item>
            </a-form>-->
          </div>
        </a-comment>
        <hr />
      </div>
      <div class="comment-box">
        <!-- :header="`${commentsCount} replies`" -->

        <a-list class="comment-list" item-layout="horizontal" :data-source="drawerParagraphComment">
          <a-list-item slot="renderItem" slot-scope="item">
            <a-comment
              :style="{'fontWeight':item.isTarget ? 'bolder' : 'normal'}"
              :author="item.userLogin ? item.userLogin.nickname : ''"
              :avatar="item.userLogin ? item.userLogin.avatar : ''"
            >
              <template slot="actions">
                <span v-for="(action,index) in item.actions" :key="index">{{ action }}</span>
              </template>
              <p
                class="comment-text"
                slot="content"
                :style="{'fontWeight':item.isTarget ? 'bolder' : 'normal'}"
              >{{ traditionlize(item.mes) }}</p>
              <div class="comment-content-buttom">
                <a-tooltip v-trans :title="item.datetime.format('YYYY-MM-DD HH:mm:ss')">
                  <span v-trans>{{ item.datetime.fromNow() }}</span>
                </a-tooltip>
                <span class="comment-basic-report-to">
                  <a-button
                    v-trans
                    type="link"
                    @click="($event)=>openTsukomiReport(item)"
                  >{{traditionlize('反馈')}}</a-button>
                </span>
              </div>
            </a-comment>
          </a-list-item>
        </a-list>
      </div>
    </a-drawer>
    <Report
      :visible="reportModalVisible"
      :postReportData="postReportData"
      @handleCancel="handleCancel"
    />
    <AccountModal :visible="visible" @handleAccountModal="handleAccountModal" :status="'login'" />
  </div>
</template>


<script>
import moment from "moment";
import xss from "xss";
import isMobile from "is-mobile";
import { mapGetters, mapMutations } from "vuex";
import { message } from "ant-design-vue";
import store from "@/store";
import {
  getNovelChapterList,
  getNovelChapter,
  getNovelChapterParagraphComment,
  addNovelChapterParagraphComment,
  getNovelChapterParagraphCommentByParagraphId
} from "@/api/novel";
import {
  parseTime,
  toThousandNumber,
  isValNullUndefined,
  removeClass
} from "@/utils";
import traditionlize from "@/utils/translate";
import HotList from "./components/HotList";
import Report from "./components/Report";
import AccountModal from "@/views/novel/components/AccountModal";
import { commonOptions } from "@/components/mixins/common";

export default {
  name: "NovelRead",
  components: { HotList, Report, AccountModal },
  mixins: [commonOptions],
  created() {},
  async mounted() {
    const _self = this;
    _self.init();
  },
  data() {
    return {
      processingContentSpinning: false,
      novelId: undefined,
      episode: undefined,
      chapter: undefined,
      paragraphId: undefined,
      chapterEntity: {},
      paragraphs: [],
      preChapter: {},
      nextChapter: {},
      preEpisode: {},
      nextEpisode: {},
      //paragraphComments: [],
      drawerParagraphComment: [],
      comment: {
        mes: undefined,
        submitting: false
      },
      drawerVisible: false,
      uploader: undefined,
      uploadDateTime: undefined,
      reportModalVisible: false,
      visible: false,
      postReportData: {},
      moment,
      parseTime,
      toThousandNumber,
      isValNullUndefined,
      traditionlize
    };
  },
  computed: {
    ...mapGetters(["settings", "avatar", "token", "roles", "localHistories"]),
    isMobile() {
      return isMobile();
    },
    logined() {
      const _self = this;
      return _self.token && Array.isArray(_self.roles) && _self.roles.length;
    },
    backgroundColor() {
      const _self = this;
      const defaultColor = "white";
      if (_self.settings) {
        let t = _self.colorOptions.find(
          option => option.value === _self.settings.backgroundColorNumber
        );
        return t ? t.color : defaultColor;
      }
      return defaultColor;
    },
    fontColor() {
      const _self = this;
      const defaultColor = "rgba(0, 0, 0, 0.65)";
      if (_self.settings) {
        let t = _self.fontColorOptions.find(
          option => option.value === _self.settings.fontColorNumber
        );
        return t ? t.color : defaultColor;
      }
      return defaultColor;
    },
    lang() {
      const _self = this;
      return _self.settings.language;
    }
  },
  methods: {
    async init() {
      const _self = this;
      const { novelId, episode, chapter } = _self.$route.params;
      let clearDom = document.getElementById("paragraph-clone");
      if (clearDom) clearDom.remove();
      if (
        isValNullUndefined(novelId) ||
        isValNullUndefined(episode) ||
        isValNullUndefined(chapter)
      ) {
        _self.$router.go(-1);
        return;
      }
      _self.novelId = Number(novelId);
      _self.episode = Number(episode);
      _self.chapter = Number(chapter);
      await _self.getNovelChapter();
      if (!_self.logined) {
        _self.saveHistory();
      }
    },
    saveHistory() {
      const _self = this;
      const { path, params, query } = _self.$route;
      const chapterId = _self.chapterEntity.id;
      const novelId = _self.novelId;
      let route = { path, params, query };
      let details = { chapterId, novelId };
      store.dispatch("localHistory/saveLocalHistory", { route, details });
    },
    async getNovelChapter() {
      const _self = this;
      try {
        _self.processingContentSpinning = true;
        const resp = await getNovelChapterList(_self.novelId, _self.episode);
        const novelChapters = resp.data;
        const { data } = await getNovelChapter(
          _self.novelId,
          _self.episode,
          _self.chapter
        );
        const {
          novelChapterParagraphs = [],
          novelChapterComments = [],
          createdTime,
          userLoginDTO = {}
        } = data;
        _self.chapterEntity.id = data.id;
        _self.chapterEntity.chapter = data.chapter;
        _self.chapterEntity.translateTitle = data.translateTitle;
        _self.chapterEntity.originTitle = data.originTitle;
        _self.chapterEntity.wordCount = data.wordCount;
        _self.chapterEntity.createdTime = createdTime;
        _self.chapterEntity.headerInfo =
          data.headerInfo && data.headerInfo !== ""
            ? data.headerInfo
            : undefined;
        _self.chapterEntity.footerInfo =
          data.footerInfo && data.footerInfo !== ""
            ? data.footerInfo
            : undefined;
        if (_self.chapterEntity.headerInfo) {
          let text = decodeURI(_self.chapterEntity.headerInfo);
          _self.chapterEntity.headerInfo = text.split("\n");
        }
        if (_self.chapterEntity.footerInfo) {
          let text = decodeURI(_self.chapterEntity.footerInfo);
          _self.chapterEntity.footerInfo = text.split("\n");
        }
        if (userLoginDTO) {
          _self.uploader = userLoginDTO;
        }
        const xssOptions = {
          whiteList: {
            // p: []
          }, // 白名单为空，表示过滤所有标签
          stripIgnoreTag: true, // 过滤所有非白名单标签的HTML
          stripIgnoreTagBody: ["script"] // script标签较特殊，需要过滤标签中间的内容
        };
        novelChapterParagraphs.map(paragraph => {
          const current = paragraph;
          let translateContent = _self.processContent(current.translateContent);
          let originContent = _self.processContent(current.originContent);

          translateContent = traditionlize(translateContent);

          paragraph.translateContent = "　　" + translateContent;
          paragraph.originContent = "　　" + originContent;
          return paragraph;
        });
        // if (novelEpisodeListDTO) {
        //   _self.uploader = novelEpisodeListDTO.userLogin;
        // }
        if (createdTime) {
          _self.uploadDateTime = moment(createdTime);
        }
        let preChapter = undefined;
        let nextChapter = undefined;
        let list = novelChapters || [];
        for (let i = 0; i < list.length; i++) {
          let p = list[i];
          if (Number(p.chapter) === Number(_self.chapter)) {
            if (i > 0) {
              preChapter = list[i - 1];
            }
            if (i < list.length - 1) {
              nextChapter = list[i + 1];
            }
          }
        }
        _self.paragraphs = novelChapterParagraphs;
        //_self.paragraphComments = novelChapterComments;
        _self.preChapter = preChapter;
        _self.nextChapter = nextChapter;
      } catch (error) {
        console.log(error);
      }
      _self.processingContentSpinning = false;
    },
    goChapter(targetChapter, event) {
      const _self = this;
      _self.$router.push({
        path: `/novel/${_self.novelId}/episode/${_self.episode}/chapter/${targetChapter.chapter}`
      });
    },
    goEpisode(targetEpisode, event) {
      const _self = this;
      // _self.$router.push({
      //   path: `/novel/${_self.novelId}/episode/${_self.episode}/chapter/${targetChapter.chapter}`
      // });
    },
    returnToNovelEpisode() {
      const _self = this;
      _self.$router.push({ path: `/novel/${_self.novelId}/details` });
    },
    hasComment(paragraph) {
      const _self = this;
      return (
        paragraph.novelChapterParagraphComments &&
        paragraph.novelChapterParagraphComments.length
      );
    },
    paragraphCommentsNumber(paragraph) {
      return paragraph.novelChapterParagraphComments
        ? paragraph.novelChapterParagraphComments.length
        : 0;
    },
    openParagraphComment(paragraph, event) {
      const _self = this;
      if (_self.drawerVisible) return false;
      let clearDom = document.getElementById("paragraph-clone");
      if (clearDom) clearDom.remove();
      let displayDom = document.getElementById("paragraph-opening");
      if (displayDom) displayDom.id = "";
      if (!_self.hasComment(paragraph)) {
        return;
      }
      if (!_self.isMobile) {
        let dom = event.toElement;
        let targetDom = dom.parentNode;
        while (targetDom) {
          if (targetDom.classList) {
            if (targetDom.classList.contains("has-comment")) {
              targetDom = targetDom.children[0];
              break;
            }
          }
          targetDom = targetDom.parentNode;
        }
        if (!targetDom) return;
        let clone = targetDom.cloneNode(true);
        clone.id = "paragraph-clone";
        targetDom.id = "paragraph-opening";
        if (targetDom.previousSibling) {
          targetDom.parentNode.insertBefore(clone, targetDom);
        } else {
          targetDom.parentNode.prepend(clone);
        }
      }
      const comments = paragraph.novelChapterParagraphComments.map(comment => {
        let entity = comment;
        entity.datetime = moment(comment.createdTime);
        return entity;
      });
      _self.drawerParagraphComment = comments;
      _self.drawerVisible = true;
      _self.paragraphId = paragraph.id;
    },
    onDrawerClose() {
      let clearDom = document.getElementById("paragraph-clone");
      if (clearDom) clearDom.remove();
      let displayDom = document.getElementById("paragraph-opening");
      if (displayDom) displayDom.id = "";
      const _self = this;
      _self.drawerVisible = false;
      _self.drawerParagraphComment = [];
      _self.paragraphId = undefined;
      _self.comment.mes = undefined;
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
      comment.paragraphId = _self.paragraphId;
      _self.comment.submitting = true;
      let selfParagraph = _self.paragraphs.find(
        paragraph => paragraph.id === _self.paragraphId
      );
      try {
        const resp1 = await addNovelChapterParagraphComment(comment);
        message.success(
          traditionlize("吐槽成功,请互相尊重意见,建立友善的对话!")
        );
        const targetId = resp1.data;
        const { data } = await getNovelChapterParagraphCommentByParagraphId(
          comment.paragraphId
        );
        if (data && Array.isArray(data)) {
          let list = data.map(newComment => {
            if (targetId && newComment.id === targetId) {
              newComment.isTarget = true;
            }
            newComment.datetime = moment(newComment.createdTime);
            return newComment;
          });
          list = list.sort((a, b) => (a.isTarget ? -1 : 0));
          _self.paragraphs.forEach(p => {
            if (p.id === _self.paragraphId) {
              p.novelChapterParagraphComments = list;
            }
          });
          //触发一次渲染
          _self.drawerParagraphComment = [];
          setTimeout(() => {
            _self.drawerParagraphComment = list;
          });
          _self.comment.mes = undefined;
        }
        //console.log(data);
      } catch (error) {
        console.log(error);
      }
      _self.comment.submitting = false;
    },
    processContent(content) {
      const xssOptions = {
        whiteList: {
          // p: []
        }, // 白名单为空，表示过滤所有标签
        stripIgnoreTag: true, // 过滤所有非白名单标签的HTML
        stripIgnoreTagBody: ["script"] // script标签较特殊，需要过滤标签中间的内容
      };
      content = xss(content, xssOptions);
      return content;
    },
    HTMLDecode(text) {
      var temp = document.createElement("div");
      temp.innerHTML = text;
      var output = temp.innerText || temp.textContent;
      temp = null;
      return output;
    },
    openTsukomiReport(comment) {
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
    GoTo(path) {
      const _self = this;
      _self.$router.push({ path });
    }
  },
  watch: {
    // 如果路由发生变化，再次执行该方法
    $route() {
      this.init();
    },
    lang(val) {
      this.init();
    }
  }
};
</script>

<style lang="less" scoped>
@import "~@/styles/selection.less";
@import "~@/styles/fonts.less";

#read-content-details {
  font-family: "Noto Serif SC", "Noto Serif TC", "Noto Serif", "Microsoft YaHei",
    -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC",
    "Hiragino Sans GB", "Helvetica Neue", Helvetica, Arial, sans-serif,
    "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
}
.paragraph {
  .text {
    p,
    br,
    span {
      white-space: pre-wrap;
    }
  }
}
#paragraph-clone {
  z-index: 9999;
  position: relative;
  background: white;
  float: left;
  @media screen and (min-width: 1465px) {
    .paragraph-comments-box {
      display: none;
    }
  }
}
#paragraph-opening {
  display: none;
}
.paragraph-comments-box {
  &.has-comment {
    cursor: pointer;
  }
}
.head-divider-row {
  width: 250px;
  .ant-btn {
    height: 30px;
    min-width: 80px;
  }
}
@fc: #949282;
.show-upload-userinfo {
  font-weight: bold;
  color: @fc;
}
.datetime-fromnow,
.datetime-format {
  margin-left: 6px;
  font-weight: bold;
  color: @fc;
  display: none;
}
.paragraph {
  margin: 12px 6px;
}
.badge::before {
  position: absolute;
  content: "";
  /* width: 0; */
  /* height: 0; */
  /* right: 100%; */
  /* top: 38px; */
  left: -7px;
  top: 8px;
  border-top: 5px solid transparent;
  border-right: 8px solid rgba(0, 0, 0, 0.65);
  border-bottom: 5px solid transparent;
}
.badge::after {
  content: "";
  clear: both;
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
@media screen and (max-width: 680px) {
  .head-divider-row {
    .ant-btn {
      height: 30px;
      min-width: 60px;
    }
  }
}

@media screen and (max-width: 540px) {
  .show-upload-datetime {
    display: none;
  }
}

@media screen and (min-width: 540px) {
  .head-divider-row {
    width: 300px;
  }
  .datetime-format {
    display: inline;
  }
}
@media screen and (max-width: 1465px) {
  .recommand-side {
    display: none;
  }
  #paragraph-clone {
    display: none;
  }
}
@media screen and (min-width: 1165px) {
  .head-divider-row {
    width: 400px;
  }
  .datetime-fromnow {
    display: inline;
  }
}
@media screen and (min-width: 1600px) {
  .head-divider-row {
    width: 800px;
  }
}
</style>