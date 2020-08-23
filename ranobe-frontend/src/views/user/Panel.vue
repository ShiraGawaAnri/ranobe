<template>
  <div id="user-panel">
    <a-card
      id="user-panel-card"
      :hoverable="true"
      :style="{width:!isMobile?'75%':'100%','max-width':'1300px',margin:'auto auto'}"
    >
      <template slot="title">
        <a-breadcrumb class="breadcrumb flat cus" style="margin: 16px 8px 8px 8px;" separator>
          <a-breadcrumb-item v-for="(item,index) in breadcrumb" :key="index">
            <a-dropdown>
              <a
                v-if="item.tab >= 0"
                href="javascript:;"
                @click="($event)=>changeTab(item.tab)"
                :class="{active:(tab === item.tab || (item.children && item.children.find(ch=>ch.tab===tab)))}"
              >
                {{traditionlize(`${(item.children && item.children.find(ch=>ch.tab===tab)) ? item.children.find(ch=>ch.tab===tab).text : item.text}`)}}
                <a-icon v-if="item.children" type="down" />
              </a>
              <a
                v-else
                href="javascript:;"
                :class="{active:(tab === item.tab || (item.children && item.children.find(ch=>ch.tab===tab)))}"
              >
                {{traditionlize(`${(item.children && item.children.find(ch=>ch.tab===tab)) ? item.children.find(ch=>ch.tab===tab).text : item.text}`)}}
                <a-icon v-if="item.children" type="down" />
              </a>
              <a-menu v-if="item.children" slot="overlay">
                <a-menu-item v-for="(it, index) in item.children" :key="index">
                  <a
                    href="javascript:;"
                    @click="($event)=>changeTab(it.tab)"
                  >{{traditionlize(it.text)}}</a>
                </a-menu-item>
              </a-menu>
            </a-dropdown>
          </a-breadcrumb-item>
        </a-breadcrumb>
      </template>
      <a-layout>
        <template v-if="tab === 10 || !tab">
          <a-layout style="height:fit-content;background-color:white;">
            <a-layout-content>
              <a-row type="flex">
                <!-- :span="screenWidth < 950 ? 24 : screenWidth / 1650 * 4" -->
                <a-col :md="24" :lg="8" :xl="6" :xxl="4">
                  <div
                    class="head-icon"
                    style="height:100%;width:100%;padding:24px 24px;margin:auto auto;"
                  >
                    <a-avatar :size="192" icon="user" :src="avatar" />
                  </div>
                </a-col>
                <a-col
                  :md="24"
                  :lg="15"
                  :span="screenWidth < 950 ? 24 : 15"
                  :offset="screenWidth < 950 ? 0 :1"
                >
                  <div class="info" style="padding:24px 0px;font-size:16px;">
                    <a-row type="flex" justify="space-between">
                      <!-- <a-col :span="12" class="info-col">
                      <span v-trans>账号:</span>
                      </a-col>-->
                      <a-col :span="24" class="info-col">
                        <span v-trans style="margin-right:8px;">昵称:</span>
                        <span v-if="edit === 'view'">{{nickname}}</span>
                        <a-input
                          v-else-if="edit === 'edit'"
                          style="margin-left:8px;width:50%;max-width:250px;font-weight:bold;"
                          v-model.trim="details.nickname"
                        ></a-input>
                      </a-col>
                      <!-- <a-col :span="24" class="info-col">
                      <span v-trans>签名:</span>
                      </a-col>-->
                      <a-divider />
                      <a-col :span="12" class="info-col">
                        <span v-trans style="margin-right:8px;">邮箱:</span>
                        <span>{{email}}</span>
                      </a-col>
                      <a-col :span="12" class="info-col">
                        <span v-trans style="margin-right:8px;">移动电话:</span>
                        <span>{{phone}}</span>
                      </a-col>
                      <a-divider />
                      <a-col :span="12" class="info-col">
                        <span v-trans style="margin-right:8px;">状态:</span>
                        <a-tag :color="statusObj.color">{{traditionlize(`${statusObj.label}`)}}</a-tag>
                      </a-col>
                      <a-col :span="12" class="info-col">
                        <span v-trans style="margin-right:8px;">身份:</span>
                        <span>{{traditionlize(roleText)}}</span>
                      </a-col>
                      <a-divider />
                      <a-col :span="12" class="info-col">
                        <span v-trans style="margin-right:8px;">注册日期:</span>
                        <span>{{createdTime}}</span>
                      </a-col>
                      <a-col :span="12" class="info-col">
                        <span v-trans style="margin-right:8px;">认证:</span>
                        <a-tag
                          :color="noValidate ? 'red' : 'green'"
                        >{{traditionlize(`${noValidate ? '未认证' : '已认证'}`)}}</a-tag>
                        <!-- 暂时以核实邮箱为认证 -->
                        <a-button
                          v-if="noValidate"
                          type="link"
                          size="small"
                          style="margin-left:10px;"
                          @click="($event)=>changeTab(22)"
                        >{{ traditionlize("前往认证") }}</a-button>
                      </a-col>
                    </a-row>
                  </div>
                </a-col>
              </a-row>
            </a-layout-content>
            <!-- <a-layout-sider>Sider</a-layout-sider> -->
          </a-layout>
          <a-layout-footer style="background-color:white;">
            <a-row type="flex" justify="space-around">
              <a-col :span="6">
                <a-button
                  v-if="edit==='view'"
                  @click="($event)=>changeEdit('edit')"
                  type="raise"
                >{{traditionlize('修改信息')}}</a-button>
                <a-button
                  v-if="edit==='edit'"
                  type="warning"
                  @click="updateInfo"
                >{{traditionlize('保存信息')}}</a-button>
              </a-col>
              <a-col :span="6" v-if="edit==='edit'">
                <a-button @click="($event)=> edit='view'">{{traditionlize('取消修改')}}</a-button>
              </a-col>
            </a-row>
          </a-layout-footer>
        </template>
        <template v-if="tab === 20">
          <div style="background-color:white;">
            <a-steps :current="currentStep">
              <a-step
                v-for="(item) in changePwdSteps"
                :key="item.index"
                :status="currentStep >= item.index ? 'finish' : 'wait'"
                :title="traditionlize(item.title)"
              >
                <a-icon
                  slot="icon"
                  :type="item.icon.type"
                  :theme="currentStep === item.index ? 'twoTone' : 'outlined'"
                  :two-tone-color="item.icon.color || '#eb2f96'"
                />
              </a-step>
            </a-steps>
            <a-list
              v-if="currentStep === 1"
              item-layout="horizontal"
              :data-source="verificationActions"
              style="margin-top:40px;"
            >
              <a-list-item slot="renderItem" slot-scope="item">
                <a-list-item-meta>
                  <span
                    slot="description"
                    style="line-height: 48px;"
                  >{{traditionlize(item.description)}}</span>
                  <a
                    slot="title"
                    href="javascript:;"
                    style="font-size:18px;"
                  >{{ traditionlize(item.title) }}</a>
                  <a-avatar
                    :size="96"
                    slot="avatar"
                    src="https://zos.alipayobjects.com/rmsportal/ODTLcjxAfvqbxHnVXCYX.png"
                  />
                </a-list-item-meta>
                <div v-if="item.action">
                  <a-button
                    v-if="!item.action.href"
                    type="link"
                    @click="($event)=> selectVerificationAction(item)"
                  >{{traditionlize(item.action.label)}}</a-button>
                  <a
                    v-if="item.action.href"
                    :href="item.action.href"
                    target="_blank"
                    style="margin-right:16px;"
                  >{{traditionlize(item.action.label)}}</a>
                </div>
              </a-list-item>
            </a-list>
            <div style="margin-top:40px;" v-if="currentStep === 2">
              <a-result id="v-code-result" :title="traditionlize('验证码可能稍有延迟,请在接受之后的指定时间内完成以下操作.')">
                <div
                  class="extra"
                  slot="extra"
                  :style="{margin:'40px auto 0 auto','text-align':'left',width:'50%'}"
                >
                  <a-row>
                    <a-col :span="24">
                      <a-form-model layout="inline" style="margin-bottom:20px;">
                        <a-form-model-item prop="code" :label="traditionlize('接收地址')">
                          <a-input
                            :disabled="true"
                            :value="verificationType === 1 ? _self.phone : _self.email"
                            style="margin-left:13px;width:160px;"
                          >
                            <a-icon
                              slot="prefix"
                              :type="verificationType === 1 ? 'mobile' : 'mail'"
                              style="color:rgba(0,0,0,.25)"
                            />
                          </a-input>
                        </a-form-model-item>
                        <a-form-model-item>
                          <a-button
                            type="primary"
                            @click="($event)=>verificationCodeSend({type:verificationType})"
                            :disabled="verificationSendLimit !== 0"
                            style="margin-left:12px;"
                          >{{traditionlize(verificationSendButtonText)}}</a-button>
                        </a-form-model-item>
                      </a-form-model>
                    </a-col>
                    <a-col :span="24">
                      <a-form-model
                        layout="inline"
                        ref="verificationForm"
                        :model="verificationData"
                        @submit="handleVerificationCodeCheck"
                        @submit.native.prevent
                        :rules="{ code: [{ message:  traditionlize('请输入验证码!'),trigger: 'change' }] }"
                      >
                        <a-form-model-item prop="code" :label="traditionlize('验证码确认')">
                          <a-input
                            v-model="verificationData.vcode"
                            placeholder="Verification Code"
                            style="width:160px;"
                          >
                            <a-icon slot="prefix" type="code" style="color:rgba(0,0,0,.25)" />
                          </a-input>
                        </a-form-model-item>
                        <a-form-model-item>
                          <a-button
                            type="primary"
                            html-type="submit"
                            :disabled="verificationData.vcode === '' || !verificationData.vcode"
                            style="margin-left:12px;"
                          >{{traditionlize('确认')}}</a-button>
                        </a-form-model-item>
                      </a-form-model>
                    </a-col>
                  </a-row>
                </div>
              </a-result>
            </div>
            <div style="margin-top:40px;" v-if="currentStep === 3">
              <a-form-model
                ref="changePwdForm"
                :model="changePwd"
                :rules="changePwdrules"
                :label-col="{span:  4}"
                :wrapper-col="{ span: 14 }"
              >
                <a-form-model-item :label="traditionlize('新的密码')" prop="password">
                  <a-input-password
                    style="margin-left:8px;width:50%;max-width:250px;font-weight:bold;"
                    v-model.trim="changePwd.password"
                  ></a-input-password>
                </a-form-model-item>
                <a-form-model-item :label="traditionlize('确认密码')" prop="passwordConfirm">
                  <a-input-password
                    style="margin-left:8px;width:50%;max-width:250px;font-weight:bold;"
                    v-model.trim="changePwd.passwordConfirm"
                  ></a-input-password>
                </a-form-model-item>
                <a-form-model-item :wrapper-col="{ span: 14, offset: 4 }">
                  <a-button type="primary" @click="onChangePwdSubmit">{{traditionlize('确认修改')}}</a-button>
                  <a-button
                    style="margin-left: 10px;"
                    @click="onChangePwdReset"
                  >{{traditionlize('重置')}}</a-button>
                </a-form-model-item>
              </a-form-model>
            </div>
            <div style="margin-top:40px;" v-if="currentStep === 4">
              <a-result
                status="success"
                :title="traditionlize('已经成功修改密码,您可以重新登录以尝试新的密码.')"
                :sub-title="traditionlize('数秒后将自动回到个人中心.')"
              ></a-result>
            </div>
          </div>
        </template>
        <template v-if="tab === 21 || tab === 22">
          <div style="background-color:white;">
            <template v-if="(noPhone && tab === 21) || (noEmail && tab === 22)">
              <a-result
                status="warning"
                :title="traditionlize(`尚未绑定 ${tab === 21 ? '移动电话' : '邮箱'} ,请先行绑定.`)"
              >
                <div
                  class="extra"
                  slot="extra"
                  style="margin:40px auto 0 auto;text-align:left;width:50%;"
                >
                  <a-row>
                    <a-col :span="24">
                      <a-form-model layout="inline" style="margin-bottom:20px;">
                        <a-form-model-item
                          prop="code"
                          :label="traditionlize(`${tab === 21 ? '移动电话' : '邮箱'}`)"
                        >
                          <a-input
                            v-if="tab === 21"
                            :disabled="sendvCodeSuccess"
                            v-model="bindMobile.code"
                            style="margin-left:13px;width:160px;"
                          >
                            <a-icon slot="prefix" :type="'mobile'" style="color:rgba(0,0,0,.25)" />
                          </a-input>
                          <a-input
                            v-else-if="tab === 22"
                            :disabled="sendvCodeSuccess"
                            v-model="bindMail.code"
                            style="margin-left:13px;width:160px;"
                          >
                            <a-icon slot="prefix" :type="'mail'" style="color:rgba(0,0,0,.25)" />
                          </a-input>
                        </a-form-model-item>
                        <a-form-model-item>
                          <a-button
                            type="primary"
                            @click="($event)=>verificationCodeSend(tab === 21 ? bindMobile : bindMail)"
                            :disabled="verificationSendLimit !== 0"
                            style="margin-left:12px;"
                          >{{traditionlize(verificationSendButtonText)}}</a-button>
                        </a-form-model-item>
                      </a-form-model>
                    </a-col>
                    <a-col :span="24">
                      <a-form-model
                        layout="inline"
                        ref="verificationForm"
                        :model="verificationData"
                        @submit="handleVerificationCodeCheckAndBind"
                        @submit.native.prevent
                        :rules="{ code: [{ message:  traditionlize('请输入验证码!'),trigger: 'change' }] }"
                      >
                        <a-form-model-item prop="code" :label="traditionlize('验证码确认')">
                          <a-input
                            v-model="verificationData.vcode"
                            placeholder="Verification Code"
                            style="width:160px;"
                          >
                            <a-icon slot="prefix" type="code" style="color:rgba(0,0,0,.25)" />
                          </a-input>
                        </a-form-model-item>
                        <a-form-model-item>
                          <a-button
                            type="primary"
                            html-type="submit"
                            :disabled="verificationData.vcode === '' || !verificationData.vcode"
                            style="margin-left:12px;"
                          >{{traditionlize('确认并绑定')}}</a-button>
                        </a-form-model-item>
                      </a-form-model>
                    </a-col>
                  </a-row>
                </div>
              </a-result>
            </template>
            <template v-else-if="tab === 21 || tab === 22">
              <a-result id="v-code-result" :title="traditionlize('验证码可能稍有延迟,请在接受之后的指定时间内完成以下操作.')">
                <div
                  class="extra"
                  slot="extra"
                  style="margin:40px auto 0 auto;text-align:left;width:50%;"
                >
                  <a-row>
                    <a-col :span="24">
                      <a-form-model layout="inline" style="margin-bottom:20px;">
                        <a-form-model-item
                          prop="code"
                          :label="traditionlize(`${tab === 21 ? '原移动电话':'原邮件地址'}`)"
                        >
                          <a-input
                            :disabled="true"
                            :value="tab === 21 ? _self.phone : _self.email"
                            style="width:160px;"
                          >
                            <a-icon
                              slot="prefix"
                              :type="tab === 21 ? 'mobile' : 'mail'"
                              style="color:rgba(0,0,0,.25)"
                            />
                          </a-input>
                        </a-form-model-item>
                        <a-form-model-item>
                          <a-button
                            type="primary"
                            @click="($event)=>verificationCodeSend({type:tab === 21 ? 1 : 2})"
                            :disabled="verificationSendLimit !== 0 || rebindData.rebindSubmit"
                            style="margin-left:12px;"
                          >{{traditionlize(verificationSendButtonText)}}</a-button>
                        </a-form-model-item>
                      </a-form-model>
                    </a-col>
                    <a-col :span="24">
                      <a-form-model
                        id="rebind-form"
                        ref="rebindForm"
                        :model="rebindData"
                        @submit="handelRebindSend"
                        @submit.native.prevent
                        :rules="{ vcode: [{ message:  traditionlize('请输入验证码!'),trigger: 'change' }] }"
                      >
                        <a-form-model-item prop="vcode" :label="traditionlize('验证码确认')">
                          <a-input
                            v-model="rebindData.vcode"
                            :disabled="rebindData.rebindSubmit"
                            placeholder="Verification Code"
                            style="width:160px;"
                          >
                            <a-icon slot="prefix" type="code" style="color:rgba(0,0,0,.25)" />
                          </a-input>
                          <a-button
                            v-if="!rebindData.rebindSubmit"
                            type="primary"
                            @click="handleRebindVerificationCodeCheck"
                            :disabled="rebindData.vcode === '' || !rebindData.vcode"
                            style="margin-left:28px;"
                          >{{traditionlize('确认认证码')}}</a-button>
                          <a-button
                            v-else-if="rebindData.rebindSubmit"
                            type="primary"
                            :disabled="true"
                            style="margin-left:28px;"
                          >{{traditionlize('已确认验证码')}}</a-button>
                        </a-form-model-item>
                        <a-form-model-item
                          prop="code"
                          :label="traditionlize(`${tab === 21 ? '新移动电话':'新邮件地址'}`)"
                        >
                          <a-input
                            v-model="rebindData.code"
                            :disabled="rebindData.rebindConfirmSubmit"
                            :placeholder="tab === 21 ? '移动电话号' : '邮件地址'"
                            style="width:160px;"
                          >
                            <a-icon
                              slot="prefix"
                              :type="tab === 21 ? 'mobile' : 'mail'"
                              style="color:rgba(0,0,0,.25)"
                            />
                          </a-input>
                          <a-button
                            v-if="!rebindData.rebindConfirmSubmit"
                            type="primary"
                            @click="handleRebindConfirmVerificationCodeCheck"
                            :disabled="rebindData.code === '' || !rebindData.code || !rebindData.rebindSubmit || verificationSendLimit !== 0"
                            style="margin-left:28px;"
                          >{{verificationRebindSendButtonText}}</a-button>
                          <a-button
                            v-else-if="rebindData.rebindConfirmSubmit"
                            type="primary"
                            :disabled="true"
                            style="margin-left:28px;"
                          >{{traditionlize('已确认新址')}}</a-button>
                        </a-form-model-item>
                        <a-form-model-item
                          v-if="rebindData.rebindConfirmSubmit"
                          prop="vconfrimcode"
                          :label="traditionlize('新址认证码')"
                        >
                          <a-input
                            v-model="rebindData.vconfrimcode"
                            placeholder="Verification Code"
                            style="width:160px;"
                          >
                            <a-icon slot="prefix" type="code" style="color:rgba(0,0,0,.25)" />
                          </a-input>
                          <a-button
                            type="primary"
                            @click="handleVerificationCodeCheckAndReBind"
                            :disabled="rebindData.vconfrimcode === '' || !rebindData.vconfrimcode || !rebindData.rebindConfirmSubmit"
                            style="margin-left:28px;"
                          >{{traditionlize('确认并修改')}}</a-button>
                        </a-form-model-item>
                      </a-form-model>
                    </a-col>
                  </a-row>
                </div>
              </a-result>
              <div style="margin-top:40px;" v-if="currentStep === 4">
                <a-result
                  status="success"
                  :title="traditionlize('已经成功修改,您可以回到个人中心后刷新页面获取最新信息.')"
                  :sub-title="traditionlize('数秒后将自动回到个人中心.')"
                ></a-result>
              </div>
            </template>
          </div>
        </template>
        <template v-if="tab === 80">
          <a-table
            :columns="reportColumns"
            :data-source="reportData"
            :pagination="reportPagination"
            rowKey="id"
          >
            <a slot="id" slot-scope="text">{{ text }}</a>
            <template slot="reportReason" slot-scope="item">
              <span>{{ traditionlize(item.reportReason) }}</span>
            </template>
            <template slot="reportMes" slot-scope="item">
              <span>{{ traditionlize(item.reportMes) }}</span>
            </template>
            <template slot="replyMes" slot-scope="item">
              <span>{{ traditionlize(item.replyMes) }}</span>
            </template>
            <template slot="status" slot-scope="item">
              <a-tag
                :color="reportStatusTags.find(each=>each.value === item.status) ? reportStatusTags.find(each=>each.value === item.status).color : 'purple'"
              >{{ traditionlize(reportStatusTags.find(each=>each.value === item.status) ? reportStatusTags.find(each=>each.value === item.status).label : '状况外')}}</a-tag>
            </template>
            <template
              slot="updatedTime"
              slot-scope="item"
            >{{ traditionlize(moment(item.updatedTime).fromNow())}}</template>
          </a-table>
        </template>
      </a-layout>
    </a-card>
  </div>
