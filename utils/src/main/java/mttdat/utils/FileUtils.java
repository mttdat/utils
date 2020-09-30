package mttdat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by swagsoft on 5/24/17.
 */

public class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * @param mode:
     *          Context.MODE_APPEND     --> write more to exist file.
     *          Context.MODE_PRIVATE    --> only available within app.
     *          FileProvider with the FLAG_GRANT_READ_URI_PERMISSION    --> share file.
     * */
    public static void writeFileInternal(Context context, int mode, String fileName, String data) {

        try {

            // 2 lines of these are the same with one line below.
            // Notice:
            //      + Instead using getFilesDir(), you can use getCacheDir() to get directory
            // of cache place in internal storage.
            //      + openFileOutput() will create in app private storage, not in cache.

//                File file = new File(context.getFilesDir(), fileName);
//                FileOutputStream fos = new FileOutputStream(file);

            FileOutputStream fos = context.openFileOutput(fileName, mode);

            // Instantiate a stream writer.
            OutputStreamWriter out = new OutputStreamWriter(fos);

            // Add data.
            if(mode == Context.MODE_APPEND) {
                out.append(data).append("\n");
            }else {
                out.write(data);
            }

            // Close stream writer.
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFileInternal(Context context, String fileName, InputStream inputStream) {

        try {

            // 2 lines of these are the same with one line below.
            // Notice:
            //      + Instead using getFilesDir(), you can use getCacheDir() to get directory
            // of cache place in internal storage.
            //      + openFileOutput() will create in app private storage, not in cache.

//                File file = new File(context.getFilesDir(), fileName);
//                FileOutputStream fos = new FileOutputStream(file);

            FileOutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);

            // Add data.
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

            int current;
            while ((current = bufferedInputStream.read()) != -1) {
                out.write(current);
            }
            out.flush();

            // Close stream writer.
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeBitmapCache(Context context, String fileName, Bitmap bitmap){

        try {
            File file = new File(context.getCacheDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);

            // Compress image to bytes.
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, bytes);

            fos.write(bytes.toByteArray());

            // Close stream writer.
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFileInternal(Context context, String fileName) {

        try {

            // 2 lines of these are the same with one line below.
            // Notice:
            //      + Instead using getFilesDir(), you can use getCacheDir() to get directory
            // of cache place in internal storage.
            //      + openFileInput will open a file in app private storage, not in cache.

//            File file = new File(context.getFilesDir(), fileName);
//            FileInputStream fis = new FileInputStream(file);

            FileInputStream fis = context.openFileInput(fileName);

            // Instantiate a buffer reader. (Buffer )
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(fis));

            String s;
            StringBuilder fileContentStrBuilder = new StringBuilder();

            // Read every lines in file.
            while ((s = bufferedReader.readLine()) != null) {
                fileContentStrBuilder.append(s);
            }

            // Close buffer reader.
            bufferedReader.close();

            return fileContentStrBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static InputStream readFileInternalAsStream(Context context, String fileName) {

        try {

            // 2 lines of these are the same with one line below.
            // Notice:
            //      + Instead using getFilesDir(), you can use getCacheDir() to get directory
            // of cache place in internal storage.
            //      + openFileInput will open a file in app private storage, not in cache.

//            File file = new File(context.getFilesDir(), fileName);
//            FileInputStream fis = new FileInputStream(file);

            return context.openFileInput(fileName);

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static InputStream readFileCacheAsStream(Context context, String fileName) {

        try {

            // 2 lines of these are the same with one line below.
            // Notice:
            //      + Instead using getFilesDir(), you can use getCacheDir() to get directory
            // of cache place in internal storage.
            //      + openFileInput will open a file in app private storage, not in cache.

            File file = new File(context.getCacheDir(), fileName);
            return new FileInputStream(file);

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public boolean deleteFileInternal(Context context, String fileName){
        // If the file is saved on internal storage, you can also ask the Context to locate and delete a file by calling deleteFile()
        return context.deleteFile(fileName);
    }

    public static String getInternalDirectory(Context context, String fileName){
        return (new File(context.getFilesDir(), fileName)).getAbsolutePath();
    }

    public static String getCacheDirectory(Context context, String fileName){
        return (new File(context.getCacheDir(), fileName)).getAbsolutePath();
    }

    /* ******************************************************************************************** *
     *                                                                                              *
     *  - If your app uses the WRITE_EXTERNAL_STORAGE permission, then it implicitly has permission *
     *  to read the external storage as well.                                                       *
     *  - Must declare READ_EXTERNAL_STORAGE or WRITE_EXTERNAL_STORAGE before manipulate with       *
     *  external storage.                                                                           *
     *  - Handle file in private external storage in low api (below 18), don't require permission.  *
     *  - Don't be confused external storage with SD external card, since internal SD card is       *
     *  considered as external storage. And internal SD card is a default external storage.         *
     *                                                                                              *
     * ******************************************************************************************** */

    // Checks if external storage is available for read and write.
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Checks if external storage is available to at least read.
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable(){
        if(!isExternalStorageWritable() || !isExternalStorageReadable()){
            return false;
        }

        return true;
    }

    /** Write to a public external directory.
     *  @param mode:
     *          Context.MODE_APPEND     --> write more to exist file.
     *          Context.MODE_PRIVATE    --> only available within app.
     *          FileProvider with the FLAG_GRANT_READ_URI_PERMISSION    --> share file.
     *  @param mainDir: representing the appropriate directory on the external storage ( Environment.DIRECTORY_MUSIC, ...)
     *  @param subFolder: usually an app name to distinguish with another app.
     *  @param fileName: ".nomedia" + fileName to hide it from MediaStore scanning.
     */
    public static void writeFileExternalPublic(String mainDir, String subFolder, int mode, String fileName, String data){

        if(!isExternalStorageAvailable()){
            Log.e(TAG, "External storage is not available.");
            return;
        }

        // Get the directory for the user's public mainDir directory.
        String directory = getExternalPublicDirectory(mainDir, subFolder);
        File folder = new File(directory);

        // If directory doesn't exist, create it.
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, fileName);

//        Log.d(TAG, "File directory: " + file.getAbsolutePath());

        try {
            FileOutputStream fos;
            if(mode == Context.MODE_APPEND) {
                fos = new FileOutputStream(file, true);
            }else {
                fos = new FileOutputStream(file);
            }

            // Instantiate a stream writer.
            OutputStreamWriter out = new OutputStreamWriter(fos);

            // Add data.
            if(mode == Context.MODE_APPEND) {
                out.append(data + "\n");
            }else {
                out.write(data);
            }

            // Close stream writer.
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeBitmapExternalPublic(String mainDir, String subFolder, String fileName, Bitmap bitmap){

        // Get the directory for the user's public mainDir directory.
        String directory = getExternalPublicDirectory(mainDir, subFolder);
        File folder = new File(directory);

        // If directory doesn't exist, create it.
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);

            // Compress image to bytes.
//            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//            fos.write(bytes.toByteArray());

            // Compress image to file (directly).
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);

            // Close stream writer.
            fos.flush();
            fos.close();

            // Recycle bitmap.
//            bitmap.recycle();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getExternalPublicDirectory(String mainDir, String subFolder){

        File root;
        if(mainDir.isEmpty()){
            root = Environment.getExternalStorageDirectory();
        }else {
            root = Environment.getExternalStoragePublicDirectory(mainDir);
        }

        return root + File.separator  + subFolder;
    }

    public static String readFileExternalPublic(String mainDir, String subFolder, String fileName) {

        try {

            String directory = getExternalPublicDirectory(mainDir, subFolder);
            File folder = new File(directory);

            File file = new File(folder, fileName);

            // If file doesn't exist.
            if (!file.exists()) {
                Log.e(TAG, "File doesn't exist.");
                return null;
            }

            FileInputStream fis = new FileInputStream(file);

            // Instantiate a buffer reader. (Buffer )
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(fis));

            String s;
            StringBuilder fileContentStrBuilder = new StringBuilder();

            // Read every lines in file.
            while ((s = bufferedReader.readLine()) != null) {
                fileContentStrBuilder.append(s);
            }

            // Close buffer reader.
            bufferedReader.close();

            return fileContentStrBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static InputStream readFileExternalPublicAsStream(String mainDir, String subFolder, String fileName) {

        try {

            String directory = getExternalPublicDirectory(mainDir, subFolder);
            File folder = new File(directory);

            File file = new File(folder, fileName);

            // If file doesn't exist.
            if (!file.exists()) {
                Log.e(TAG, "File doesn't exist.");
                return null;
            }

            return new FileInputStream(file);

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public boolean deleteFileExternalPublic(String mainDir, String subFolder, String fileName){

        String directory = getExternalPublicDirectory(mainDir, subFolder);
        File folder = new File(directory);

        File file = new File(folder, fileName);

        // If file doesn't exist.
        if (!file.exists()) {
            Log.e(TAG, "File doesn't exist.");
            return true;
        }
        return file.delete();
    }

    /** Write to a public external directory.
     *  @param mode:
     *          Context.MODE_APPEND     --> write more to exist file.
     *          Context.MODE_PRIVATE    --> only available within app.
     *          FileProvider with the FLAG_GRANT_READ_URI_PERMISSION    --> share file.
     *  @param mainDir: - Representing the appropriate directory on the external storage ( Environment.DIRECTORY_MUSIC, ...)
     *                  - It can be null --> represent that directory as a parent file of private external storage in the app.
     *  @param subFolder: usually an app name to distinguish with another app.
     */
    public static void writeFileExternalPrivate(Context context, String mainDir, String subFolder, int mode, String fileName, String data){

        // Get the directory for the user's private mainDir directory.
        String directory = context.getExternalFilesDir(mainDir) + File.separator  + subFolder;
        File folder = new File(directory);

        // If directory doesn't exist, create it.
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, fileName);

        try {
            FileOutputStream fos;
            if(mode == Context.MODE_APPEND) {
                fos = new FileOutputStream(file, true);
            }else {
                fos = new FileOutputStream(file);
            }

            // Instantiate a stream writer.
            OutputStreamWriter out = new OutputStreamWriter(fos);

            // Add data.
            if(mode == Context.MODE_APPEND) {
                out.append(data + "\n");
            }else {
                out.write(data);
            }

            // Close stream writer.
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeBitmapExternalPrivate(Context context, String mainDir, String subFolder, String fileName, Bitmap bitmap){

        // Get the directory for the user's private mainDir directory.
        String directory = context.getExternalFilesDir(mainDir) + File.separator  + subFolder;
        File folder = new File(directory);

        // If directory doesn't exist, create it.
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);

            // Compress image to bytes.
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

            fos.write(bytes.toByteArray());

            // Close stream writer.
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getExternalPrivateDirectory(Context context, String mainDir, String subFolder){
        return context.getExternalFilesDir(mainDir) + File.separator  + subFolder;
    }

    public static String readFileExternalPrivate(Context context, String mainDir, String subFolder, String fileName) {

        try {

            String directory = getExternalPrivateDirectory(context, mainDir, subFolder);
            File folder = new File(directory);

            File file = new File(folder, fileName);

            // If file doesn't exist.
            if (!file.exists()) {
                Log.e(TAG, "File doesn't exist.");
                return null;
            }

            FileInputStream fis = new FileInputStream(file);

            // Instantiate a buffer reader. (Buffer )
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(fis));

            String s;
            StringBuilder fileContentStrBuilder = new StringBuilder();

            // Read every lines in file.
            while ((s = bufferedReader.readLine()) != null) {
                fileContentStrBuilder.append(s);
            }

            // Close buffer reader.
            bufferedReader.close();

            return fileContentStrBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static InputStream readFileExternalPrivateAsStream(Context context, String mainDir, String subFolder, String fileName) {

        try {

            String directory = getExternalPrivateDirectory(context, mainDir, subFolder);
            File folder = new File(directory);

            File file = new File(folder, fileName);

            // If file doesn't exist.
            if (!file.exists()) {
                Log.e(TAG, "File doesn't exist.");
                return null;
            }

            return new FileInputStream(file);

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static boolean deleteFileExternalPrivate(Context context, String mainDir, String subFolder, String fileName){

        String directory = getExternalPrivateDirectory(context, mainDir, subFolder);
        File folder = new File(directory);

        File file = new File(folder, fileName);

        // If file doesn't exist.
        if (!file.exists()) {
            Log.e(TAG, "File doesn't exist.");
            return true;
        }
        return file.delete();
    }

    /* ******************************************************************************************** */

    // Looking for File directory of all external cards (including onboard sd card).
    public static ArrayList<File> getExternalSdCardDirectory(){

        // Retrieve the primary External Storage (usually onboard sd card, it's based on user setting).
        final File primaryExternalStorage = Environment.getExternalStorageDirectory();

        // Primary external storage (onboard sd card) usually has path: [storage]/emulated/0
        File externalStorageRoot = primaryExternalStorage.getParentFile().getParentFile();

        // Get list folders under externalStorageRoot (which includes primaryExternalStorage folder).
        File[] files = externalStorageRoot.listFiles();

        ArrayList<File> listStorage = new ArrayList<>();

        for (File file : files) {

            // it is a real directory (not a USB drive)...
            if ( file.isDirectory() && file.canRead() && (file.listFiles().length > 0) ) {
                listStorage.add(file);
            }
        }

        return listStorage;
    }

    // Base on the list of file directory gotten from getExternalSdCardDirectory() method,
    // you can choose what file directory/ file to read or write.
    public static void writeFile(File directory, String fileName, int mode, String data){
        try {

            // Create file.
            File file = new File(directory, fileName);

            FileOutputStream fos;
            if(mode == Context.MODE_APPEND) {
                fos = new FileOutputStream(file, true);
            }else {
                fos = new FileOutputStream(file);
            }

            // Instantiate a stream writer.
            OutputStreamWriter out = new OutputStreamWriter(fos);

            // Add data.
            if(mode == Context.MODE_APPEND) {
                out.append(data + "\n");
            }else {
                out.write(data);
            }

            // Close stream writer.
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFile(File directory, String fileName){
        try {

            // Create file.
            File file = new File(directory, fileName);

            FileInputStream fis = new FileInputStream(file);

            // Instantiate a buffer reader. (Buffer )
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(fis));

            String s;
            StringBuilder fileContentStrBuilder = new StringBuilder();

            // Read every lines in file.
            while ((s = bufferedReader.readLine()) != null) {
                fileContentStrBuilder.append(s);
            }

            // Close buffer reader.
            bufferedReader.close();

            return fileContentStrBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static String readFile(File file){
        try {

            FileInputStream fis = new FileInputStream(file);

            // Instantiate a buffer reader. (Buffer )
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(fis));

            String s;
            StringBuilder fileContentStrBuilder = new StringBuilder();

            // Read every lines in file.
            while ((s = bufferedReader.readLine()) != null) {
                fileContentStrBuilder.append(s);
            }

            // Close buffer reader.
            bufferedReader.close();

            return fileContentStrBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static InputStream readFileAsStream(String fileDirectory){
        try {

            return new FileInputStream(new File(fileDirectory));

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static InputStream readFileAsStream(File file){
        try {

            return new FileInputStream(file);

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static void deleteFile(File file){

        if(file.exists()) {
            file.delete();
        }
    }

    public static void deleteFile(String filePath){

        File file = new File(filePath);

        if(file.exists()) {
            file.delete();
        }
    }

    /* MIME stuff */

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /* Asset stuff */

    // Khi nào cần viết lại, chưa hoàn thiện.
    private boolean listAssetFiles(Context context, String path) {

        String [] list;
        try {
            list = context.getAssets().list(path);
            if (list.length > 0) {

                // This is a folder.
                for (String file : list) {
                    if (!listAssetFiles(context, path + "/" + file))
                        return false;
                    else {
                        // This is a file
                        // TODO: add file name to an array list
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static String getFileExtension(String filePath){
        return filePath.substring(filePath.lastIndexOf(".") + 1);
    }
}
