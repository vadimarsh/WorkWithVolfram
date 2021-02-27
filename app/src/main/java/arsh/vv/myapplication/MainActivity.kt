package arsh.vv.myapplication

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wolfram.alpha.WAEngine
import com.wolfram.alpha.WAPlainText
import java.util.*
import android.speech.tts.TextToSpeech

class MainActivity : AppCompatActivity() {
    private lateinit var questionInput: TextView
    private lateinit var answerText: TextView
    private lateinit var searchButton: Button
    private lateinit var speakButton: Button
    private val wolframAppID = ""
    private val engine = WAEngine()
    private lateinit var readAnswer: FloatingActionButton
    lateinit var textToSpeech : TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.top_app_bar))
        questionInput = findViewById(R.id.question_input)
        searchButton = findViewById(R.id.search_button)
        answerText = findViewById(R.id.text_output)
        searchButton.setOnClickListener { askWolfram(questionInput.text.toString()) }
        speakButton = findViewById(R.id.speak_button)
        readAnswer = findViewById(R.id.read_answer)
        engine.appID = wolframAppID
        engine.addFormat("plaintext")
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {})

        val intentRecognizing = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What do you want to know?")
        speakButton.setOnClickListener {
            try {
                startActivityForResult(intentRecognizing,1)
            }
            catch (a: ActivityNotFoundException) {
                Toast.makeText(
                    applicationContext,
                    "Sorry your device not supported",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        textToSpeech.language = Locale.US


        readAnswer.setOnClickListener {
            textToSpeech.stop()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1){
            if(resultCode == RESULT_OK && data !=null){
                val result: ArrayList<String>? = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val questionText: String? = result?.get(0)
                if (questionText != null){
                    questionInput.text = questionText
                }
            }
        }

    }
    fun askWolfram(question: String) {

        val query = engine.createQuery()
        query.input = question

        Thread(Runnable {
            val queryResult = engine.performQuery(query)
            answerText.post {
                var speechRequest = 0
                if (queryResult.isError) {
                    answerText.text = queryResult.errorMessage
                    Log.d("volfram", queryResult.errorMessage)
                } else if (!queryResult.isSuccess) {
                    answerText.text = "I don't understand your question"
                    Log.d("volfram", "I don't understand your question")
                } else {
                    for (pod in queryResult.pods) {
                        if (!pod.isError) {
                            for (subpod in pod.subpods) {
                                for (element in subpod.contents) {
                                    if (element is WAPlainText) {
                                        Log.d("volfram", element.text)
                                        answerText.text = answerText.text.toString() + "\n"+element.text

                                        textToSpeech.speak(element.text, TextToSpeech.QUEUE_ADD, null, speechRequest.toString())
                                        speechRequest += 1
                                    }
                                }
                            }
                        } else {
                            Log.d("volfram", "poderror")
                        }
                    }
                }

            }
        }).start()
    }
}