<template>
  <div>
    <button @click="login">登录</button>
    <button @click="hello">登录</button>
  </div>
</template>

<script>
  import{getRequest,postRequest} from '../api'
  import qs from 'qs';

  export default {
    name: "Login",
    data() {
      return {
        user: {
          access_token:''
        }
      }
    },
    computed: {},
    created() {
    },
    methods: {
      hello(){
        getRequest(`api/hello`,{
          headers: {'Authorization': 'bearer ' + this.access_token}
        })
          .then(response=>{
            console.log(response.data);
          })
      },
      login() {
        postRequest(`api/oauth/token`, qs.stringify({
          grant_type: 'password',
          username: 'user',
          password: 'e305cca0-3053-48cf-b57a-5cb3420b00af',
          scope: 'all'
        }), {
          headers: {'content-type': 'application/x-www-form-urlencoded'},
          auth: {
            username: '1ddd7db7-4190-4b7b-88c9-8e4d3a87485f',
            password: '40f8fa9c-226a-42b6-8dc7-46a7cdb4e5ef'
          }
        })
          .then(response => {
            // localStorage.setItem('token',response.data.access_token);
            this.access_token=response.data.access_token;
            console.log(this.access_token);
          });
      }
    },
    components: {}
  }
</script>

<style scoped>

</style>
