import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;

public class GZIPArchiveInputStream extends ArchiveInputStream {

    @Override
    public ArchiveEntry getNextEntry() throws IOException {
        return null;
    }
}
