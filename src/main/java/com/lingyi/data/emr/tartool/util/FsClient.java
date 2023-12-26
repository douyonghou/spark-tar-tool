package com.lingyi.data.emr.tartool.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FsClient {
    String path;
    byte[] readBuf;

    FileStatus[] fileStatuses;

    public FileStatus[] getFileStatuses() {
        return fileStatuses;
    }

    public FsClient() {
    }

    public FsClient(String path, byte[] readBuf) {
        this.path = path;
        this.readBuf = readBuf;
    }

    public boolean isDirectory(String path) {
        try {
            Configuration conf = new Configuration();
            Path pathExists = new Path(path);
            FileSystem fs = FileSystem.get(new URI(path), conf);
            if (fs.isDirectory(pathExists)) {
//                this.fileStatuses = fs.listStatus(pathExists);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean exists(String path) {
        try {
            Configuration conf = new Configuration();
            Path pathExists = new Path(path);
            FileSystem fs = FileSystem.get(new URI(path), conf);
            return fs.exists(pathExists);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(String path, byte[] readBuf) {
        Configuration conf = new Configuration();
        Path writeHDFSPath = new Path(path);
        FileSystem fs = null;
        try {
            fs = FileSystem.get(new URI(path), conf);
            fs.createNewFile(writeHDFSPath);
            FSDataOutputStream out = fs.create(writeHDFSPath);
            out.write(readBuf);
            out.flush();
            out.close();
            fs.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void copyToLocalFile(String inPath, String outPath) {
        try {
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS","tos://report/");
            Path writeOutPath = new Path(outPath);
            Path writeInPath = new Path(inPath);
            FileSystem fs = FileSystem.get(conf);
            File file = new File(outPath);
            if(file.exists()){
                System.out.println("下载到本地路径已存在:" + outPath);
            }else {
                System.out.println("下载到本地路径:" + outPath);
                fs.copyToLocalFile(writeInPath, writeOutPath);
            }
            fs.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

//    public boolean open(String path){
//        Configuration conf = new Configuration();
//        Path writeHDFSPath = new Path(path);
//        FileSystem fs = null;
//        try {
//            fs = FileSystem.get(new URI(path), conf);
//            fs.createNewFile(writeHDFSPath);
//            fs.open(path)
//            FSDataOutputStream out = fs.create(writeHDFSPath);
//            out.write(readBuf);
//            out.flush();
//            out.close();
//            fs.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
