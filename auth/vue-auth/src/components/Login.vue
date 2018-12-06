<template>
  <div>
    <span>username：{{myuser}}</span>
    <input type="text" v-model="username">
    <input type="password" v-model="password">
    <button @click="login">登录</button>
    <button @click="hello">读取数据</button>
  </div>
</template>

<script>
  import {getRequest, postRequest} from '../api'
  import qs from 'qs';
  import base64url from 'base64url'

  export default {
    name: "Login",
    data() {
      return {
        username: '',
        password: '',
        access_token: '',
        myuser:''
      }
    },
    computed: {
      user_name(){
        let username='';
        if(this.access_token){
          username=JSON.parse(base64url.decode(localStorage.getItem('access_token').split('.')[1])).user_name;
          console.log(username);
        }
        return username;
      }
    },
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
            this.access_token = response.data.access_token;
            localStorage.setItem('access_token', this.access_token);
            this.myuser=JSON.parse(base64url.decode(this.access_token.split('.')[1]));
            this.$router.push({path: decodeURIComponent(this.$route.query.redirect)});
          });
      }
    },
    components: {}
  }
</script>

<style scoped>

</style>
