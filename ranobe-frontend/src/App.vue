<template>
  <div id="app">
    <a-layout id="sider-bar-layout">
      <a-layout-sider
        v-model="collapsed"
        :trigger="null"
        collapsible
        id="sider-bar"
        class="pc-trigger"
        :width="isMobile ? 160 : 200"
        :collapsedWidth="isMobile ? 0 : 80"
      >
        <div class="logo" />
        <a-menu
          :default-selected-keys="['1']"
          mode="inline"
          theme="dark"
          :inline-collapsed="collapsed"
          @click="handleMenuClick"
        >
          <a-menu-item key="0" v-show="!isMobile">
            <a-button
              type
              style="margin-left:-17px;margin-top:4px;"
              @click="toggleCollapsed"
              :ghost="true"
            >
              <a-icon :type="collapsed ? 'menu-unfold' : 'menu-fold'" />
              <span>缩放侧栏</span>
            </a-button>
          </a-menu-item>
          <a-sub-menu key="account-service">
            <span slot="title">
              <a-icon type="user" />
              <span v-trans>账号服务</span>
            </span>
            <a-menu-item v-show="!logined" key="account-login">
              <a-icon type="login" />
              <span v-trans>登入账号</span>
              <router-link to="/account?type=login" />
            </a-menu-item>
            <a-menu-item v-show="!logined" key="account-reg">
              <a-icon type="user-add" />
              <span v-trans>注册账号</span>
              <router-link to="/account?type=reg" />
            </a-menu-item>
            <a-menu-item v-show="!logined" key="account-forget">
              <a-icon type="question" />
              <span v-trans>找回账号</span>
              <router-link to="/account?type=forget" />
            </a-menu-item>
            <a-menu-item v-show="logined" key="account-panel" to="/novel" path="/novel">
              <a-icon type="solution" />
              <span v-trans>个人中心</span>
              <router-link to="/user" />
            </a-menu-item>
            <a-menu-item v-show="logined" key="account-logout">
              <a-icon type="logout" />
              <span v-trans>注销登入</span>
            </a-menu-item>
          </a-sub-menu>
          <a-sub-menu key="setting-service">
            <span slot="title">
              <a-icon type="setting" />
              <span v-trans>我的设置</span>
            </span>
            <a-menu-item key="setting-read">
              <a-icon type="read" />
              <span v-trans>阅读设置</span>
            </a-menu-item>
            <!-- <a-menu-item key="setting-other">
              <a-icon type="swap-right" />
              <span v-trans>其他设置</span>
            </a-menu-item> -->
          </a-sub-menu>
          <a-sub-menu key="novel-service" v-show="checkPermission(['ROLE_BROZON_USER'])">
            <span slot="title">
              <a-icon type="cloud-upload" />
              <span v-trans>我的小说</span>
            </span>
            <a-menu-item key="novel-list">
              <a-icon type="unordered-list" />
              <span v-trans>上传列表</span>
              <router-link to="/upload"></router-link>
            </a-menu-item>
          </a-sub-menu>
          <a-sub-menu key="history-service">
            <span slot="title">
              <a-icon type="history" />
              <span v-trans>历史浏览</span>
            </span>
            <a-menu-item key="history-common">
              <a-icon type="sliders" />
              <span v-trans>本地历史</span>
              <router-link to="/novel/history?type=local"></router-link>
            </a-menu-item>
            <a-menu-item key="history-cloud" v-show="logined">
              <a-icon type="cloud" />
              <span v-trans>云历史</span>
              <router-link to="/novel/history?type=cloud"></router-link>
            </a-menu-item>
          </a-sub-menu>
          <a-sub-menu key="subscribe-service">
            <span slot="title">
              <a-icon type="mail" />
              <span v-trans>订阅</span>
            </span>
            <a-menu-item key="common-subscribe">
              <a-icon type="fire" />
              <span v-trans>热门订阅</span>
              <router-link to="/subscribe?type=common"></router-link>
            </a-menu-item>
            <a-menu-item key="cloud-subscribe" v-show="checkPermission(['ROLE_BROZON_USER'])">
              <a-badge
                :count="subscribesBadge || 0"
                :numberStyle="{zoom: '0.7'}"
                :offset="collapsed ? [0,15] : [-90,0]"
              >
                <a-icon type="user" />
                <span v-trans>我的订阅</span>
              </a-badge>
              <router-link to="/subscribe?type=cloud"></router-link>
            </a-menu-item>
          </a-sub-menu>
          <a-sub-menu key="quickaccess-service">
            <span slot="title">
              <a-icon type="menu" />
              <span v-trans>快捷访问</span>
            </span>
            <a-menu-item key="quickaccess-link1">
              <a-icon type="link" />
              <span v-trans>一周更新</span>
              <router-link to="/novel/main?type=lastweek"></router-link>
            </a-menu-item>
            <a-menu-item key="quickaccess-home">
              <a-icon type="home" />
              <span v-trans>主页</span>
              <router-link to="/"></router-link>
            </a-menu-item>
          </a-sub-menu>
          <a-sub-menu key="backend-service">
            <span slot="title">
              <a-icon type="menu" />
              <span v-trans>后台服务</span>
            </span>
          </a-sub-menu>
        </a-menu>
      </a-layout-sider>
      <a-layout>
        <a-layout-header style="background: #fff; padding: 0">
          <a-button class="trigger" @click="toggleCollapsed" v-show="isMobile">
            <a-icon :type="collapsed ? 'menu-unfold' : 'menu-fold'" />
          </a-button>
        </a-layout-header>
        <a-layout-content
          :style="{ margin: isMobile ? '0 0' : '0 0', padding: isMobile ? '16px' : '24px', 'min-height': 'auto','min-width': isMobile ? '450px' : '700px','background': 'white','overflow':'auto'}"
        >
          <div class="main-container">
            <router-view />
          </div>
        </a-layout-content>
      </a-layout>
    </a-layout>
    <a-modal v-model="settingVisible">
      <template slot="title">
        <span v-trans>阅读设置</span>
      </template>
      <template slot="footer">
        <a-button key="back" @click="settingVisible = false">{{traditionlize('关闭')}}</a-button>
      </template>
      <div style="padding: 4px 0;">
        <span style="width:120px;" v-trans>语言</span>
        <a-select v-model="language" style="width:120px;margin-left:10px;">
          <a-select-option
            v-for="(lang,index) in langs"
            :key="index"
            :value="lang.value"
          >{{traditionlize(lang.text)}}</a-select-option>
        </a-select>
      </div>
      <div style="padding: 4px 0;">
        <span style="width:120px;" v-trans>字体大小</span>
        <a-select v-model="fontSize" style="width:120px;margin-left:10px;">
          <a-select-option
            v-for="fontSize in fontSizes"
            :key="fontSize"
            :value="fontSize"
          >{{fontSize}}</a-select-option>
        </a-select>
      </div>
      <div style="padding: 4px 0;">
        <span v-trans>背景颜色</span>
        <a-radio-group
          class="left-nav-menu"
          size="large"
          name="setting-read-background-color"
          v-for="(color,index) in colorOptions"
          :key="index"
          v-model="backgroundColorNumber"
        >
          <a-tooltip placement="top">
            <div slot="title">{{traditionlize(color.label)}}</div>
            <a-radio
              style="margin-left:8px;"
              :class="`colors-radio-${color.value}`"
              :value="color.value"
            ></a-radio>
          </a-tooltip>
        </a-radio-group>
      </div>
      <div style="padding: 4px 0;">
        <span v-trans>字体颜色</span>
        <a-radio-group
          class="left-nav-menu"
          size="large"
          name="setting-read-font-color"
          v-for="(color,index) in fontColorOptions"
          :key="index"
          v-model="fontColorNumber"
        >
          <a-tooltip placement="top">
            <div slot="title">{{traditionlize(color.label)}}</div>
            <a-radio
              style="margin-left:8px;"
              :class="`fontcolors-radio-${color.value}`"
              :value="color.value"
            ></a-radio>
          </a-tooltip>
        </a-radio-group>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { mapGetters,mapMutations } from "vuex";
