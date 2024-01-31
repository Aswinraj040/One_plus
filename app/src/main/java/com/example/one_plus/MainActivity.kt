package com.example.one_plus

import android.Manifest
import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var tts1: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )
        val printa = findViewById<TextView>(R.id.textview)
        printa.movementMethod = ScrollingMovementMethod()
        val micIV: ImageView = findViewById(R.id.button)
        printa.text="Hello , How can i help you?"
        micIV.setOnClickListener(){
            micIV.setImageResource(R.drawable.indicator)
            startSpeechToText()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(applicationContext, "Permission refused", Toast.LENGTH_SHORT).show()
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setMessage("permission to required to access this app,please click allow!");setTitle(
                "permission required"
            );setPositiveButton("OK")
            { _, _ ->
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    0
                )
            }
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun startSpeechToText() {
        val micIV: ImageView = findViewById(R.id.button)
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray?) {}
            override fun onEndOfSpeech(){
                micIV.setImageResource(R.drawable.mic2)
            }
            override fun onError(i: Int) {}
            override fun onResults(bundle: Bundle) {
                val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (result != null) {
                    dowhatisay(result[0])
                }
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle?) {}
        })
        speechRecognizer.startListening(speechRecognizerIntent)
    }

    private fun tts(text: String) {
        tts1 = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
            tts1.language = Locale.US
            tts1.setSpeechRate(0.8f)
            tts1.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        })
    }

    private fun dowhatisay(a : String){
        val printa = findViewById<TextView>(R.id.textview)
        if ("play" in a.lowercase()) {
            val b = a.replace("play".toRegex(), "")
            val o = b.replace("song".toRegex(), "")
            tts("Playing $o"+"song")
            printa.text = "Playing $o" + "song"
            linkgeteryui(o.trim()+" song")
        }else if ("alarm" in a.lowercase()){
            var x = ""
            for (i in 0..a.length - 1) {
                if (a[i].isDigit() || a[i] == ':') {
                    x = x + a[i]
                }
            }
            if (x.length == 1){
                x = x + ":00"
            }
            var m = 0
            var n = 0
            if (':' in x) {
                m = x.length - 1
                n = x.slice((m downTo m - 1).reversed()).toInt()
            } else {
                n = 0
            }
            var kn = x.slice(0..1)
            if(":" in kn){
                kn = kn.replace(":".toRegex() , "")
            }
            var k = kn.toInt()
            var l = ""
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
            var current = formatter.format(time)
            var min = current.slice(14..15)
            var hr = current.slice(11..12)
            if ("p.m" in a ){
                k = k + 12
                l = "p.m."
            }
            else if("a.m" in a){
                k =k
                l = "a.m."
            }
            else{
                if (k.toInt() <12){
                    l = "a.m."
                }
                else if(k.toInt() > 12 || k.toInt() == 12){
                    l = "p.m."
                }
            }
            var nn = n.toString()
            if (hr[0] == '0'){
                hr = hr[1].toString()
            }
            if (min[0]== '0'){
                min = min[1].toString()
            }
            if (nn.length == 2 && nn[0] == '0'){
                nn = nn[0].toString()
            }
            var multip = 0
            if (hr.toInt() > 12 && l == "p.m." && (hr.toInt() > k || hr.toInt() == k ) && (min.toInt() > nn.toInt() || min.toInt() == nn.toInt())) {
                multip = k + 24
            }
            else if(l == "p.m."){
                multip = k
            }
            else{
                multip = k +24
            }
            var amin = ((multip *60) + n) - ((hr.toInt()*60) + min.toInt())
            var modu = amin % 60
            var smap = amin - modu
            var poer = smap / 60
            val inte = Intent(AlarmClock.ACTION_SET_ALARM)
            inte.putExtra(AlarmClock.EXTRA_HOUR, k)
            inte.putExtra(AlarmClock.EXTRA_MINUTES, n)
            startActivity(inte)
            printa.text = "Your alarm is set for $x " + l
            tts("Your alarm is set for $x " + l)
            Toast.makeText(this, "Alarm will go off in $poer hours , $modu minutes ", Toast.LENGTH_SHORT).show()
        }
        else if("search" in a.lowercase()){
            val ppoi = a.replace("search".toRegex() , "")
            tts("searching in google")
            val iik = Intent(Intent.ACTION_VIEW , Uri.parse("https://www.google.com/search?q="+ppoi.replace(" ".toRegex() , "%20")))
            startActivity(iik)
        }
    }
    private fun linkopener(a: String) {
        try {
            val inte = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+a))
            inte.setPackage("com.google.android.youtube")
            startActivity(inte)
        }
        catch(e : Exception){
            Toast.makeText(this , "Error has occured in playing song" , Toast.LENGTH_SHORT).show()
        }
    }
    private fun linkgeter(a : String) {
        val samp = findViewById<TextView>(R.id.textView)
        val mn = a
        try {
            val lint = samp.text.toString()
            linkopener(lint)
        }
        catch (e : Exception){
            linkgeteryui(mn)
        }
    }
    private fun linkgeteryui(a : String){
        val samp = findViewById<TextView>(R.id.textView)
        try {
            Thread(Runnable {
                val doc: Document = Jsoup.connect("https://www.google.com/search?q="+a.replace(" ".toRegex() , "%20")).get()
                val links: Elements = doc.select("a[href]")
                val k = links.toString().indexOf("https://www.youtube.com/watch")+36
                samp.text =  links.toString().slice(k..k+10)
            }).start()
        }
        catch (e : Exception) {
            Toast.makeText(this, "Error occured in fetching url", Toast.LENGTH_SHORT).show()
        }
        Thread.sleep(2000)
        linkgeter(a)
    }
}