# Docker安装RocketMQ

## 部署namespace

```
docker pull apache/rocketmq:5.2.0
```

```
# 日志目录
mkdir /data/rocketmq/nameserver/logs -p
# 脚本目录
mkdir /data/rocketmq/nameserver/bin -p

```

```
# 777 文件所属者、文件所属组和其他人有读取 & 写入 & 执行全部权限。rwxrwxrwx
chmod 777 -R /data/rocketmq/nameserver/*

```

```
docker run -d \
--privileged=true \
--name rmqnamesrv \
apache/rocketmq:5.2.0 sh mqnamesrv

```

```
docker cp rmqnamesrv:/home/rocketmq/rocketmq-5.2.0/bin/runserver.sh /data/rocketmq/nameserver/bin/runserver.sh

```

```
打开脚本文件

vi /data/rocketmq/nameserver/bin/runserver.sh 

找到调用calculate_heap_sizes函数的位置注释掉保存即可，拉到脚本最底部就能找到
```

```
docker stop rmqnamesrv
docker rm rmqnamesrv
```

```
sudo docker run -d \
--privileged=true \
--restart=always \
--name rmqnamesrv \
-p 9876:9876  \
-v /data/rocketmq/nameserver/logs:/home/rocketmq/logs \
-v /data/rocketmq/nameserver/bin/runserver.sh:/home/rocketmq/rocketmq-5.2.0/bin/runserver.sh \
-e "MAX_HEAP_SIZE=256M" \
-e "HEAP_NEWSIZE=128M" \
apache/rocketmq:5.2.0 sh mqnamesrv
```

## 部署Broker

```
# 创建需要的挂载目录
mkdir /data/rocketmq/broker/logs -p \
mkdir /data/rocketmq/broker/data -p \
mkdir /data/rocketmq/broker/conf -p \
mkdir /data/rocketmq/broker/bin -p

```

```
# 777 文件所属者、文件所属组和其他人有读取 & 写入 & 执行全部权限。rwxrwxrwx
chmod 777 -R /data/rocketmq/broker/*

```

```
vi /data/rocketmq/broker/conf/broker.conf

```

添加以下配置信息到broker.conf，这里不对参数做过多的说明，在下面Broker配置详解中有对Broker常用参数做详细介绍

```
# nameServer 地址多个用;隔开 默认值null
# 例：127.0.0.1:6666;127.0.0.1:8888
namesrvAddr = 10.1.249.198:9876
# 集群名称
brokerClusterName = DefaultCluster
# 节点名称
brokerName = broker-a
# broker id节点ID， 0 表示 master, 其他的正整数表示 slave，不能小于0
brokerId = 0
# Broker服务地址        String  内部使用填内网ip，如果是需要给外部使用填公网ip
brokerIP1 = 10.1.249.198
#在发送消息时，自动创建服务器不存在的topic，默认创建的队列数
defaultTopicQueueNums=4

#文件保留时间，默认48小时
fileReservedTime=120
#commitLog每个文件的大小默认1G
mapedFileSizeCommitLog=1073741824

#ConsumeQueue每个文件默认存30W条，根据业务情况调整
mapedFileSizeConsumeQueue=300000
# Broker角色
brokerRole = ASYNC_MASTER
# 刷盘方式
flushDiskType = ASYNC_FLUSH
# 在每天的什么时间删除已经超过文件保留时间的 commit log，默认值04
deleteWhen = 04
#检测物理文件磁盘空间
diskMaxUsedSpaceRatio=88

# 是否允许Broker 自动创建Topic，建议线下开启，线上关闭
autoCreateTopicEnable=true
# 是否允许Broker自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=true
```

说明：建立broker.conf文件，通过这个文件把RocketMQ的broker管理起来

拷贝容器内Broker启动脚本到宿主机（如果不需要自定义堆内存可以跳过）
Broker启动脚本中有一个自动计算最大堆内存和新生代内存的函数会导致在不同硬件环境下设置最大堆内存和新生代内存环境变量不被应用，，这里先提前copy一份容器内部启动脚本做挂载，如果想自定义内存可以自己调整。

```
docker run -d \
--name rmqbroker \
--privileged=true \
apache/rocketmq:5.2.0 \
sh mqbroker

```

```
docker cp rmqbroker:/home/rocketmq/rocketmq-5.2.0/bin/runbroker.sh /data/rocketmq/broker/bin/runbroker.sh
```

```
# 打开脚本文件
vi /data/rocketmq/broker/bin/runbroker.sh 

```

找到调用calculate_heap_sizes函数的位置注释掉保存即可，拉到脚本最底部就能找到

```
docker stop rmqbroker
docker rm rmqbroker
```

```
docker run -d \
--restart=always \
--name rmqbroker \
-p 10911:10911 -p 10909:10909 \
--privileged=true \
-v /data/rocketmq/broker/logs:/root/logs \
-v /data/rocketmq/broker/store:/root/store \
-v /data/rocketmq/broker/conf/broker.conf:/home/rocketmq/broker.conf \
-v /data/rocketmq/broker/bin/runbroker.sh:/home/rocketmq/rocketmq-5.2.0/bin/runbroker.sh \
-e "MAX_HEAP_SIZE=512M" \
-e "HEAP_NEWSIZE=256M" \
apache/rocketmq:5.2.0 \
sh mqbroker -c /home/rocketmq/broker.conf
```

