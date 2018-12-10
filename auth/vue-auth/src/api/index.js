import axios from 'axios'

// axios.defaults.baseURL = '';
// axios.defaults.headers.common['Authorization'] = AUTH_TOKEN;
// axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

// Add a request interceptor
axios.interceptors.request.use(function (config) {
  // Do something before request is sent
  // config.headers.Authorization=localStorage.getItem('token');
  return config;
}, function (error) {
  // Do something with request error
  return Promise.reject(error);
});

// Add a response interceptor
axios.interceptors.response.use(function (response) {
  // Do something with response data
  return response;
}, function (error) {
  // Do something with response error
  return Promise.reject(error);
});


export const getRequest = (url, config) => {
  return new Promise((resolve, reject) => {
    axios.get(url, config)
      .then(response => {
        resolve(response);
      })
      .catch(error => {
        reject(error);
      });
  });
};

export const postRequest = (url,data,config) => {
  return new Promise((resolve, reject) => {
    axios.post(url,data,config)
      .then(response => {
        resolve(response);
      })
      .catch(error => {
        reject(error);
      });
  });
};
