package com.example.cinecritique

import android.util.Log
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.concurrent.Executor

class WebHelper(private val cronetEngine: CronetEngine, private val ex: Executor) {

    fun get(url: String, headers: Map<String, String>, callback: (String?) -> Unit) {
        val requestBuilder = cronetEngine.newUrlRequestBuilder(
            url,
            object : UrlRequest.Callback() {
                override fun onRedirectReceived(
                    request: UrlRequest?,
                    info: UrlResponseInfo?,
                    newLocationUrl: String?
                ) {
                    request?.followRedirect()
                }

                override fun onResponseStarted(request: UrlRequest?, info: UrlResponseInfo?) {
                    request?.read(ByteBuffer.allocateDirect(102400))
                }

                override fun onReadCompleted(
                    request: UrlRequest?,
                    info: UrlResponseInfo?,
                    byteBuffer: ByteBuffer?
                ) {
                    byteBuffer?.flip() // Prepare the buffer for reading
                    val response = byteBuffer?.let { byteBufferToString(it) }
                    callback(response)
                }

                override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
                    Log.i("MYTAG", "Connection succeeded")
                }

                override fun onFailed(
                    request: UrlRequest?,
                    info: UrlResponseInfo?,
                    error: CronetException?
                ) {
                    Log.e("MYTAG", "Connection failed: ${error?.message}")
                }

                fun byteBufferToString(buffer: ByteBuffer, charset: Charset = Charsets.UTF_8): String {
                    val byteArray = ByteArray(buffer.remaining())
                    buffer.get(byteArray)
                    return String(byteArray, charset)
                }
            },
            ex
        )

        // Add headers to the request
        headers.forEach { (key, value) ->
            requestBuilder.addHeader(key, value)
        }

        // Kick off the connection
        requestBuilder.build().start()
    }
}
