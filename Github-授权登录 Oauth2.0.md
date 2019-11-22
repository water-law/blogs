### 1. 在 github 的 settings -> Developer settings -> 新建应用

需要填写 

application name, (应用名称)， 

homepage url(), （网站首页）

auth callback url, (授权后的回调地址， 即为 redirect_uri)

新建应用后， github 给我们两个重要的参数 client_id 和 client_secret


### 2. 应用场景-github 授权登录

如果你建立了一个个人的网站， 你想让别人不用通过网站本身的注册就可以访问你的网站，那么用户
拥有的第三方帐号体系如微博、github 等第三方应用授权登录是个不错的方案。

想要获得 github 的授权， 你必须进行第 1 步操作--最基本的申请。
接下来还要将用户引导到 github 的授权登录页， github 的授权登录页是
https://github.com/login/oauth/authorize?client_id=[你的client_id]&redirect_uri=[你的auth callback url]

其中 redirect_uri 是第 1 步你配置好的， 在授权登录页可以不传此参数， github 会默认跳到你配置好的授权后的回调地址, 本地开发时， 我喜欢将授权地址设置为 http://localhost:8000/github/oauth/callback


### 3. 处理回调

服务器处理 http://localhost:8000/github/oauth/callback 请求
该回调地址是 github 授权处理后返回的 url 地址， github 会在请求中带一个参数 code
我们可以使用 code 获取 access_token, 请求为 

```python
acc_res = requests.get("https://github.com/login/oauth/access_token",
                            params={"client_id": "xxx",
                                    "client_secret": "xxx",
                                    "code": code,
                                    "redirect_uri": "xxx"
                                    })
```

返回的 acc_res 中就含有 access_token, 使用 access_token 就可以获得用户的 github 帐号信息。

### 4. 图解 github 授权

基于 Oauth2.0 的授权流程都是差不多的， 区别在于申请 client_id 和 client_secret 这个过程是否复杂，像新浪微博的申请就比较麻烦。


```
graph LR
a[开发者]-->|在 Github 创建应用|b[Github]
b-->|登记应用信息并返回client_id 和 client_secret|d[开发者]

```

```
graph LR
a[匿名用户]-->|使用Github 帐号登录|b[引导到 Github 登录授权页]
b-->|用户授权|d[Github 处理授权请求并返回回调地址和参数code]

```

```
graph LR
d[服务器处理回调地址]-->|使用code获取access_token继而获取 Github帐号信息|e[创建用户并绑定用户的Github帐号]
e-->f[跳转到网站首页或某个特定页]

```

### 5. 开源的 Oauth2.0 仓库

本人在 Github 开发了一个 Github 授权登录的 SDK, 如果有需要的朋友请移步 [oauth2](https://github.com/water-law/oauth2), 代码尚有许多不足之处，欢迎 pull request。