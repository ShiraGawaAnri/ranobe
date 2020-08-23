import {
  message
} from "ant-design-vue";
import {
  login,
  logout,
  getInfo
} from '@/api/user';
import {
  getToken,
  setToken,
  removeToken
} from '@/utils/auth';
import router, {
  resetRouter
} from '@/router';
import traditionlize from "@/utils/translate";
import store from "@/store"

const state = {
  token: getToken(),
  username: '',
  nickname: '',
  avatar: '',
  introduction: '',
  roles: [],
  status: undefined,
  histories: [],
  subscribes: [],
  likes: [],
};

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token;
  },
  SET_INTRODUCTION: (state, introduction) => {
    state.introduction = introduction;
  },
  SET_USERNAME: (state, username) => {
    state.username = username;
  },
  SET_NICKNAME: (state, nickname) => {
    state.nickname = nickname;
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar;
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles;
  },
  SET_STATUS: (state, status) => {
    state.status = status;
  },
  SET_HISTORIES: (state, histories) => {
    state.histories = histories;
  },
  SET_SUBSCRIBES: (state, subscribes) => {
    state.subscribes = subscribes;
  },
  SET_LIKES: (state, likes) => {
    state.likes = likes;
  },
};

const actions = {
  // user login
  login({
    commit,
    dispatch
  }, userInfo) {
    const {
      username,
      password
    } = userInfo;
    return new Promise((resolve, reject) => {
      login(userInfo)
        .then(async response => {
          const resp = response;
          const data = resp.data;
          if (resp.code !== 20000) {
            message.error(traditionlize(`登陆失败!\r\n原因: ${resp.message}`));
          } else {
            commit('SET_TOKEN', data.token);
            setToken(data.token);
            await dispatch("getInfo");
            resetRouter();
            message.success(traditionlize("成功登陆,欢迎回到本站!"));
            resolve();
          }
        })
        .catch(error => {
          console.log(error)
          reject(error);
        });
    });
  },

  // get user info
  getInfo({
    commit,
    state,
    dispatch
  }) {
    return new Promise((resolve, reject) => {
      getInfo(state.token)
        .then(async response => {
          const {
            data
          } = response;
          if (!data) {
            reject(traditionlize('验证失败,请重新登陆.'));
          }
          let {
            roles,
            username,
            avatar,
            introduction = '',
            status,
            nickname,
            novelSubscribes = [],
            novelHistories = [],
            novelLikes = [],
            userSettings = undefined,
          } = data;
          if (!roles || roles.length <= 0) {
            await dispatch("resetToken");
            reject(traditionlize('用户信息有误,请联系管理员!'));
          }
          commit('SET_STATUS', status);
          commit('SET_ROLES', roles);
          commit('SET_USERNAME', username);
          commit('SET_NICKNAME', traditionlize(nickname));
          commit('SET_AVATAR', avatar);
          commit('SET_INTRODUCTION', introduction);
          commit('SET_HISTORIES', novelHistories);
          commit('SET_SUBSCRIBES', novelSubscribes);
          commit('SET_LIKES', novelLikes);
          if(!userSettings || !userSettings.updatedTime){
            await store.dispatch("settings/syncToCloud");
          }else{
            await store.dispatch("settings/syncToLocal",userSettings);
          }
          resolve(data);
        })
        .catch(error => {
          reject(error);
        });
    });
  },

  // user logout
  logout({
    commit,
    state,
    dispatch
  }) {
    return new Promise((resolve, reject) => {
      logout(state.token)
        .then(async () => {
          commit('SET_TOKEN', '');
          commit('SET_ROLES', []);
          commit('SET_NICKNAME', '');
          commit('SET_USERNAME', '');
          removeToken();
          resetRouter();

          // generate accessible routes map based on roles
          const accessRoutes = await dispatch('permission/generateRoutes', [], {
            root: true,
          });

          // dynamically add accessible routes
          router.addRoutes(accessRoutes);
          // // reset visited views and cached views
          // // to fixed https://github.com/PanJiaChen/vue-element-admin/issues/2485
          // dispatch('tagsView/delAllViews', null, { root: true })

          resolve();
        })
        .catch(error => {
          reject(error);
        })
        .finally(response => {
          removeToken();
          resetRouter();
          // reset visited views and cached views
          // to fixed https://github.com/PanJiaChen/vue-element-admin/issues/2485
          // dispatch('tagsView/delAllViews', null, {
          //   root: true,
          // });
        });
    });
  },

  setToken({
    commit
  }, token) {
    return new Promise(resolve => {
      commit('SET_TOKEN', token);
      resolve();
    });
  },
  // remove token
  resetToken({
    commit
  }) {
    return new Promise(resolve => {
      commit('SET_TOKEN', '');
      commit('SET_ROLES', []);
      removeToken();
      resolve();
    });
  },

  // dynamically modify permissions
  // changeRoles({ commit, dispatch }, role) {
  //   return new Promise(async resolve => {
  //     const token = role + '-token';

  //     commit('SET_TOKEN', token);
  //     setToken(token);

  //     const { roles } = await dispatch('getInfo');

  //     resetRouter();

  //     // generate accessible routes map based on roles
  //     const accessRoutes = await dispatch('permission/generateRoutes', roles, {
  //       root: true,
  //     });

  //     // dynamically add accessible routes
  //     router.addRoutes(accessRoutes);

  //     // reset visited views and cached views
  //     dispatch('tagsView/delAllViews', null, { root: true });

  //     resolve();
  //   });
  // },
  refreshRoutes({
    commit,
    dispatch
  }) {
    return new Promise(async resolve => {
      let {
        roles
      } = state;
      if (!roles) {
        const data = await dispatch('getInfo');
        roles = data.roles;
      }else{
        roles = [];
      }
      resetRouter();
      const accessRoutes = await dispatch('permission/generateRoutes', roles, {
        root: true,
      });

      // dynamically add accessible routes
      router.addRoutes(accessRoutes);
      resolve();
    });
  },
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
};