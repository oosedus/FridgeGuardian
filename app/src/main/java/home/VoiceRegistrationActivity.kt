package home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fridgeguardian.databinding.ActivityVoiceRegistrationBinding
import android.content.pm.PackageManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.fridgeguardian.CommunityMainActivity
import com.example.fridgeguardian.MyPageActivity
import com.example.fridgeguardian.R
import com.example.fridgeguardian.RecipeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.example.fridgeguardian.BuildConfig

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import java.util.Locale

class VoiceRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVoiceRegistrationBinding
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private var tempRecording: MutableList<String> = mutableListOf()
    private val apiKey = BuildConfig.API_KEY

    companion object {
        private const val PERMISSION_REQUEST_RECORD_AUDIO = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupBottomNavigationView()
        setupButtonListeners()
        checkMicrophonePermission()
    }

    private fun setupButtonListeners() {
        binding.btnStart.setOnClickListener {
            startListening()

        }

        binding.btnPause.setOnClickListener {
            pauseListening()
        }

        binding.btnStop.setOnClickListener {
            stopListening()
        }

        binding.btnRegistration.setOnClickListener {
            sendTextToGPT(binding.convertedText.text.toString())
        }
    }


    private fun sendTextToGPT(text: String) {

        lifecycleScope.launch(Dispatchers.IO) {
            val openAI =OpenAI(apiKey)

            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.System,
                        content = text + "\n이 정보를 기반으로 {\"category\" :, \"name\" :, \"quantity\"(int type) :, \"expDate\"(type : \"dd/mm/yy\") : } 으로 json 형식의 데이터를 각 재료별로 생성해주세요. 각 식재료의 Key는 name으로 해주세요 답변은 json 형식의 데이터만 해주세요. 언어는 한국어로 해주세요"
                    )
                )
            )
            val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
            Log.d("ITM", "${completion.choices.first().message.content}")
            val response: String? = completion.choices.firstOrNull()?.message?.content
            runOnUiThread {
                showLoading(false)
                if (response != null) {
                    val ingredients = parseResponse(response)
                    val intent = Intent(this@VoiceRegistrationActivity, VoiceDataEditActivity::class.java)
                    intent.putExtra("parsedIngredients", ArrayList(ingredients))
                    startActivity(intent)
                    }
            }
        }
    }

    fun parseResponse(response: String): List<Map<String, Any>> {
        val jsonObject = JSONObject(response)
        val ingredients = mutableListOf<Map<String, Any>>()

        jsonObject.keys().forEach { key ->
            val jsonIngredient = jsonObject.getJSONObject(key)
            val ingredientMap = mutableMapOf<String, Any>()

            ingredientMap["key"] = key
            ingredientMap["name"] = jsonIngredient.optString("name", "Unknown")
            ingredientMap["category"] = jsonIngredient.optString("category", "Unknown")
            ingredientMap["quantity"] = jsonIngredient.optInt("quantity", 0)
            ingredientMap["expDate"] = jsonIngredient.optString("expDate", "Unknown")

            ingredients.add(ingredientMap)
        }
        Log.d("ITM", "${ingredients}")
        return ingredients
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }


    private fun checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_REQUEST_RECORD_AUDIO)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Microphone permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }

    private fun startListening() {
        isListening = true
        updateRecordingStatus("Recording Started")
        binding.btnStart.visibility = View.INVISIBLE
        binding.btnPause.visibility = View.VISIBLE
        binding.btnStop.visibility = View.VISIBLE
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(recognitionListener)
            startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            })
        }
    }

    private fun pauseListening() {
        if (!isListening) {
            Toast.makeText(this, "Recording has not started.", Toast.LENGTH_SHORT).show()
            return
        }
        updateRecordingStatus("Recording Paused")
        binding.btnStart.visibility = View.VISIBLE
        binding.btnPause.visibility = View.INVISIBLE
        binding.btnStop.visibility = View.VISIBLE

        speechRecognizer?.stopListening()
        isListening = false
    }

    private fun stopListening() {
        updateRecordingStatus("Check the converted Text and Click the Registration Button")

        speechRecognizer?.stopListening()
        isListening = false

        binding.btnStart.visibility = View.INVISIBLE
        binding.btnStart.isClickable = false
        binding.btnStart.isFocusable = false
        binding.btnPause.visibility = View.INVISIBLE
        binding.btnPause.isClickable = false
        binding.btnPause.isFocusable = false
        binding.btnStop.visibility = View.INVISIBLE
        binding.btnStop.isClickable = false
        binding.btnStop.isFocusable = false
        binding.btnRegistration.visibility = View.VISIBLE
        binding.btnRegistration.isClickable = true
        binding.btnRegistration.isFocusable = true

    }

    private fun updateRecordingStatus(status: String) {
        binding.infoRecord.text = status
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            Toast.makeText(applicationContext, "Error occurred: $error", Toast.LENGTH_SHORT).show()
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.let {
                if (it.isNotEmpty()) {
                    tempRecording.add(it[0])
                    updateConvertedText()
                }
            }
            if (isListening) {
                startListening()
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun updateConvertedText() {
        val fullText = tempRecording.joinToString(" ")
        binding.convertedText.setText(fullText)
    }


    private fun setupBottomNavigationView() {
        binding.navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_recipe -> {
                    startActivity(Intent(this, RecipeActivity::class.java))
                    true
                }
                R.id.nav_community -> {
                    startActivity(Intent(this, CommunityMainActivity::class.java))
                    true
                }
                R.id.nav_mypage -> {
                    startActivity(Intent(this, MyPageActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Add Ingredient with Voice Registration"
        setSupportActionBar(binding.toolbar)
    }
}