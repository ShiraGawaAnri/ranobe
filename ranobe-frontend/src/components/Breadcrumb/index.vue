<template>
  <a-breadcrumb class="breadcrumb flat cus" style="margin: 16px 16px 16px 4px;" separator>
    <a-breadcrumb-item v-for="(item,index) in breadcrumbItems" :key="index">
      <a v-if="item.href" :href="item.href">{{traditionlize(item.text)}}</a>
      <a v-else>{{traditionlize(item.text)}}</a>
    </a-breadcrumb-item>
  </a-breadcrumb>
</template>

<style lang="less" scoped>
@import "~@/styles/breadcrumb-cus1.less";
</style>


<script>
import traditionlize from "@/utils/translate";

const defaultBreadcrumb = [
  {
    href: "/",
    text: "主页"
  },
  {
    href: undefined,
    text: "当前"
  }
];
export default {
  name: "Breadcrumb",
  props: {
    breadcrumb: {
      type: Array,
      default() {
        return [];
      }
    }
  },
  data() {
    return {
      traditionlize
    };
  },
  computed: {
    breadcrumbItems() {
      const _self = this;
      return Array.isArray(_self.breadcrumb) && _self.breadcrumb.length > 0
        ? _self.breadcrumb
        : defaultBreadcrumb;
    },
    
  }
};
</script>