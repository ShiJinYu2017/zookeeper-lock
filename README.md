# zookeeper-lock
这是一个利用zookepper实现分布式锁的例子
###1、为什么需要分布式锁
当单个服务器不能满足处理需求时，通过java的线程锁将无法应对高并发的处理环境，这时只能通过分布式锁来实现并发处理。
###2、zookeeper
下载地址https://mirrors.tuna.tsinghua.edu.cn/apache/zookeeper/
zookeeper是一个开源的分布式应用程序**协调服务**，是Hadoop和Hbase的重要组件。
