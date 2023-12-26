package com.lingyi.data.emr.tartool.util;

import org.apache.spark.input.PortableDataStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 * @Project：spark-unzfile
 * @name：UnZip
 * @Date：2023/12/18 11:03
 * @Filename：UnZip
 */
public class TarPlug {
    private static final Logger log = LoggerFactory.getLogger(TarPlug.class);
    private static final String SON_PATH = "(/\\S+)";//匹配子路径
    PortableDataStream pds;
    String path;

    public TarPlug(String path, PortableDataStream pds) {
        this.pds = pds;
        this.path = path;
    }

    public void unZFile() throws IOException, URISyntaxException {

        if (this.pds.getPath().endsWith(".zip")) {
            System.out.println("解压zip格式: " + this.path);
            try {
                this.path = this.path.substring(0, path.indexOf(".zip"));
            } catch (java.lang.StringIndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
            }
            new TarArchive(this.path, this.pds).unZip();
        } else if (this.pds.getPath().endsWith(".gz")) {
            System.out.println("解压gzip格式: " + this.path);
            try {
                this.path = this.path.substring(0, this.path.indexOf(".gz"));
            } catch (java.lang.StringIndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
            }
            new TarArchive(this.path, this.pds).unGzip();
        } else if (this.pds.getPath().endsWith(".tar")) {
            System.out.println("解压tar格式: " + this.path);
            try {
                this.path = this.path.substring(0, path.indexOf(".tar"));
            } catch (java.lang.StringIndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
            }
            new TarArchive(this.path, this.pds).unTar();
        } else if (this.pds.getPath().endsWith(".bz2")) {
            System.out.println("解压bz2格式: " + this.path);
            try {
                this.path = this.path.substring(0, path.indexOf(".bz2"));
            } catch (java.lang.StringIndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
            }
            new TarArchive(this.path, this.pds).unBz2();
        } else if (this.pds.getPath().endsWith(".7z")) {
            System.out.println("解压7z格式: " + this.path);
            try {
                this.path = this.path.substring(0, path.indexOf(".7z"));
            } catch (java.lang.StringIndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
            }
            new TarArchive(this.path, this.pds).sevenZ();
        } else System.out.println("该格式目前不支持，只支持tar/gz/zip压缩格式");
    }
}
