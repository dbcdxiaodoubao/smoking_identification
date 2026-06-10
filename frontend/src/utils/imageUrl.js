/**
 * 图片URL处理 — 本地开发用相对路径走 Vite 代理
 */

export function convertImageUrl(url) {
  if (!url) return url
  // 去掉后端返回的 localhost 前缀，改为相对路径走代理
  return url.replace('http://localhost:8080', '')
}

export function getImageUrl(url) {
  if (!url) return ''
  return url.replace('http://localhost:8080', '')
}

export function convertImageUrls(urls) {
  if (!Array.isArray(urls)) return urls
  return urls.map(url => (url ? url.replace('http://localhost:8080', '') : ''))
}

export function convertObjectImageUrls(obj, fields) {
  if (!obj || typeof obj !== 'object') return obj
  const result = { ...obj }
  const fieldList = Array.isArray(fields) ? fields : [fields]
  fieldList.forEach(field => {
    if (result[field]) result[field] = result[field].replace('http://localhost:8080', '')
  })
  return result
}
