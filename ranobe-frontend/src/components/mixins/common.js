export const commonOptions = {
  data() {
    return {
      roleOptions: [{
          value: "ROLE_ROOT",
          label: "超级管理员"
        },
        {
          value: "ROLE_ADMIN",
          label: "管理员"
        },
        {
          value: "ROLE_GLODEN_USER",
          label: "黄金用户"
        },
        {
          value: "ROLE_SILVER_USER",
          label: "白银用户"
        },
        {
          value: "ROLE_BROZON_USER",
          label: "青铜用户"
        },
        {
          value: "ROLE_USER",
          label: "临时用户"
        },
      ],
      colorOptions: [{
          value: 0,
          label: "白色",
          color: "white",
        },
        {
          value: 1,
          label: "爱丽丝蓝",
          color: "aliceblue",
        },
        {
          value: 2,
          label: "泛黄色",
          color: "#fff0d2",
        },
        {
          value: 3,
          label: "浅灰色",
          color: "#f0f2f5",
        },
        {
          value: 4,
          label: "黑色",
          color: "black",
        }
      ],
      fontColorOptions: [{
          value: 0,
          label: "白色",
          color: "white",
        },
        {
          value: 1,
          label: "黑色",
          color: "rgba(0, 0, 0, 0.65)",
        }
      ],
      reportOptions: [{
          id: 1000,
          text: "涉及政治时事"
        },
        {
          id: 1001,
          text: "违法违禁"
        },
        {
          id: 1002,
          text: "色情低俗"
        },
        {
          id: 1003,
          text: "赌博诈骗"
        },
        {
          id: 1004,
          text: "违法违禁"
        },
        {
          id: 1005,
          text: "过激血腥暴力"
        },
        {
          id: 1006,
          text: "人身攻击"
        },
        {
          id: 1007,
          text: "内容重复投稿"
        },
        {
          id: 1008,
          text: "不良封面或标题"
        },
        {
          id: 1009,
          text: "转载/原创注明等错误"
        },
        {
          id: 1010,
          text: "引战"
        },
        {
          id: 1011,
          text: "其他问题"
        }
      ]
    }
  }
}