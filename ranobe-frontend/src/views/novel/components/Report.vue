<template>
  <a-modal v-model="reportModalVisible" on-ok="handleOk"  @cancel="handleCancel">
    <a-form-model ref="reportForm" :model="report" :rules="reportRules">
      <a-form-model-item prop="reportReasonNum" :label="traditionlize('请必须选择其中一个恰当的理由')">
        <a-radio-group v-model="report.reportReasonNum">
          <a-row type="flex" justify="space-around">
            <a-col
              :span="12"
              v-for="(option,index) in reportOptions"
              :key="index"
              style="padding:8px 0px;"
            >
              <a-radio style="margin-left:8px;" :value="option.id">{{option.text}}</a-radio>
            </a-col>
          </a-row>
        </a-radio-group>
      </a-form-model-item>
      <a-form-model-item prop="reportMes" :label="traditionlize('请补充详细内容以协助管理员排查')">
        <a-input
          type="textarea"
          v-model="report.reportMes"
          :placeholder="traditionlize('最多可以输入250字')"
          style="min-height:100px;max-height:300px;font-size:16px;line-height:24px;letter-spacing:1px;"
        />
      </a-form-model-item>
    </a-form-model>
    <template slot="footer">
      <a-button key="back" @click="handleCancel">{{traditionlize('取消')}}</a-button>
      <a-button
        key="submit"
        type="primary"
        :loading="reportedLoading"
        @click="sendReport"
      >{{traditionlize('发送反馈')}}</a-button>
    </template>
  </a-modal>
</template>

<script>
import store from "@/store";
import moment from "moment";
import { mapGetters } from "vuex";
import { message } from "ant-design-vue";
import { addReport } from "@/api/novel";
import {
  parseTime,
  toThousandNumber,
  isValNullUndefined,
  removeClass
} from "@/utils";
import traditionlize from "@/utils/translate";
import { commonOptions } from "@/components/mixins/common";
export default {
  name: "ReporModal",
  mixins: [commonOptions],
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    postReportData: {
      type: Object,
      require: true
    }
  },
  data() {
    return {
      report: {
        reportUrl: undefined,
        reportData: undefined,
        reportReason: undefined,
        reportReasonNum: undefined
      },
      reportRules: {
        reportReasonNum: [
          {
            required: true,
            message: traditionlize("请选择以上的一个理由"),
            trigger: "blur"
          }
        ],
        reportMes: [
          {
            required: true,
            min: 5,
            max: 250,
            message: traditionlize("反馈细节必须在5-250字之间"),
            trigger: "change"
          }
        ]
      },
      reportedLoading: false,
      traditionlize
    };
  },
  computed: {
    ...mapGetters(["settings", "token", "roles"]),
    logined() {
      const _self = this;
      return _self.token && Array.isArray(_self.roles) && _self.roles.length;
    },
    reportModalVisible: {
      get() {
        const _self = this;
        return _self.visible;
      },
      set(val) {}
    }
  },
  methods: {
    async sendReport(postData) {
      const _self = this;
      _self.reportedLoading = true;
      try {
        await _self.$refs.reportForm.validate(async valid => {
          if (valid) {
            let postData = Object.assign({}, _self.report);
            postData.reportData = JSON.stringify(
              _self.postReportData.reportData
            );
            postData.reportUrl = _self.$route.fullPath;
            let reason = _self.reportOptions.find(
              each => each.id === postData.reportReasonNum
            );
            postData.reportReason = reason ? reason.text : "";
            try {
              const { data } = await addReport(postData);
              message.success(
                traditionlize(
                  "成功发送反馈!感谢对我们的支持与理解,您的反馈我们会尽快回覆,请耐心等待!"
                )
              );
              _self.report = {};
              _self.handleCancel();
            } catch (error) {
              console.log(error);
            }
          }
        });
      } catch (error) {
        throw new Error(error);
      } finally {
        _self.reportedLoading = false;
      }
    },
    handleCancel() {
      const _self = this;
      _self.$emit("handleCancel");
    }
  }
};
</script>