## webpack 基础

### [webpack 官网](https://www.webpackjs.com/concepts/)

### webpack 安装

推荐在项目中安装而不是全局安装

```bash
npm i webpack webpack-cli -D
```

### webpack 使用

npm v5.2.0 引入的一条命令 npx, 原理就是在 node_modules 下的 .bin 目录中找到对应的命令执行

使用 webpack 命令

```bash
npx webpack
```

webpack 4.0 零配置

### webpack 配置

webpack 四大核心概念

- 入口
- 输出
- loader
- 插件

#### webpack.config.js

```javascript
const path = require('path');

module.exports = {
    entry: './src/index.js',
    // 绝对路径
    output: {
        path: path.resolve('./dist'),
        filename: 'out.js'
    },
    mode: 'development'
}
```

#### package.json

```json
{
  "name": "myblog-app",
  "version": "1.0.0",
  "description": "",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1",
    "build": "webpack --config webpack.config.js"
  },
  "author": "Water Law",
  "license": "MIT",
  "devDependencies": {
    "webpack-cli": "^4.9.2"
  }
}
```

npm run build



### 开发时自动编译工具

1. webpack watch mode

webpack --watch  或者 webpack.config.js 配置 watch: true

1. webpack-dev-server

依赖于 webpack

npm i webpack-dev-server webpack -D

scripts 增加一个 dev

```json
{
  "name": "myblog-app",
  "version": "1.0.0",
  "description": "",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1",
    "build": "webpack --config webpack.config.js",
    "dev": "webpack-dev-server"
  },
  "author": "Water Law",
  "license": "MIT",
  "devDependencies": {
    "webpack": "^5.73.0",
    "webpack-cli": "^4.9.2",
    "webpack-dev-server": "^4.9.2"
  }
}
```



webpack-dev-server 会在内存中生成一个打包好的 out.js ,  放在 url 根目录下 http:localhost:8080/out.js

html 需引用 /out.js

webpack-dev-server --hot --open --port 8080

--hot 热模块编译



```json
const path = require('path');

module.exports = {
    entry: './src/index.js',
    output: {
        path: path.resolve('./dist'),
        filename: 'out.js'
    },
    mode: 'development',
    devServer: {
        open: true,
        compress: true,
        port: 8000,
        hot: true
    }

}
```





### html 插件

npm i html-webpack-plugin -D



1. devServer 模式下根据模版生成 html 文件，自动引入 打包好的 out.js
2. 打包时生成 html， 并且引入 out.js

```js
    plugins: [
        new HtmlWebpackPlugin({
            filename: 'index.html',
            template: './src/index.html'
        })
    ]
```



### Css-loader 和 style-loader

```json
npm i css-loader style-loader -D
```

```js
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            }
        ]
    }
```

webpack 的 loader 是从右到左链式调用

在入口 js 中引入

```js
import './css/index.css'
```

