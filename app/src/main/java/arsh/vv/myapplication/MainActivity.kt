package arsh.vv.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wolfram.alpha.WAEngine
import com.wolfram.alpha.WAPlainText

class MainActivity : AppCompatActivity() {
    lateinit var questionInput: TextView
    lateinit var answerText: TextView

    lateinit var searchButton: Button
    private val wolframAppID = "xxxxxx-xxxxxxxxxxx"
    private val engine = WAEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.top_app_bar))
        questionInput = findViewById(R.id.question_input)
        searchButton = findViewById(R.id.search_button)
        answerText = findViewById(R.id.text_output)
        searchButton.setOnClickListener { askWolfram(questionInput.text.toString()) }
        engine.appID = wolframAppID
        engine.addFormat("plaintext")
    }

    fun askWolfram(question: String) {

        val query = engine.createQuery()
        query.input = question

        Thread(Runnable {
            val queryResult = engine.performQuery(query)
            answerText.post {
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