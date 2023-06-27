package ovh.kabus.proxitok

import android.net.Uri
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class TiktokHelper(val proxitokInstance: String, val tiktokUrl: Uri) {

    var doc: Document

    init {
        try {
            doc = Jsoup.connect(proxitokInstance + "redirect/search?type=url&term=$tiktokUrl").get()
        }catch (e: IOException) {
            throw Error("Instance is unavailable")
        }
    }

    fun getStreamUrl(): String {
        val streamUrl = doc.getElementsByTag("video")[0].child(0).attr("src")
        return "$proxitokInstance$streamUrl"
    }

    fun getVideoTitle(): String {
        return doc.getElementsByClass("content")[0].child(0).text()
    }

    fun getVideoAuthor(): String {
        return doc.getElementsByTag("small")[0].child(0).text()
    }

    fun getVideoViews(): String {
        return doc.getElementsByClass("content")[0].child(3).child(0).text()
    }

    fun getVideoLikes(): String {
        return doc.getElementsByClass("content")[0].child(3).child(1).text()
    }

    fun getVideoComments(): String {
        return doc.getElementsByClass("content")[0].child(3).child(2).text()
    }

    fun getVideoShares(): String {
        return doc.getElementsByClass("content")[0].child(3).child(3).text()
    }

    fun getVideoDownloadUrlWithWatermark(): String {
        return proxitokInstance + doc.getElementsByClass("control")[2].child(0).attr("href")
    }

    fun getVideoDownloadUrl(): String {
        return proxitokInstance + doc.getElementsByClass("control")[3].child(0).attr("href")
    }

    fun getVideoId(): String {
        val origUrl = tiktokUrl.toString()
        val split = origUrl.split("/")
        val mutSplit = split.toMutableList()
        if(origUrl.endsWith("/")) mutSplit.removeLast()
        return mutSplit.last()
    }
}