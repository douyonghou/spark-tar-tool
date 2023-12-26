import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class testgz {
    public static void main(String[] args) throws IOException {


        String inputFile = "D:/7z.7z";
        String outputDirectory = "D:/7z_out/";
        SevenZFile sevenZFile = new SevenZFile(new File(inputFile));
        SevenZArchiveEntry te;

        while ((te = sevenZFile.getNextEntry()) != null) {
            System.out.println(te.getName());
            if (!te.isDirectory()) {
                String fileNames = te.getName();
                File outputFile = new File(outputDirectory, fileNames);
                if (!outputFile.getParentFile().exists()) {
                    outputFile.getParentFile().mkdirs();
                }
                byte[] b = new byte[(int) te.getSize()];
                sevenZFile.read(b);
//                InputStream inputStream = sevenZFile.getInputStream(te);
//                IOUtils.copy(inputStream, new FileOutputStream(outputFile));
                new FileOutputStream(outputFile).write(b);
            }

        }

    }
}