package com.example.mazealarm

import android.os.AsyncTask
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class AsyncHttp(var name: String, var value: Double) :
    AsyncTask<String?, Int?, Boolean>() {
    var urlConnection: HttpURLConnection? = null
    var flg = false
    //非同期処理ここから
    override fun doInBackground(vararg p0: String?): Boolean? {
        val urlinput = "http://160.16.95.101/post.php"
        try {
            val url = URL(urlinput)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection!!.requestMethod = "POST"
            urlConnection!!.doOutput = true
            //POST用パラメータ
            val postDataSample = "name=$name&text=$value"
            //POSTパラメータ設定
            val out = urlConnection!!.outputStream
            out.write(postDataSample.toByteArray())
            out.flush()
            out.close()
            Log.d("test", postDataSample)
            //レスポンスを受け取る
            urlConnection!!.inputStream
            flg = true
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return flg
    }

}