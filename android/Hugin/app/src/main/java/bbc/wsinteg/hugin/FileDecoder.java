package bbc.wsinteg.hugin;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by cablej01 on 09/12/2016.
 */

public class FileDecoder {

    private static FileDecoder x = null;
    private Context context;

    public static FileDecoder getDecoder(Context context) {
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

    public Uri getFile(String name, int lastpart) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File folder = new File(context.getFilesDir(), "docs");
            boolean ok = folder.mkdir();
            File f = export(name, lastpart, folder);
            if(f != null) {
                return FileProvider.getUriForFile(context, "bbc.wsinteg.hugin.fileprovider", f);
            }
        }
        else {
            File folder = context.getExternalFilesDir("docs");
            boolean ok = folder.mkdir();
            File f = export(name, lastpart, folder);
            if(f != null) {
                return Uri.fromFile(f);
            }
        }
        return null;
    }

    private String getFromFiles(String name, int lastpart) {
        StringBuilder sb = new StringBuilder();
        try {
            for(int i = 0; i<=lastpart; i++) {
                FileInputStream fis = context.openFileInput(String.format(Locale.US, "%s_%d", name, i));
                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                reader.close();
                fis.close();
            }
        } catch (IOException e) {
            Log.e("Internal Storage", e.getMessage());
            // TODO request resend of this part
            return null;
        }
        return sb.toString();
    }

    private File export(String name, int lastpart, File folder) {
        String s = getFromFiles(name, lastpart);
        InputStream is = new ByteArrayInputStream(Base64.decode(s, Base64.DEFAULT));
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
            Log.e("Internal Storage", e.getMessage());
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
