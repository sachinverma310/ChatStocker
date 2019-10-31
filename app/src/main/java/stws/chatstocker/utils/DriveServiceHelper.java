package stws.chatstocker.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import stws.chatstocker.R;
import stws.chatstocker.interfaces.FileRecievedListener;
import stws.chatstocker.model.FileDetails;
import stws.chatstocker.view.LoginActivity;

public class DriveServiceHelper {

    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private static Drive mDriveService=null;
    private static DriveServiceHelper driveServiceHelper=null;
    private DriveServiceHelper(){

    }
    public static DriveServiceHelper getInstance(Drive driveService){
        if (driveServiceHelper==null){
            mDriveService = driveService;
            driveServiceHelper=new DriveServiceHelper();
        }
        return driveServiceHelper;
    }
//    public DriveServiceHelper(Drive driveService) {
//
//    }


    /**
     * Creates a text file in the user's My Drive folder and returns its file ID.
     */
    public Task<String> createFile() {
        return Tasks.call(mExecutor, () -> {
                File metadata = new File()
                        .setParents(Collections.singletonList("root"))
                        .setMimeType("text/plain")
                        .setName("Untitled file");

                File googleFile = mDriveService.files().create(metadata).execute();
                if (googleFile == null) {
                    throw new IOException("Null result when requesting file creation.");
                }

                return googleFile.getId();
            });
    }
    public void uploadFile(java.io.File path,String folderId,String type)
            throws Exception {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
//                    while(true) {
                    List<File> files = mDriveService.files().list().setQ("mimeType = 'application/vnd.google-apps.folder'").execute().getFiles();
//                        File fileMetadata = new File();
//                        fileMetadata.setName(path.getName());
//                        java.io.File filePath = new java.io.File(path.getAbsolutePath());
//                        FileContent mediaContent = new FileContent("image/jpeg", filePath);
//                        File file = mDriveService.files().create(fileMetadata, mediaContent)
//                                .setFields("id")
//
//                                .execute();
//                        System.out.println("File ID: " + file.getId());

//                    String folderId = "0BwwA4oUTeiV1TGRPeTVjaWRDY1E";
                    File fileMetadata = new File();
                    fileMetadata.setName(path.getName());

                    fileMetadata.setParents(Collections.singletonList(folderId));
                    java.io.File filePath = new java.io.File(path.getAbsolutePath());
                    FileContent mediaContent = new FileContent(type, filePath);

                    File file = mDriveService.files().create(fileMetadata, mediaContent)
                            .setFields("id, parents")
                            .execute();
                    System.out.println("File ID: " + file.getId());
//                    }
                }  catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
//        logDebug("upload file...");


    }
    /**
     * Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and
     * contents.
     */
    public  List<File> listAllFiles(){
        List<File> result = new ArrayList<File>();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {

                Drive.Files.List request = null;
                try {
                    request = mDriveService.files().list();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                do {
                    try {
                        FileList files = request.execute();

                        result.addAll(files.getFiles());
                        request.setPageToken(files.getNextPageToken());
                    } catch (IOException e) {
                        System.out.println("An error occurred: " + e);
                        request.setPageToken(null);
                    }
                } while (request.getPageToken() != null &&
                        request.getPageToken().length() > 0);
            }
        });
        thread.start();
        return result;
    }

    public void createFolder(String fileName,java.io.File path,String type) {

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                File fileMetadata = new File();
                fileMetadata.setName(fileName);
                fileMetadata.setMimeType("application/vnd.google-apps.folder");

                File file = null;
                try {
                    file = mDriveService.files().create(fileMetadata)
                            .setFields("id")
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    uploadFile(path,file.getId(),type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Folder ID: " + file.getId());
            }
        });
        thread.start();

    }


    public Task<Pair<String, String>> readFile(String fileId) {
        return Tasks.call(mExecutor, () -> {
                // Retrieve the metadata as a File object.
                File metadata = mDriveService.files().get(fileId).execute();
                String name = metadata.getName();

                // Stream the file contents to a String.
                try (InputStream is = mDriveService.files().get(fileId).executeMediaAsInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    String contents = stringBuilder.toString();

                    return Pair.create(name, contents);
                }
            });
    }

    /**
     * Updates the file identified by {@code fileId} with the given {@code name} and {@code
     * content}.
     */
    public Task<Void> saveFile(String fileId, String name, String content) {
        return Tasks.call(mExecutor, () -> {
                // Create a File containing any metadata changes.
                File metadata = new File().setName(name);

                // Convert content to an AbstractInputStreamContent instance.
                ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain", content);

                // Update the metadata and contents.
                mDriveService.files().update(fileId, metadata, contentStream).execute();
                return null;
            });
    }

    /**
     * Returns a {@link FileList} containing all the visible files in the user's My Drive.
     *
     * <p>The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the <a href="https://play.google.com/apps/publish">Google
     * Developer's Console</a> and be submitted to Google for verification.</p>
     */
    public Task<FileList> queryFiles() {
        return Tasks.call(mExecutor, () ->
                mDriveService.files().list().setSpaces("drive").execute());
    }

    /**
     * Returns an {@link Intent} for opening the Storage Access Framework file picker.
     */
    public Intent createFilePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        return intent;
    }

    /**
     * Opens the file at the {@code uri} returned by a Storage Access Framework {@link Intent}
     * created by {@link #createFilePickerIntent()} using the given {@code contentResolver}.
     */
    public Task<Pair<String, String>> openFileUsingStorageAccessFramework(
            ContentResolver contentResolver, Uri uri) {
        return Tasks.call(mExecutor, () -> {
                // Retrieve the document's display name from its metadata.
                String name;
                try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        name = cursor.getString(nameIndex);
                    } else {
                        throw new IOException("Empty cursor returned for file.");
                    }
                }

                // Read the document's contents as a String.
                String content;
                try (InputStream is = contentResolver.openInputStream(uri);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    content = stringBuilder.toString();
                }

                return Pair.create(name, content);
            });
    }
    public class GetFilesUrl extends AsyncTask<String,String, List<FileDetails>>{
        String filedId;
        FileRecievedListener fileRecievedListener;
        Context context;
        public GetFilesUrl(Context context,String filedId, FileRecievedListener fileRecievedListener){
            this.filedId=filedId;
            this.context=context;
            this.fileRecievedListener=fileRecievedListener;
        }

        @Override
        protected   List<FileDetails> doInBackground(String... strings) {
            List<FileDetails> googleDriveFileHolderList = new ArrayList<>();
//            List<Bitmap> googleDriveFileHolderList = new ArrayList<>();
            String parent = "root";
            if (filedId != null) {
                parent = filedId;
            }

//            FileList result = mDriveService.files().list().setQ("'" + parent + "' in parents").setFields("files(id, name,size,createdTime,modifiedTime,starred)").setSpaces("drive").execute();
            Drive.Files.List request= null;
            try {
                request = mDriveService.files().list().setFields("files/thumbnailLink, files/name, files/mimeType, files/id,files/createdTime").setQ("'" + parent + "' in parents");
            } catch (IOException e) {
                e.printStackTrace();
            }
            do {
                try {
                    FileList files = request.execute();
                    files.getFiles().get(0).getIconLink();
                    files.getFiles().get(0).getThumbnailLink();
//                    String fileId = "1ZdR3L3qP4Bkq8noWLJHSr_iBau0DNT4Kli4SxNc2YEo";
                    for (int i=0;i<files.getFiles().size();i++) {
//                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                        mDriveService.files().get(files.getFiles().get(0).getId())
//                                .executeMediaAndDownloadTo(outputStream);
////                        Bitmap bitmap = BitmapFactory.de(outputStream);
//                        byte[] data = outputStream.toByteArray();
//                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                        googleDriveFileHolderList.add(bmp);
//                        Log.e("dta",data+"");

                        if (files.getFiles().get(i).getThumbnailLink()!=null) {
                            FileDetails fileDetails=new FileDetails(files.getFiles().get(i).getId(),files.getFiles().get(i).getThumbnailLink());
                            googleDriveFileHolderList.add(fileDetails);
                        }
                        else {
                            FileDetails fileDetails=new FileDetails(files.getFiles().get(i).getId(),files.getFiles().get(i).getName());
                            googleDriveFileHolderList.add(fileDetails);
                        }
                    }
//                    googleDriveFileHolderList.addAll(files.getFiles());
                    request.setPageToken(files.getNextPageToken());
                } catch (IOException e) {
                    System.out.println("An error occurred: " + e);
                    request.setPageToken(null);
                }
            } while (request.getPageToken() != null &&
                    request.getPageToken().length() > 0);
//          List<String> list= queryFiles(filedId).getResult();
//            try {
//                File file = mDriveService.files().get(filedId).execute();
//                file.
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            return googleDriveFileHolderList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            ProgressBarHandler.Companion.getInstance();
//            ProgressBarHandler.Companion.show(context);
        }

        @Override
        protected void onPostExecute(List<FileDetails> genericUrl) {

            super.onPostExecute(genericUrl);
            fileRecievedListener.Downloaded(genericUrl);
//            ProgressBarHandler.Companion.hide();

//            Log.e("url",genericUrl.get(0).getu+"");
        }
    }

    public Task<List<File>> queryFiles(@Nullable final String folderId) {
        return Tasks.call(mExecutor, new Callable<List<File>>() {
                    @Override
                    public List<File> call() throws Exception {
                        List<File> googleDriveFileHolderList = new ArrayList<>();
                        String parent = "root";
                        if (folderId != null) {
                            parent = folderId;
                        }

                        FileList result = mDriveService.files().list().setQ("'" + parent + "' in parents").setFields("files(id, name,size,createdTime,modifiedTime,starred)").setSpaces("drive").execute();
                        Drive.Files.List request=mDriveService.files().list().setQ("'" + parent + "' in parents");
                        do {
                            try {
                                FileList files = request.execute();

                                googleDriveFileHolderList.addAll(files.getFiles());
                                request.setPageToken(files.getNextPageToken());
                            } catch (IOException e) {
                                System.out.println("An error occurred: " + e);
                                request.setPageToken(null);
                            }
                        } while (request.getPageToken() != null &&
                                request.getPageToken().length() > 0);

//                        for (int i = 0; i < result.getFiles().size(); i++) {
//
//                            String googleDriveFileHolder = result.getFiles().get(i).getOriginalFilename();
////                            googleDriveFileHolder.setId(result.getFiles().get(i).getId());
////                            googleDriveFileHolder.setName(result.getFiles().get(i).getName());
////                            if (result.getFiles().get(i).getSize() != null) {
////                                googleDriveFileHolder.setSize(result.getFiles().get(i).getSize());
////                            }
////
////                            if (result.getFiles().get(i).getModifiedTime() != null) {
////                                googleDriveFileHolder.setModifiedTime(result.getFiles().get(i).getModifiedTime());
////                            }
////
////                            if (result.getFiles().get(i).getCreatedTime() != null) {
////                                googleDriveFileHolder.setCreatedTime(result.getFiles().get(i).getCreatedTime());
////                            }
////
////                            if (result.getFiles().get(i).getStarred() != null) {
////                                googleDriveFileHolder.setStarred(result.getFiles().get(i).getStarred());
////                            }
//
//                            googleDriveFileHolderList.add(googleDriveFileHolder);
//
//                        }


                        return googleDriveFileHolderList;


                    }
                }
        );
    }

}