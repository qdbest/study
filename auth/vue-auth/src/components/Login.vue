<template>
  <div>
    <input type="text" v-model="username">
    <input type="password" v-model="password">
    <button @click="login">登录</button>
    <button @click="hello">读取数据</button>
  </div>
</template>

<script>
  import {getRequest, postRequest} from '../api'
  import qs from 'qs';

  export default {
    name: "Login",
    data() {
      return {
        username: '',
        password: '',
        access_token: ''
      }
    },
    computed: {},
    created() {
    },
    methods: {
      hello() {
        getRequest(`api/hello`, {
          headers: {'Authorization': 'bearer ' + this.access_token}
        })
          .then(response => {
            console.log(response.data);
          })
      },
      login() {
        postRequest(`api/oauth/token`, qs.stringify({
          grant_type: 'password',
          username: this.username,
          password: this.password,
          scope: 'all'
        }), {
          headers: {'content-type': 'application/x-www-form-urlencoded'},
          auth: {
            username: 'test',
            password: '123456'
          }
        })
          .then(response => {
            // localStorage.setItem('token',response.data.access_token);
            this.access_token = response.data.access_token;
            console.log(this.access_token);
          });
      }
    },
    components: {}
  }
</script>

<style scoped>

</style>
