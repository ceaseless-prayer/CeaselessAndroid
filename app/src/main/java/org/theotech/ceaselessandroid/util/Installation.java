package org.theotech.ceaselessandroid.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

/**
* Created by chrislim on 1/12/16.*
 * {@link http://android-developers.blogspot.com/2011/03/identifying-app-installations.html}
**/
public class Installation {

    private static final String INSTALLATION = "INSTALLATION";
    private static String sID = null;

    public synchronized static String id(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists()) {
                    writeInstallationFile(installation);
                }
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        try {
            String id = UUID.randomUUID().toString();
            out.write(id.getBytes());
        } finally {
            out.close();
        }

    }

}
