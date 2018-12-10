# Spring Security Oauth2 最基本的实现
## 后端
- 加入依赖包
```
<dependency>
  <groupId>org.springframework.security.oauth</groupId>
  <artifactId>spring-security-oauth2</artifactId>
</dependency>
```
- 启动认证服务器
```
@Configuration
@EnableAuthorizationServer
public class YucnAuthorizationServerConfig{
}
```
注意：先实现最原始的功能，所有设置全部采用默认，不要在继承AuthorizationServerConfigurerAdapter，一旦继承，就需要在类中进行相应配置
- 启动资源服务器
```
@Configuration
@EnableResourceServer
public class YuCnResourceServerConfig{
}
```
- 运行程序

![avatar](img/1543924510.png)

- postman测试

![avatar](img/1543924956.png)

![avatar](img/1543925057.png)

## 前端Vue
```
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
```
# Lesson 1实现数据库读取用户
## 后端
- 文件结构

![vartar](img/1544006483.png)

- 认证服务器
```
@Configuration
@EnableAuthorizationServer
public class YucnAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("test")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("all")
                .secret("123456")
                .accessTokenValiditySeconds(10000)
                .refreshTokenValiditySeconds(10000);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }
}
```
## 前端
- 登录和读取数据代码
```
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
```
- 使用全局导航守卫进行登录拦截
```
import Vue from 'vue'
import Router from 'vue-router'
import Login from '../components/Login'

Vue.use(Router)

const router = new Router({
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    }
  ]
});

router.beforeEach((to, from, next) => {
  let access_token = localStorage.getItem('access_token');
  console.log(access_token);
  if (access_token || to.path === '/login') {//一定要加|| to.path === '/login'，否则引起死循环
    next();
  } else {
    next({
      path: '/login',
      query: {redirect: to.fullPath}//把要跳转的地址作为参数传到下一步，点击登录跳转回来
    })
  }
});

export default router;
```

- 点击登录后跳转到引发跳转的页面
```
this.$router.push({path: decodeURIComponent(this.$route.query.redirect)});
```

## Lesson 2 用JwtToken代替普通token

### 后端
- 新建JwtTokenConfig类
```
@Configuration
public class JwtTokenConfig {
    @Bean
    public TokenStore tokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter jwtAccessTokenConverter= new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey("yucn");
        return jwtAccessTokenConverter;
    }
}
```
- 修改YucnAuthorizationServerConfig类
```
@Configuration
@EnableAuthorizationServer
public class YucnAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenStore tokenStore;
    // 新增
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("test")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("all")
                .secret("123456")
                .accessTokenValiditySeconds(10000)
                .refreshTokenValiditySeconds(10000);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .accessTokenConverter(jwtAccessTokenConverter)//这句非常重要，不加则生成普通token
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }
}
```
### 前端不需要变化，可以通过base64url解析出用户信息
```
this.myuser=JSON.parse(base64url.decode(this.access_token.split('.')[1]));
```


### springboot升级到2.0.7，非spring-boot-starter依赖包需要加版本号，否则，maven依赖会出现unknow情况
- 依赖包发生变化
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>com.yucn</groupId>
	<artifactId>spring-auth</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>spring-auth</name>
	<description>Demo project for Spring Boot</description>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.0.7.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<!--<dependency>-->
			<!--<groupId>org.springframework.boot</groupId>-->
			<!--<artifactId>spring-boot-starter-data-redis</artifactId>-->
		<!--</dependency>-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.security.oauth.boot</groupId>
			<artifactId>spring-security-oauth2-autoconfigure</artifactId>
			<version>2.0.7.RELEASE</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-jwt</artifactId>
			<version>1.0.9.RELEASE</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-properties-migrator</artifactId>
			<scope>runtime</scope>
		</dependency>
		<!--<dependency>-->
			<!--<groupId>io.jsonwebtoken</groupId>-->
			<!--<artifactId>jjwt</artifactId>-->
			<!--<version>0.7.0</version>-->
		<!--</dependency>-->

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>


</project>

```

- 解决authenticationManager注入失败
新建YucnWebSecurityConfigureAdapter，继承自WebSecurityConfigurerAdapter
```
package com.yucn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by Administrator on 2018/12/10.
 */
@Configuration
public class YucnWebSecurityConfigureAdapter extends WebSecurityConfigurerAdapter {
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

```
- userDetailsService不需要显示注入
```
package com.yucn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * Created by Administrator on 2018/12/3.
 */
@Configuration
@EnableAuthorizationServer
public class YucnAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;
    //@Autowired
    //private UserDetailsService userDetailsService;
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("test")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("all")
                //.secret("123456")
                .secret(passwordEncoder.encode("123456"))
                .accessTokenValiditySeconds(10000)
                .refreshTokenValiditySeconds(10000);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .accessTokenConverter(jwtAccessTokenConverter)//这句非常重要，不加则生成普通token
                .authenticationManager(authenticationManager);
                //.userDetailsService(userDetailsService);
    }
}

```
- 解决passwordEncoder重构问题
```
package com.yucn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by Administrator on 2018/12/4.
 */
@Configuration
public class PasswordEncoderConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        //return new BCryptPasswordEncoder();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

```
默认采用BCryptPasswordEncoder进行加密，机密后在密码前加{id}，通过id采用不同的加密器进行加密和匹配，如
{bcrypt}$2a$10$8Id7PSAFmehfd798sOKQjO8xOJuggmNGKzt7oyzWB.lqVzLoWrs/.
- secret在2.0以上也需要加密
```
package com.yucn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * Created by Administrator on 2018/12/3.
 */
@Configuration
@EnableAuthorizationServer
public class YucnAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;
    //@Autowired
    //private UserDetailsService userDetailsService;
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("test")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("all")
                //.secret("123456")
                .secret(passwordEncoder.encode("123456"))
                .accessTokenValiditySeconds(10000)
                .refreshTokenValiditySeconds(10000);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
                .accessTokenConverter(jwtAccessTokenConverter)//这句非常重要，不加则生成普通token
                .authenticationManager(authenticationManager);
                //.userDetailsService(userDetailsService);
    }
}

```

