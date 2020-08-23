<template>
  <div class="account-panel">
    <a-card
      :hoverable="true"
      :style="{width:!isMobile?'75%':'100%','max-width':'750px',margin:'auto auto'}"
    >
      <template slot="title">
        <a-button
          v-if="dialogStatus !== 'forget'"
          type="link"
          @click="handleChangeToForget"
          style="position: absolute;left: 5px;font-size:16px;"
        >{{traditionlize('忘记账号密码?')}}</a-button>
      </template>
      <a-form-model ref="dataForm" :rules="rules" :model="userData">
        <a-form-model-item
          :label="traditionlize('账号名')"
          prop="username"
          :style="{width:!isMobile?'75%':'100%'}"
        >
          <a-input v-model.trim="userData.username" :placeholder="traditionlize('请输入账号名')" />
        </a-form-model-item>
        <a-form-model-item
          v-if="['login','reg'].includes(dialogStatus)"
          :label="traditionlize('密码')"
          prop="password"
          :style="{width:!isMobile?'75%':'100%'}"
        >
          <a-input
            v-model.trim="userData.password"
            type="password"
            :placeholder="traditionlize('请输入密码')"
          />
        </a-form-model-item>
        <a-form-model-item
          v-if="dialogStatus === 'reg'"
          :label="traditionlize('确认密码')"
          prop="passwordConfirm"
          :style="{width:!isMobile?'75%':'100%'}"
        >
          <a-input
            v-model.trim="userData.passwordConfirm"
            type="password"
            :placeholder="traditionlize('请重复确认您的密码')"
          />
        </a-form-model-item>
        <a-form-model-item
          v-if="['reg','forget'].includes(dialogStatus)"
          :label="traditionlize('验证邮箱')"
          prop="email"
          :style="{width:!isMobile?'75%':'100%'}"
        >
          <a-input v-model.trim="userData.email" :placeholder="traditionlize('请输入有效的邮箱以用于验证')" />
        </a-form-model-item>
        <a-form-model-item
          v-if="dialogStatus === 'reg'"
          :label="traditionlize('昵称')"
          prop="nickname"
          :style="{width:!isMobile?'75%':'100%'}"
        >
          <a-input v-model.trim="userData.nickname" :placeholder="traditionlize('仅支持中英文和_')" />
        </a-form-model-item>
        <a-form-model-item>
          <a-row type="flex" justify="start">
            <a-col :span="!isMobile ? 8 : 18">
              <a-button
                v-if="['login','reg'].includes(dialogStatus)"
                type="link"
                @click="handleChangeToAnother"
                size="small"
              >{{changeToAnotherText}}</a-button>
            </a-col>
            <a-col :span="16">
              <a-row type="flex" justify="start">
                <a-col :span="4">
                  <a-button
                    key="submit"
                    type="primary"
                    :loading="loading"
                    @click="handleOk"
                  >{{traditionlize('提交')}}</a-button>
                </a-col>
                <a-col :span="4" :offset="!isMobile ? 4: 12">
                  <a-button key="back" @click="handleReset">{{traditionlize('重置')}}</a-button>
                </a-col>
              </a-row>
            </a-col>
          </a-row>
        </a-form-model-item>
      </a-form-model>
    </a-card>
  </div>
</template>


<script>
import { mapGetters } from "vuex";
import { message, Modal } from "ant-design-vue";
import store from "@/store";
import isMobile from "is-mobile";
import traditionlize from "@/utils/translate";
import SiderBar from "@/components/common/Siderbar";
import checkPermission from "@/utils/permission";
import {
  validUsername,
  validPassword,
  validNickname,
  validURL,
  validEmail
} from "@/utils/validate";
import { reg, login } from "@/api/user";

