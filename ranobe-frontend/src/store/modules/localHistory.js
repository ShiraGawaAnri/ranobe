import store from "@/store"

import {
  saveLocalHistory,
  getLocalHistories
} from "@/utils/history";

const state = {
  histories: getLocalHistories() || []
};


const mutations = {
  SET_HISTORY: (state, val) => {
    console.log(val);
    state.histories = val;
  }
};

const actions = {
  saveLocalHistory({
    commit,
    dispatch
  },{route,details}) {
    return new Promise((resolve, reject) => {
      let ret = saveLocalHistory(route,details);
      commit("SET_HISTORY",getLocalHistories());
      resolve(ret);
    });
  }
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
};