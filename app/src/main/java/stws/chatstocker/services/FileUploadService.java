package stws.chatstocker.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
//import com.iceteck.silicompressorr.SiliCompressor;
import com.vincent.videocompressor.VideoCompress;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import stws.chatstocker.R;
import stws.chatstocker.recievers.FileProgressReceiver;
import stws.chatstocker.recievers.RetryJobReceiver;
import stws.chatstocker.utils.DriveServiceHelper;
import stws.chatstocker.utils.NotificationHelper;
import stws.chatstocker.view.HomeActivity;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
import static stws.chatstocker.recievers.RetryJobReceiver.ACTION_CLEAR;
import static stws.chatstocker.recievers.RetryJobReceiver.ACTION_RETRY;

public class FileUploadService extends JobIntentService {
    private static final String TAG = "FileUploadService";
    Disposable mDisposable;
    static Drive mDriveServices;
    public static final int NOTIFICATION_ID = 1;
    public static final int NOTIFICATION_RETRY_ID = 2;
    public static String mFilePath;
    public String previousFile="";
    /**
     * Unique job ID for this service.
     */
    private static final int JOB_ID = 102;
    /**
     * Unique job ID for this service.
     */

    NotificationHelper mNotificationHelper;

    public static void enqueueWork(Context context, Intent intent, Drive mDriveService) {
        mDriveServices = mDriveService;
        enqueueWork(context, FileUploadService.class, JOB_ID, intent);
    }

    public static void enqueueWork(Context context, Intent intent) {

        enqueueWork(context, FileUploadService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationHelper = new NotificationHelper(this);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        /**
         * Download/Upload of file
         * The system or framework is already holding a wake lock for us at this point
         */

        // get file file here
        String filePaths = intent.getStringExtra("mFilePath");
        String type=intent.getStringExtra("type");
//        filePaths.replace("%2f","/");
        File photoFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);

//        Uri fileUri = getOutputMediaFileUri(photoFile);

//        try {
            if (type.equals("video/mp4")){
                VideoCompress.compressVideoLow(filePaths, "/storage/emulated/0/Pictures/Chatstocker/", new VideoCompress.CompressListener() {
                    @Override
                    public void onStart() {
                        //Start Compress
                    }

                    @Override
                    public void onSuccess() {
                        //Finish successfully
                    }

                    @Override
                    public void onFail() {
                        //Failed
                    }

                    @Override
                    public void onProgress(float percent) {
                        //Progress
                    }
                });
//                MediaMetadataRetriever retriever = new MediaMetadataRetriever() ;
//                retriever.setDataSource(filePaths);
//                int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
//
//                int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
//                int bitRate = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
//                mFilePath = SiliCompressor.with(this).compressVideo(filePaths, "/storage/emulated/0/Pictures/Chatstocker/");
           mFilePath=filePaths;
            }

            else
                mFilePath=filePaths;
//        }
//        catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
        String fileType = intent.getStringExtra("type");
        String folderId = intent.getStringExtra("folderId");
        File path = new File(mFilePath);
        if (mFilePath == null) {
            Log.e(TAG, "onHandleWork: Invalid file URI");
            return;
        }

        try {
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(path.getName());

            fileMetadata.setParents(Collections.singletonList(folderId));
            java.io.File filePath = new java.io.File(path.getAbsolutePath());
            FileContent mediaContent = new FileContent(fileType, filePath);
            if (!previousFile.equals(path.getAbsolutePath())) {

                com.google.api.services.drive.model.File file = mDriveServices.files().create(fileMetadata, mediaContent)
                        .setFields("id, parents")
                        .execute();

                previousFile=path.getAbsolutePath();
                System.out.println("File ID: " + file.getId());
            }
            onSuccess();
//                    }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public Uri getOutputMediaFileUri(File file) {
        return FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
    }

    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Chatstocker");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (type == MEDIA_TYPE_VIDEO) {

            // For unique video file name appending current timeStamp with file name
            return new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + "." + "mp4");

        }

        else {
            return new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + "." + "jpg");
        }
    }

    private void onErrors(Throwable throwable) {
        /**
         * Error occurred in file uploading
         */
        Intent successIntent = new Intent("com.wave.ACTION_CLEAR_NOTIFICATION");
        successIntent.putExtra("notificationId", NOTIFICATION_ID);
        sendBroadcast(successIntent);


        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, new Intent(this, HomeActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        /**
         * Add retry action button in notification
         */
        Intent retryIntent = new Intent(this, RetryJobReceiver.class);
        retryIntent.putExtra("notificationId", NOTIFICATION_RETRY_ID);
        retryIntent.putExtra("mFilePath", mFilePath);
        retryIntent.setAction(ACTION_RETRY);

        /**
         * Add clear action button in notification
         */
        Intent clearIntent = new Intent(this, RetryJobReceiver.class);
        clearIntent.putExtra("notificationId", NOTIFICATION_RETRY_ID);
        clearIntent.putExtra("mFilePath", mFilePath);
        clearIntent.setAction(ACTION_CLEAR);

        PendingIntent retryPendingIntent = PendingIntent.getBroadcast(this, 0, retryIntent, 0);
        PendingIntent clearPendingIntent = PendingIntent.getBroadcast(this, 0, clearIntent, 0);
        NotificationCompat.Builder mBuilder = mNotificationHelper.getNotification(getString(R.string.error_upload_failed), getString(R.string.message_upload_failed), resultPendingIntent);
        // attached Retry action in notification
        mBuilder.addAction(android.R.drawable.ic_menu_revert, getString(R.string.btn_retry_not), retryPendingIntent);
        // attached Cancel action in notification
        mBuilder.addAction(android.R.drawable.ic_menu_revert, getString(R.string.btn_cancel_not), clearPendingIntent);
        // Notify notification
        mNotificationHelper.notify(NOTIFICATION_RETRY_ID, mBuilder);
    }

    /**
     * Send Broadcast to FileProgressReceiver with progress
     *
     * @param progress file uploading progress
     */
    private void onProgress(Double progress) {
        Intent progressIntent = new Intent(this, FileProgressReceiver.class);
        progressIntent.setAction("com.wave.ACTION_PROGRESS_NOTIFICATION");
        progressIntent.putExtra("notificationId", NOTIFICATION_ID);
        progressIntent.putExtra("progress", (int) (100 * progress));
        sendBroadcast(progressIntent);
    }

    /**
     * Send Broadcast to FileProgressReceiver while file upload successful
     */
    private void onSuccess() {
        Intent successIntent = new Intent(this, FileProgressReceiver.class);
        successIntent.setAction("com.wave.ACTION_UPLOADED");
        successIntent.putExtra("notificationId", NOTIFICATION_ID);
        successIntent.putExtra("progress", 100);
        sendBroadcast(successIntent);
    }


}