import request from '@/utils/request';

export async function vCodeCheck(params){
  return await request({
    url: `/verification/check`,
    type: 'get',
    params
  });
}

export async function vCodeCheckByTypeAndCode(params){
  return await request({
    url: `/verification/check/type`,
    type: 'get',
    params
  });
}



export async function vCodeSendByType(data){
  return await request({
    url: `/verification`,
    type: 'post',
    data
  });
}

export async function vCodeSendToEmailRequest(data){
  return await request({
    url: `/verification/email`,
    type: 'post',
    data
  });
}

