package com.sensoro.smartcity.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.fengmap.android.data.FMDataManager;
import com.sensoro.common.utils.ZipUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sensoro on 17/8/15.
 */

public class FileUtil {
    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }

    private static int bufferd = 1024;

    public static void copyAssets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                File file = new File(newPath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * <!-- 在SDCard中创建与删除文件权限 --> <uses-permission
     * android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/> <!--
     * 往SDCard写入数据权限 --> <uses-permission
     * android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     */

    // =================get SDCard information===================
    public static boolean isSdcardAvailable() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public static long getSDAllSizeKB() {
        // get path of sdcard
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // get single block size(Byte)
        long blockSize = sf.getBlockSize();
        // 获取所有数据块数
        long allBlocks = sf.getBlockCount();
        // 返回SD卡大小
        return (allBlocks * blockSize) / 1024; // KB
    }

    /**
     * free size for normal application
     *
     * @return
     */
    public static long getSDAvalibleSizeKB() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        long avaliableSize = sf.getAvailableBlocks();
        return (avaliableSize * blockSize) / 1024;// KB
    }

    // =====================File Operation==========================
    public static boolean isFileExist(String director) {
        File file = new File(Environment.getExternalStorageDirectory()
                + File.separator + director);
        return file.exists();
    }

    /**
     * create multiple director
     *
     * @param director
     * @return
     */
    public static boolean createFile(String director) {
        if (isFileExist(director)) {
            return true;
        } else {
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + director);
            if (!file.mkdirs()) {
                return false;
            }
            return true;
        }
    }

    public static File writeToSDCardFile(String directory, String fileName,
                                         String content, boolean isAppend) {
        return writeToSDCardFile(directory, fileName, content, "", isAppend);
    }

    /**
     * @param directory (you don't need to begin with
     *                  Environment.getExternalStorageDirectory()+File.separator)
     * @param fileName
     * @param content
     * @param encoding  (UTF-8...)
     * @param isAppend  : Context.MODE_APPEND
     * @return
     */
    public static File writeToSDCardFile(String directory, String fileName,
                                         String content, String encoding, boolean isAppend) {
        // mobile SD card path +path
        File file = null;
        OutputStream os = null;
        try {
            if (!createFile(directory)) {
                return file;
            }
            file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + directory + File.separator + fileName);
            os = new FileOutputStream(file, isAppend);
            if (encoding.equals("")) {
                os.write(content.getBytes());
            } else {
                os.write(content.getBytes(encoding));
            }
            os.flush();
        } catch (IOException e) {
            Log.e("FileUtil", "writeToSDCardFile:" + e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * write data from inputstream to SDCard
     */
    public File writeToSDCardFromInput(String directory, String fileName,
                                       InputStream input) {
        File file = null;
        OutputStream os = null;
        try {
            if (createFile(directory)) {
                return file;
            }
            file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + directory + fileName);
            os = new FileOutputStream(file);
            byte[] data = new byte[bufferd];
            int length = -1;
            while ((length = input.read(data)) != -1) {
                os.write(data, 0, length);
            }
            // clear cache
            os.flush();
        } catch (Exception e) {
            Log.e("FileUtil", "" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * this url point to image(jpg)
     *
     * @param url
     * @return image name
     */
    public static String getUrlLastString(String url) {
        String[] str = url.split("/");
        int size = str.length;
        return str[size - 1];
    }


    /**
     * 主题文件类型
     */
    public static final String FILE_TYPE_THEME = ".theme";

    /**
     * 地图文件类型
     */
    public static final String FILE_TYPE_MAP = ".fmap";

    /**
     * 默认地图
     */
    public static final String DEFAULT_MAP_ID = "10347";

    /**
     * 默认主题
     */
    public static final String DEFAULT_THEME_ID = "3007";

    /**
     * 通过主题id获取主题路径
     *
     * @param themeId 主题id
     * @return 主题文件绝对路径
     */
    public static String getThemePath(String themeId) {
        String themePath = FMDataManager.getFMThemeResourceDirectory() + themeId + File.separator + themeId +
                FILE_TYPE_THEME;
        return themePath;
    }

    /**
     * 通过地图id获取地图文件路径
     *
     * @param mapId 地图id
     * @return 地图文件绝对路径
     */
    public static String getMapPath(String mapId) {
        String mapPath = FMDataManager.getFMMapResourceDirectory() + mapId + File.separator + mapId + FILE_TYPE_MAP;
        return mapPath;
    }

    /**
     * 获取默认地图文件路径
     *
     * @param context 上下文
     * @return 默认地图绝对路径
     */
    public static String getDefaultMapPath(Context context) {
        String srcFile = DEFAULT_MAP_ID + FILE_TYPE_MAP;
        String destFile = getMapPath(DEFAULT_MAP_ID);
        try {
            copyAssetsToSdcard(context, srcFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destFile;
    }

    /**
     * 获取默认地图主题路径
     *
     * @param context 上下文
     * @return 默认主题绝对路径
     */
    public static String getDefaultThemePath(Context context) {
        return getThemePath(context, DEFAULT_THEME_ID);
    }

    /**
     * 获取本地主题路径
     *
     * @param context 上下文
     * @param themeId 主题名称
     * @return 本地主题绝对路径
     */
    public static String getThemePath(Context context, String themeId) {
        String path = getThemePath(themeId);
        File file = new File(path);
        if (!file.exists()) {
            copyAssetsThemeToSdcard(context);
        }
        return path;
    }

    /**
     * 将assets目录下theme.zip主题复制、解压到sdcard中
     *
     * @param context 上下文
     */
    public static void copyAssetsThemeToSdcard(Context context) {
        String srcFileName = "theme.zip";
        String themeDir = FMDataManager.getFMThemeResourceDirectory();
        String destFileName = themeDir + srcFileName;
        try {
            copyAssetsToSdcard(context, srcFileName, destFileName);
            // 解压压缩包文件并删除主题压缩包文件
            ZipUtils.unZipFolder(destFileName, themeDir);
            deleteDirectory(destFileName);

            // 遍历目录是否存在主题文件,不存在则解压
            File themeFile = new File(themeDir);
            File[] files = themeFile.listFiles();

            String extension = ".zip";
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(extension)) {
                    File f = new File(file.getName().replace(extension, ""));
                    String fileDir = file.getAbsolutePath();
                    if (!f.exists()) {
                        ZipUtils.unZipFolder(fileDir, themeDir);
                    }
                    deleteDirectory(fileDir);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     *
     * @param fileDir 文件夹路径
     * @return {@code true}删除文件夹/文件成功,{@code false}删除文件夹/文件失败
     */
    public static boolean deleteDirectory(String fileDir) {
        if (fileDir == null) {
            return false;
        }

        File file = new File(fileDir);
        if (file == null || !file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i].getAbsolutePath());
                } else {
                    files[i].delete();
                }
            }
        }

        file.delete();
        return true;
    }

    /**
     * 复制assets下文件到sdcard下文件
     *
     * @param context      上下文
     * @param srcFileName  复制源文件
     * @param destFileName 复制至sdcard文件
     * @throws IOException 发生 I/O 错误
     */
    public static void copyAssetsToSdcard(Context context, String srcFileName, String destFileName) throws IOException {
        File file = new File(destFileName);
        File parentFile = file.getParentFile();
        if (parentFile != null & !parentFile.exists()) {
            parentFile.mkdirs();
        }

        if (!file.exists()) {
            file.createNewFile();
        } else {
            return;
        }

        InputStream is = context.getAssets().open(srcFileName);
        OutputStream os = new FileOutputStream(destFileName);

        byte[] buffer = new byte[1024];
        int byteCount = 0;
        while ((byteCount = is.read(buffer)) != -1) {
            os.write(buffer, 0, byteCount);
        }

        closeSilently(is);
        closeSilently(os);
    }

    /**
     * 关闭流数据
     *
     * @param closeable 数据源
     * @throws IOException 发生 I/O 错误
     */
    public static void closeSilently(Closeable closeable) throws IOException {
        if (closeable != null) {
            closeable.close();
        }
    }


}
