<template>
  <a-list
    class="hot-novels"
    :loading="loading"
    item-layout="horizontal"
    :data-source="data"
    style="padding:8px 8px 8px 16px;max-height:600px;"
    size="small"
    :split="false"
  >
    <div
      v-if="showLoadingMore && !noMore"
      slot="loadMore"
      :style="{ textAlign: 'center', marginTop: '12px', height: '32px', lineHeight: '32px' }"
    >
      <a-spin v-if="loadingMore" />
      <a-button v-else @click="onLoadMore">{{traditionlize('加载更多')}}</a-button>
    </div>
    <a-list-item slot="renderItem" slot-scope="item,index">
      <a-list-item-meta>
        <a slot="title" :href="toNovelDetails(item.novel)">
          <span v-trans>{{ item.novel.translateTitle }}</span>
          <span
            style="margin-left:4px;color:#bf7171;"
          >{{ `${moment(item.novel.lastChapterUpdatedTime).format("YYYY-MM-DD")}`}}</span>
        </a>
        <div slot="description">
          <a-icon
            v-if="index <= Math.max(parseInt(data.length * 0.3),3)"
            type="fire"
            theme="twoTone"
            two-tone-color="#eb2f96"
          />
          <a-icon v-else type="fire" />
          <span>{{toThousandNumber(item.score,1)}}</span>
          <span style="margin-left:8px;color:#bf7171;">{{item.novel.shortTitle}}</span>
          <br />
          <span>
            <a-tag
              v-trans
              color="orange"
              v-for="tag in item.novel.novelTags.slice(0,5)"
              :key="tag.id"
            >{{tag.novelTags.nameCn}}</a-tag>
          </span>
        </div>
        <a-avatar
          slot="avatar"
          :src="(!isValNullUndefined(item.novel.novelCovers) && item.novel.novelCovers.length > 0) ? item.novel.novelCovers[0].url:''"
        />
      </a-list-item-meta>

      <!-- <div>{{ item.score }}</div> -->
    </a-list-item>
    <div slot="header" arrow-point-at-center>
      <a-tooltip placement="top">
        <div slot="title">{{ `${moment(new Date()).add(-7, "days").format("YYYY-MM-DD")} ~` }}</div>
        <strong v-trans style="font-size:18px;">热门小说</strong>
      </a-tooltip>
    </div>
  </a-list>
</template>

<script>
import moment from "moment";
import { getHotNovel } from "@/api/novel";
import { isValNullUndefined, toThousandNumber } from "@/utils";
import traditionlize from "@/utils/translate";

export default {
  name: "HotList",
  props: {
    size: {
      type: Number,
      default: 10
    }
  },
  data() {
    const _self = this;
    return {
      noMore: false,
      loading: true,
      loadingMore: false,
      showLoadingMore: true,
      data: [],
      query: {
        currentPage: 1,
        pageSize: _self.size,
        startTime: new Date(
          moment(new Date())
            .add(-7, "days")
            .utc()
            .format("YYYY-MM-DD") + " GMT +8"
        ).getTime(),
        endTime: new Date().getTime()
      },
      moment,
      traditionlize,
      isValNullUndefined,
      toThousandNumber
    };
  },
  async mounted() {
    const _self = this;
    let content = await this.getHotNovel();
    _self.data = _self.data.concat(content);
  },
  methods: {
    async getHotNovel() {
      const _self = this;
      let content = [];
      try {
        let query = Object.assign({}, _self.query);
        query.page = _self.query.currentPage - 1;
        query.size = _self.query.pageSize;
        _self.loading = true;
        const { data } = await getHotNovel(query);
        data.content.map(currentNovel => {
          let novel = currentNovel.novel;
          const lastEp = [...novel.novelEpisodes].pop();
          if (lastEp) {
            novel.shortTitle = `${lastEp.translateTitle} ${
              lastEp.novelChapters && lastEp.novelChapters.length > 0
                ? [...lastEp.novelChapters].pop().translateTitle
                : ""
            }`;
          }
          return currentNovel;
        });
        content = data.content;
      } catch (error) {
        console.log(error);
      }
      _self.loading = false;
      return content;
    },
    toNovelDetails(novel) {
      return `/novel/${novel.id}/details`;
    },
    async onLoadMore() {
      const _self = this;
      _self.loadingMore = true;
      _self.query.currentPage += 1;
      let content = await this.getHotNovel();
      if (!content || !Array.isArray(content) || content.length === 0) {
        _self.noMore = true;
        _self.loadingMore = false;

        return;
      }
      _self.data = _self.data.concat(content);
      _self.loadingMore = false;
      _self.$nextTick(() => {
        window.dispatchEvent(new Event("resize"));
      });
    }
  }
};
</script>

<style lang="less" scoped>
</style>