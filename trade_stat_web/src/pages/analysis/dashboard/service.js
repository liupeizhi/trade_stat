import { request } from 'umi';
import { API_BASE_URL } from '@/utils/apiConfig';

export async function fakeChartData() {
  return request(`${API_BASE_URL}dashboard`);
}
