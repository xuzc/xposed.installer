package de.robv.android.xposed.installer.util;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyFromAssetsUtil {
    public static class Builder {
        private final Context mContext;
        private String mUrl = null;
        private File mDestination = null;
        private String mAssetsName = null;
        private CopyFinishedCallback mCallback = null;
        private DownloadsUtil.MIME_TYPES mMimeType = DownloadsUtil.MIME_TYPES.APK;

        public String getmUrl() {
            return mUrl;
        }
        public File getmDestination() {
            return mDestination;
        }

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setAssetsName(String assetsName) {
            mAssetsName = assetsName;
            return this;
        }

        public Builder setMimeType(DownloadsUtil.MIME_TYPES mimeType) {
            mMimeType = mimeType;
            return this;
        }

        public Builder setUrl(String url) {
            mUrl = url;
            return this;
        }

        public Builder setDestination(File file) {
            mDestination = file;
            return this;
        }

        public Builder setDestinationFromUrl(String subDir) {
            if (mUrl == null) {
                throw new IllegalStateException("URL must be set first");
            }
            return setDestination(DownloadsUtil.getDownloadTargetForUrl(subDir, mUrl));
        }

        public Builder setCallback(CopyFinishedCallback callback) {
            mCallback = callback;
            return this;
        }

        public boolean copy() {
            return add(this);
        }
    }

    private static boolean add(Builder b) {
        Context context = b.mContext;
        if (b.mUrl != null && b.mMimeType.toString().equals(DownloadsUtil.MIME_TYPES.ZIP.toString())) {
            DownloadsUtil.removeAllForUrl(context, b.mUrl);
        }

        if (b.mDestination != null && (b.mDestination.getParentFile().exists() || b.mDestination.getParentFile().mkdirs())) {
            if (b.mMimeType.toString().equals(DownloadsUtil.MIME_TYPES.ZIP.toString())) {
                DownloadsUtil.removeAllForLocalFile(context, b.mDestination);
            }
        } else {
            Toast.makeText(context, "获取ROOT权限失败无法安装xposed框架", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean result = copyBigDataToSD(context, b.mAssetsName, b.mDestination);

        if (result && b.mCallback != null) {
            b.mCallback.onCopyFinished(context, b);

            return true;
        } else {
            Toast.makeText(context, "获取ROOT权限失败无法安装xposed框架", Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    private static boolean copyBigDataToSD(Context context, String filename, File destination) {
        try {
            InputStream myInput;
            OutputStream myOutput = new FileOutputStream(destination);
            myInput = context.getAssets().open(filename);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public interface CopyFinishedCallback {
        void onCopyFinished(Context context, Builder builder);
    }
}
