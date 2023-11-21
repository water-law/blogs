# 1. Go 安装



# 2. 项目

初始化项目 firstgo

```shell
go mod init firstgo
```

Go 安装依赖

```shell
go get xxx
```

自动检查依赖项

```shell
go mod tidy
```

package

https://pkg.go.dev

# swagger

```shell
go install github.com/swaggo/swag/cmd/swag@latest
go get github.com/swaggo/gin-swagger
go get github.com/swaggo/files
```

```shell
swag init
```

```go
package main
import (
   "github.com/gin-gonic/gin"
   "firstgo/docs"
   swaggerfiles "github.com/swaggo/files"
   ginSwagger "github.com/swaggo/gin-swagger"
   "net/http"
)
```

导入docs文件夹,`tips`:如果使用了 mod，前缀一定要添加 mod 模块名称，例如：`_ "github.com/xxx/docs"`

Go.mod

```mod
module firstgo
```

```go
// PingExample godoc
// @Summary ping example
// @Schemes
// @Description do ping
// @Tags ping
// @Accept json
// @Produce json
// @Success 200 {string} ping
// @Router /ping [get]
func GetIndex(c *gin.Context) {
	c.JSON(http.StatusOK, gin.H{
		"message": "welcome!!",
	})
}
```

### gorm

```shell
go get gorm.io/gorm
go get gorm.io/driver/mysql
```

```go
	db, err := gorm.Open(mysql.Open("root:rl6174zjp@tcp(127.0.0.1:3306)/chat?charset=utf8mb4&parseTime=true&loc=Local"), &gorm.Config{})
	if err != nil {
		panic("error occur when open mysql")
	}

	db.AutoMigrate(&models.BasicUser{})
	user := &models.BasicUser{}
	user.Name = "zjp"
	user.Password = "1111"
	db.Create(user)
  // 更新
  db.Model(&user).Update("password", "1234")
```

