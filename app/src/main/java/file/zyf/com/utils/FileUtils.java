package file.zyf.com.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by 0 on 2018/3/20.
 */

public class FileUtils {
    /**
     * 获取根目录
     */
    public static String getRootDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return "";
        }
    }

    /**
     * 可创建多个文件夹
     * dirPath 文件路径
     */
    public static boolean mkDir(String dirPath) {
        String[] dirArray = dirPath.split("/");
        String pathTemp = "";
        boolean mkdir = false;
        for (int i = 0; i < dirArray.length; i++) {
            pathTemp = pathTemp + "/" + dirArray[i];
            File newF = new File(dirArray[0] + pathTemp);
            if (!newF.exists()) {
                mkdir = newF.mkdir();
            }
        }
        return mkdir;
    }


    /**
     * 创建文件
     * <p>
     * dirpath 文件目录
     * fileName 文件名称
     */
    public static boolean creatFile(String dirPath, String fileName) {
        File file = new File(dirPath, fileName);
        boolean newFile = false;
        if (!file.exists()) {
            try {
                newFile = file.createNewFile();
            } catch (IOException e) {
                newFile = false;
            }
        }
        return newFile;
    }

    /**
     * 创建文件
     * filePath 文件路径
     */
    public static boolean creatFile(String filePath) {
        File file = new File(filePath);
        boolean newFile = false;
        if (!file.exists()) {
            try {
                newFile = file.createNewFile();
            } catch (IOException e) {
                newFile = false;
            }
        }
        return newFile;
    }

    /**
     * 创建文件
     * file 文件
     */
    public static boolean creatFile(File file) {
        boolean newFile = false;
        if (!file.exists()) {
            try {
                newFile = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                newFile = false;
            }
        }
        return newFile;
    }

    /**
     * 删除文件
     * dirpath 文件目录
     * fileName 文件名称
     */
    public static boolean delFile(String dirpath, String fileName) {
        File file = new File(dirpath, fileName);
        boolean delete = false;
        if (file == null || !file.exists() || file.isDirectory()) {
            delete = false;
        } else {
            delete = file.delete();
        }
        return delete;
    }

    /**
     * 删除文件
     * filepath 文件路径
     */
    public static boolean delFile(String filepath) {
        File file = new File(filepath);
        boolean delete = false;
        if (file == null || !file.exists() || file.isDirectory()) {
            delete = false;
        } else {
            delete = file.delete();
        }
        return delete;
    }

    /**
     * 删除文件
     * filepath 文件路径
     */
    public static boolean delFile(File filepath) {
        boolean delete = false;
        if (filepath == null || !filepath.exists() || filepath.isDirectory()) {
            delete = false;
        } else {
            delete = filepath.delete();
        }
        return delete;
    }


    /**
     * Rename the file.
     *
     * @param filePath The path of file.
     * @param newName  The new name of file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean rename(final String filePath, final String newName) {
        return rename(getFileByPath(filePath), newName);
    }
    /**
     * Return the file by path.
     *
     * @param filePath The path of file.
     * @return the file
     */
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }
    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    /**
     * Rename the file.
     *
     * @param file    The file.
     * @param newName The new name of file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean rename(final File file, final String newName) {
        // file is null then return false
        if (file == null) return false;
        // file doesn't exist then return false
        if (!file.exists()) return false;
        // the new name is space then return false
        if (isSpace(newName)) return false;
        // the new name equals old name then return true
        if (newName.equals(file.getName())) return true;
        File newFile = new File(file.getParent() + File.separator + newName);
        // the new name of file exists then return false
        return !newFile.exists()
                && file.renameTo(newFile);
    }

    /**
     * 修改SD卡上的文件或目录名
     * oldFilePath 旧文件或文件夹路径
     * newFilePath 新文件或文件夹路径
     */
    public static boolean renameFile(String oldFilePath, String newFilePath) {
        File oldFile = new File(oldFilePath);
        File newFile = new File(newFilePath);
        return oldFile.renameTo(newFile);
    }

    public static boolean copyFileTo(String srcFile, String destFile) {
        return copyFileTo(new File(srcFile), new File(destFile));

    }

    /**
     * 拷贝一个文件
     * srcFile源文件
     * destFile目标文件
     */
    public static boolean copyFileTo(File srcFile, File destFile) {
        boolean copyFile = false;
        if (!srcFile.exists() || srcFile.isDirectory() || destFile.isDirectory()) {
            copyFile = false;
        } else {
            FileInputStream is = null;
            FileOutputStream os = null;
            try {
                is = new FileInputStream(srcFile);
                os = new FileOutputStream(destFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                copyFile = true;
            } catch (Exception e) {
                copyFile = false;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return copyFile;
    }

    /**
     * 拷贝目录下的所有文件到指定目录
     * srcDir 原目录
     * destDir 目标目录
     */
    public static boolean copyFilesTo(File srcDir, File destDir) {
        if (!srcDir.exists() || !srcDir.isDirectory() || !destDir.isDirectory()) {
            return false;
        }
        File[] srcFiles = srcDir.listFiles();

        for (int i = 0; i < srcFiles.length; i++) {
            if (srcFiles[i].isFile()) {
                File destFile = new File(destDir.getAbsolutePath(), srcFiles[i].getName());
                copyFileTo(srcFiles[i], destFile);
            } else {
                File theDestDir = new File(destDir.getAbsolutePath(), srcFiles[i].getName());
                copyFilesTo(srcFiles[i], theDestDir);
            }

        }
        return true;
    }

    /**
     * 移动一个文件
     * srcFile源文件
     * destFile目标文件
     */
    public static boolean moveFileTo(File srcFile, File destFile) {
        if (!srcFile.exists() || srcFile.isDirectory() || destFile.isDirectory()) {
            return false;
        }
        boolean iscopy = copyFileTo(srcFile, destFile);
        if (!iscopy) {
            return false;
        } else {
            delFile(srcFile);
            return true;
        }
    }

    /**
     * 移动目录
     *
     * @param srcDirName  源目录完整路径
     * @param destDirName 目的目录完整路径
     * @return 目录移动成功返回true，否则返回false
     */
    public static boolean moveDirectory(String srcDirName, String destDirName) {

        File srcDir = new File(srcDirName);
        if (!srcDir.exists() || !srcDir.isDirectory())
            return false;

        File destDir = new File(destDirName);
        if (!destDir.exists())
            destDir.mkdirs();

        /**
         * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹
         * 注意移动文件夹时保持文件夹的树状结构
         */
        File[] sourceFiles = srcDir.listFiles();
        for (File sourceFile : sourceFiles) {
            if (sourceFile.isFile())
                moveFile(sourceFile.getAbsolutePath(), destDir.getAbsolutePath());
            else if (sourceFile.isDirectory())
                moveDirectory(sourceFile.getAbsolutePath(),
                        destDir.getAbsolutePath() + File.separator + sourceFile.getName());
            else
                ;
        }
        return srcDir.delete();
    }

    /**
     * 移动文件
     *
     * @param srcFileName 源文件完整路径
     * @param destDirName 目的目录完整路径
     * @return 文件移动成功返回true，否则返回false
     */
    public static boolean moveFile(String srcFileName, String destDirName) {

        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile())
            return false;

        File destDir = new File(destDirName);
        if (!destDir.exists())
            destDir.mkdirs();

        return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));
    }

    /**
     * 移动目录下的所有文件到指定目录
     * srcDir 原路径
     * destDir 目标路径
     */
    public static boolean moveFilesTo(File srcDir, File destDir) {
        if (!srcDir.exists() || !srcDir.isDirectory() || !destDir.isDirectory()) {
            return false;
        }

        File[] srcDirFiles = srcDir.listFiles();
        for (int i = 0; i < srcDirFiles.length; i++) {
            if (srcDirFiles[i].isFile()) {
                File oneDestFile = new File(destDir.getAbsolutePath(), srcDirFiles[i].getName());
                moveFileTo(srcDirFiles[i], oneDestFile);
            } else {
                File oneDestFile = new File(destDir.getAbsolutePath(), srcDirFiles[i].getName());
                moveFilesTo(srcDirFiles[i], oneDestFile);
            }
        }
        return true;
    }

    /**
     * 文件转byte数组
     * file 文件路径
     */

    public static byte[] file2byte(File file) throws IOException {
        byte[] bytes = null;
        if (file != null) {
            InputStream is = new FileInputStream(file);
            int length = (int) file.length();
            Log.e("file2byte: ", length+"==="+Integer.MAX_VALUE);
            if (length > Integer.MAX_VALUE) {// 当文件的长度超过了int的最大值
                is.close();
                return null;
            }
            bytes = new byte[length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            is.close();
            // 如果得到的字节长度和file实际的长度不一致就可能出错了
            if (offset < bytes.length) {
                System.out.println("file length is error");
                return null;
            }
        }
        return bytes;
    }


    /**
     * 文件读取
     * filePath 文件路径
     */
    public static String readFile(File filePath) {

        BufferedReader bufferedReader = null;
        String fileStr = "";
        if (!filePath.exists() || filePath.isDirectory()) {
            return null;
        }
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            String tempFileStr = "";

            while ((tempFileStr = bufferedReader.readLine()) != null) {
                fileStr += tempFileStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileStr;

    }

    /**
     * 文件读取
     * strPath 文件路径
     */
    public static String readFile(String strPath) {
        return readFile(new File(strPath));
    }

    /**
     * InputStream 转字符串
     */
    public static String readInp(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            int len1;
            while ((len1 = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len1);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException var5) {
        }

        return outputStream.toString();
    }

    /**
     * InputStream转byte数组
     *
     * @param inputStream
     * @return
     */
    public static byte[] inputStreamToByteArray(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            int len1;
            while ((len1 = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len1);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException var5) {
        }

        return outputStream.toByteArray();
    }

    /**
     * BufferedReader 转字符串
     */
    public static String readBuff(BufferedReader bufferedReader) {
        String readerstr = "";
        try {
            String tempstr = "";
            while ((tempstr = bufferedReader.readLine()) != null) {
                readerstr += tempstr;
            }
            return readerstr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * InputStream转文件
     *
     * @param inputStream
     * @param absPath
     */
    public static boolean inputStreamToFile(InputStream inputStream, String absPath) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(absPath, false);
            fos.write(inputStreamToByteArray(inputStream));
            return true;
        } catch (IOException var7) {
            var7.printStackTrace();
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 文件转InputStream
     *
     * @param absPath
     * @return
     */
    public static InputStream file2Inp(String absPath) {
        File file = new File(absPath);
        if (!file.exists()) {
            return null;
        }
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            return is;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }


    }

    /**
     * 写入数据到文件
     *
     * @param filePath
     * @param content
     * @return
     */
    public static boolean writeText(File filePath, String content) {
        creatFile(filePath);
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(filePath));
            bufferedWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static boolean writeText(String filePath, String content) {
        return writeText(filePath, content);
    }


    /**
     * byte数组转文件
     *
     * @param content
     * @param file_name
     */
    public static boolean writeByteArrayToFile(byte[] content, String file_name) {
        try {
            File file = new File(file_name);
            FileOutputStream fileW = new FileOutputStream(file.getCanonicalPath());
            fileW.write(content);
            fileW.close();
        } catch (Exception var4) {
            return false;
        }
        return true;
    }


    /**
     * 追加数据
     *
     * @param filePath
     * @param content
     * @return
     */
    public static boolean appendText(File filePath, String content) {
        creatFile(filePath);
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(filePath, true);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return true;
    }


    /**
     * 追加数据
     *
     * @param filePath
     * @param content
     * @param header   是否在头部追加数据
     */
    public static void appendText(String filePath, String content, boolean header) {
        RandomAccessFile raf = null;
        FileOutputStream tmpOut = null;
        FileInputStream tmpIn = null;
        try {
            File tmp = File.createTempFile("tmp", null);
            tmp.deleteOnExit();//在JVM退出时删除

            raf = new RandomAccessFile(filePath, "rw");
            //创建一个临时文件夹来保存插入点后的数据
            tmpOut = new FileOutputStream(tmp);
            tmpIn = new FileInputStream(tmp);
            long fileLength = 0;
            if (!header) {
                fileLength = raf.length();
            }
            raf.seek(fileLength);
            /**将插入点后的内容读入临时文件夹**/

            byte[] buff = new byte[1024];
            //用于保存临时读取的字节数
            int hasRead = 0;
            //循环读取插入点后的内容
            while ((hasRead = raf.read(buff)) > 0) {
                // 将读取的数据写入临时文件中
                tmpOut.write(buff, 0, hasRead);
            }
            //插入需要指定添加的数据
            raf.seek(fileLength);//返回原来的插入处
            //追加需要追加的内容
            raf.write(content.getBytes());
            //最后追加临时文件中的内容
            while ((hasRead = tmpIn.read(buff)) > 0) {
                raf.write(buff, 0, hasRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tmpOut != null) {
                try {
                    tmpOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (tmpIn != null) {
                try {
                    tmpIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取文件大小
     *
     * @param filePath
     * @return
     */
    public static long getLength(File filePath) {
        if (!filePath.exists()) {
            return -1;
        } else {
            return filePath.length();
        }
    }

    /**
     * 获取文件大小
     *
     * @param filePath
     * @return
     */
    public static long getLength(String filePath) {
        return getLength(new File(filePath));
    }

    /**
     * 获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        return file.getName();

    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean exists(String filePath) {
        if (new File(filePath).exists()) {
            return true;
        }
        return false;
    }


    /**
     * 按文件时间排序
     *
     * @param fliePath
     * @param desc
     * @return
     */
    public static File[] orderByDate(File fliePath, boolean desc) {
        File[] fs = fliePath.listFiles();
        Arrays.sort(fs, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }

            public boolean equals(Object obj) {
                return true;
            }

        });
        if (desc) {
            File[] nfs = new File[fs.length];
            for (int i = fs.length - 1; i > -1; i--) {
                nfs[fs.length - 1 - i] = fs[i];
            }
            return nfs;
        } else {
            return fs;
        }
    }

    /**
     * 按照文件名称排序
     *
     * @param fliePath
     * @param desc
     * @return
     */
    public static File[] orderByName(File fliePath, boolean desc) {
        File[] files = fliePath.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });

        if (desc) {
            File[] nfs = new File[files.length];
            for (int i = files.length - 1; i > -1; i--) {
                nfs[files.length - 1 - i] = files[i];
            }
            return nfs;
        } else {
            return files;
        }

    }

    /**
     * 按照文件大小排序
     *
     * @param fliePath
     */
    public static File[] orderByLength(File fliePath, boolean desc) {
        File[] files = fliePath.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.length() - f2.length();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }

            public boolean equals(Object obj) {
                return true;
            }
        });

        if (desc) {
            File[] nfs = new File[files.length];
            for (int i = files.length - 1; i > -1; i--) {
                nfs[files.length - 1 - i] = files[i];
            }
            return nfs;
        } else {
            return files;
        }
    }


    /**
     * 文件筛选
     *
     * @param files
     * @param filter
     * @return
     */
    public static List<File> filter(File[] files, String filter) {
        List<File> filels = new ArrayList<>();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().contains(filter)) {
                    filels.add(files[i]);
                }
            }
        }
        return filels;
    }

    /**
     * 文件筛选
     *
     * @param file
     * @param filterName
     * @return
     */
    public static File[] fileNameFilter(File file, final String filterName) {
        if (!file.isDirectory()) {
            return null;
        }
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().contains(filterName)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        return files;

    }

    /**
     * 获取文件列表
     *
     * @param fileDir
     */
    public static File[] getFiles(String fileDir) {
        return getFiles(new File(fileDir));
    }

    /**
     * 获取文件列表
     *
     * @param fileDir
     */
    public static File[] getFiles(File fileDir) {
        if (!fileDir.isDirectory()) {
            return null;
        }
        return fileDir.listFiles();
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName
     *            要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 根据byte数组，生成文件
     * @param bfile 文件数组
     * @param filePath 文件存放路径
     * @param fileName 文件名称
     */
    public static void byte2File(byte[] bfile,String filePath,String fileName){
        BufferedOutputStream bos=null;
        FileOutputStream fos=null;
        File file=null;
        try{
            File dir=new File(filePath);
            if(!dir.exists() && !dir.isDirectory()){//判断文件目录是否存在
                dir.mkdirs();
            }
            file=new File(filePath+fileName);
            fos=new FileOutputStream(file);
            bos=new BufferedOutputStream(fos);
            bos.write(bfile);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        finally{
            try{
                if(bos != null){
                    bos.close();
                }
                if(fos != null){
                    fos.close();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
