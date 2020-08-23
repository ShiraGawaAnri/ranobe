<template>
  <MainList
    ref="mainlist"
    :searchBar="true"
    :breadcrumb="breadcrumb"
    :list-type="listType"
    :params="params"
    :reorderBar="reorderBar"
    :reorderTimeBar="reorderTimeBar"
  />
</template>

<script>
import isMobile from "is-mobile";
import MainList from "./components/MainList";

const breadcrumb = [
  {
    href: "/",
    text: "主页"
  },
  {
    href: undefined,
    text: "小说"
  },
  {
    value: undefined,
    text: "一般搜索"
  }
];

export default {
  name: "Main",
  components: { MainList },
  mounted() {
    this.init();
  },
  computed: {
    type() {
      const _self = this;
      let retType = "normal";
      const { type } = _self.$route.query;
      const options = [
        {
          value: "normal",
          text: "一般搜索"
        },
        {
          value: "lastweek",
          text: "最近一周"
        }
      ];
      if (type) {
        retType = type;
      }
      let target = options.find(option => option.value === retType);
      //触发一次渲染
      _self.breadcrumb.pop();
      _self.breadcrumb.push({
        href: undefined,
        text: target ? target.text : "未知搜索"
      });
      return retType;
    },
    listType() {
      const _self = this;
      return _self.type === "normal" ? undefined : `condition-search`;
    },
    params() {
      const _self = this;
      return _self.type === "normal"
        ? undefined
        : {
            search: _self.type
          };
    },
    reorderBar(){
      const _self = this;
      return true;
    },
    reorderTimeBar(){
      const _self = this;
      return _self.type === "normal";
    }
  },
  data() {
    return {
      breadcrumb
    };
  },
  methods: {
    init() {}
  },
  watch: {
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