</template>

<script>
import { mapGetters, mapMutations } from "vuex";
import moment from "moment";
import { message, Modal } from "ant-design-vue";
import store from "@/store";
import isMobile from "is-mobile";
import traditionlize from "@/utils/translate";
import checkPermission from "@/utils/permission";
import { commonOptions } from "@/components/mixins/common";
import {
  getDetails,
  updateInfo,
  bindMail,
  bindMobile,
  rebindMail,
  rebindMobile,
  changePassword
} from "@/api/user";
import {
  validUsername,
  validPassword,
  validNickname,
  validURL,
  validEmail
} from "@/utils/validate";
import { getReportList } from "@/api/novel";
import {
  vCodeCheck,
  vCodeCheckByTypeAndCode,
  vCodeSendByType,
  vCodeSendToEmailRequest
} from "@/api/verification";

const breadcrumb = [
  {
    text: "个人面板",
    children: [
      {
        tab: 10,
        text: "主页"
      }
    ]
  },
  {
    text: "修改信息",
    children: [
      {
        tab: 20,
        text: "修改密码"
      },
      {
        tab: 21,
        text: "修改移动电话"
      },
      {
        tab: 22,
        text: "修改邮箱"
      }
    ]
  },
  {
    tab: 80,
    text: "反馈记录"
  },
  {
    text: "日志记录",
    children: [
      {
        tab: 90,
        text: "登录日志"
      },
      {
        tab: 91,
        text: "操作日志"
      }
    ]
  }
];

