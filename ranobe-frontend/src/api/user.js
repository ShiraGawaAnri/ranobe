import request from '@/utils/request';

export async function reg(data) {
  return await request({
    url: '/auth/reg',
    type: 'post',
    data,
  });
}

export async function regSimple(data) {
  return await request({
    url: '/auth/reg/simple',
    type: 'post',
    data,
  });
}

export async function login(data) {
  return await request({
    url: '/auth/login',
    type: 'post',
    data,
    withCredentials:false,
  });
}

export async function logout() {
  return await request({
    url: '/auth/logout',
    type: 'get',
  });
}

export async function getInfo() {
  return await request({
    url: '/user/info',
    type: 'get',
  });
}

export async function getDetails() {
  return await request({
    url: '/user/details',
    type: 'get',
  });
}

export async function updateInfo(data) {
  return await request({
    url: '/user',
    type: 'put',
    data,
  });
}

export async function addOrUpdateSettings(data) {
  return await request({
    url: '/user/settings',
    type: 'put',
    data,
  });
}

export async function bindMail(data){
  return await request({
    url: `/user/mail`,
    type: 'put',
    data
  });
}

export async function bindMobile(data){
  return await request({
    url: `/user/phone`,
    type: 'put',
    data
  });
}

export async function rebindMail(data){
  return await request({
    url: `/user/rebind/mail`,
    type: 'put',
    data
  });
}

export async function rebindMobile(data){
  return await request({
    url: `/user/rebind/phone`,
    type: 'put',
    data
  });
}

export async function changePassword(data){
  return await request({
    url: `/user/password`,
    type: 'put',
    data
  });
}