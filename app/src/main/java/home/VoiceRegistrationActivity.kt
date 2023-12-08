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
import com.example.fridgeguardian.CommunityActivity
import com.example.fridgeguardian.CommunityMainActivity
import com.example.fridgeguardian.MyPageActivity
import com.example.fridgeguardian.R
import com.example.fridgeguardian.RecipeActivity

class VoiceRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVoiceRegistrationBinding
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    companion object {
        private const val PERMISSION_REQUEST_RECORD_AUDIO = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupBottomNavigationView()
        checkMicrophonePermission()

        binding.btnStart.setOnClickListener {
            if (isListening) {
                stopListening()
                binding.btnStart.setImageResource(R.drawable.voice_stt_registration_button)
                binding.infoRecord.setText("Please check and correct the converted text \nand press the above button. ")
            } else {
                startListening()
                binding.btnStart.setImageResource(R.drawable.voice_record_stop_icon)
                binding.infoRecord.setText("Recording")
            }
        }
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

        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(p0: Bundle?) {
                }

                override fun onBeginningOfSpeech() {
                }

                override fun onRmsChanged(p0: Float) {
                }

                override fun onBufferReceived(p0: ByteArray?) {
                }

                override fun onEndOfSpeech() {
                }

                override fun onError(p0: Int) {
                    Toast.makeText(applicationContext, "Error occurred", Toast.LENGTH_SHORT).show()
                }

                override fun onResults(results: Bundle?) {
                    isListening = false
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    matches?.let {
                        if (it.isNotEmpty()) {
                            binding.convertedText.setText(it[0])
                            binding.btnStart.setImageResource(R.drawable.voice_stt_registration_button)
                        }
                    }
                }

                override fun onPartialResults(p0: Bundle?) {
                }

                override fun onEvent(p0: Int, p1: Bundle?) {
                }
            })
            startListening(recognizerIntent)
        }
    }

    private fun stopListening() {
        isListening = false
        speechRecognizer?.stopListening()
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