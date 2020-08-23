<template>
  <div>
    <a-button type="raise" @click="addNovel">{{traditionlize('添加小说')}}</a-button>
    <a-table
      :columns="columns"
      :data-source="data"
      class="novel-table"
      :pagination="pagination"
      :loading="tableLoading"
      rowKey="id"
    >
      <template slot="translateTitle" slot-scope="novel">
        <a
          :href="`/novel/${novel.id}/details`"
          target="_blank"
          v-if="checkPermission(['ROLE_ADMIN'])"
        >{{ `${traditionlize(novel.translateTitle)} (ID:${novel.id})` }}</a>
        <a
          :href="`/novel/${novel.id}/details`"
          target="_blank"
          v-else
        >{{ `${traditionlize(novel.translateTitle)}` }}</a>
      </template>
      <template slot="validate" slot-scope="novel">
        <div v-if="novel.validate === 1">
          <a-badge status="success"></a-badge>
          <span>{{traditionlize('已审核')}}</span>
        </div>
        <div v-else-if="novel.validate === 0">
          <a-badge status="warning"></a-badge>
          <span>{{traditionlize('待审核')}}</span>
        </div>
        <div v-else>
          <a-badge status="error"></a-badge>
          <span>{{traditionlize('未定义')}}</span>
        </div>
      </template>
      <template slot="isDelete" slot-scope="novel">
        <div v-if="novel.isDelete === 0">
          <a-badge status="success"></a-badge>
          <span>{{traditionlize('正常')}}</span>
        </div>
        <div v-else-if="novel.isDelete === 1">
          <a-badge status="warning"></a-badge>
          <span>{{traditionlize('下架中')}}</span>
        </div>
        <div v-else>
          <a-badge status="error"></a-badge>
          <span>{{traditionlize('未定义')}}</span>
        </div>
      </template>
      <template slot="locked" slot-scope="novel">
        <div v-if="novel.locked === 0">
          <a-badge status="success"></a-badge>
          <span>{{traditionlize('正常')}}</span>
        </div>
        <div v-else-if="novel.locked === 1">
          <a-badge status="warning"></a-badge>
          <span>{{traditionlize('锁定')}}</span>
        </div>
        <div v-else>
          <a-badge status="error"></a-badge>
          <span>{{traditionlize('未定义')}}</span>
        </div>
      </template>
      <a slot="operation" slot-scope="novel">
        <a-button
          type="link"
          size="small"
          @click="($event)=>editNovel(novel)"
        >{{ traditionlize('编辑') }}</a-button>
        <a-divider type="vertical" />
        <a-button
          type="link"
          size="small"
          @click="($event)=>addEpisode(novel)"
        >{{ traditionlize('新增卷') }}</a-button>
        <a-divider type="vertical" />
        <a-dropdown v-permission="['ROLE_ADMIN']">
          <a-menu slot="overlay">
            <a-menu-item
              @click="($event)=>changeNovelStatus(novel,{validate:novel.validate === 1 ? 0 : 1})"
            >{{ ` ${ novel.validate === 1 ? traditionlize('取消审核') : traditionlize('审核')}` }}</a-menu-item>
            <a-menu-item
              @click="($event)=>changeNovelStatus(novel,{isDelete:novel.isDelete === 1 ? 0 : 1})"
            >{{ ` ${ novel.isDelete === 1 ? traditionlize('上架') : traditionlize('下架')}` }}</a-menu-item>
            <a-menu-item
              @click="($event)=>changeNovelStatus(novel,{locked:novel.locked === 1 ? 0 : 1})"
            >{{ ` ${ novel.locked === 1 ? traditionlize('解锁') : traditionlize('锁定')}` }}</a-menu-item>
            <a-menu-item @click="($event)=>deleteNovel(novel)">{{ traditionlize('删除')}}</a-menu-item>
          </a-menu>
          <a>
            {{traditionlize("管理")}}
            <a-icon type="down" />
          </a>
        </a-dropdown>
      </a>
      <a-table
        slot="expandedRowRender"
        slot-scope="eachNovel"
        :columns="episodeColumns"
        :data-source="eachNovel.novelEpisodes"
        style="background-color:aliceblue;"
      >
        <template slot="validate" slot-scope="episode">
          <div v-if="episode.validate === 1">
            <a-badge status="success"></a-badge>
            <span>{{traditionlize('已审核')}}</span>
          </div>
          <div v-else-if="episode.validate === 0">
            <a-badge status="warning"></a-badge>
            <span>{{traditionlize('待审核')}}</span>
          </div>
          <div v-else>
            <a-badge status="error"></a-badge>
            <span>{{traditionlize('未定义')}}</span>
          </div>
        </template>
        <span slot="operation" slot-scope="episode" class="table-operation">
          <a-button
            type="link"
            size="small"
            @click="($event)=>editEpisode(eachNovel,episode)"
          >{{ traditionlize('编辑') }}</a-button>
          <a-divider type="vertical" />
          <a-button
            type="link"
            size="small"
            @click="($event)=>addChapter(eachNovel,episode)"
          >{{ traditionlize('新增章节') }}</a-button>
          <a-divider type="vertical" />
          <a-dropdown v-permission="['ROLE_ADMIN']">
            <a-menu slot="overlay">
              <a-menu-item
                @click="($event)=>changeEpisodeStatus(eachNovel,episode,{validate:episode.validate === 1 ? 0 : 1})"
              >{{ `${ episode.validate === 1 ? traditionlize('取消审核') : traditionlize('审核')}` }}</a-menu-item>
              <a-menu-item
                @click="($event)=>deleteEpisode(eachNovel,episode)"
              >{{ traditionlize('删除')}}</a-menu-item>
            </a-menu>
            <a>
              {{traditionlize("管理")}}
              <a-icon type="down" />
            </a>
          </a-dropdown>
        </span>
        <a-table
          slot="expandedRowRender"
          slot-scope="eachEp"
          :columns="chapterColumns"
          :data-source="eachEp.novelChapters"
        >
          <template slot="translateTitle" slot-scope="chapter">
            <a
              :href="`/novel/${eachNovel.id}/episode/${eachEp.episode}/chapter/${chapter.chapter}`"
              target="_blank"
              v-if="checkPermission(['ROLE_ADMIN'])"
            >{{ `${traditionlize(chapter.translateTitle)} (ID:${chapter.id})` }}</a>
            <a
              :href="`/novel/${eachNovel.id}/episode/${eachEp.episode}/chapter/${chapter.chapter}`"
              target="_blank"
              v-else
            >{{ `${traditionlize(chapter.translateTitle)}` }}</a>
          </template>
          <span slot="operation" slot-scope="chapter" class="table-operation">
            <a-button
              type="link"
              size="small"
              @click="($event)=>editChapter(eachNovel,eachEp,chapter)"
            >{{ traditionlize('编辑') }}</a-button>
            <a-divider type="vertical" />
            <a-dropdown v-if="permissionCheck">
              <a-menu slot="overlay">
                <a-menu-item
                  @click="($event)=>deleteChapter(eachNovel,episode,chapter)"
                >{{ traditionlize('删除')}}</a-menu-item>
              </a-menu>
              <a>
                {{traditionlize("管理")}}
                <a-icon type="down" />
              </a>
            </a-dropdown>
          </span>
        </a-table>
      </a-table>
    </a-table>
    <a-modal v-model="novelModalVisible" on-ok="handleOk">
      <template slot="title">
        <span v-if="novelModalStatus === 'create'" v-trans>{{traditionlize('添加小说')}}</span>
        <span v-else>{{ traditionlize('编辑小说')}}</span>
      </template>
      <template slot="footer">
        <a-button key="back" @click="handleCancel">{{traditionlize('返回')}}</a-button>
        <a-button
          key="submit"
          type="primary"
          :loading="novelLoading"
          @click="handleNovel"
        >{{traditionlize('提交')}}</a-button>
      </template>
      <a-form-model
        ref="novelForm"
        :rules="novelRules"
        :model="novel"
        :label-col="labelCol"
        :wrapper-col="wrapperCol"
      >
        <a-form-model-item :label="traditionlize('小说译名')" prop="translateTitle">
          <a-input
            v-model.trim="novel.translateTitle"
            :placeholder="traditionlize('请输入')"
            style="min-width:300px;max-width:600px; width:30%;"
          />
        </a-form-model-item>
        <a-form-model-item :label="traditionlize('小说原名')" prop="originTitle">
          <a-input
            v-model.trim="novel.originTitle"
            style="min-width:300px;max-width:600px; width:30%;"
            :placeholder="traditionlize('请输入')"
          />
        </a-form-model-item>
        <a-form-model-item :label="traditionlize('小说简介')" prop="introduction">
          <a-input
            v-model.trim="novel.introduction"
            style="min-width:300px;max-width:600px; width:30%;"
            type="textarea"
            :placeholder="traditionlize('请输入简介')"
          />
        </a-form-model-item>
        <a-form-model-item :label="traditionlize('小说类型')" prop="categoryId">
          <a-select
            v-model="novel.categoryId"
            v-for="(category,index) in categories"
            :key="index"
            :placeholder="traditionlize('请选择最适合的类型')"
            style="min-width:300px;max-width:600px; width:30%;"
          >
            <a-select-option :value="category.id">{{ traditionlize(category.name)}}</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item :label="traditionlize('小说作者')" prop="artistId">
          <a-select
            v-model="novel.artistId"
            v-for="(artist,index) in artists"
            :key="index"
            :placeholder="traditionlize('请选择作者,如果不在列表中,可以主动添加或联系管理员')"
            style="min-width:300px;max-width:600px; width:30%;"
          >
            <a-select-option
              :value="artist.id"
            >{{ `${traditionlize(artist.translateName)} (${artist.originName})`}}</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item :label="traditionlize('小说标签')" prop="tagIds">
          <a-select v-model="novel.tagIds" mode="multiple" :placeholder="traditionlize('请选择恰当的标签')">
            <a-select-option
              v-for="(tag,index) in tags"
              :key="index"
              :value="tag.id"
              style="min-width:300px;max-width:600px; width:30%;"
            >{{ traditionlize(tag.nameCn) }}</a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item :label="traditionlize('小说封面')" prop="translateTitle">
          <div>
            <a-button
              @click="addNovelCover"
            >{{ `${traditionlize('添加封面')}(${ novel.covers.length })` }}</a-button>
            <div v-for="(cover,index) in novel.covers" :key="index">
              <a-input
                v-model.trim="cover.url"
                :placeholder="traditionlize('未上传文件')"
                style="min-width:300px;max-width:600px; width:30%;"
                :disabled="true"
              />
              <a-button
                :loading="novelLoading"
                @click="($event)=>uploadCover(index)"
                type="raise"
              >{{ traditionlize( (!cover.url ?'上传':'覆盖上传') ) }}</a-button>
              <a-divider type="vertical" />
              <a-button
                :loading="novelLoading"
                @click="($event)=>deleteCover(index)"
                type="raise"
              >{{ traditionlize('删除') }}</a-button>
              <a-divider type="vertical" />
              <a-popover v-show="cover.url" placement="right">
                <template slot="content">
                  <div style="max-width:200px;">
                    <img style="width:100%;" alt="picture" :src="cover.url" />
                  </div>
                </template>
                <a-button type="primary">{{ traditionlize('预览') }}</a-button>
              </a-popover>
              <hr />
            </div>
          </div>
        </a-form-model-item>
      </a-form-model>
    </a-modal>
    <a-modal v-model="episodeModalVisible" on-ok="handleOk">
      <template slot="title">
        <span v-if="episodeModalStatus === 'create'" v-trans>{{traditionlize('添加卷')}}</span>
        <span v-else>{{ traditionlize('编辑卷')}}</span>
      </template>
      <template slot="footer">
        <a-button key="back" @click="handleCancel">{{traditionlize('返回')}}</a-button>
        <a-button
          key="submit"
          type="primary"
          :loading="episodeLoading"
          @click="handleEpisode"
        >{{traditionlize('提交')}}</a-button>
      </template>
      <a-form-model
        ref="episodeForm"
        :rules="episodeRules"
        :model="episode"
        :label-col="labelCol"
        :wrapper-col="wrapperCol"
      >
        <a-form-model-item :label="traditionlize('卷译名')" prop="translateTitle">
          <a-input
            v-model.trim="episode.translateTitle"
            :placeholder="traditionlize('请输入')"
            style="min-width:300px;max-width:600px; width:30%;"
          />
        </a-form-model-item>
        <a-form-model-item :label="traditionlize('卷原名')" prop="originTitle">
          <a-input
            v-model.trim="episode.originTitle"
            style="min-width:300px;max-width:600px; width:30%;"
            :placeholder="traditionlize('请输入')"
          />
        </a-form-model-item>
        <a-form-model-item :label="traditionlize('卷数')" prop="episode">
          <a-input-number
            id="inputNumber"
            v-model="episode.episode"
            :min="0"
            :placeholder="traditionlize('请输入恰当的卷数')"
            style="min-width:150px;max-width:600px; width:30%;"
          />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
    <a-modal
      v-model="chapterModalVisible"
      on-ok="handleOk"
      :width="1080"
      :dialogStyle="{top:'10px'}"
      :destroyOnClose="true"
    >
      <template slot="title">
        <span v-if="chapterModalStatus === 'create'" v-trans>{{traditionlize('添加章节')}}</span>
        <span v-else>{{ traditionlize('编辑章节')}}</span>
      </template>
      <template slot="footer">
        <a-button key="back" @click="handleCancel">{{traditionlize('返回')}}</a-button>
        <a-button
          key="submit"
          type="primary"
          :loading="chapterLoading"
          @click="handleChapter"
        >{{traditionlize('提交')}}</a-button>
      </template>
      <a-form-model
        ref="chapterForm"
        :rules="chapterRules"
        :model="chapter"
        :label-col="labelCol"
        :wrapper-col="wrapperCol"
      >
        <a-form-model-item :label="traditionlize('标题译名')" prop="translateTitle">
          <a-input
            v-model.trim="chapter.translateTitle"
            :placeholder="traditionlize('请输入')"
            style="min-width:300px;max-width:600px; width:30%;"
          />
        </a-form-model-item>
        <a-form-model-item :label="traditionlize('标题原名')" prop="originTitle">
          <a-input
            v-model.trim="chapter.originTitle"
            style="min-width:300px;max-width:600px; width:30%;"
            :placeholder="traditionlize('请输入')"
          />
        </a-form-model-item>
        <a-form-model-item :label="traditionlize('章节序数')" prop="episode">
          <a-input-number
            id="inputNumber"
            v-model="chapter.chapter"
            :min="0"
            :placeholder="traditionlize('请输入恰当的章节序数')"
            style="min-width:150px;max-width:600px; width:30%;"
          />
        </a-form-model-item>
        <a-form-model-item :label="traditionlize('头部信息')" prop="headerInfo">
          <a-input
            v-model.trim="chapter.headerInfo"
            :placeholder="traditionlize('可输入')"
            style="min-width:300px;max-width:600px; width:30%;"
            type="textarea"
          />
        </a-form-model-item>
        <a-form-model-item :label="traditionlize('译文内容')" prop="translateContent">
          <Tinymce
            ref="editor"
            v-model="chapter.translateContent"
            :toolbar="toolbar"
            :menubar="'edit view'"
            :height="400"
            :width="800"
          />
          <!-- <a-input
            v-model.trim="chapter.translateContent"
            style="height:400px;width:800px;"
            type="textarea"
          />-->
        </a-form-model-item>
        <a-form-model-item :label="traditionlize('尾部信息')" prop="footerInfo">
          <a-input
            v-model.trim="chapter.footerInfo"
            :placeholder="traditionlize('可输入')"
            style="min-width:300px;max-width:600px; width:30%;"
            type="textarea"
          />
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import moment from "moment";
import { mapGetters } from "vuex";
import { message,notification, Modal } from "ant-design-vue";
import xss from "xss";
import store from "@/store";
import isMobile from "is-mobile";
import traditionlize from "@/utils/translate";
import permission from "@/directive/permission/index.js";
import checkPermission from "@/utils/permission";
import {
  getBackendOptions,
  getUploadList,
  addBackendNovel,
  updateBackendNovel,
  deleteBackendNovel,
  addBackendNovelEpisode,
  updateBackendNovelEpisode,
  deleteBackendNovelEpisode,
  addBackendNovelEpisodeChapter,
  updateBackendNovelEpisodeChapter,
  getBackendChapterByChapter,
  deleteBackendNovelEpisodeChapter
} from "@/api/novel";
import Tinymce from "@/components/Tinymce/tinyIndex";

