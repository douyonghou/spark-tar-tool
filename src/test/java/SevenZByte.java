import com.lingyi.data.emr.tartool.conf.SSConf;
import com.lingyi.data.emr.tartool.util.FsClient;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SevenZByte implements Serializable {
    private static final String SON_PATH = "(/.*)";//匹配子路径
    String inputPath;

    public SevenZByte(String inputPath) {
        this.inputPath = inputPath;
    }

    public void saveSevenZByte(FsClient fsClient) {
        Pattern sonPathPattern = Pattern.compile(SON_PATH);
        try (SevenZFile sevenZFile = new SevenZFile(new File(inputPath))) {
            ArchiveEntry entry;
            while ((entry = sevenZFile.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                byte[] sevenZByte = new byte[(int) entry.getSize()];
                sevenZFile.read(sevenZByte, 0, sevenZByte.length);
                String sonPathStr = entry.getName();
                Matcher sonPathMatcher = sonPathPattern.matcher(sonPathStr);
                if (sonPathMatcher.find() && entry.getSize() > 0) {
                    this.inputPath = this.inputPath.substring(0, this.inputPath.indexOf(".7z"));
                    sonPathStr = sonPathMatcher.group(0);
                    String outPutPath = this.inputPath + sonPathStr;
                    System.out.println(outPutPath);
                    fsClient.write("tos://report/tmp/tarout",sevenZByte);
                }
            }

        } catch (IOException e) {
            System.out.println("压缩包下的子文件格式7z解压器不支持: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String inputPath = args[0];
        Pattern sonPathPattern = Pattern.compile(SON_PATH);
        FsClient fsClient = new FsClient();
        try (SevenZFile sevenZFile = new SevenZFile(new File(inputPath))) {
            ArchiveEntry entry;
            inputPath = inputPath.substring(0, inputPath.indexOf(".7z"));
            while ((entry = sevenZFile.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                byte[] sevenZByte = new byte[(int) entry.getSize()];
                sevenZFile.read(sevenZByte, 0, sevenZByte.length);
                String sonPathStr = entry.getName();
                Matcher sonPathMatcher = sonPathPattern.matcher(sonPathStr);
                if (sonPathMatcher.find() && entry.getSize() > 0) {

                    sonPathStr = sonPathMatcher.group(0);
                    String outPutPath = inputPath + sonPathStr;
                    System.out.println("---------------" + outPutPath + "--------------------------------");
                    SparkContext sc = new SSConf("produce", "sparkTarTool-", "tmp.produce.properties").setSs().sparkContext();
                    JavaSparkContext jsc = new JavaSparkContext(sc);
                    JavaRDD<byte[]> parallelize = jsc.parallelize(Arrays.asList(sevenZByte));
                    parallelize.foreach(sevenZByterdd -> {
                        fsClient.write("tos://report/tmp/tarout",sevenZByterdd);
                    });

                }
            }

        } catch (IOException e) {
            System.out.println("压缩包下的子文件格式7z解压器不支持: " + e.getMessage());
        }



    }
}
