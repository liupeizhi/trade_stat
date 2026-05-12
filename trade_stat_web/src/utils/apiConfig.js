// API 基础地址配置
// 开发环境使用代理，生产环境通过 nginx 代理转发到后端
const isDev = process.env.NODE_ENV === 'development';

// API 基础地址（注意末尾有 /）
// 开发和生产环境都使用 /api/ 前缀，由代理转发到后端服务
export const API_BASE_URL = '/api/';

