package com.example.agroeasy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var dontHaveAccountButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var togglePasswordVisibility: ImageView // Toggle password visibility

    private var isPasswordVisible = false // Variable to track password visibility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        backButton = findViewById(R.id.backButton)
        emailEditText = findViewById(R.id.etEmail)
        passwordEditText = findViewById(R.id.etPassword)
        loginButton = findViewById(R.id.btnLogin)
        dontHaveAccountButton = findViewById(R.id.btnDontHaveAccount)
        togglePasswordVisibility = findViewById(R.id.ivTogglePassword)


        auth = FirebaseAuth.getInstance()

        // Back button to go to LanguageSelectionActivity
        backButton.setOnClickListener {
            val intent = Intent(this, LanguageSelectionActivity::class.java)
            startActivity(intent)
            finish() // Optional: Finish the current activity to prevent going back to it
        }

        // Handle password visibility toggle
        togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible // Toggle visibility
            if (isPasswordVisible) {
                passwordEditText.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePasswordVisibility.setImageResource(R.drawable.ic_eye_open) // Change to open eye icon
            } else {
                passwordEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePasswordVisibility.setImageResource(R.drawable.ic_eye_closed) // Change to closed eye icon
            }
            passwordEditText.setSelection(passwordEditText.text.length) // Set cursor to the end of the text
        }

        // Handle login button click
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                if (email.isEmpty()) emailEditText.error = "Email is required"
                if (password.isEmpty()) passwordEditText.error = "Password is required"
            }
        }

        // Don't have an account button to go to RegistrationActivity
        dontHaveAccountButton.setOnClickListener {
            startActivity(Intent(this, Registration_Activity::class.java))
        }
    }

    // Function to handle user login
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                // Save login state to SharedPreferences
                val sharedPreferences = getSharedPreferences("AgroEasyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("isLoggedIn", true)
                editor.apply()

                // Redirect to home page (NextActivity)
                startActivity(Intent(this, HomePage::class.java))
                finish() // Optional: Finish the login activity
            } else {
                Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
