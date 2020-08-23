import request from '@/utils/request';

// export async function getNovelList(params) {
//   return await request({
//     url: '/novel',
//     type: 'get',
//     params,
//   });
// }

export async function getNovelList(data) {
  return await request({
    url: '/novel/list',
    type: 'post',
    data,
  });
}

export async function getNovelHistory(params) {
  return await request({
    url: '/novel/history',
    type: 'get',
    params,
  });
}
export async function getNovelSubscribe(params) {
  return await request({
    url: '/novel/subscribe',
    type: 'get',
    params,
  });
}


export async function getNovelById(id) {
  return await request({
    url: `/novel/${id}`,
    type: 'get',
  });
}

export async function novelLikes(data){
  return await request({
    url: `/novel/likes`,
    type: 'post',
    data
  });
}

export async function addNovelSubscribe(data){
  return await request({
    url: `/novel/subscribe`,
    type: 'post',
    data
  });
}

export async function deleteNovelSubscribe(params){
  return await request({
    url: `/novel/subscribe`,
    type: 'delete',
    params
  });
}



export async function getNovelComment(params){
  return await request({
    url: `/novel/comment`,
    type: 'get',
    params
  });
}

export async function novelCommentLikes(data){
  return await request({
    url: `/novel/comment/likes`,
    type: 'post',
    data
  });
}

export async function novelCommentDislikes(data){
  return await request({
    url: `/novel/comment/dislikes`,
    type: 'post',
    data
  });
}

export async function getNovelChapterList(novelId,episode){
  return await request({
    url: `/novel/${novelId}/${episode}/chapter`,
    type: 'get'
  });
}



export async function getNovelChapter(novelId,episode,chapter){
  return await request({
    url: `/novel/${novelId}/${episode}/${chapter}`,
    type: 'get'
  });
}

export async function getNovelChapterParagraphComment(data){
  return await request({
    url: `/novel/episode/chapter/paragraph/comment/list`,
    type: 'post',
    data,
  });
}

export async function getHotNovel(params){
  return await request({
    url: `/novel/hot`,
    type: 'get',
    params,
  });
}

export async function addNovelComment(data){
  return await request({
    url: `/novel/comment`,
    type: 'post',
    data,
  });
}

export async function addNovelChapterParagraphComment(data){
  return await request({
    url: `/novel/episode/chapter/paragraph/comment`,
    type: 'post',
    data,
  });
}

export async function getNovelChapterParagraphCommentByParagraphId(id){
  return await request({
    url: `/novel/episode/chapter/paragraph/comment/${id}`,
    type: 'get',
  });
}

export async function getUploadList(params){
  return await request({
    url: `/backend/upload`,
    type: 'get',
    params
  });
}

export async function getBackendOptions(params){
  return await request({
    url: `/backend/options`,
    type: 'get',
    params
  });
}

export async function addBackendNovel(data){
  return await request({
    url: `/backend/novel`,
    type: 'post',
    data
  });
}

export async function updateBackendNovel(data){
  return await request({
    url: `/backend/novel`,
    type: 'put',
    data
  });
}

export async function deleteBackendNovel(params){
  return await request({
    url: `/backend/novel`,
    type: 'delete',
    params
  });
}

export async function addBackendNovelEpisode(data){
  return await request({
    url: `/backend/novel/episode`,
    type: 'post',
    data
  });
}

export async function updateBackendNovelEpisode(data){
  return await request({
    url: `/backend/novel/episode`,
    type: 'put',
    data
  });
}
export async function deleteBackendNovelEpisode(params){
  return await request({
    url: `/backend/novel/episode`,
    type: 'delete',
    params
  });
}


export async function addBackendNovelEpisodeChapter(data){
  return await request({
    url: `/backend/novel/episode/chapter`,
    type: 'post',
    data
  });
}

export async function updateBackendNovelEpisodeChapter(data){
  return await request({
    url: `/backend/novel/episode/chapter`,
    type: 'put',
    data
  });
}

export async function getBackendChapterByChapter(novelId,episode,chapter){
  return await request({
    url: `/backend/novel/${novelId}/${episode}/${chapter}`,
    type: 'get',
  });
}

export async function deleteBackendNovelEpisodeChapter(params){
  return await request({
    url: `/backend/novel/episode/chapter`,
    type: 'delete',
    params
  });
}

export async function getReportList(params){
  return await request({
    url: `/report`,
    type: 'get',
    params
  });
}


export async function addReport(data){
  return await request({
    url: `/report`,
    type: 'post',
    data
  });
}
