<template>
  <MainList ref="mainlist" :searchBar="false" :breadcrumb="breadcrumb" :list-type="listType" />
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
    text: "订阅"
  },
  {
    href: undefined,
    text: "公共订阅"
  }
];

export default {
  name: "Subscribe",
  components: { MainList },
  data() {
    return {
      breadcrumb
    };
  },
  creatd(){
    this.init();
  },
  mounted() {
    
  },
  activated() {},
  computed: {
    ...mapGetters(["subscrib", "roles", "token"]),
    type() {
      const _self = this;
      let retType = "common";
      if (
        _self.$route.query &&
        _self.token &&
        _self.roles &&
        _self.roles.length > 0
      ) {
        const { type } = _self.$route.query;
        retType = type === "cloud" ? "cloud" : retType;
      } else {
        retType = "common";
      }
      //触发一次渲染
      _self.breadcrumb.pop();
      _self.breadcrumb.push({
        href: undefined,
        text: retType === "common" ? "热门订阅" : "我的订阅"
      });
      return retType;
    },
    listType() {
      const _self = this;
      return `${_self.type}-subscribe`;
    }
  },
  methods: {
    async init() {
      const _self = this;
      if (_self.type === "common") {
        await _self.initLocal();
      } else {
        await _self.initCloud();
      }
    },
    initLocal() {},
    async initCloud() {
      await store.dispatch("user/getInfo");
    }
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