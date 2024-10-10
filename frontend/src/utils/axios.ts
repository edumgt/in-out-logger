import _axios from 'axios';

const axios = _axios.create({
  headers: {
    'Content-Type': 'application/json'
  }
});

// 요청 인터셉터
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('$at') || '';
  config.headers.Authorization = `Bearer ${token}`;
  return config;
}, error => {
  return Promise.reject(error);
});

export default axios;
