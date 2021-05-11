### 前言

Java NIO 全称 Java New IO, 是 Java1.4 后引入的新特性

主要组件是通道(Channel), 缓冲区(Buffer), 选择器(Selector)

### Buffer

[用法参考](https://ifeve.com/buffers/)

### Channel

Channel 和 Buffer 有好几种类型。下面是 JAVA NIO 中的一些主要 Channel 的实现：

- FileChannel（阻塞）
- DatagramChannel
- SocketChannel
- ServerSocketChannel

FileChannel 是阻塞的，不能和选择器一起使用

下面代码为读取一个文件

```java
		RandomAccessFile aFile = new RandomAccessFile("nio-data.txt", "rw");
        FileChannel fileChannel = aFile.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(4);
        int bytesRead = fileChannel.read(buffer);

        while (bytesRead != -1) {
            System.out.println("read "+bytesRead);
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.println((char)buffer.get());
                //break;
            }
            buffer.clear();
            //buffer.compact();
            bytesRead = fileChannel.read(buffer);
        }
        aFile.close();
        fileChannel.close();
```

nio-data.txt 文件内容为 "abcdefg" 时，可以尝试下取消两行注释的效果会是什么。

SocketChannel 读取 http 请求

```java
package top.waterlaw.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class SocketChannelTest {


    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false); // 设置为非阻塞
        socketChannel.connect(new InetSocketAddress("waterlaw.top", 80));

        while (!socketChannel.finishConnect()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        StringBuilder builder = new StringBuilder();
        byte[] request = builder.append("GET https://www.waterlaw.top/ HTTP/1.1\r\n")
            	.append("Host: www.waterlaw.top\r\n")
                .append("Connection: keep-alive\r\n")
                .append("Cache-Control: no-cache\r\n").append("Upgrade-Insecure-Requests: 1\r\n")
                .append("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64)\r\n")
                .append("Accept: text/html,application/xhtml+xml,application/xml;\r\n")
                .append("Accept-Encoding: gzip, deflate, br\r\n").append("Accept-Language: zh-CN,zh;q=0.9\r\n")
                .append("\r\n").toString().getBytes();

        socketChannel.write(ByteBuffer.wrap(request));
		// socketChannel.write 会立即返回， 所以需要等待
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int bytesRead = socketChannel.read(byteBuffer);
        boolean readed = false;
        while (bytesRead != -1) {
            if(bytesRead == 0 && readed) {
                break;
            }
            else if(bytesRead == 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("CCCCCCCCCCCC");
                continue;
            }

            byteBuffer.flip();
            String receivedString= Charset.forName("UTF-8").newDecoder().decode(byteBuffer).toString();
            System.out.println(receivedString);
            while (byteBuffer.hasRemaining()) {
                System.out.println((char)byteBuffer.get());
            }
            byteBuffer.clear();
            readed = true;
            bytesRead = socketChannel.read(byteBuffer);
        }
        socketChannel.close();
    }
}
```



### 其他博客

并发编程网的博客写的不错，可以参考下 [ Java NIO系列教程 ](https://ifeve.com/overview/)