package com.lingyi.data.emr.tartool.util;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.File;
import java.io.IOException;

public class WriteSevenZFile {

    public static void write(String inputFile, String writePath, FsClient fsClient) throws IOException {
        String inputFileL = "/tmp/" + inputFile.split("/")[inputFile.split("/").length - 1];
        SevenZFile sevenZFile = new SevenZFile(new File(inputFileL));
        try {
        SevenZArchiveEntry te;
            while ((te = sevenZFile.getNextEntry()) != null ) {
                System.out.println(te.getName());
                if (!te.isDirectory()) {
                    String fileNames = te.getName().replaceAll("[!,=| ]","");
                    byte[] readB = new byte[(int) te.getSize()];
                    sevenZFile.read(readB);
                    fsClient.write(writePath + "/" + fileNames, readB);
                }

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void down(String inputFile, FsClient fsClient) throws IOException {
        String outPathL = "/tmp/" + inputFile.split("/")[inputFile.split("/").length - 1];
        fsClient.copyToLocalFile(inputFile, outPathL);
    }

    public static void main(String[] args) throws IOException {
        String inputFile = "tos://report/tmp/typora_64bit_v1.4.8_setup.7z";
        String outputDirectory = "tos://report/tmp/sevenZout";
        // 先下载
        WriteSevenZFile.down(inputFile, new FsClient());

        // 在本地解压写入hdfs
        WriteSevenZFile.write(inputFile, outputDirectory, new FsClient());
    }
}
