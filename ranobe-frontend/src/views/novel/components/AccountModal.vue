<template>
  <a-modal v-model="accountModalVisible" on-ok="handleOk" @cancel="handleCancel">
    <template slot="title">
      <a-row type="flex" justify="start">
        <a-col :span="8">
          <a-button
            v-if="dialogStatus !== 'forget'"
            type="link"
            @click="handleChangeToForget"
            style="font-size:16px;"
          >{{traditionlize('忘记账号密码?')}}</a-button>
        </a-col>
        <a-col :offset="4" :span="6" v-if="dialogStatus === 'reg'">
          <a-checkbox style="margin-top:6px;" v-model="simpleReg">{{traditionlize('简易注册') }}</a-checkbox>
        </a-col>
      </a-row>
    </template>
    <template slot="footer">
      <a-button
        v-if="['login','reg'].includes(dialogStatus)"
        type="link"
        @click="handleChangeToAnother"
        style="position: absolute;left: 5px;font-size:12px;"
        size="small"
      >{{changeToAnotherText}}</a-button>
      <a-button key="back" @click="handleCancel">{{traditionlize('返回')}}</a-button>
      <a-button
        key="submit"
        type="primary"
        :loading="loading"
        @click="handleOk"
      >{{traditionlize('提交')}}</a-button>
    </template>
    <a-form-model ref="accountModalForm" :rules="rules" :model="userData">
      <a-form-model-item
        :label="traditionlize('账号名')"
        prop="username"
        style="width:75%;"
        v-if="!simpleRegShow"
      >
        <a-input v-model.trim="userData.username" :placeholder="traditionlize('请输入账号名')" />
      </a-form-model-item>
      <a-form-model-item
        v-if="['login','reg'].includes(dialogStatus) && !simpleRegShow"
        :label="traditionlize('密码')"
        prop="password"
        style="width:75%;"
      >
        <a-input
          v-model.trim="userData.password"
          type="password"
          :placeholder="traditionlize('请输入密码')"
        />
      </a-form-model-item>
      <a-form-model-item
        v-if="dialogStatus === 'reg'  && !simpleRegShow"
        :label="traditionlize('确认密码')"
        prop="passwordConfirm"
        style="width:75%;"
      >
        <a-input
          v-model.trim="userData.passwordConfirm"
          type="password"
          :placeholder="traditionlize('请重复确认您的密码')"
        />
      </a-form-model-item>
      <a-form-model-item
        v-if="['reg','forget'].includes(dialogStatus) && !simpleRegShow"
        :label="traditionlize('验证邮箱')"
        prop="email"
        style="width:75%;"
      >
        <a-input v-model.trim="userData.email" :placeholder="traditionlize('请输入有效的邮箱以用于验证')" />
      </a-form-model-item>
      <a-form-model-item
        v-if="dialogStatus === 'reg'"
        :label="traditionlize('昵称')"
        prop="nickname"
        style="width:75%;"
      >
        <a-input v-model.trim="userData.nickname" :placeholder="traditionlize('仅支持中英文和_')" />
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>

<script>
import { mapGetters } from "vuex";
import { message, notification, Modal } from "ant-design-vue";
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
import { getToken, setToken, removeToken } from "@/utils/auth";
import { reg, regSimple, login } from "@/api/user";
import { vCodeCheck,vCodeSendToEmailRequest } from "@/api/verification";

message.config({
  top: `50px`,
  duration: 5,
  maxCount: 3
});

