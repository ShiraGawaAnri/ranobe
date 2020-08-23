import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import "@/styles/button.less";
import moment from 'moment'


import xss from 'xss'

import {
  Button,
  message,
  Modal,
  Menu,
  Icon,
  Layout,
  Card,
  Row,
  Col,
  Avatar,
  Breadcrumb,
  Pagination,
  Popover,
  Tag,
  Switch,
  Divider,
  Badge,
  Form,
  FormModel,
  Input,
  Radio,
  Checkbox,
  Select,
  DatePicker,
  Spin,
  List,
  Collapse,
  Tooltip,
  Comment,
  Tabs,
  Drawer,
  Empty,
  Table,
  Dropdown,
  Carousel,
  InputNumber,
  notification,
  Cascader,
  Steps,
  Result
} from 'ant-design-vue';
import traditionlize from "@/utils/translate";
Vue.prototype.$message = message;

message.config({
  top: `100px`,
  duration: 5,
  maxCount: 3,
});

moment.locale('zh-cn')

Vue.directive('trans', {
  inserted: function (el) {
    if(el.type){
      // console.log("Type : " + el.type + " IT : " + el.innerText)
      return;
    }
    if (el.innerText) {
      el.innerText = traditionlize(el.innerText);
    }
  }
});


Vue
  .use(Button)
  .use(Modal)
  .use(Menu)
  .use(Icon)
  .use(Layout)
  .use(Card)
  .use(Row)
  .use(Col)
  .use(Avatar)
  .use(Breadcrumb)
  .use(Pagination)
  .use(Popover)
  .use(Tag)
  .use(Switch)
  .use(Divider)
  .use(Badge)
  .use(Form)
  .use(FormModel)
  .use(Input)
  .use(Radio)
  .use(Checkbox)
  .use(Select)
  .use(DatePicker)
  .use(Spin)
  .use(List)
  .use(Collapse)
  .use(Tooltip)
  .use(Comment)
  .use(Tabs)
  .use(Drawer)
  .use(Empty)
  .use(Table)
  .use(Dropdown)
  .use(Carousel)
  .use(InputNumber)
  .use(notification)
  .use(Cascader)
  .use(Steps)
  .use(Result)

Vue.config.productionTip = false
Object.defineProperty(Vue.prototype, '$xss', {
  value: xss
});


new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')