const toolbar = [
  "searchreplace outdent indent undo redo removeformat ",
  "link image fullscreen"
];

export default {
  components: { Tinymce },
  directives: { permission },
  data() {
    const _self = this;
    return {
      tableLoading: false,
      data: [],
      totalPages: 0,
      pagination: {
        defaultPageSize: 10,
        showTotal: total =>
          `Total ${_self.totalPages} pages, ${total} results.`,
        showSizeChanger: true,
        pageSizeOptions: [ "10", "20", "50"],
        onShowSizeChange: _self.pageSizeChange,
        size: "small",
        onChange: _self.pageChange,
        total: 0
      },
      query: {
        page: 0,
        size: 10
      },
      novelModalVisible: false,
      novelModalStatus: "create",
      novelLoading: false,
      novel: {
        categoryId: undefined,
        artistId: undefined,
        tagIds: [],
        covers: [{ url: undefined, description: undefined }],
        introduction: traditionlize("暂无简介")
      },
      resetNovel: {},
      episodeModalVisible: false,
      episodeModalStatus: "create",
      episodeLoading: false,
      episode: {
        translateTitle: undefined,
        originTitle: undefined,
        episode: 1,
        novelId: undefined
      },
      resetEpisode: {},
      chapterModalVisible: false,
      chapterModalStatus: "create",
      chapterLoading: false,
      chapter: {
        translateTitle: undefined,
        originTitle: undefined,
        chapter: 1,
        novelId: undefined,
        episodeId: undefined,
        headerInfo: undefined,
        footerInfo: undefined,
        translateContent: undefined,
        originContent: undefined
      },
      resetChapter: {},
      novelRules: {},
      episodeRules: {},
      chapterRules: {},
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      artists: [],
      categories: [],
      tags: [],
      moment,
      isMobile,
      traditionlize,
      checkPermission,
      toolbar
    };
  },
  computed: {
    ...mapGetters(["token", "roles"]),
    columns() {
      return [
        {
          title: traditionlize("小说译名"),
          key: "translateTitle",
          ellipsis: true,
          width: 150,
          scopedSlots: { customRender: "translateTitle" }
        },
        {
          title: traditionlize("类别"),
          dataIndex: "novelCategories",
          ellipsis: true,
          width: 120,
          customRender: (object, record, index) => {
            return traditionlize(object ? object.name : "未定义");
          }
        },
        {
          title: traditionlize("浏览"),
          dataIndex: "views",
          ellipsis: true,
          width: 100
        },
        {
          title: traditionlize("喜欢"),
          dataIndex: "likesCount",
          ellipsis: true,
          width: 100
        },
        {
          title: traditionlize("订阅"),
          dataIndex: "subscribesCount",
          ellipsis: true,
          width: 100
        },
        {
          title: traditionlize("评论"),
          dataIndex: "commentsCount",
          ellipsis: true,
          width: 100
        },
        {
          title: traditionlize("章节更新时间"),
          dataIndex: "lastChapterUpdatedTime",
          ellipsis: true,
          width: 180,
          customRender: (text, record, index) => {
            return record.lastChapterUpdatedTime
              ? moment(record.lastChapterUpdatedTime).format(
                  "YYYY-MM-DD HH:mm:ss"
                )
              : traditionlize("无记录");
          }
        },
        {
          title: traditionlize("审核状态"),
          key: "validate",
          ellipsis: true,
          width: 100,
          scopedSlots: { customRender: "validate" }
        },
        {
          title: traditionlize("下架"),
          key: "isDelete",
          ellipsis: true,
          width: 100,
          scopedSlots: { customRender: "isDelete" }
        },
        {
          title: traditionlize("锁定"),
          key: "locked",
          width: 100,
          ellipsis: true,
          scopedSlots: { customRender: "locked" }
        },
        {
          title: traditionlize("操作"),
          key: "operation",
          scopedSlots: { customRender: "operation" },
          width: 200
        }
      ];
    },
    episodeColumns() {
      return [
        {
          title: traditionlize("小说卷译名"),
          dataIndex: "translateTitle",
          key: "translateTitle",
          width: 200,
          customRender: (text, record, index) => {
            return traditionlize(text);
          }
        },
        {
          title: traditionlize("小说卷原名"),
          dataIndex: "originTitle",
          key: "originTitle",
          width: 200
        },
        {
          title: traditionlize("小说卷数"),
          dataIndex: "episode",
          key: "episode",
          width: 100
        },
        {
          title: traditionlize("审核状态"),
          key: "validate",
          ellipsis: true,
          width: 100,
          scopedSlots: { customRender: "validate" }
        },
        {
          title: traditionlize("操作"),
          key: "operation",
          scopedSlots: { customRender: "operation" },
          width: 200
        }
      ];
    },
    chapterColumns() {
      return [
        {
          title: traditionlize("章节译名"),
          key: "translateTitle",
          width: 200,
          scopedSlots: { customRender: "translateTitle" }
        },
        {
          title: traditionlize("章节原名"),
          dataIndex: "originTitle",
          width: 200
        },
        {
          title: traditionlize("章节序数"),
          dataIndex: "chapter",
          width: 200
        },
        {
          title: traditionlize("头部信息"),
          dataIndex: "headerInfo",
          width: 200,
          customRender: (text, record, index) => {
            return traditionlize(text);
          }
        },
        {
          title: traditionlize("尾部信息"),
          dataIndex: "footerInfo",
          ellipsis: true,
          width: 200,
          customRender: (text, record, index) => {
            return traditionlize(text);
          }
        },
        {
          title: traditionlize("操作"),
          key: "operation",
          scopedSlots: { customRender: "operation" },
          width: 200
        }
      ];
    },
    permissionCheck() {
      const _self = this;
      if (!(_self.token && Array.isArray(_self.roles) && _self.roles.length)) {
        return false;
      }
      return true;
    }
  },
  async mounted() {
    const _self = this;
    _self.init();
  },
  methods: {
    init() {
      const _self = this;
      _self.getList();
      _self.getOptions();
      _self.resetNovel = Object.assign({}, _self.novel);
      _self.resetEpisode = Object.assign({}, _self.episode);
      _self.resetChapter = Object.assign({}, _self.chapter);
    },
    async getList() {
      const _self = this;
      try {
        _self.tableLoading = true;
        let query = Object.assign({}, _self.query);
        let { data } = await getUploadList(query);
        _self.pagination.total = data.totalElements;
        _self.totalPages = data.totalPages;
        const content = data.content.map(current => {
          const entity = current;
          current.novelEpisodes.map(ep => {
            ep.key = ep.id;
            if (Array.isArray(ep.novelChapters)) {
              ep.novelChapters.map(ch => {
                ch.key = ch.id;
                if (ch.headerInfo) {
                  ch.headerInfo = decodeURI(ch.headerInfo);
                }
                if (ch.footerInfo) {
                  ch.footerInfo = decodeURI(ch.footerInfo);
                }
                return ch;
              });
            }
            return ep;
          });
          return current;
        });
        _self.data = content;
      } catch (error) {
        console.log(error);
      }
      _self.tableLoading = false;
    },
    async getOptions() {
      const _self = this;
      try {
        const {
          data: { categories, tags, artists }
        } = await getBackendOptions();
        _self.categories = categories;
        _self.tags = tags;
        _self.artists = artists;
      } catch (error) {
        console.log(error);
      }
    },
    pageSizeChange(currentSize, pageSize) {
      const _self = this;
      _self.query.page = 0;
      _self.query.size = pageSize;
      _self.getList();
    },
    pageChange(currentPage) {
      const _self = this;
      _self.query.page = currentPage - 1;
      _self.getList();
    },
    addNovel() {
      const _self = this;
      if (_self.novelModalStatus == "edit") {
        // _self.$nextTick(() => {
        //   _self.$refs.novelForm.resetFields();
        // });
        _self.novel = Object.assign({}, _self.resetNovel);
      }
      _self.novelModalStatus = "create";
      _self.novelModalVisible = true;
    },
    editNovel(novel) {
      const _self = this;
      let tagIds = novel.novelTags.map(tags => {
        return tags.novelTags.id;
      });
      _self.novel = {
        id: novel.id,
        translateTitle: novel.translateTitle,
        originTitle: novel.originTitle,
        categoryId: novel.novelCategories.id,
        artistId: novel.novelArtist.id,
        tagIds: tagIds,
        covers: novel.novelCovers || [],
        introduction: novel.introduction
      };
      _self.novelModalStatus = "edit";
      _self.novelModalVisible = true;
    },
    addNovelCover() {
      const _self = this;
      _self.novel.covers.push({
        key: _self.novel.covers.length,
        url: undefined,
        description: undefined
      });
    },
    uploadCover(index) {
      const _self = this;
      try {
        _self.novel.covers[index] = {
          key: index,
          url:
            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3045869616,3556407474&fm=26&gp=0.jpg",
          description: "123"
        };
        _self.novel.covers.sort((a, b) => a.key - b.key);
      } catch (error) {
        console.log(error);
      }
    },
    deleteCover(index) {
      const _self = this;
      try {
        _self.novel.covers.splice(index, 1);
      } catch (error) {
        console.log(error);
      }
    },
    async handleNovel() {
      const _self = this;
      _self.novelLoading = true;
      try {
        let resp;
        let data = Object.assign({}, _self.novel);
        data.covers = _self.novel.covers.filter(each => {
          return each.url !== "" && each.url !== undefined;
        });
        data.translateTitle = _self.traditionlizeForce(data.translateTitle,0);
        if (data.covers.length === 0) {
          delete data.covers;
        }
        if (_self.novelModalStatus === "create") {
          resp = await addBackendNovel(data);
        } else if (_self.novelModalStatus === "edit") {
          resp = await updateBackendNovel(data);
        }
        message.success(traditionlize("操作成功,正在刷新列表!"));
        _self.novelModalVisible = false;
        _self.getList();
      } catch (error) {
        console.log(error);
      }
      _self.novelLoading = false;
    },
    async changeNovelStatus(novel, option = {}) {
      const _self = this;
      _self.tableLoading = true;
      try {
        let tagIds = novel.novelTags.map(tags => {
          return tags.novelTags.id;
        });
        let data = {
          id: novel.id,
          translateTitle: novel.translateTitle,
          originTitle: novel.originTitle,
          categoryId: novel.novelCategories.id,
          artistId: novel.novelArtist.id,
          tagIds: tagIds,
          covers: novel.novelCovers || [],
          introduction: novel.introduction
        };
        for (let prop in option) {
          data[prop] = option[prop];
        }
        data.translateTitle = _self.traditionlizeForce(data.translateTitle,0);
        const resp = await updateBackendNovel(data);
        message.success(traditionlize("状态更改,正在刷新列表!"));
        _self.getList();
      } catch (error) {
        console.log(error);
      }
      _self.tableLoading = false;
    },
    async deleteNovel(novel) {
      const _self = this;
      try {
        Modal.confirm({
          title: traditionlize(`删除小说`),
          content: traditionlize(
            `是否真的删除小说「${novel.translateTitle}」 (ID:${novel.id})?\n删除小说会连同其所有相关内容一并删除且无法恢复.`
          ),
          okText: traditionlize("确定删除"),
          okType: "danger",
          cancelText: traditionlize("放弃"),
          onOk: async () => {
            let params = {
              id: novel.id
            };
            try {
              const { data } = await deleteBackendNovel(params);
              if (data) {
                notification.warning({
                  message: traditionlize("系统消息"),
                  description: traditionlize(
                    `成功删除「${novel.translateTitle}」 (ID:${novel.id})`
                  )
                });
                _self.getList();
              }
            } catch (error) {
              console.log(error);
              notification.error({
                message: traditionlize("系统消息"),
                description: traditionlize(
                  `删除「${novel.translateTitle}」 (ID:${novel.id}) 失败!`
                ),
                duration:null
              });
            }
          },
          onCancel: () => {}
        });
      } catch (error) {
        console.log(error);
      }
    },
    addEpisode(novel) {
      const _self = this;
      if (_self.episodeModalStatus == "edit") {
        // _self.$nextTick(() => {
        //   _self.$refs.episodeForm.resetFields();
        // });
        _self.episode = Object.assign({}, _self.resetEpisode);
      }
      _self.episode.novelId = novel.id;
      _self.episodeModalStatus = "create";
      _self.episodeModalVisible = true;
    },
    editEpisode(novel, episode) {
      const _self = this;
      _self.episode = {};
      _self.episode = Object.assign({}, episode);
      _self.episode.novelId = novel.id;
      _self.episodeModalStatus = "edit";
      _self.episodeModalVisible = true;
    },
    async handleEpisode() {
      const _self = this;
      _self.episodeLoading = true;
      try {
        let resp;
        let data = Object.assign({}, _self.episode);
        data.translateTitle = _self.traditionlizeForce(data.translateTitle,0);
        if (_self.episodeModalStatus === "create") {
          resp = await addBackendNovelEpisode(data);
        } else if (_self.episodeModalStatus === "edit") {
          resp = await updateBackendNovelEpisode(data);
        }
        message.success(traditionlize("操作成功,正在刷新列表!"));
        _self.episodeModalVisible = false;
        _self.getList();
      } catch (error) {
        console.log(error);
      }
      _self.episodeLoading = false;
    },
    async changeEpisodeStatus(novel, episode, option = {}) {
      const _self = this;
      _self.tableLoading = true;
      try {
        let data = Object.assign({}, episode);
        data.novelId = novel.id;
        for (let prop in option) {
          data[prop] = option[prop];
        }
        data.translateTitle = _self.traditionlizeForce(data.translateTitle,0);
        const resp = await updateBackendNovelEpisode(data);
        message.success(traditionlize("状态更改,正在刷新列表!"));
        _self.getList();
      } catch (error) {
        console.log(error);
      }
      _self.tableLoading = false;
    },
    async deleteEpisode(novel, episode) {
      const _self = this;
      try {
        Modal.confirm({
          title: traditionlize(`删除小说卷`),
          content: traditionlize(
            `是否真的删除小说卷「${episode.translateTitle}」 (ID:${episode.id}) (来自「${novel.translateTitle}」 (ID:${novel.id}))?\n删除小说卷会连同其以下的所有相关内容一并删除且无法恢复.`
          ),
          okText: traditionlize("确定删除"),
          okType: "danger",
          cancelText: traditionlize("放弃"),
          onOk: async () => {
            let params = {
              id: episode.id
            };
            try {
              const { data } = await deleteBackendNovelEpisode(params);
              if (data) {
                notification.warning({
                  message: traditionlize("系统消息"),
                  description: traditionlize(
                     `成功删除「${episode.translateTitle}」 (ID:${episode.id}) (来自「${novel.translateTitle}」 (ID:${novel.id}))`
                  )
                });
                _self.getList();
              }
            } catch (error) {
              console.log(error);
              notification.error({
                message: traditionlize("系统消息"),
                description: traditionlize(
                  `删除「${episode.translateTitle}」 (ID:${episode.id}) (来自「${novel.translateTitle}」 (ID:${novel.id})) 失败!`
                ),
                duration:null
              });
            }
          },
          onCancel: () => {}
        });
      } catch (error) {
        console.log(error);
      }
    },
    addChapter(novel, episode) {
      const _self = this;
      if (_self.chapterModalStatus == "edit") {
        // _self.$nextTick(() => {
        //   _self.$refs.chapterForm.resetFields();
        // });
        _self.chapter = Object.assign({}, _self.resetChapter);
      }
      _self.chapter.novelId = novel.id;
      _self.chapter.episodeId = episode.id;
      _self.chapterModalStatus = "create";
      _self.chapterModalVisible = true;
    },
    async editChapter(novel, episode, chapter) {
      const _self = this;
      _self.chapter = {};
      _self.chapter = Object.assign({}, chapter);
      _self.chapter.episodeId = episode.id;
      _self.chapter.novelId = novel.id;
      try {
        const { data } = await getBackendChapterByChapter(
          novel.id,
          episode.episode,
          chapter.chapter
        );
        let content = data.novelChapterParagraphs.reduce(
          (a, b) => {
            if (b.translateContent) {
              a.translateContent = a.translateContent.concat(
                b.translateContent
              );
            }
            if (b.originContent) {
              a.originContent = a.originContent.concat(b.originContent);
            }
            return a;
          },
          { translateContent: "", originContent: "" }
        );
        const xssOptions = {
          whiteList: {
            // p: []
          }, // 白名单为空，表示过滤所有标签
          stripIgnoreTag: true, // 过滤所有非白名单标签的HTML
          stripIgnoreTagBody: ["script"] // script标签较特殊，需要过滤标签中间的内容
        };
        //content.translateContent = xss(content.translateContent,xssOptions);
        //content.originContent = xss(content.translateContent,xssOptions);
        _self.chapter.translateContent = content.translateContent;
        _self.chapter.originContent = content.originContent;
        _self.chapterModalStatus = "edit";
        _self.chapterModalVisible = true;
      } catch (error) {
        console.log(error);
      }
    },
    async handleChapter() {
      const _self = this;
      _self.chapterLoading = true;
      try {
        let resp;
        let data = Object.assign({}, _self.chapter);
        if (data.headerInfo) {
          data.headerInfo = encodeURI(data.headerInfo);
        }
        if (data.footerInfo) {
          data.footerInfo = encodeURI(data.footerInfo);
        }

        data.translateContent = _self.processContent(data.translateContent);
        data.originContent = _self.processContent(data.originContent);

        data.translateTitle = _self.traditionlizeForce(data.translateTitle,0);

        if (_self.chapterModalStatus === "create") {
          resp = await addBackendNovelEpisodeChapter(data);
        } else if (_self.chapterModalStatus === "edit") {
          resp = await updateBackendNovelEpisodeChapter(data);
        }
        message.success(traditionlize("操作成功,正在刷新列表!"));
        _self.chapterModalVisible = false;
        _self.getList();
      } catch (error) {
        console.log(error);
      }
      _self.chapterLoading = false;
    },
    async deleteChapter(novel, episode, chapter) {
      const _self = this;
      try {
        Modal.confirm({
          title: traditionlize(`删除小说章节`),
          content: traditionlize(
            `是否真的删除小说章节「${chapter.translateTitle}」 (ID:${chapter.id}) (来自「${novel.translateTitle}」 (ID:${novel.id}) 的第 ${episode.episode} 卷)?\n删除小说章节会连同其以下的所有相关内容一并删除且无法恢复.`
          ),
          okText: traditionlize("确定删除"),
          okType: "danger",
          cancelText: traditionlize("放弃"),
          onOk: async () => {
            let params = {
              id: chapter.id
            };
            try {
              const { data } = await deleteBackendNovelEpisodeChapter(params);
              if (data) {
                notification.warning({
                  message: traditionlize("系统消息"),
                  description: traditionlize(
                     `成功删除「${chapter.translateTitle}」 (ID:${chapter.id}) (来自「${novel.translateTitle}」 (ID:${novel.id}) 的第 ${episode.episode} 卷)`
                  )
                });
                _self.getList();
              }
            } catch (error) {
              console.log(error);
              notification.error({
                message: traditionlize("系统消息"),
                description: traditionlize(
                  `删除「${chapter.translateTitle}」 (ID:${chapter.id}) (来自「${novel.translateTitle}」 (ID:${novel.id}) 的第 ${episode.episode} 卷) 失败!`
                ),
                duration:null
              });
            }
          },
          onCancel: () => {}
        });
      } catch (error) {
        console.log(error);
      }
    },
    handleCancel() {
      const _self = this;
      _self.novelModalVisible = false;
      _self.episodeModalVisible = false;
      _self.chapterModalVisible = false;
    },
    HTMLDecode(text) {
      var temp = document.createElement("div");
      temp.innerHTML = text;
      var output = temp.innerText || temp.textContent;
      temp = null;
      return output;
    },
    HTMLDecode2(text) {
      var temp = document.createElement("div");
      temp.innerHTML = text;
      var output = temp.innerHTML;
      temp = null;
      return output;
    },
    processContent(content) {
      const _self = this;
      const xssOptions = {
        whiteList: {
          p: [],
          br: [],
          img: []
        }, // 白名单为空，表示过滤所有标签
        stripIgnoreTag: true, // 过滤所有非白名单标签的HTML
        stripIgnoreTagBody: ["script"] // script标签较特殊，需要过滤标签中间的内容
      };
      content = xss(content, xssOptions);
      content = _self.HTMLDecode2(content);
      let array = content.split("<br>").filter(each => each.trim() !== "");
      content = array
        .map(each => {
          let result = each;
          if (each.indexOf("<p>") !== -1) {
            if (each.indexOf("</p>") === -1) {
              result = each + "</p>";
            } else {
              result = each;
            }
          } else {
            result = "<p>" + each + "</p>";
          }
          return result;
        })
        .join("");
      return content;
    },
    traditionlizeForce(val,type = undefined){
      return traditionlize(val,type);
    },
  }
};
</script>