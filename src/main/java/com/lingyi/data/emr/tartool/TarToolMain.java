package com.lingyi.data.emr.tartool;

import com.lingyi.data.emr.tartool.conf.SSConf;
import com.lingyi.data.emr.tartool.util.FsClient;
import com.lingyi.data.emr.tartool.util.TarPlug;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.input.PortableDataStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.*;
/**
 * @Project：spark-unzfile
 * @name：UnZFileMain
 * @Date：2023/12/18 10:31
 * @Filename：UnZFileMain
 */
public class TarToolMain {
    private static final Logger log = LoggerFactory.getLogger(TarToolMain.class);
    public static void main(String[] args) throws IOException, URISyntaxException {

        String inputPath = "";
        String outPath = null;
        String jobid = "produce.properties";
        if (args.length <= 3 && args.length >= 2) {
            inputPath = args[0];
            outPath = args.length == 3 ? args[1] : null;
            jobid = args.length == 3 ? args[2] : args[1];

        } else {
            System.err.println("Input parameter type: Absolute Path");
            return;
        }
//        inputPath = "file:/D:/7z.7z";

        FsClient fs = new FsClient();
        if(fs.isDirectory(inputPath)){
            System.out.println("是一个目录");
            Configuration conf = new Configuration();
            Path pathExists = new Path(inputPath);
            FileSystem fs1 = FileSystem.get(new URI(inputPath),conf);

            RemoteIterator<LocatedFileStatus> listedFiles = fs1.listFiles(pathExists,true);
            while (listedFiles.hasNext()) {
                LocatedFileStatus fileStatus = listedFiles.next();
                inputPath = fileStatus.getPath().toString();
                String inputPathSon = fileStatus.getPath().getName();
                SparkContext sc = new SSConf("produce", "sparkTarTool-" + jobid, jobid).setSs().sparkContext();
                JavaSparkContext jsc = new JavaSparkContext(sc);
                JavaPairRDD<String, PortableDataStream> binaryFiles = jsc.binaryFiles(inputPath);
                String finalOutPath = outPath;
                System.out.println(String.format("开始解压[%s]: %s", System.currentTimeMillis(), inputPath));
                binaryFiles.foreach(x -> {
                    String path = x._1();
                    PortableDataStream pds = x._2();
                    String out = finalOutPath == null ? path : finalOutPath + "/" + inputPathSon;
                    TarPlug tarPlug = new TarPlug(out, pds);
                    tarPlug.unZFile();
                });
                jsc.close();
                System.out.println(String.format("解压完成[%s]: %s", System.currentTimeMillis(),outPath));
            }

        }else{
            System.out.println("是一个文件");
            SparkContext sc = new SSConf("produce", "sparkTarTool-" + jobid, jobid).setSs().sparkContext();
            JavaSparkContext jsc = new JavaSparkContext(sc);
            JavaPairRDD<String, PortableDataStream> binaryFiles = jsc.binaryFiles(inputPath);
            String finalOutPath = outPath;
            System.out.println(String.format("开始解压[%s]: %s", System.currentTimeMillis(), inputPath));
            binaryFiles.foreach(x -> {
                String path = x._1();
                PortableDataStream pds = x._2();
                String out = finalOutPath == null ? path : finalOutPath;
                TarPlug tarPlug = new TarPlug(out, pds);
                tarPlug.unZFile();
            });
            jsc.close();
            System.out.println(String.format("解压完成[%s]: %s", System.currentTimeMillis(),outPath));
        }

    }
}


