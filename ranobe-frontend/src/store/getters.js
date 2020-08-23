const getters = {
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  username: state => state.user.username,
  nickname: state => state.user.nickname,
  introduction: state => state.user.introduction,
  roles: state => state.user.roles,
  status: state => state.user.status,
  histories: state => state.user.histories,
  localHistories : state=>state.localHistory.histories,
  subscribes: state => state.user.subscribes,
  likes: state => state.user.likes,
  permission_routes: state => state.permission.routes,
  settings: state => state.settings,
};
export default getters;
