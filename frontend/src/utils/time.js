const SHANGHAI_TIME_ZONE = 'Asia/Shanghai'

function format(value, options) {
  if (!value) return ''
  return new Date(value).toLocaleString('zh-CN', {
    timeZone: SHANGHAI_TIME_ZONE,
    hour12: false,
    ...options
  })
}

export function formatShanghaiDateTime(value) {
  return format(value)
}

export function formatShanghaiDate(value) {
  return format(value, {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}
