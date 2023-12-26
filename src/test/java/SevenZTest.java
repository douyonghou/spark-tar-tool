

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.*;
import java.net.URI;

public class SevenZTest {
    public static void main(String[] args) {

        String inputFile = "D:/typora_64bit_v1.4.8_setup.7z"; // 要解压缩的7z文件路径

        SparkSession spark = SparkSession.builder()
                .appName("SevenZipExample")
                .master("local[*]")
                .getOrCreate();

        // 读取7z压缩文件
        Dataset<Row> df = spark.read().format("binaryFile").option("path", "file:/D:/typora_64bit_v1.4.8_setup.bz2").load();
        df.foreach(x -> {
            Configuration conf = new Configuration();
            FileSystem fs = FileSystem.get(conf);
            CompressionCodecFactory codecFactory = new CompressionCodecFactory(conf);
            Path path = new Path(x.getString(0));
            CompressionCodec codec = codecFactory.getCodec(path);
            CompressionInputStream inputStream = codec.createInputStream(fs.open(path));


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            IOUtils.copyBytes(inputStream, outputStream, conf);
            System.out.println(outputStream.toString());

            FSDataOutputStream fsDataOutputStream = fs.create(path);

//      val data: Array[Byte] = outputStream.toByteArray()
//      data.foreach(s=> println(s.toString))
//      println(data.toString)
        });


    }


}
