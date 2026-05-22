import { apiBaseURL } from '../api/http'

const configuredAssetBase = import.meta.env.VITE_ASSET_BASE_URL

function apiOrigin() {
  if (!apiBaseURL || apiBaseURL.startsWith('/')) {
    return ''
  }
  return apiBaseURL.replace(/\/api\/?$/, '').replace(/\/$/, '')
}

export function assetUrl(url) {
  if (!url || /^(https?:|data:|blob:)/.test(url)) {
    return url
  }
  const base = (configuredAssetBase || apiOrigin()).replace(/\/$/, '')
  return base ? `${base}${url.startsWith('/') ? url : `/${url}`}` : url
}