export default {
  name: "Account",
  data() {
    const _self = this;
    const validateUsername = (rule, value, callback) => {
      if (value === "" || !validUsername(value)) {
        callback(
          new Error(
            traditionlize("账号名必须在4-16位以内,只能使用英文以及数字")
          )
        );
      } else {
        callback();
      }
    };
    const validateNickname = (rule, value, callback) => {
      if (value === "" || !validNickname(value)) {
        callback(
          new Error(
            traditionlize(
              '昵称必须在2-12位以内,昵称只可含有"中英文","数字"以及,"_"(下划线),不能以下划线开头或结尾'
            )
          )
        );
      } else {
        callback();
      }
    };
    const validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(traditionlize("请输入密码")));
      } else if (!validPassword(value)) {
        callback(
          new Error(traditionlize("密码必须在4-16位以内,只能使用英文以及数字"))
        );
      } else {
        if (
          _self.userData.password !== "" &&
          _self._self.dialogStatus === "reg"
        ) {
          _self.$refs.dataForm.validateField("passwordConfirm");
        }
        callback();
      }
    };
    const validatePass2 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(traditionlize("请再次输入密码")));
      } else if (!validPassword(value)) {
        callback(
          new Error(traditionlize("密码必须在4-16位以内,只能使用英文以及数字"))
        );
      } else if (value !== _self.userData.password) {
        callback(new Error(traditionlize("两次输入密码不一致!")));
      } else {
        callback();
      }
    };
    const validateUrl = (rule, value, callback) => {
      if (value === "" || !validURL(value)) {
        callback(new Error(traditionlize("请输入有效的连接")));
      } else {
        callback();
      }
    };
    const validateEmail = (rule, value, callback) => {
      if (value === "" || !validEmail(value)) {
        callback(new Error(traditionlize("请输入有效的邮箱地址")));
      } else {
        callback();
      }
    };
    return {
      //collapsed: false,
      traditionlize,
      checkPermission,
      modalType: 0,
      loading: false,
      simpleReg: false,
      userData: {
        username: "",
        password: undefined,
        passwordConfirm: undefined,
        email: undefined,
        nickname: undefined
      },
      redirect_uri: undefined,
      reset: {},
      rules: {
        username: [
          {
            trigger: "change",
            require: true,
            validator: validateUsername
          }
        ],
        password: [
          {
            trigger: "change",
            require: true,
            validator: validatePass
          }
        ],
        passwordConfirm: [
          {
            trigger: "change",
            require: _self._self.dialogStatus === "reg" ? true : false,
            validator: validatePass2
          }
        ],
        email: [
          {
            trigger: "blur",
            require: _self._self.dialogStatus === "reg" ? true : false,
            validator: validateEmail
          }
        ],
        nickname: [
          {
            trigger: "blur",
            require: _self._self.dialogStatus === "reg" ? true : false,
            validator: validateNickname
          }
        ]
      },
      dialogStatus: "reg"
    };
  },
  computed: {
    ...mapGetters(["roles", "token"]),
    isMobile(){
      return isMobile();
    },
    logined() {
      const _self = this;
      return _self.token && Array.isArray(_self.roles) && _self.roles.length;
    },
    changeToAnotherText() {
      const _self = this;
      let text = "";
      if (_self.dialogStatus === "login") {
        text = "尚未注册帐号?请点击注册";
      } else if (_self.dialogStatus === "reg") {
        text = "已有账号?快来登录吧!";
      }
      return traditionlize(text);
    }
  },
  mounted() {
    const _self = this;
    _self.reset = Object.assign({}, _self.userData);
    _self.init();
  },
  methods: {
    init() {
      const _self = this;
      const { query } = _self.$route;
      if (query) {
        let { type } = query;
        _self.redirect_uri = query.redirect_uri;
        if (["login", "reg", "forget"].includes(type)) {
          _self.dialogStatus = type;
        }
      }
    },
    handleChangeToAnother() {
      const _self = this;
      _self.$refs.dataForm.resetFields();
      //保留动画
      if (_self.dialogStatus === "login") {
        _self.dialogStatus = "reg";
      } else if (_self.dialogStatus === "reg") {
        _self.dialogStatus = "login";
      }
    },
    handleChangeToForget() {
      const _self = this;
      _self.dialogStatus = "forget";
      _self.$nextTick(() => {
        _self.$refs.dataForm.resetFields();
      });
    },
    async handleOk() {
      const _self = this;
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          if (_self.dialogStatus === "login") {
            _self.login();
          } else if (_self.dialogStatus === "reg") {
            _self.reg();
          }
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    googleCheck() {
      this.googleChecking = true;
      this.recaptchaToken = undefined;
      this.$refs.google.check();
    },
    async login() {
      const _self = this;
      // if (!_self.recaptchaToken) {
      //   return;
      // }
      try {
        _self.loading = true;
        let data = Object.assign({}, _self.userData);
        await store.dispatch("user/login", data);
        _self.visible = false;
        _self.$refs.dataForm.resetFields();
        if(_self.redirect_uri){
          _self.$router.push({path:_self.redirect_uri})
        }else{
          _self.$router.push({path:"/novel"})
        }
      } catch (error) {
        console.log(error);
      }
      _self.loading = false;
    },
    async reg() {
      const _self = this;
      // if (!_self.recaptchaToken) {
      //   return;
      // }
      try {
        _self.loading = true;
        let data = Object.assign({}, _self.userData);
        data.recaptchaToken = _self.recaptchaToken;
        const resp = await reg(data);
        if (resp.code !== 20000) {
          _self.recaptchaToken = undefined;
          //_self.$refs.google.reset();
          message.error(`注册失败!\r\n原因: ${resp.message}`);
        } else {
          // _self.$message.warning(
          //   '成功注册,欢迎新用户来到本站,请先进行邮箱验证!'
          // );
          message.info("成功注册,欢迎新用户来到本站!");
          _self.$refs.dataForm.resetFields();
          _self.handleChangeToAnother();
          _self.userData.username = data.username;
        }
      } catch (error) {
        console.log(error);
      }
      _self.loading = false;
    },
    handleReset() {
      const _self = this;
      _self.$refs.dataForm.clearValidate();
      _self.userData = {};
      _self.userData = Object.assign({}, _self.reset);
    },
  },
  watch: {
    $route() {
      const _self = this;
      _self.init();
    }
  }
};
</script>
<style lang="less">
@media screen and (min-width: 1165px) {
  .account-panel /deep/ .ant-card-body {
    padding: 24px 48px;
  }
}
</style>
<style lang="less" scoped>
</style>