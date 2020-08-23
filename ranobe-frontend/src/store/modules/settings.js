import router, {
  resetRouter
} from '@/router';
import {
  addOrUpdateSettings
} from '@/api/user';
import moment from "moment";
import store from "@/store";
import {
  getToken
} from '@/utils/auth';

const namespaced = true;

let userSettings = window.localStorage.getItem("userSettings");
let settings = {};
if (userSettings) {
  try {
    settings = JSON.parse(userSettings);
  } catch (error) {
    console.log(error);
  }
}

const state = {
  showTags: settings.showTags !== undefined ? settings.showTags === 1 : true,
  fontSize: settings.fontSize || 24,
  showTsukomi: settings.showTsukomi !== undefined ? settings.showTsukomi === 1 : true,
  language: settings.language || 1,
  showOriginContent: settings.showOriginContent !== undefined ? settings.showOriginContent === 1 : false,
  fontColorNumber: settings.fontColorNumber || 1,
  backgroundColorNumber: settings.backgroundColorNumber || 2,
  closeSiderBar: settings.closeSiderBar !== undefined ? settings.closeSiderBar === 1 : false,
  customColor: {
    fontColor: "#000",
    backgroundColors: "#fff"
  },
  updatedTime: settings.updatedTime || undefined
};
const defaultState = Object.assign({}, state);

const mutations = {
  SET_SHOWTAGS: (state, val) => {
    state.showTags = val;
    state.updatedTime = moment(new Date()).format("YYYY-MM-DD HH:mm:ss");
    store.dispatch("settings/syncToCloud");
  },
  SET_FONTSIZE: (state, val) => {
    state.fontSize = val;
    state.updatedTime = moment(new Date()).format("YYYY-MM-DD HH:mm:ss");
    store.dispatch("settings/syncToCloud");
  },
  SET_SHOWTSUKOMI: (state, val) => {
    state.showTsukomi = val;
    state.updatedTime = moment(new Date()).format("YYYY-MM-DD HH:mm:ss");
    store.dispatch("settings/syncToCloud");
  },
  SET_LANGUAGE: (state, val) => {
    state.language = val;
    state.updatedTime = moment(new Date()).format("YYYY-MM-DD HH:mm:ss");
    store.dispatch("settings/syncToCloud");
  },
  SET_SHOWORIGINCONTENT: (state, val) => {
    state.showOriginContent = val;
    state.updatedTime = moment(new Date()).format("YYYY-MM-DD HH:mm:ss");
    store.dispatch("settings/syncToCloud");
  },
  SET_FONTCOLORNUMBER: (state, val) => {
    state.fontColorNumber = val;
    state.updatedTime = moment(new Date()).format("YYYY-MM-DD HH:mm:ss");
    store.dispatch("settings/syncToCloud");
  },
  SET_BACKGROUNDCOLORNUMBER: (state, val) => {
    state.backgroundColorNumber = val;
    state.updatedTime = moment(new Date()).format("YYYY-MM-DD HH:mm:ss");
    store.dispatch("settings/syncToCloud");
  },

  SET_CLOSESIDERBAR: (state, {
    val,
    flag
  }) => {
    state.closeSiderBar = val;
    if (!flag) {
      state.updatedTime = moment(new Date()).format("YYYY-MM-DD HH:mm:ss");
      store.dispatch("settings/syncToCloud");
    }

  },
  RESET: (state) => {
    state = Object.assign({}, defaultState);
    state.updatedTime = moment(new Date()).format("YYYY-MM-DD HH:mm:ss");
  },
  SYNC: (state, {
    propName,
    propVal
  }) => {
    if (typeof state[propName] === "boolean") {
      state[propName] = propVal === 1;
    } else {
      state[propName] = propVal;
    }

  }
};

const actions = {
  closeSiderBar({
    commit
  }, {
    val,
    flag
  }) {
    return new Promise(resolve => {
      commit('SET_CLOSESIDERBAR', {
        val,
        flag
      });
      resolve();
    });
  },
  reset({
    commit
  }) {
    return new Promise(resolve => {
      commit('RESET');
      resolve();
    });
  },
  syncToCloud({
    commit
  }) {
    return new Promise(async resolve => {
      const hasToken = getToken();
      const hasRoles = store.getters.roles && store.getters.roles.length > 0;
      let postData = Object.assign({}, state);
      for (let prop in state) {
        if (typeof state[prop] === "boolean") {
          postData[prop] = state[prop] ? 1 : 0;
        }
      }
      if (hasToken && hasRoles) {
        const {
          data
        } = await addOrUpdateSettings(postData);
      }
      window.localStorage.setItem("userSettings", JSON.stringify(postData));
      resolve();
    });
  },
  syncToLocal({
    commit,
  }, userSettings) {
    return new Promise(resolve => {
      if (!state.updatedTime || moment(userSettings.updatedTime).isSameOrAfter(state.updatedTime)) {
        console.log("云配置 覆盖 本地配置")
        for (let prop in userSettings) {
          commit("SYNC", {
            propName: prop,
            propVal: userSettings[prop]
          });
        }
        let saveData = Object.assign({}, state);
        for (let prop in state) {
          if (typeof state[prop] === "boolean") {
            saveData[prop] = state[prop] ? 1 : 0;
          }
        }
        window.localStorage.setItem("userSettings", JSON.stringify(saveData));
      } else {
        //TODO:弹modal询问适用于哪个配置
        console.log("TODO:本地配置优先")
      }
      resolve();
    });
  }
};

export default {
  namespaced,
  state,
  mutations,
  actions,
};