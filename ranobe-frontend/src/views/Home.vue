<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png" />
    <!-- <HelloWorld msg="Welcome to Your Vue.js App" /> -->
  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from "@/components/HelloWorld.vue";
import { message, Modal } from "ant-design-vue";


export default {
  name: "Home",
  // components: {
  //   HelloWorld
  // },
  data(){
    const _self = this;
    return {

    }
  },
  created() {
    const _self = this;
    // Modal.confirm({
    //   title: "权限不足",
    //   content: "您没有权限访问请求此次数据, 您可以重新尝试登录后再访问此页面",
    //   okText: "跳转登录页",
    //   okType: "danger",
    //   cancelText: "留在本页",
    //   onOk: () => {
    //     message.error('时间测试');
    //   },
    //   onCancel: () => {
    //     console.log("cancel");
    //   }
    // });
  }
};
</script>
