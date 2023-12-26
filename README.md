TOS 解压缩
一、目标
spark解压缩工具，目前支持tar、gz、zip压缩格式，默认解压到当前路下，也支持自定义的解压输出路径
二、详细设计
2.1 使用hadoop的FileSystem类，对tos文件的进行读取、查找、写入等操作
2.2 获取到tos文件或目录，对压缩文件进行解压，解压成字节数组，以流的方式写入tos文件系统
2.3 使用maven工具打包，单独生成一个spark配置文件(提交spark作业时进行资源的分配)，每次启动spark job时，会加载这个自定义配置文件
2.4 封装到shell脚本，通过sparktar命令执行解压
三、操作说明
1.支持3种spark提交模式
1.1 本地local模式
1.2 yarn-client
1.3 yarn-cluster
2.压缩输出路径支持2种方式
2.1 默认解压到当前路径
2.2 指定解压输出路径
3.操作命令及说明
3.1操作命令
目前已在172.24.12.118机器配置好了，可通过 sparktar 命令来执行解压操作
sparktar -cluster tos://report/tmp/tar/ tos://report/tmptar/ tmp.produce.properties
3.2命令说明
sparktar 提交方式 解压路径 [解压输出路径] 配置文件
提交方式, 必选参数，提交可选3种方式:  -client, -local, -cluster
解压路径: 必选参数，支持输入路径和文件绝对路径, 比如tos://report/tmp/或tos://report/tmp/xx.gz
输出路径: 非选参数，默认解压到当前路径下
配置文件: 必选参数，约定在/bin/spark-tar/config下创建, 文件格式xxx.produce.properties(后缀名统一写成produce.properties, xxx前缀自定义写，最好是见名知意，后面xxx作为spark作业名称)
四、操作案例
4.1.yarn-cluster模式，将tos://report/tmp/下的压缩包，解压到当前路径
第一步：在/bin/spark-tar/config路径下编写配置文件
vim /bin/spark-tar/config/tmp.produce.properties

# spark.driver内存

spark.driver.memory=2g

# spark.executor的核数,官网推荐4~5个

spark.executor.cores=4

# spark.executor内存,大概1个yarn节点总内存/excuter数,具体还需要结合job的数据集以及划分并行度情况设定

spark.executor.memory=4g

# 对外内存

spark.yarn.executor.memoryOverhead=1g

第二步：执行解压命令
sparktar -cluster tos://report/tmptar/ tmp.produce.properties
4.2.yarn-cluster模式，将tos://report/tmp/压缩包，解压到tos://report/tmp/cluster_out/下
备注：配置文件我可以直接用案例1的，就不再重新创建了，直接进行执行命令
sparktar -cluster tos://report/tmptar/ tos://report/cluster_out/ tmp.produce.properties
spark代码已提交到飞连git仓库，访问地址：https://code.lingyiwanwu.net/douyonghou/spark-tar-tool
sparktar -cluster tos://report/tmptar/ tos://report/cluster_out/ tmp.produce.properties

spark-submit \
--class com.lingyi.data.emr.tartool.util.SevenZByte \
./spark-tar-tool-1.1-SNAPSHOT.jar "file:///home/work/douyonghou/typora_64bit_v1.4.8_setup.7z"


hadoop jar ./spark-tar-tool-1.1-SNAPSHOT.jar "file:///home/work/douyonghou/typora_64bit_v1.4.8_setup.7z"


