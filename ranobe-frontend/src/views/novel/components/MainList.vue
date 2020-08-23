<template>
  <a-layout :style="{padding: !isMobile ? '0 24px' : '0 0'}">
    <a-layout-header
      v-show="showBreadcrumbHeader"
      style="background: whitesmoke; padding: 0 0;"
      class="supoort-header"
    >
      <a-row type="flex" justify="space-between">
        <a-col>
          <Breadcrumb :breadcrumb="breadcrumb" />
        </a-col>
        <a-col v-if="!isMobile">
          <a-row type="flex" justify="space-between">
            <a-col v-if="reorderBar" style="margin: 0px 16px">
              <div class="filter">
                <!-- <strong v-trans>排序:</strong> -->
                <a-cascader
                  v-model="reorderValue"
                  @change="reorderChange"
                  :options="ascOrDescOptions"
                  :placeholder="traditionlize('可选排序方法')"
                />
              </div>
            </a-col>
            <a-col v-if="reorderTimeBar" style="margin: 0px 16px">
              <div class="filter">
                <!-- <strong v-trans>排序:</strong> -->
                <a-cascader
                  change-on-select
                  v-model="reorderTimeValue"
                  @change="onDateCascaderChange"
                  :options="yearMonthDayOptions"
                  :placeholder="traditionlize('可选时间类型')"
                  style="width:150px;margin: 0px 6px"
                />
                <a-range-picker
                  v-if="reorderTimeType === 100"
                  v-model="datetimeRange"
                  @change="onDateChange"
                />
                <a-week-picker
                  v-else-if="reorderTimeType === 101"
                  v-model="datetime"
                  @change="onDateChange"
                  :placeholder="traditionlize('选择周')"
                />
                <a-month-picker
                  v-else-if="reorderTimeType === 10"
                  v-model="datetime"
                  @change="onDateChange"
                  :placeholder="traditionlize('选择月')"
                />
                <a-select
                  v-else-if="reorderTimeType === 0"
                  v-model="time"
                  @change="onYearChange"
                  :placeholder="traditionlize('选择年')"
                  style="width:100px;"
                >
                  <a-select-option v-for="year in yearOptions" :key="year">{{ `${year} Year` }}</a-select-option>
                </a-select>
                <a-select
                  v-else
                  :disabled="true"
                  :placeholder="traditionlize('请选择类型')"
                  style="width:150px;"
                ></a-select>
              </div>
            </a-col>
            <a-col style="margin: 0px 16px">
              <div class="show-tags-button">
                <a-switch
                  checked-children="详细标签"
                  un-checked-children="详细标签"
                  v-model="settings.showTags"
                />
              </div>
            </a-col>
          </a-row>
        </a-col>
      </a-row>
    </a-layout-header>
    <a-layout>
      <a-layout-content
        :style="{ background: '#fff', padding: isMobile ? '0px' : '16px', margin: 0, minHeight: '320px' }"
      >
        <!-- <a-row :gutter="[{ xs: 44, lg:32, xxl:20},12]">
        <a-col :xs="18" :lg="12" :xxl="6" v-for="(item,index) in list" :key="index">-->
        <a-spin v-if="!tableMode" :spinning="spinning" tip="Loading...">
          <a-row
            type="flex"
            justify="start"
            :gutter="[{ xs: 16, md:16, lg:32, xxl:64},24]"
            v-if="total > 0"
          >
            <a-col v-for="(item,index) in list" :key="index">
              <div class="show-views">
                <a-icon type="eye" />
                <span>{{` 阅读 ${item.views} 次`}}</span>
                <br />
              </div>

              <a-card hoverable :style="{width: showTagsStyle.w1,height:'100%'}" class="novel-card">
                <img
                  v-show="!isMobile"
                  slot="cover"
                  alt="Novel Cover"
                  :src="(!isValNullUndefined(item.novelCovers) && item.novelCovers.length > 0) ? item.novelCovers[0].url:''"
                  :style="{maxHeight:showTagsStyle.mh1}"
                  @click="($event)=>toNovelDetails(item)"
                />

                <template slot="actions" class="ant-card-actions">
                  <a-icon key="eye" type="eye" @click="($event)=>toNovelDetails(item)" />
                  <template v-if="subscribeNovels.includes(item.id)">
                    <a-badge dot v-if="subscribeHasNewNovels.includes(item.id)">
                      <a-icon
                        key="mail"
                        type="mail"
                        theme="twoTone"
                        two-tone-color="#eb2f96"
                        @click="($event)=>deleteNovelSubscribe(item)"
                      />
                    </a-badge>
                    <a-icon
                      v-else
                      key="mail"
                      type="mail"
                      theme="twoTone"
                      two-tone-color="#52c41a"
                      @click="($event)=>deleteNovelSubscribe(item)"
                    />
                  </template>
                  <template v-else>
                    <a-icon key="mail" type="mail" @click="($event)=>addNovelSubscribe(item)" />
                  </template>
                  <a-icon key="ellipsis" type="ellipsis" />
                </template>
                <a-popover :title="item.shortTitle">
                  <template slot="content">
                    <p>{{item.introduction}}</p>
                    <p>{{`全 ${item.epCount} 卷 | 共 ${item.chapterCount} 话`}}</p>
                    <p>{{`合计 ${toThousandNumber(item.totalChar)}字`}}</p>
                  </template>
                  <a-card-meta
                    :title="traditionlize(item.translateTitle)"
                    :description="!isValNullUndefined(item.novelArtist) ? `原著: ${item.novelArtist.translateName}(${item.novelArtist.originName})` :''"
                  ></a-card-meta>
                </a-popover>
                <hr />
                <p v-trans>
                  {{`类别: `}}
                  <a-tag color="red" v-trans>{{item.novelCategories.name}}</a-tag>
                </p>
                <template v-if="settings.showTags && item.novelTags">
                  <p class="show-tags">
                    <a-tag
                      v-for="tag in item.novelTags"
                      :key="tag.id"
                      v-trans
                      color="orange"
                    >{{traditionlize(tag.novelTags.nameCn)}}</a-tag>
                  </p>
                </template>
                <hr />
                <div class="novel-other-info">
                  <span>{{` ${traditionlize('更新')}: ${parseTime(item.lastChapterUpdatedTime)}`}}</span>
                  <br />
                  <!-- <a-icon type="eye" />
                  <span>{{`  阅读  ${item.views}`}}</span>
                  <br/>-->
                  <a-icon type="mail" />
                  <span>{{` ${traditionlize('订阅')} `}}</span>
                  <a-badge
                    :showZero="true"
                    :count="item.subscribesCount || 0"
                    style="margin-top:-1px;"
                  />
                  <br />
                  <a-icon type="message" />
                  <span>{{` ${traditionlize('评论')} `}}</span>
                  <a-badge
                    :showZero="true"
                    :count="item.commentsCount || 0"
                    :number-style="{ backgroundColor: '#52c41a' }"
                    style="margin-top:-1px;"
                  />
                  <template v-if="hasNovelHistory(item)">
                    <br />
                    <a-icon type="history" spin />
                    <span>{{` ${traditionlize('历史')}`}}</span>
                    <span style="margin-left:4px;">{{ `${getNovelHistoryLastReadTime(item)}`}}</span>
                    <template v-if="getNovelHistoryLastReadChapter(item)">
                      <br />
                      <span
                        class="ellipsis"
                      >{{traditionlize(`阅读至: ${getNovelHistoryLastReadChapter(item)}`)}}</span>
                    </template>
                  </template>
                </div>
              </a-card>
            </a-col>
          </a-row>
          <a-empty v-else style="margin-top: 150px" :description="traditionlize('没有找到结果')"></a-empty>
          <div class="pagination" style="margin-top:64px;text-align:center;">
            <a-pagination
              v-if="total > 0"
              v-model="query.currentPage"
              :page-size.sync="query.pageSize"
              show-quick-jumper
              show-size-changer
              :page-size-options="['12', '24', '36', '48']"
              :default-current="query.currentPage"
              :total="total"
              @change="pageChange"
              @showSizeChange="onShowSizeChange"
            />
          </div>
        </a-spin>
        <template v-else>
          <div
            v-infinite-scroll="handleInfiniteOnLoad"
            :infinite-scroll-disabled="loadedAll"
            :infinite-scroll-distance="10"
            class="scroll-table"
          >
            <a-table
              :data-source="list"
              :row-selection="showTableAction ? rowSelection : null"
              :columns="columns"
              :showHeader="false"
              :pagination="false"
            >
              <template slot="cover" slot-scope="item">
                <img
                  alt="Novel Cover"
                  :src="(!isValNullUndefined(item.novelCovers) && item.novelCovers.length > 0) ? item.novelCovers[0].url:''"
                  style="min-width:120px;width:120px;padding:4px 0px;min-height:160px;"
                />
              </template>
              <template slot="tags" slot-scope="item">
                <p style="text-align:left;font-size:16px;font-weight:bold;">
                  <a :href="`/novel/${item.id}/details`">{{ `${item.translateTitle}` }}</a>
                </p>
                <a-tag
                  v-for="tag in item.novelTags"
                  :key="tag.id"
                  v-trans
                  color="orange"
                >{{traditionlize(tag.novelTags.nameCn)}}</a-tag>
              </template>
              <template slot="infomation" slot-scope="item">
                <p>{{traditionlize(`全${item.epCount}卷 共${item.chapterCount}话 ${toThousandNumber(item.totalChar)}字`)}}</p>
                <div class="novel-other-info">
                  <span>{{` ${traditionlize('更新')}: ${parseTime(item.lastChapterUpdatedTime)}`}}</span>
                  <br />
                  <a-icon type="mail" />
                  <span>{{` ${traditionlize('订阅')} `}}</span>
                  <a-badge
                    :showZero="true"
                    :count="item.subscribesCount || 0"
                    style="margin-top:-1px;"
                  />
                  <br />
                  <a-icon type="message" />
                  <span>{{` ${traditionlize('评论')} `}}</span>
                  <a-badge
                    :showZero="true"
                    :count="item.commentsCount || 0"
                    :number-style="{ backgroundColor: '#52c41a' }"
                    style="margin-top:-1px;"
                  />
                </div>
                <template v-if="hasNovelHistory(item) && collapsed">
                  <a-icon type="history" spin />
                  <span>{{` ${traditionlize('历史')}`}}</span>
                  <span style="margin-left:4px;">{{ `${getNovelHistoryLastReadTime(item)}`}}</span>
                  <template v-if="getNovelHistoryLastReadChapter(item)">
                    <span
                      class="ellipsis"
                    >{{traditionlize(`阅读至: ${getNovelHistoryLastReadChapter(item)}`)}}</span>
                  </template>
                </template>
              </template>
              <template v-if="loadedAll" slot="footer">
                <div style="height:40px;text-align:center;">
                  <a-button type="link" style="font-size:18px;">{{ traditionlize('已经到末尾啦~') }}</a-button>
                </div>
              </template>
            </a-table>
          </div>
          <div v-if="spinning && !loadedAll" class="loading-container">
            <a-spin tip="数据加载中，请稍等..." />
          </div>
        </template>
      </a-layout-content>
      <a-layout-sider
        v-if="searchBar"
        class="search-side"
        width="260"
        collapsible
        defaultCollapsed
        reverseArrow
        collapsedWidth="0"
      >
        <a-form-model
          ref="ruleSearchForm"
          :model="searchForm"
          :rules="rules"
          :label-col="labelCol"
          :wrapper-col="wrapperCol"
          id="search-form"
          style="margin: 10px 10px;"
        >
          <a-form-model-item ref="title" label prop="title" :autoLink="false">
            <p>搜索筛选设定</p>
            <span>标题</span>
            <a-input v-model="searchForm.title" allowClear placeholder="Search Title" />
          </a-form-model-item>
          <a-form-model-item label prop="artistId">
            <span>作者</span>
            <a-select
              v-model="searchForm.artistId"
              allowClear
              show-search
              placeholder="Select a artist"
              option-filter-prop="children"
              :filter-option="filterSearchArtistOption"
            >
              <a-select-option value="1">Jack</a-select-option>
              <a-select-option value="2">Lucy</a-select-option>
              <a-select-option value="3">Tom</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label prop="categoryId">
            <span>类别</span>
            <a-select v-model="searchForm.categoryId" allowClear placeholder="Select a category">
              <a-select-option value="1">冒险</a-select-option>
              <a-select-option value="2">Lucy</a-select-option>
              <a-select-option value="3" disabled>R18</a-select-option>
              <a-select-option value="4">yiminghe</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label prop="finish">
            <span>连载状态</span>
            <a-select v-model="searchForm.finish" allowClear placeholder="Select a status">
              <a-select-option value="0">连载中</a-select-option>
              <a-select-option value="1">已完结</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label prop="tagIds">
            <span>包含标签</span>
            <a-select v-model="searchForm.tagIds" mode="multiple" placeholder="Please select tags">
              <a-select-option
                v-for="i in 25"
                :key="(i + 9).toString(36) + i"
                :value="i"
              >{{ (i + 9).toString(36) + i }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item>
            <p />
            <a-button type="primary" icon="search" @click="onSearchSubmit">快速检索</a-button>
            <a-button type="danger" @click="resetSearchForm" style="margin-left:10px;">重置</a-button>
          </a-form-model-item>
        </a-form-model>
      </a-layout-sider>
    </a-layout>
  </a-layout>
</template>

<script>
import { message, Modal } from "ant-design-vue";
import { mapGetters } from "vuex";
import moment from "moment";
import isMobile from "is-mobile";
import infiniteScroll from "vue-infinite-scroll";
import store from "@/store";
import {
  getNovelList,
  getNovelHistory,
  getNovelSubscribe,
  addNovelSubscribe,
  deleteNovelSubscribe
} from "@/api/novel";
import { parseTime, toThousandNumber, isValNullUndefined } from "@/utils";
import traditionlize from "@/utils/translate";
import Breadcrumb from "@/components/Breadcrumb";

// const reorderAscOptions = [
//   {
//     value: 3,
//     label: traditionlize("更新时间")
//   },
//   {
//     value: 5,
//     label: traditionlize("阅览数")
//   },
//   {
//     value: 7,
//     label: traditionlize("订阅数")
//   },
//   {
//     value: 9,
//     label: traditionlize("点赞数")
//   },
//   {
//     value: 11,
//     label: traditionlize("评论数")
//   },
//   {
//     value: 1,
//     label: traditionlize("入库时间"),
//     order: "asc"
//   }
// ];

// const reorderDescOptions = [
//   {
//     value: 2,
//     label: traditionlize("更新时间")
//   },
//   {
//     value: 4,
//     label: traditionlize("阅览数")
//   },
//   {
//     value: 6,
//     label: traditionlize("订阅数")
//   },
//   {
//     value: 8,
//     label: traditionlize("点赞数")
//   },
//   {
//     value: 10,
//     label: traditionlize("评论数")
//   },
//   {
//     value: 0,
//     label: traditionlize("入库时间"),
//     order: "asc"
//   }
// ];

// const ascOrDescOptions = [
//   {
//     value: 0,
//     label: traditionlize("倒序↓"),
//     order: "desc",
//     children: reorderDescOptions
//   },
//   {
//     value: 1,
//     label: traditionlize("正序↑"),
//     order: "asc",
//     children: reorderAscOptions
//   }
// ];

// const yearMonthDayOptions = [
//   {
//     value: 0,
//     label: traditionlize("年"),
//     children: [
//       {
//         value: 10,
//         label: traditionlize("月"),
//         children: [
//           {
//             value: 100,
//             label: traditionlize("日")
//           },
//           {
//             value: 101,
//             label: traditionlize("周")
//           }
//         ]
//       }
//     ]
//   }
// ];

export default {
  name: "MainList",
  components: { Breadcrumb },
  directives: { infiniteScroll },
  props: {
    searchBar: {
      type: Boolean,
      default: false
    },
    reorderBar: {
      type: Boolean,
      default: false
    },
    reorderTimeBar: {
      type: Boolean,
      default: false
    },
    breadcrumb: {
      type: Array,
      default() {
        return [];
      }
    },
    searchIds: {
      type: Array,
      default() {
        return [];
      }
    },
    listType: {
      type: String,
      default() {
        return "";
      }
    },
    params: {
      type: Object,
      default() {
        return {};
      }
    }
  },
  data() {
    return {
      spinning: false,
      loadedAll: false,
      spinningSettimeout: undefined,
      list: [],
      total: 0,
      query: {
        currentPage: 1,
        pageSize: 12,
        sortBy: 3
      },
      reorderValue: [0, 2],
      reorderTimeValue: [0],
      //
      labelCol: { span: 4 },
      wrapperCol: { span: 24 },
      other: "",
      searchForm: {
        title: undefined,
        artistId: undefined,
        categoryId: undefined,
        finish: undefined,
        tagIds: []
      },
      searchFormCopy: {},
      rules: {
        title: [
          {
            required: false,
            trigger: "blur"
          },
          {
            min: 2,
            max: 15,
            message: "Length should be 2 to 15",
            trigger: "blur"
          }
        ]
      },
      showTableAction: false,
      selectedRowKeys: [],
      ascOrDescOptions: [
        {
          value: 0,
          label: traditionlize("倒序↓"),
          order: "desc",
          children: [
            {
              value: 2,
              label: traditionlize("更新时间")
            },
            {
              value: 4,
              label: traditionlize("阅览数")
            },
            {
              value: 6,
              label: traditionlize("订阅数")
            },
            {
              value: 8,
              label: traditionlize("点赞数")
            },
            {
              value: 10,
              label: traditionlize("评论数")
            },
            {
              value: 0,
              label: traditionlize("入库时间"),
              order: "asc"
            }
          ]
        },
        {
          value: 1,
          label: traditionlize("正序↑"),
          order: "asc",
          children: [
            {
              value: 3,
              label: traditionlize("更新时间")
            },
            {
              value: 5,
              label: traditionlize("阅览数")
            },
            {
              value: 7,
              label: traditionlize("订阅数")
            },
            {
              value: 9,
              label: traditionlize("点赞数")
            },
            {
              value: 11,
              label: traditionlize("评论数")
            },
            {
              value: 1,
              label: traditionlize("入库时间"),
              order: "asc"
            }
          ]
        }
      ],
      yearMonthDayOptions: [
        {
          value: 0,
          label: traditionlize("年"),
          children: [
            {
              value: 10,
              label: traditionlize("月"),
              children: [
                {
                  value: 100,
                  label: traditionlize("日")
                },
                {
                  value: 101,
                  label: traditionlize("周")
                }
              ]
            }
          ]
        }
      ],
      yearOptions: [],
      datetimeRange: undefined,
      datetime: undefined,
      time: undefined,
      moment,
      traditionlize,
      toThousandNumber,
      isValNullUndefined
    };
  },
  computed: {
    ...mapGetters(["subscribes", "histories", "localHistories", "settings"]),
    isMobile() {
      return isMobile();
    },
    showTagsStyle() {
      const _self = this;
      if (_self.settings.showTags) {
        return {
          w1: "250px",
          mh1: "350px"
        };
      } else {
        return {
          w1: "200px",
          mh1: "280px"
        };
      }
    },
    subscribeNovels() {
      const _self = this;
      return Array.isArray(_self.subscribes)
        ? _self.subscribes.map(each => each.novel.id)
        : [];
    },
    subscribeHasNewNovels() {
      const _self = this;
      return Array.isArray(_self.subscribes)
        ? _self.subscribes
            .filter(each => each.hasNew)
            .map(each => each.novel.id)
        : [];
    },
    mixinHistories() {
      const _self = this;
      let result = [];
      if (Array.isArray(_self.histories)) {
        result = result.concat(_self.histories);
      }
      if (Array.isArray(_self.localHistories)) {
        result = result.concat(_self.localHistories);
      }
      //去重
      result = result.reduce((total, nextElement) => {
        if (total.length === 0) {
          total.push(nextElement);
        } else {
          let index = total.findIndex(
            each => each.novel.id === nextElement.novel.id
          );
          if (index === -1) {
            total.push(nextElement);
          } else {
            total[index] = nextElement;
          }
        }
        return total;
      }, []);
      return result;
    },
    novelIds() {
      const _self = this;
      return _self.searchIds;
    },
    collapsed() {
      const _self = this;
      return _self.settings.closeSiderBar;
    },
    showBreadcrumbHeader() {
      const _self = this;
      return !_self.isMobile || (_self.isMobile && _self.collapsed);
    },
    tableMode() {
      const _self = this;
      return _self.isMobile;
    },
    columns() {
      return [
        {
          title: "cover",
          key: "cover",
          width: 150,
          scopedSlots: { customRender: "cover" }
        },
        // {
        //   title: "translateTitle",
        //   key: "translateTitle",
        //   ellipsis: true,
        //   width: 150,
        //   scopedSlots: { customRender: "translateTitle" }
        // },
        {
          title: "tags",
          key: "tags",
          width: 150,
          scopedSlots: { customRender: "tags" }
        },
        {
          title: "infomation",
          key: "infomation",
          width: 200,
          scopedSlots: { customRender: "infomation" }
        }
      ];
    },
    rowSelection() {
      const { selectedRowKeys } = this;
      return {
        selectedRowKeys,
        onChange: this.onSelectChange,
        hideDefaultSelections: true,
        selections: [
          {
            key: "all-data",
            text: "Select All Data",
            onSelect: () => {
              this.selectedRowKeys = [...Array(46).keys()]; // 0...45
            }
          },
          {
            key: "odd",
            text: "Select Odd Row",
            onSelect: changableRowKeys => {
              let newSelectedRowKeys = [];
              newSelectedRowKeys = changableRowKeys.filter((key, index) => {
                if (index % 2 !== 0) {
                  return false;
                }
                return true;
              });
              this.selectedRowKeys = newSelectedRowKeys;
            }
          },
          {
            key: "even",
            text: "Select Even Row",
            onSelect: changableRowKeys => {
              let newSelectedRowKeys = [];
              newSelectedRowKeys = changableRowKeys.filter((key, index) => {
                if (index % 2 !== 0) {
                  return true;
                }
                return false;
              });
              this.selectedRowKeys = newSelectedRowKeys;
            }
          }
        ],
        onSelection: this.onSelection
      };
    },
    reorderTimeType() {
      const _self = this;
      if (
        Array.isArray(_self.reorderTimeValue) &&
        _self.reorderTimeValue.length > 0
      ) {
        return [..._self.reorderTimeValue].pop();
      }
      return -1;
    }
  },
  created() {
    const _self = this;
    _self.searchFormCopy = Object.assign({}, _self.searchForm);
    const { query } = _self.$route;
    Object.assign(_self.searchForm, query);
    Object.assign(_self.query, query);
    let startYear = 2019;
    let endYear = moment().year();
    for (let i = startYear; i <= endYear; i++) {
      _self.yearOptions.push(i);
    }
    //_self.init();
  },
  async mounted() {
    const _self = this;
    _self.init();
  },
  methods: {
    init() {
      const _self = this;
      _self.query.currentPage = 1;
      _self.query.pageSize = 12;
      _self.list = [];
      _self.getNovelList();
    },
    async getNovelList() {
      const _self = this;
      let query = Object.assign({}, _self.query);
      query.page = _self.query.currentPage - 1;
      query.size = _self.query.pageSize;
      _self.spinning = true;
      _self.spinningSettimeout = setTimeout(() => {
        _self.spinning = false;
      }, 20000);
      try {
        let data, list;
        if (_self.listType === "cloud-history") {
          let { data } = await getNovelHistory(query);
          let p = Object.assign({}, data);
          p.content = data.content.map(his => {
            return his.novel;
          });
          list = p;
        } else if (_self.listType === "local-history") {
          query.novelIds = _self.novelIds || [];
          if (
            Array.isArray(_self.reorderValue) &&
            _self.reorderValue.length > 0
          ) {
            query.sortBy = [..._self.reorderValue].pop();
          }
          let { data } = await getNovelList(query);
          list = data;
        } else if (_self.listType === "cloud-subscribe") {
          query.sortBy = 2;
          if (
            Array.isArray(_self.reorderValue) &&
            _self.reorderValue.length > 0
          ) {
            query.sortBy = [..._self.reorderValue].pop();
          }
          let { data } = await getNovelSubscribe(query);
          let p = Object.assign({}, data);
          p.content = data.content.map(his => {
            return his.novel;
          });
          list = p;
        } else if (_self.listType === "common-subscribe") {
          query.sortBy = 6;
          let { data } = await getNovelList(query);
          list = data;
        } else if (_self.listType === "condition-search") {
          const { search } = _self.params;
          switch (search) {
            default:
              break;
            case "lastweek":
              query.startTime =
                moment(new Date())
                  .add(-7, "days")
                  .unix() * 1000;
              query.endTime = new Date().getTime();
              query.sortBy = 2;
              break;
          }
          if (
            Array.isArray(_self.reorderValue) &&
            _self.reorderValue.length > 0
          ) {
            query.sortBy = [..._self.reorderValue].pop();
          }
          let { data } = await getNovelList(query);
          list = data;
        } else {
          if (
            Array.isArray(_self.reorderValue) &&
            _self.reorderValue.length > 0
          ) {
            query.sortBy = [..._self.reorderValue].pop();
          }
          let { data } = await getNovelList(query);
          list = data;
        }
        const content = list.content.map(novel => {
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
              ? `最新标题:${
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
          currentNovel.key = novel.id;
          return currentNovel;
        });
        if (!_self.tableMode) {
          _self.list = content;
        } else {
          _self.list = _self.list.concat(content);
          _self.loadedAll = list.last;
        }
        _self.total = list.totalElements;
      } catch (error) {
        console.log(error);
      }
      _self.spinning = false;
      if (_self.spinningSettimeout) {
        clearInterval(_self.spinningSettimeout);
      }
    },
    toNovelDetails(novelItem) {
      this.$router.push({ path: `/novel/${novelItem.id}/details` });
    },
    async addNovelSubscribe(item) {
      const _self = this;
      try {
        let postData = {};
        postData.novelId = item.id;
        const { data } = await addNovelSubscribe(postData);
        if (data) {
          message.success(
            traditionlize(`成功订阅小说 「${item.translateTitle}」`)
          );
          await store.dispatch("user/getInfo");
        }
      } catch (error) {
        console.log(error);
      }
    },
    async deleteNovelSubscribe(item) {
      const _self = this;
      try {
        let postData = {};
        postData.novelId = item.id;
        const { data } = await deleteNovelSubscribe(postData);
        if (data) {
          message.warning(
            traditionlize(`已经取消订阅小说 「${item.translateTitle}」`)
          );
          await store.dispatch("user/getInfo");
        }
      } catch (error) {
        console.log(error);
      }
      _self.subscribedLoading = false;
    },
    hasNovelHistory(novel) {
      const _self = this;
      if (!novel || !Array.isArray(_self.histories)) return false;
      return _self.mixinHistories.find(his => his.novel.id === novel.id);
    },
    getNovelHistoryLastReadTime(novel) {
      const _self = this;
      const target = _self.hasNovelHistory(novel);
      if (!target) return "未知时间";
      if (_self.settings.showTags) {
        return moment(target.lastReadTime).format("YYYY-MM-DD HH:mm");
      }
      return moment(target.lastReadTime).format("YYYY-MM-DD");
    },
    getNovelHistoryLastReadChapter(novel) {
      const _self = this;
      const target = _self.hasNovelHistory(novel);
      if (!target || !target.novelChapter || !target.novelChapter.id) {
        return undefined;
      }
      if (!novel || !Array.isArray(novel.novelEpisodes)) {
        return undefined;
      }
      let { id } = target.novelChapter;
      id = Number(id);
      let novelCh;
      const novelEp = novel.novelEpisodes.find(ep => {
        if (!Array.isArray(ep.novelChapters)) {
          return false;
        }
        let temp = ep.novelChapters.find(ch => ch.id === id);
        if (temp) {
          novelCh = temp;
        }
        return temp;
      });
      return `${novelEp ? novelEp.translateTitle : ""} ${
        novelCh ? novelCh.translateTitle : ""
      }`;
    },
    pageChange(current) {
      const _self = this;
      _self.query.currentPage = current;
      _self.getNovelList();
    },
    onShowSizeChange(current, pageSize) {
      const _self = this;
      _self.query.currentPage = current;
      _self.query.pageSize = pageSize;
      _self.getNovelList();
    },
    parseTime(time) {
      return parseTime(time, "{y}-{m}-{d}");
    },
    filterSearchArtistOption(input, option) {
      return (
        option.componentOptions.children[0].text
          .toLowerCase()
          .indexOf(input.toLowerCase()) >= 0
      );
    },
    reorderChange() {
      const _self = this;
      _self.getNovelList();
    },
    onDateCascaderChange() {
      const _self = this;
      if (_self.reorderTimeValue.length === 0) {
        _self.query.startTime = undefined;
        _self.query.endTime = undefined;
        _self.datetimeRange = undefined;
        _self.datetime = undefined;
        _self.time = undefined;
        _self.getNovelList();
      }
    },
    onDateChange(date, dateString) {
      const _self = this;
      //range
      try {
        const nowMoment = moment();
        //range
        if (_self.reorderTimeType === 100) {
          let startMoment = date[0];
          let endMoment = date[1];
          if (typeof startMoment === "string") {
            _self.query.startTime = undefined;
            _self.query.endTime = undefined;
          } else {
            _self.query.startTime =
              startMoment.utcOffset("+08:00").unix() * 1000;
            _self.query.endTime =
              endMoment
                .add(1, "days")
                .utcOffset("+08:00")
                .unix() * 1000;
          }
          // if (startMoment.isAfter(nowMoment)) {
          //   _self.datetimeRange[0] = nowMoment;
          // }
          // if (endMoment.isAfter(nowMoment)) {
          //   _self.datetimeRange[1] = nowMoment;
          // }
        }
        //week
        else if (_self.reorderTimeType === 101) {
          if (!date) {
            _self.query.startTime = undefined;
            _self.query.endTime = undefined;
          } else {
            let week = moment(dateString, "YYYY-ww").format("YYYY-MM-DD");
            let startDay = moment(week);
            let endDay = moment(week).add(7, "days");
            _self.query.startTime = startDay.utcOffset("+08:00").unix() * 1000;
            _self.query.endTime = endDay.utcOffset("+08:00").unix() * 1000;
            // if (date.isAfter(nowMoment)) {
            //   _self.datetime = nowMoment;
            // }
          }
        }
        //month
        else if (_self.reorderTimeType === 10) {
          if (!date) {
            _self.query.startTime = undefined;
            _self.query.endTime = undefined;
          } else {
            let month = moment(dateString).format("YYYY-MM-DD");
            let startDay = moment(month);
            let endDay = moment(month).add(1, "months");
            _self.query.startTime = startDay.utcOffset("+08:00").unix() * 1000;
            _self.query.endTime = endDay.utcOffset("+08:00").unix() * 1000;
            // if (date.isAfter(nowMoment)) {
            //   _self.datetime = nowMoment;
            // }
          }
        }
        //unknown
        else {
          console.log(date, dateString);
          console.log(date.utcOffset("+08:00").unix() * 1000);
        }
      } catch (error) {
        _self.query.startTime = undefined;
        _self.query.endTime = undefined;
      }
      _self.getNovelList();
    },
    onYearChange(val) {
      const _self = this;
      _self.query.startTime =
        moment([val])
          .utcOffset("+08:00")
          .unix() * 1000;
      _self.query.endTime =
        moment([val])
          .add(1, "years")
          .utcOffset("+08:00")
          .unix() * 1000;
      _self.getNovelList();
    },
    onSearchSubmit() {
      const _self = this;
      _self.$refs.ruleSearchForm.validate(valid => {
        if (valid) {
          Object.assign(_self.query, _self.searchForm);
          _self.getNovelList();
          //alert("submit!");
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    resetSearchForm() {
      this.$refs.ruleSearchForm.resetFields();
    },
    onSelectChange(selectedRowKeys) {
      console.log("selectedRowKeys changed: ", selectedRowKeys);
      this.selectedRowKeys = selectedRowKeys;
    },
    handleInfiniteOnLoad() {
      console.log("TRIGGER");
      const _self = this;
      _self.loadedAll = true;
      ++_self.query.currentPage;
      _self.getNovelList();
    }
  }
};
</script>
<style lang="less">
</style>
<style lang="less" scoped>
@import "~@/styles/selection.less";

.search-side {
  background-color: rgb(255, 255, 255);
}
.ant-pagination:not(:last-child) {
  margin-top: 24px;
  margin-bottom: 24px;
}
.show-views {
  position: absolute;
  left: 40px;
  z-index: 100;
  color: white;
  font-weight: bold;
  background-color: #5a5555ad;
  padding: 1px 5px;
}
.ant-form-item {
  margin-bottom: 0;
}
.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  &.one-line {
    -webkit-line-clamp: 1;
  }
}
.scroll-table {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  padding: 16px 0px;
  height: 750px;
  overflow: auto auto;
}
.loading-container {
  margin-bottom: 40px;
  width: 100%;
  text-align: center;
}

@media screen and (max-width: 540px) {
  .supoort-header {
    display: none;
  }
}
@media screen and (max-width: 1165px) {
  .search-side {
    display: none;
  }
  .show-tags {
    display: none;
  }
}
@media screen and (max-width: 1600px) {
  .show-views {
    left: 25px;
  }
}

#search-form {
  animation: animated-border 3s infinite;
  font-family: "Microsoft YaHei", Arial;
  color: #f7d8d8;
  border: 2px solid;
  border-radius: 10px;
  padding: 15px;
}
@keyframes animated-border {
  0% {
    box-shadow: 0 0 0 0 rgba(255, 159, 159, 0.4);
  }
  100% {
    box-shadow: 0 0 0 20px rgba(255, 255, 255, 0);
  }
}
</style>
