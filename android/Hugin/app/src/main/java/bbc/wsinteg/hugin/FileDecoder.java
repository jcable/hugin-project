package bbc.wsinteg.hugin;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by cablej01 on 09/12/2016.
 */

public class FileDecoder {

    private static FileDecoder x = null;
    private Context context;

    public static FileDecoder getInstance(Context context) {
        if(x==null) x = new FileDecoder(context);
        return x;
    }

    protected FileDecoder(Context context) {
        this.context = context;
    }

    public void addPart(String name, int position, String data) {
        try {
            FileOutputStream fos = context.openFileOutput(String.format(Locale.US, "%s_%d", name, position), Context.MODE_PRIVATE);
            fos.write(data.getBytes("UTF-8"));
            fos.close();
        } catch (IOException e) {
            Log.e("Internal Storage", e.getMessage());
        }
    }


    public void joinFiles(String name, int parts) {
        try {
            FileOutputStream fo = context.openFileOutput(name, Context.MODE_PRIVATE);
            for(int i = 0; i<parts; i++) {
                FileInputStream fis = context.openFileInput(String.format(Locale.US, "%s_%d", name, i));
                while(true) {
                    int b = fis.read();
                    if(b == -1) {
                        break;
                    }
                    fo.write(b);
                }
                fis.close();
            }
            fo.close();
        } catch (IOException e) {
            Log.e("Internal Storage", e.getMessage());
            // TODO request resend of this part
        }
    }

    public File unzip(InputStream is, File folder) {
        ZipInputStream zis = new ZipInputStream(is);
        try {
            ZipEntry ze = zis.getNextEntry();
            String filename = ze.getName();
            File file = new File(folder, filename);
            FileOutputStream fos = new FileOutputStream(file);
            int n=0;
            byte[] b = new byte[1024];
            while((n=zis.read(b, 0, 1024)) != -1) {
                fos.write(b, 0, n);
            }
            fos.close();
            zis.close();
            is.close();
            return file;
        } catch (IOException e) {
            //Log.e("Internal Storage", e.getMessage());
            try {
                zis.close();
                is.close();
            } catch (IOException e2) {
                Log.e("Internal Storage", e2.getMessage());
            }
            return null;
        }
    }
}
