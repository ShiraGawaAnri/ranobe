import store from "@/store"

const key = "local-history";

export function saveLocalHistory(route, details) {
  let lastReadTime = new Date().getTime();
  const {
    novelId
  } = details;
  let his = searchLocalHistory(novelId);
  if (his) {
    his.lastReadTime = lastReadTime;
    his.novelChapter = {
      id: details.chapterId
    }
  } else {
    his = {
      id: undefined,
      lastReadTime,
      novel: {
        id: novelId
      },
      novelChapter: {
        id: details.chapterId
      }
    }
  }
  let ret = refreshLocalHistory(his);
  console.log("Update Local Read History : " + ret);
  return ret;
}

function searchLocalHistory(novelId) {
  let str = window.localStorage.getItem(key);
  try {
    let history = !str ? [] : JSON.parse(str);
    if (!Array.isArray(history)) return undefined;
    let target = history.find(his => his.novel && Number(his.novelId) === Number(novelId));
    return target;
  } catch (error) {
    console.log(error)
    return undefined;
  }
}

function refreshLocalHistory(entity) {
  let str = window.localStorage.getItem(key);
  try {
    let history = !str ? [] : JSON.parse(str);
    if (!Array.isArray(history)) return false;
    let modifited = false;
    for (let i = 0; i < history.length; i++) {
      if (history[i] && history[i].novel.id === entity.novel.id) {
        modifited = true;
        history[i].lastReadTime = entity.lastReadTime;
        history[i].novelChapter = entity.novelChapter;
        break;
      }
    }
    if (!modifited) {
      history.unshift(entity);
    }
    window.localStorage.setItem(key, JSON.stringify(history));
    return Array.isArray(history) && history.length > 0;
  } catch (error) {
    console.log(error)
    return false;
  }
}

export function getLocalHistories(){
  let str = window.localStorage.getItem(key);
  try {
    let history = !str ? [] : JSON.parse(str);
    if (!Array.isArray(history)) return [];
    return history;
  } catch (error) {
    console.log(error)
    return [];
  }
}

export function getLocalHistoryIds(){
  let str = window.localStorage.getItem(key);
  try {
    let history = !str ? [] : JSON.parse(str);
    if (!Array.isArray(history)) return [];
    return history.map(his=> his.novel.id);
  } catch (error) {
    console.log(error)
    return [];
  }
}