const changePwdSteps = [
  {
    index: 1,
    title: "选择认证方式",
    icon: {
      type: "question-circle"
    }
  },
  {
    index: 2,
    title: "确认验证码",
    icon: {
      type: "code"
    }
  },
  {
    index: 3,
    title: "修改密码",
    icon: {
      type: "edit"
    }
  },
  {
    index: 4,
    title: "完成修改",
    icon: {
      type: "check-circle",
      color: "#52c41a"
    }
  }
];

const reportStatusTags = [
  {
    value: 0,
    label: "未处理",
    color: "pink"
  },
  {
    value: 1,
    label: "处理中",
    color: "cyan"
  },
  {
    value: 2,
    label: "已回复",
    color: "orange"
  },
  {
    value: 10,
    label: "已关闭(来自反馈人)",
    color: "red"
  },
  {
    value: 20,
    label: "已关闭(来自客服)",
    color: "red"
  }
];

export default {
  name: "UserPanel",
  mixins: [commonOptions],

  data() {
    const _self = this;
    const validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error(traditionlize("请输入密码")));
      } else if (!validPassword(value)) {
        callback(
          new Error(traditionlize("密码必须在4-16位以内,只能使用英文以及数字"))
        );
      } else {
        if (_self.changePwd.password !== "") {
          _self.$refs.changePwdForm.validateField("passwordConfirm");
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
      } else if (value !== _self.changePwd.password) {
        callback(new Error(traditionlize("两次输入密码不一致!")));
      } else {
        callback();
      }
    };
    return {
      tab: 10,
      edit: "view",
      screenWidth: _self.isMobile ? 300 : 1300,
      listenerTimer: false,
      details: {
        nickname: "",
        sign: ""
      },
      changePwd: {
        password: undefined,
        passwordConfirm: undefined
      },
      changePwdrules: {
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
            require: true,
            validator: validatePass2
          }
        ]
      },
      sendvCodeSuccess: false,
      verificationData: {
        vcode: undefined,
        code: undefined
      },
      rebindData: {
        vcode: undefined,
        vconfrimcode: undefined,
        code: undefined,
        type: undefined,
        rebindSubmit: false,
        rebindConfirmSubmit: false
      },
      noPhone: false,
      noEmail: false,
      noValidate: false,
      email: undefined,
      phone: undefined,
      createdTime: undefined,
      validate: undefined,
      currentStep: 1,
      //1 phone,2 mail
      verificationType: undefined,
      verificationSendLimit: 0,
      bindMobile: {
        type: 1,
        code: undefined
      },
      bindMail: {
        type: 2,
        code: undefined
      },
      backToTopTab: -1,
      reportData: [],
      reportColumns: [
        {
          title: "反馈ID",
          dataIndex: "id",
          key: "id",
          scopedSlots: { customRender: "id" }
        },
        {
          title: "反馈理由",
          key: "reportReason",
          scopedSlots: { customRender: "reportReason" },
          ellipsis: true
        },
        {
          title: "反馈代号",
          dataIndex: "reportReasonNum"
        },
        {
          title: "反馈详细",
          key: "reportMes",
          scopedSlots: { customRender: "reportMes" },
          ellipsis: true
        },
        {
          title: "反馈回覆",
          key: "replyMes",
          scopedSlots: { customRender: "replyMes" },
          ellipsis: true
        },
        {
          title: "状态",
          key: "status",
          scopedSlots: { customRender: "status" }
        },
        {
          title: "反馈日期",
          dataIndex: "createdTime"
        },
        {
          title: "状态更新日期",
          key: "updatedTime",
          scopedSlots: { customRender: "updatedTime" }
        }
      ],
      reportPagination: {
        defaultPageSize: 10,
        showTotal: total =>
          `Total ${_self.reportPage.totalPages} pages, ${total} results.`,
        showSizeChanger: false,
        pageSizeOptions: ["10", "20", "50"],
        size: "small",
        onChange: _self.reportPageChange,
        total: 0
      },
      reportQuery: {
        currentPage: 1,
        pageSize: 10
      },
      reportPage: {
        total: 0,
        totalPages: 0
      },
      breadcrumb,
      changePwdSteps,
      reportStatusTags,
      moment,
      traditionlize,
      checkPermission
    };
  },
  computed: {
    ...mapGetters([
      "settings",
      "avatar",
      "roles",
      "username",
      "nickname",
      "status",
      "token"
    ]),
    isMobile() {
      return isMobile();
    },
    statusObj() {
      const _self = this;
      const options = [
        {
          value: 0,
          label: "封禁",
          color: "red"
        },
        {
          value: 1,
          label: "正常",
          color: "green"
        }
      ];
      return (
        options.find(op => op.value === Number(_self.status)) || {
          value: _self.status,
          label: "未知",
          color: "red"
        }
      );
    },
    roleText() {
      const _self = this;
      const options = _self.roleOptions;
      const roles = _self.roles;
      let indexs = [];
      roles.forEach(role => {
        let index = options.findIndex(op => op.value === role);
        if (index > -1) {
          indexs.push(index);
        }
      });
      let indexVal = Math.min(...indexs);
      return indexVal >= 0 ? options[indexVal].label : "未定义身份";
    },
    verificationActions() {
      const _self = this;
      let arr = [];
      arr.push(
        {
          id: 1,
          title: "通过移动电话验证",
          description:
            "如果您的绑定移动电话可用,则可通过绑定的移动电话发送验证码修改",
          action: {
            label: !_self.noPhone ? "<  立即验证" : "<  绑定移动电话",
            useable: !_self.noPhone,
            value: 1,
            redirect_tab: 21
          }
        },
        {
          id: 2,
          title: "通过邮箱验证",
          description: "如果您的绑定邮箱可用,则可通过绑定的邮箱发送验证码修改",
          action: {
            label: !_self.noEmail ? "<  立即验证" : "<  绑定邮箱",
            useable: !_self.noEmail,
            value: 2,
            redirect_tab: 22
          }
        },
        {
          id: 3,
          title: "以上方式都不能用",
          description: "如果您的原手机号,邮箱已经无法使用,请联系我们",
          action: {
            label: "联系客服",
            useable: true,
            href: "http://www.baidu.com"
          }
        }
      );
      return arr;
    },
    verificationSendButtonText() {
      const _self = this;
      return _self.verificationSendLimit === 0
        ? "发送验证码"
        : `再次发送(${_self.verificationSendLimit}s)`;
    },
    verificationRebindSendButtonText() {
      const _self = this;
      return _self.verificationSendLimit === 0
        ? "发送验证码"
        : `请稍后(${_self.verificationSendLimit}s)`;
    }
  },
  created() {
    const _self = this;
    //_self.init();
  },
  mounted() {
    const _self = this;
    _self.init();
    window.onresize = () => {
      return (() => {
        window.screenWidth = document.body.clientWidth;
        _self.screenWidth = window.screenWidth;
      })();
    };
  },
  methods: {
    init() {
      const _self = this;
      _self.getDetails();
      _self.getReportList();
    },
    async getDetails() {
      const _self = this;
      try {
        const {
          data: { email, phone, validate, createdTime }
        } = await getDetails();
        _self.email = email
          ? email
          : ((_self.noEmail = true), traditionlize("未绑定"));
        _self.phone = phone
          ? phone
          : ((_self.noPhone = true), traditionlize("未绑定"));
        _self.validate = validate
          ? validate
          : ((_self.noValidate = true), traditionlize("未认证"));
        _self.createdTime = createdTime;
        _self.details.nickname = _self.nickname;
      } catch (error) {
        console.log(error);
      }
    },
    async updateInfo() {
      const _self = this;
      try {
        let postData = Object.assign({}, _self.details);
        const resp = await updateInfo(postData);
        await store.dispatch("user/getInfo");
        _self.details.nickname = _self.nickname;
        _self.changeEdit("view");

        message.success(traditionlize("修改成功!"));
      } catch (error) {
        console.log(error);
      }
    },
    async getReportList() {
      const _self = this;
      try {
        let query = {
          page: _self.reportQuery.currentPage - 1,
          size: _self.reportQuery.pageSize
        };
        const { data } = await getReportList(query);
        if (data) {
          let content = data.content || [];
          _self.reportData = content;
          _self.totalPages = data.totalPages;
          _self.reportPagination.total = data.totalElements;
        }
      } catch (error) {
        console.log(error);
      }
    },
    changeEdit(status) {
      const _self = this;
      _self.edit = status || "view";
    },
    changeTab(tab) {
      const _self = this;
      _self.resetAll();
      _self.tab = tab;
    },
    resetAll() {
      const _self = this;
      _self.currentStep = 1;
      _self.sendvCodeSuccess = false;
      if (_self.backToTopTab !== -1) {
        clearTimeout(_self.backToTopTab);
      }
      for (let prop in _self.changePwd) {
        _self.changePwd[prop] = undefined;
      }
      for (let prop in _self.verificationData) {
        _self.verificationData[prop] = undefined;
      }
      for (let prop in _self.rebindData) {
        if (typeof _self.rebindData[prop] === "boolean") {
          _self.rebindData[prop] = false;
        } else {
          _self.rebindData[prop] = undefined;
        }
      }
    },
    selectVerificationAction(item) {
      const _self = this;
      !item.action.useable
        ? _self.changeTab(item.action.redirect_tab)
        : _self.doVerificationAction(item.action.value);
    },
    doVerificationAction(val) {
      const _self = this;
      _self.verificationType = val;
      _self.currentStep = 2;
    },
    //确认验证码
    handleVerificationCodeCheck() {
      const _self = this;
      _self.$refs.verificationForm.validate(async valid => {
        if (valid) {
          let postData = Object.assign(
            _self.verificationData,
            _self.tab === 21 ? _self.bindMobile : _self.bindMail
          );
          try {
            //await vCodeCheck(postData);
            await vCodeCheckByTypeAndCode(postData);

            console.log("check success:", JSON.stringify(postData));
            _self.currentStep = 3;
          } catch (error) {
            console.log(error);
            console.log("check failed:", JSON.stringify(postData));
          }
        } else {
          console.log("no check");
        }
      });
    },
    //确认验证码并绑定
    handleVerificationCodeCheckAndBind() {
      const _self = this;
      _self.$refs.verificationForm.validate(async valid => {
        if (valid) {
          let postData = Object.assign(
            _self.verificationData,
            _self.tab === 21 ? _self.bindMobile : _self.bindMail
          );
          try {
            //await vCodeCheck(postData);
            if (_self.tab === 21) {
              const { data } = await bindMobile(postData);
              console.log(data);
            } else {
              const { data } = await bindMail(postData);
              await store.dispatch("user/getInfo");
              _self.getDetails();
              message.info("成功绑定,如资料未更新,请刷新网页或重新登录即可!");
              _self.changeTab(10);
            }
            console.log("check success:", JSON.stringify(postData));
            _self.currentStep = 3;
          } catch (error) {
            console.log(error);
            console.log("check failed:", JSON.stringify(postData));
          }
        } else {
          console.log("no check");
        }
      });
    },
    //确认验证码 用于修改重绑时确认认证码(旧地址)
    handleRebindVerificationCodeCheck() {
      const _self = this;
      _self.$refs.rebindForm.validate(async valid => {
        if (valid) {
          let postData = Object.assign({}, _self.rebindData);
          postData.type = _self.tab === 21 ? 1 : 2;
          try {
            await vCodeCheckByTypeAndCode(postData);
            _self.rebindData.rebindSubmit = true;
            console.log("check success:", JSON.stringify(postData));
          } catch (error) {
            console.log(error);
            console.log("check failed:", JSON.stringify(postData));
          }
          console.log("check :", JSON.stringify(_self.rebindData));
        } else {
          console.log("no check");
        }
      });
    },
    //发送验证码请求 用于修改重绑(新地址)
    handleRebindConfirmVerificationCodeCheck() {
      const _self = this;
      _self.$refs.rebindForm.validate(async valid => {
        let postData = Object.assign({}, _self.rebindData);
        if (valid) {
          try {
            if (_self.tab === 21) {
              console.log("Send Phone");
            } else {
              const { data } = await vCodeSendToEmailRequest(postData);
              console.log(data);
              _self.rebindData.vconfrimcode = data;
            }
            _self.rebindData.rebindConfirmSubmit = true;
            console.log("check :", JSON.stringify(_self.rebindData));
          } catch (error) {
            if (_self.verificationSendLimit === 0) {
              _self.verificationSendLimit = 5;
              let id = setInterval(() => {
                _self.verificationSendLimit--;
                if (_self.verificationSendLimit <= 0) {
                  clearInterval(id);
                }
              }, 1000);
            }
          }
        } else {
          console.log("no check");
        }
      });
    },
    //确认验证码并绑定(新地址)
    handleVerificationCodeCheckAndReBind() {
      const _self = this;
      _self.$refs.rebindForm.validate(async valid => {
        if (valid) {
          let postData = Object.assign({}, _self.rebindData);
          try {
            //await vCodeCheck(postData);
            if (_self.tab === 21) {
              const { data } = await rebindMobile(postData);
              console.log(data);
            } else {
              const { data } = await rebindMail(postData);
              _self.getDetails();
              await store.dispatch("user/getInfo");
              console.log(data);
              message.info("成功绑定,如资料未更新,请刷新网页或重新登录即可!");
              _self.changeTab(10);
            }
            console.log("check success:", JSON.stringify(postData));
          } catch (error) {
            console.log(error);
            console.log("check failed:", JSON.stringify(postData));
          }
        } else {
          console.log("no check");
        }
      });
    },
    //发送验证码请求
    async verificationCodeSend(postData) {
      const _self = this;
      try {
        if (postData.code === undefined) {
          const { data } = await vCodeSendByType(postData);
          console.log(data);
          _self.rebindData.vcode = data;
        } else {
          if (postData.type == 1) {
            console.log("Send Phone");
          } else {
            const { data } = await vCodeSendToEmailRequest(postData);
            console.log(data);
            _self.verificationData.vcode = data;
          }
        }
        _self.sendvCodeSuccess = true;
        if (_self.verificationSendLimit === 0) {
          _self.verificationSendLimit = 60;
          let id = setInterval(() => {
            _self.verificationSendLimit--;
            if (_self.verificationSendLimit <= 0) {
              clearInterval(id);
            }
          }, 1000);
        }
      } catch (error) {
        console.log(error);
        if (_self.verificationSendLimit === 0) {
          _self.verificationSendLimit = 5;
          let id = setInterval(() => {
            _self.verificationSendLimit--;
            if (_self.verificationSendLimit <= 0) {
              clearInterval(id);
            }
          }, 1000);
        }
      }
    },
    async onChangePwdSubmit() {
      const _self = this;
      _self.$refs.changePwdForm.validate(async valid => {
        let postData = Object.assign({}, _self.changePwd);
        postData.vcode = _self.verificationData.vcode;
        if (valid) {
          try {
            await changePassword(postData);
            console.log("change :", JSON.stringify(_self.changePwd));
            _self.currentStep = 4;
            _self.backToTopTab = setTimeout(() => {
              _self.changeTab(10);
            }, 5000);
          } catch (error) {
            console.log(error);
          }
          //TODO:send code
        } else {
          console.log("no check");
        }
      });
    },
    onChangePwdReset() {
      const _self = this;
      _self.$refs.changePwdForm.resetFields();
      _self.$refs.changePwdForm.clearValidate();
    },
    handelRebindSend() {},
    reportPageChange() {
      const _self = this;
      _self.getReportList();
    }
  },
  watch: {
    screenWidth(val) {
      const _self = this;
      if (_self.listenerTimer) {
        _self.screenWidth = val;
        _self.timer = true;
        setTimeout(() => {
          _self.timer = false;
        }, 400);
      }
    }
  }
};
</script>

<style lang="less">
#user-panel-card {
  & /deep/ .ant-card-head-title {
    padding: 0 0;
  }

  & /deep/ .ant-card-head {
    padding: 0 8px;
  }
}
#rebind-form {
  & /deep/ .ant-form-item-label {
    text-align: left;
  }
}
</style>
<style lang="less" scoped>
@import "~@/styles/breadcrumb-cus1.less";

.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  &.one-line {
    -webkit-line-clamp: 1;
  }
}
.info-col {
  padding: 0px 0px;
}
@media screen and (max-width: 1200px) {
  .extra {
    width: 75% !important;
  }
}
@media screen and (max-width: 1600px) {
  #user-panel-card {
    width: 100% !important;
  }
}
</style>