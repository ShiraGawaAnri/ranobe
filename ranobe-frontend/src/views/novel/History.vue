<template>
  <MainList
    ref="mainlist"
    :searchBar="false"
    :reorderBar="true"
    :breadcrumb="breadcrumb"
    :list-type="listType"
    :searchIds="searchIds"
  />
</template>

<script>
import { mapGetters } from "vuex";
import store from "@/store";
import MainList from "./components/MainList";

let breadcrumb = [
  {
    href: "/",
    text: "主页"
  },
  {
    href: undefined,
    text: "历史记录"
  },
  {
    key: "type",
    href: undefined,
    text: "本地记录"
  }
];

export default {
  name: "History",
  components: { MainList },
  data() {
    return {
      breadcrumb,
      searchIds: []
    };
  },
  created() {
    //放在created才会把local-history需要的novelIds先取得，再传值渲染子组件
    this.init();
  },
  mounted() {},
  activated() {},
  computed: {
    ...mapGetters(["histories", "roles", "token", "localHistories"]),
    type() {
      const _self = this;
      let retType = "local";
      if (
        _self.$route.query &&
        _self.token &&
        _self.roles &&
        _self.roles.length > 0
      ) {
        const { type } = _self.$route.query;
        retType = type === "cloud" ? "cloud" : retType;
      } else {
        retType = "local";
      }
      //触发一次渲染
      _self.breadcrumb.pop();
      _self.breadcrumb.push({
        href: undefined,
        text: retType === "cloud" ? "云记录" : "本地记录"
      });
      return retType;
    },
    listType() {
      const _self = this;
      return `${_self.type}-history`;
    }
  },
  methods: {
    async init() {
      const _self = this;
      if (_self.type === "local") {
        await _self.initLocal();
      } else {
        await _self.initCloud();
      }
    },
    initLocal() {
      const _self = this;
      let temp = _self.localHistories
        .filter(his => his.novel && his.novelChapter)
        .map(his => his.novel.id);
      _self.searchIds = temp || [];
    },
    async initCloud() {
      await store.dispatch("user/getInfo");
    }
  },
  watch: {
    // 本地历史&云历史来回切换时
    $route() {
      const _self = this;
      _self.init();
      //待computed计算的listType传值后再调用子组件的init()方法
      _self.$nextTick(() => {
        _self.$refs.mainlist && _self.$refs.mainlist.init();
      });
    }
  }
};
</script>