export default {
  name: "AccountModal",
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    status: {
      type: String,
      default() {
        return undefined;
      }
    }
  },
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
          _self.$refs.accountModalForm.validateField("passwordConfirm");
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
      simpleReg: true,
      userData: {
        username: "",
        password: undefined,
        passwordConfirm: undefined,
        email: undefined,
        nickname: undefined
      },
      rules: {
        username: [
          {
            trigger: "change",
            require: !_self._self.simpleRegShow,
            validator: validateUsername
          }
        ],
        password: [
          {
            trigger: "change",
            require: !_self._self.simpleRegShow,
            validator: validatePass
          }
        ],
        passwordConfirm: [
          {
            trigger: "change",
            require:
              _self._self.dialogStatus === "reg" && !_self._self.simpleRegShow,
            validator: validatePass2
          }
        ],
        email: [
          {
            trigger: "blur",
            require:
              _self._self.dialogStatus === "reg" && !_self._self.simpleRegShow,
            validator: validateEmail
          }
        ],
        nickname: [
          {
            trigger: "blur",
            require: _self._self.dialogStatus === "reg",
            validator: validateNickname
          }
        ]
      },
      dialogStatus: "reg"
    };
  },
  created() {
    const _self = this;
    if (_self.status) {
      _self.dialogStatus = _self.status;
    }
  },
  computed: {
    ...mapGetters(["roles", "token"]),
    isMobile() {
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
    },
    accountModalVisible: {
      get() {
        const _self = this;
        return _self.visible;
      },
      set(val) {}
    },
    simpleRegShow() {
      const _self = this;
      return _self.dialogStatus === "reg" && _self.simpleReg;
    }
  },
  methods: {
    handleChangeToAnother() {
      const _self = this;
      _self.handleCancel();
      //保留动画
      setTimeout(() => {
        if (_self.dialogStatus === "login") {
          _self.dialogStatus = "reg";
        } else if (_self.dialogStatus === "reg") {
          _self.dialogStatus = "login";
        }
        _self.handleReopen();
      }, 250);
    },
    handleChangeToForget() {
      const _self = this;
      _self.handleReopen();
      _self.dialogStatus = "forget";
      _self.$nextTick(() => {
        _self.$refs.accountModalForm.resetFields();
      });
    },
    async handleOk() {
      const _self = this;
      this.$refs.accountModalForm.validate(valid => {
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
    async reg() {
      const _self = this;
      // if (!_self.recaptchaToken) {
      //   return;
      // }
      try {
        _self.loading = true;
        let data = Object.assign({}, _self.userData);
        data.recaptchaToken = _self.recaptchaToken;
        let resp;
        if (_self.simpleReg) {
          resp = await regSimple(data);
          if (resp.code !== 20000) {
            _self.recaptchaToken = undefined;
            notification.warning({
              message: traditionlize("系统消息"),
              description: traditionlize(`注册失败!\r\n原因: ${resp.message}`)
            });
          } else {
            let token = resp.data;
            setToken(token, 3650);
            await store.dispatch("user/setToken", token);
            await store.dispatch("user/getInfo");
            notification.warning({
              message: traditionlize("系统消息"),
              description: traditionlize(
                "简易注册用户,可在个人中心验证邮箱等资料转为正式用户.\r\n简易注册用户此浏览器或此浏览器帐户有效,清空Cookies或缓存后将无法再登入此帐户,请务必注意!"
              ),
              duration: null
            });
            _self.handleCancel();
          }
        } else {
          resp = await reg(data);
          if (resp.code !== 20000) {
            _self.recaptchaToken = undefined;
            message.error(`注册失败!\r\n原因: ${resp.message}`);
          } else {
            message.info("成功注册,欢迎新用户来到本站!");
            _self.handleCancel();
          }
        }
      } catch (error) {
        console.log(error);
      }
      _self.loading = false;
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
        _self.handleCancel();
      } catch (error) {
        console.log(error);
      }
      _self.loading = false;
    },
    handleReopen() {
      const _self = this;
      //_self.$refs.accountModalForm.clearValidate();
      _self.$emit("handleAccountModal", true);
    },
    handleCancel() {
      const _self = this;
      _self.$refs.accountModalForm.clearValidate();
      _self.$emit("handleAccountModal", false);
      if (_self.dialogStatus === "forget") {
        _self.dialogStatus = "reg";
      }
    }
  }
};
</script>

<style lang="less">
</style>