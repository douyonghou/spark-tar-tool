package com.lingyi.data.emr.tartool.conf;

import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Project：spark-unzfile
 * @name：SSConf
 * @Date：2023/12/18 21:06
 * @Filename：SSConf
 */
public class SSConf {
    public String jobType;
    public String appName;
    public String jobid;

    public SSConf(String jobType, String appName, String jobid) {
        this.jobType = jobType;
        this.appName = appName;
        this.jobid = jobid;
    }

    public SparkSession setSs() throws IOException {
        Properties properties = new Properties();
        SparkSession spark;
        if (jobType.equals("produce")) {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(jobid);
            properties.load(inputStream);
            spark = this.setSsProduce(properties);
        } else {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("dev.properties");
            properties.load(inputStream);
            spark = this.setSsDev();
        }
        return spark;
    }

    public SparkSession setSsDev() {
        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName(appName)
                .getOrCreate();
        // 加载udf函数
//        spark.sql("CREATE TEMPORARY FUNCTION format_decoder AS 'com.litb.udf.FormatDecoder'");
        return spark;
    }

    public SparkSession setSsProduce(Properties properConf) {
        SparkSession spark = SparkSession
                .builder()
                .appName(appName)
                .config("spark.driver.memory", properConf.get("spark.driver.memory").toString())
                .config("spark.executor.memory", properConf.get("spark.executor.memory").toString())
                .config("spark.executor.cores", properConf.get("spark.executor.cores").toString())
                .config("spark.yarn.executor.memoryOverhead", properConf.get("spark.yarn.executor.memoryOverhead").toString())
                .getOrCreate();
        // 加载udf函数
//        spark.sql("CREATE TEMPORARY FUNCTION format_decoder AS 'com.lingyi.data.emr.unzfile'");
        return spark;
    }

}
