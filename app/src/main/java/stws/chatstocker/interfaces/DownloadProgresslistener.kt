package stws.chatstocker.interfaces

import com.google.api.client.googleapis.media.MediaHttpDownloader
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener

class DownloadProgresslistener : MediaHttpDownloaderProgressListener {
    override fun progressChanged(downloader: MediaHttpDownloader?) {

    }
}