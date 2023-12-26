package com.lingyi.data.emr.tartool.util;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.spark.input.PortableDataStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * @Project：spark-unzfile
 * @name：UnZip
 * @Date：2023/12/18 21:03
 * @Filename：UnZip
 */
public class TarArchive {
    private static final Logger log = LoggerFactory.getLogger(TarArchive.class);
    private static final String SON_PATH = "(/.*)";//匹配子路径
    PortableDataStream pds;
    String path;

    public TarArchive(String path, PortableDataStream pds) {
        this.pds = pds;
        this.path = path;
    }

    public void unGzip() throws IOException {
        Pattern sonPathPattern = Pattern.compile(SON_PATH);
        TarInputStream gz = new TarInputStream(new GZIPInputStream(new ByteArrayInputStream(this.pds.toArray())));
        TarEntry te;
        while ((te = gz.getNextEntry()) != null) {
            String sonPathStr = te.getName();
            Matcher sonPathMatcher = sonPathPattern.matcher(sonPathStr);
            if (sonPathMatcher.find() && te.getSize() > 0) {
                sonPathStr = sonPathMatcher.group(0);
                String outPutPath = path + sonPathStr;
                FsClient fsClient = new FsClient();
                if (!fsClient.exists(outPutPath)) {
                    byte[] readBuf = new byte[(int) te.getSize()];
                    gz.read(readBuf);
                    fsClient.write(outPutPath, readBuf);
                    System.out.println("解压到: " + outPutPath);
                } else {
                    log.warn(String.format("你写入一个已存在的文件(%s)，是不允许的", outPutPath));
                }
            }
        }
    }
    public void unZip() throws IOException {
        Pattern sonPathPattern = Pattern.compile(SON_PATH);
        ByteArrayInputStream is = new ByteArrayInputStream(this.pds.toArray());
        ZipArchiveInputStream zipIn = new ZipArchiveInputStream(is, "UTF-8");
        ArchiveEntry nze;
        while ((nze = zipIn.getNextZipEntry()) != null) {
            String sonPathStr = nze.getName();
            Matcher sonPathMatcher = sonPathPattern.matcher(sonPathStr);
            if (sonPathMatcher.find() && nze.getSize() > 0) {
                sonPathStr = sonPathMatcher.group(0);
                String outPutPath = path + sonPathStr;
                FsClient fsClient = new FsClient();
                if (!fsClient.exists(outPutPath)) {
                    byte[] readBuf = new byte[(int) nze.getSize()];
                    zipIn.read(readBuf);
                    fsClient.write(outPutPath, readBuf);
                    System.out.println("解压到: " + outPutPath);
                } else {
                    log.warn(String.format("你写入一个已存在的文件(%s)，是不允许的", outPutPath));
                }
            }
        }
    }

    public void unTar() throws IOException, URISyntaxException {
        Pattern sonPathPattern = Pattern.compile(SON_PATH);
        ByteArrayInputStream is = new ByteArrayInputStream(this.pds.toArray());
        TarArchiveInputStream tarIn = new TarArchiveInputStream(is, "UTF-8");
        ArchiveEntry nze;
        while ((nze = tarIn.getNextTarEntry()) != null) {
            String sonPathStr = nze.getName();
            Matcher sonPathMatcher = sonPathPattern.matcher(sonPathStr);
            if (sonPathMatcher.find() && nze.getSize() > 0) {
                sonPathStr = sonPathMatcher.group(0);
                String outPutPath = path + sonPathStr;
                FsClient fsClient = new FsClient();
                if (!fsClient.exists(outPutPath)) {
                    byte[] readBuf = new byte[(int) nze.getSize()];
                    tarIn.read(readBuf);
                    fsClient.write(outPutPath, readBuf);
                    System.out.println("解压到: " + outPutPath);
                } else {
                    log.warn(String.format("你写入一个已存在的文件(%s)，是不允许的", outPutPath));
                }
            }
        }
    }

    public void sevenZ() throws IOException, URISyntaxException {
        String inPutPath = this.pds.getPath();
        String outPutPath = path;
        // 先下载
        WriteSevenZFile.down(inPutPath, new FsClient());

        // 在本地解压写入hdfs
        WriteSevenZFile.write(inPutPath, outPutPath, new FsClient());
    }


    public void unBz2() throws IOException {
        Pattern sonPathPattern = Pattern.compile(SON_PATH);
        TarInputStream bz2In = new TarInputStream(new BZip2CompressorInputStream(new ByteArrayInputStream(this.pds.toArray())));
        TarEntry te;
        while ((te = bz2In.getNextEntry()) != null) {
            String sonPathStr = te.getName();
            Matcher sonPathMatcher = sonPathPattern.matcher(sonPathStr);
            if (sonPathMatcher.find() && te.getSize() > 0) {
                sonPathStr = sonPathMatcher.group(0);
                String outPutPath = path + sonPathStr;
                FsClient fsClient = new FsClient();
                if (!fsClient.exists(outPutPath)) {
                    byte[] readBuf = new byte[(int) te.getSize()];
                    bz2In.read(readBuf);
                    fsClient.write(outPutPath, readBuf);
                    System.out.println("解压到: " + outPutPath);
                } else {
                    log.warn(String.format("你写入一个已存在的文件(%s)，是不允许的", outPutPath));
                }
            }
        }
    }
}
