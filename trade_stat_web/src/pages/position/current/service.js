import { request } from 'umi';
import { API_BASE_URL } from '@/utils/apiConfig';

export async function getPosition(params, sorter, filter) {
  console.log(params, sorter, filter);
  return request(`${API_BASE_URL}trade/positions`, {
    method: 'GET',
    params: {...params},
    ...(options || {}),
  });
}
