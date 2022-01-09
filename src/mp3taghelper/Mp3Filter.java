package mp3taghelper;

import java.io.File;
import java.io.FilenameFilter;

public class Mp3Filter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
        String[] nsp = name.split("\\.");
        String ext = nsp[nsp.length - 1];
        return ext.equals("mp3");
    }
}
