package com.example.cinecritique

import android.util.Log
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.concurrent.Executor

class WebHelper(private val cronetEngine: CronetEngine, private val ex: Executor){

    fun get(url:String, callback: (String?) -> Unit){
        val requestBuilder = cronetEngine.newUrlRequestBuilder(url,
            object : UrlRequest.Callback() {
                // the implementation of the UrlRequest class, all of the callback methods
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
                    byteBuffer?.clear()
                    request?.read(byteBuffer)
                    val response = byteBuffer?.let{byteBufferToString(it)}
                    //callback actually sends it back
                    callback(response);
                }

                fun byteBufferToString(buffer: ByteBuffer, charset: Charset = Charsets.UTF_8): String? {
                    val byteArray = buffer?.let { ByteArray(it.remaining()) }
                    if (buffer != null) {
                        buffer.get(byteArray)
                    }
                    var response = byteArray?.toString(Charsets.UTF_8)
                    return response;
                }

                override fun onSucceeded(request: UrlRequest?, info: UrlResponseInfo?) {
                    Log.i("MYTAG","Connection succeeded")
                }

                override fun onFailed(
                    request: UrlRequest?,
                    info: UrlResponseInfo?,
                    error: CronetException?
                ) {
                    Log.i("MYTAG","Connection failed")
                }
            }, ex )
        //kicks off the connection
        requestBuilder.build().start()
    }
}