import { message, Modal } from "ant-design-vue";
import store from "@/store";
import isMobile from "is-mobile";
import traditionlize from "@/utils/translate";
import checkPermission from "@/utils/permission";

import {
  validUsername,
  validPassword,
  validNickname,
  validURL,
  validEmail
} from "@/utils/validate";
import { reg, login } from "@/api/user";
import { commonOptions } from "@/components/mixins/common";

const fontSizes = [12, 14, 16, 18, 20, 22, 24, 26, 28, 30];
const langs = [
  { value: 0, text: "简中" },
  {
    value: 1,
    text: "繁中"
  }
];

export default {
  name: "App",
  mixins: [commonOptions],

  data() {
    const _self = this;
    return {
      traditionlize,
      checkPermission,
      fontSizes,
      langs,
      visible: false,
      settingVisible: false
    };
  },
  computed: {
    ...mapGetters([
      "settings",
      "avatar",
      "roles",
      "username",
      "token",
      "subscribes"
    ]),
    isMobile() {
      return isMobile();
    },
    collapsed() {
      const _self = this;
      if (!_self.settings) return false;
      if (_self.settings.closeSiderBar === undefined) {
        let val = _self.isMobile;
        let flag = true;
        //store.dispatch("settings/closeSiderBar", { val, flag });
        _self.setCloseSiderBar({ val, flag });
      }
      return _self.settings.closeSiderBar;
    },
    logined() {
      const _self = this;
      return _self.token && Array.isArray(_self.roles) && _self.roles.length;
    },
    backgroundColorNumberSelectedText() {
      const _self = this;
      const defaultColor = "白色";
      if (_self.settings) {
        let t = _self.colorOptions.find(
          option => option.value === _self.settings.backgroundColorNumber
        );
        return t ? traditionlize(t.label) : traditionlize(defaultColor);
      }
      return traditionlize(defaultColor);
    },
    fontColorNumberSelectedText() {
      const _self = this;
      const defaultColor = "黑色";
      if (_self.settings) {
        let t = _self.fontColorOptions.find(
          option => option.value === _self.settings.fontColorNumber
        );
        return t ? traditionlize(t.label) : traditionlize(defaultColor);
      }
      return traditionlize(defaultColor);
    },
    subscribesBadge() {
      const _self = this;
      if (
        !_self.logined ||
        !Array.isArray(_self.subscribes) ||
        _self.subscribes.length === 0
      )
        return 0;
      let count = _self.subscribes.reduce((a, b) => {
        return a + (b.hasNew ? 1 : 0);
      }, 0);
      return count;
    },
    language: {
      get() {
        const _self = this;
        return _self.settings.language;
      },
      set(val) {
        const _self = this;
        _self.setLanguage(val);
      }
    },
    fontSize: {
      get() {
        const _self = this;
        return _self.settings.fontSize;
      },
      set(val) {
        const _self = this;
        _self.setFontSize(val);
      }
    },
    backgroundColorNumber: {
      get() {
        const _self = this;
        return _self.settings.backgroundColorNumber;
      },
      set(val) {
        const _self = this;
        _self.setBackgroundColorNumber(val);
      }
    },
    fontColorNumber: {
      get() {
        const _self = this;
        return _self.settings.fontColorNumber;
      },
      set(val) {
        const _self = this;
        _self.setFontColorNumber(val);
      }
    }
  },
  methods: {
    ...mapMutations({
      setLanguage: "settings/SET_LANGUAGE",
      setShowTags: "settings/SET_SHOWTAGS",
      setFontSize: "settings/SET_FONTSIZE",
      setShowTsukomi: "settings/SET_SHOWTSUKOMI",
      setFontColorNumber: "settings/SET_FONTCOLORNUMBER",
      setBackgroundColorNumber: "settings/SET_BACKGROUNDCOLORNUMBER",
      setShowOriginContent: "settings/SET_SHOWORIGINCONTENT",
      setCloseSiderBar: "settings/SET_CLOSESIDERBAR"
    }),
    async toggleCollapsed() {
      const _self = this;

      // await store.dispatch("settings/closeSiderBar", {
      //   val: !_self.collapsed,
      //   flag: false
      // });
      _self.setCloseSiderBar({ val: !_self.collapsed, flag: false });
    },
    handleMenuClick({ item, key, keyPath }) {
      const _self = this;
      if (keyPath.includes("account-service")) {
        switch (key) {
          default:
            break;
          // case "account-login":
          //   _self.visible = true;
          //   _self.dialogStatus = "login";
          //   break;
          // case "account-reg":
          //   _self.visible = true;
          //   _self.dialogStatus = "reg";
          //   break;
          // case "account-forget":
          //   _self.handleChangeToForget();
          //   break;
          case "account-logout":
            _self.logout();
            break;
          case "account-panel":
            break;
        }
      } else if (keyPath.includes("novel-service")) {
        switch (key) {
          default:
            console.log(item, key, keyPath);
            break;
          case "novel-list":
            //_self.$router.push("/upload");
            break;
        }
      } else if (keyPath.includes("setting-service")) {
        switch (key) {
          default:
            console.log(item, key, keyPath);
            break;
          case "setting-read":
            _self.settingVisible = true;
            break;
        }
      }
      if (_self.isMobile) {
        _self.toggleCollapsed();
      }
    },
    // handleChangeToAnother() {
    //   const _self = this;
    //   _self.$refs.dataForm.resetFields();
    //   _self.visible = false;
    //   //保留动画
    //   setTimeout(() => {
    //     if (_self.dialogStatus === "login") {
    //       _self.dialogStatus = "reg";
    //     } else if (_self.dialogStatus === "reg") {
    //       _self.dialogStatus = "login";
    //     }
    //     _self.visible = true;
    //   }, 250);
    // },
    // handleChangeToForget() {
    //   const _self = this;
    //   _self.visible = true;
    //   _self.dialogStatus = "forget";
    //   _self.$nextTick(() => {
    //     _self.$refs.dataForm.resetFields();
    //   });
    // },
    // async handleOk() {
    //   const _self = this;
    //   this.$refs.dataForm.validate(valid => {
    //     if (valid) {
    //       if (_self.dialogStatus === "login") {
    //         _self.login();
    //       } else if (_self.dialogStatus === "reg") {
    //         _self.reg();
    //       }
    //     } else {
    //       console.log("error submit!!");
    //       return false;
    //     }
    //   });
    // },
    // googleCheck() {
    //   this.googleChecking = true;
    //   this.recaptchaToken = undefined;
    //   this.$refs.google.check();
    // },
    // async reg() {
    //   const _self = this;
    //   // if (!_self.recaptchaToken) {
    //   //   return;
    //   // }
    //   try {
    //     _self.loading = true;
    //     let data = Object.assign({}, _self.userData);
    //     data.recaptchaToken = _self.recaptchaToken;
    //     const resp = await reg(data);
    //     if (resp.code !== 20000) {
    //       _self.recaptchaToken = undefined;
    //       //_self.$refs.google.reset();
    //       message.error(`注册失败!\r\n原因: ${resp.message}`);
    //     } else {
    //       // _self.$message.warning(
    //       //   '成功注册,欢迎新用户来到本站,请先进行邮箱验证!'
    //       // );
    //       message.info("成功注册,欢迎新用户来到本站!");
    //       _self.visible = false;
    //       _self.$refs.dataForm.resetFields();
    //     }
    //   } catch (error) {
    //     console.log(error);
    //   }
    //   _self.loading = false;
    // },
    // async login() {
    //   const _self = this;
    //   // if (!_self.recaptchaToken) {
    //   //   return;
    //   // }
    //   try {
    //     _self.loading = true;
    //     let data = Object.assign({}, _self.userData);
    //     await store.dispatch("user/login", data);
    //     _self.visible = false;
    //     _self.$refs.dataForm.resetFields();
    //   } catch (error) {
    //     console.log(error);
    //   }
    //   _self.loading = false;
    // },
    async logout() {
      try {
        Modal.confirm({
          title: traditionlize("注销登入"),
          content: traditionlize(
            "是否真的注销登入?\r\n退出登录后将有部分资源无法访问."
          ),
          okText: traditionlize("确定注销"),
          okType: "danger",
          cancelText: traditionlize("放弃"),
          onOk: async () => {
            await store.dispatch("user/logout");
          },
          onCancel: () => {}
        });

        //location.href = location.origin;
      } catch (error) {
        await store.dispatch("user/resetToken");
        location.reload();
      }
    },
    // handleCancel() {
    //   const _self = this;
    //   _self.visible = false;
    //   _self.$refs.dataForm.clearValidate();
    // },
    handleChangeBackgroundColor(e) {
      console.log(e);
    }
  },
  mounted() {}
};
</script>
<style>
@import url("https://fonts.googleapis.com/css?family=Noto+Serif+SC");
@import url("https://fonts.googleapis.com/css?family=Noto+Serif+TC");

</style>
<style lang="less">
@import "~@/styles/radio-cus1.less";


</style>
<style lang="less" scoped>
#app {
  height: 100%;
}
#sider-bar-layout {
  height: inherit;
}

.logo {
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
  margin: 16px;
}
.trigger {
  font-size: 18px;
  cursor: pointer;
  transition: color 0.3s;
}

@media screen and (max-width: 560px) {
}
</style>
