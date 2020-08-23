import axios from 'axios'
import Cookies from 'js-cookie';
import {
  message,
  Modal
} from 'ant-design-vue';
import store from '@/store'
import {
  getToken
} from '@/utils/auth'
import traditionlize from "@/utils/translate";



const SERVICE = process.env.VUE_APP_SERVICE;
const TIMEOUT = process.env.VUE_APP_TIMEOUT;

axios.defaults.headers['Content-Type'] = 'application/json; charset=utf-8';
axios.defaults.baseURL = SERVICE;
axios.defaults.timeout = Number(TIMEOUT);
axios.defaults.withCredentials = true;

const service = axios.create({});

const _self = this;

service.interceptors.request.use(
  config => {
    // do something before request is sent
    const options = Object.assign({}, config);
    if (!options.url) {
      console.log('请填写接口地址');
      return;
    }
    config.method = options.type.toUpperCase() || 'POST';
    config.data = options.data || {};
    for (const k in config.data) {
      if (config.data[k] === null || config.data[k] === void 0) {
        delete config.data[k];
      }
    }
    if (store.getters.token) {
      config.headers['Authorization'] = getToken();
    }
    return config;
  },
  error => {
    // do something with request error
    console.log(error); // for debug
    return Promise.reject(error);
  }
);

service.interceptors.response.use(
  async response => {
    const res = response.data;
    //20000 正常
    //20004 验证码问题
    //40000 一般性服务逻辑错误
    //40001 返回到调用本身的逻辑错误
    //50000 系统性错误
    //50008,50012,50014 权限问题

    //以下 自定义状态码 的对应 自行处理
    if (![20000, 20001, 20002, 20003, 20004].includes(res.code)) {
      message.error({
        content: traditionlize((res.message || 'Error')),
        key: 'error'
      });

      if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
        // to re-login
        await store.dispatch('user/refreshRoutes');
        Modal.confirm({
          title: traditionlize('权限不足'),
          content: traditionlize('您没有权限访问请求此次数据, 您可以重新尝试登录后再访问此页面'),
          okText: traditionlize('跳转登录页'),
          okType: traditionlize('danger'),
          cancelText: traditionlize('留在本页'),
          onOk: () => {
            store.dispatch('user/resetToken').then(() => {
              //location.reload();
            });
          },
          onCancel: () => {
            console.log('cancel')
          }
        });
      }
      return Promise.reject(new Error(res.message || 'Error'));
    } else {
      return res;
    }
  },
  error => {
    console.log('err' + error); // for debug
    const response = error.response;
    if (error && response) {
      const data = response.data;
      const code = data.code || data.status;
      if (code || data.message) {
        const validatedErrors = data.errors;
        if (Array.isArray(validatedErrors)) {
          let msg;
          validatedErrors.forEach(err => {
            msg += 'Filed:' + err.field + ' Error: ' + err.defaultMessage;
            msg += '   ';
          });
          message.error({
            content: traditionlize('错误状态:' + code + '  ' + msg),
            key: 'error'
          });
        } else {
          message.error({
            content: traditionlize('错误状态:' + code + '  ' + data.message),
            key: 'error'
          });
        }
      }
    } else {
      message.error({
        content: traditionlize(error.message),
        key: 'error'
      });
    }
    return Promise.reject(error);
  }
);

export default service