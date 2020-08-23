<template>
  <div class="information">
    <el-card class="box-card" shadow="hover">
      <div>您所访问的是外部连接</div>
      <div>我们无法保证其合法性与安全性</div>
      <hr />
      <div v-if="url">{{ `将要访问的连接:` }}</div>
      <div v-if="url">{{ `${url}` }}</div>
      <br />
      <div>
        <el-link v-if="url" :href="url" type="danger">继续访问</el-link>
        <el-link @click="goBack" type="success">返回上一页</el-link>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'RedirectUrl',

  data() {
    return {
      text: undefined,
      url: undefined,
    };
  },
  created() {
    if (this.$route.query) {
      const { text, url } = this.$route.query;
      this.text = text;
      this.url = url;
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1);
    },
  },
};
</script>

<style lang="scss" scoped>
.information {
  text-align: center;
  width: 50%;
  letter-spacing: 2px;
}
.el-link {
  margin: 0 10px;
  font-size: 18px;
}
</style>
