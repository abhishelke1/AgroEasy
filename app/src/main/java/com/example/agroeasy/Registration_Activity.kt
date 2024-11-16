package com.example.agroeasy

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Registration_Activity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var mobileEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var togglePasswordVisibility: ImageView
    private lateinit var toggleConfirmPasswordVisibility: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize Firebase Database and Authentication references
        database = Firebase.database.reference
        auth = FirebaseAuth.getInstance()

        // Initialize UI components
        nameEditText = findViewById(R.id.etName)
        emailEditText = findViewById(R.id.etEmail)
        mobileEditText = findViewById(R.id.etMobile)
        passwordEditText = findViewById(R.id.etPassword)
        confirmPasswordEditText = findViewById(R.id.etConfirmPassword)
        togglePasswordVisibility = findViewById(R.id.ivTogglePassword)
        toggleConfirmPasswordVisibility = findViewById(R.id.ivToggleConfirmPassword)
        val getStartedButton: Button = findViewById(R.id.btnGetStarted)
        val loginLink: TextView = findViewById(R.id.tvLoginHere)
        val backButton: ImageView = findViewById(R.id.backButton)

        // Back button listener
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Toggle password visibility for the password field
        togglePasswordVisibility.setOnClickListener {
            togglePasswordVisibility(passwordEditText, togglePasswordVisibility)
        }

        // Toggle password visibility for the confirm password field
        toggleConfirmPasswordVisibility.setOnClickListener {
            togglePasswordVisibility(confirmPasswordEditText, toggleConfirmPasswordVisibility)
        }

        // Get Started button click listener
        getStartedButton.setOnClickListener {
            validateAndRegisterUser()
        }

        // Login link click listener
        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun togglePasswordVisibility(editText: EditText, toggleIcon: ImageView) {
        if (editText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // Change input type to password
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggleIcon.setImageResource(R.drawable.ic_eye_closed)
        } else {
            // Change input type to visible password
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            toggleIcon.setImageResource(R.drawable.ic_eye_open)
        }
        // Set the cursor to the end of the text
        editText.setSelection(editText.text.length)
    }

    private fun validateAndRegisterUser() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val mobile = mobileEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        when {
            name.isEmpty() -> nameEditText.error = "Name is required"
            !isValidEmail(email) -> emailEditText.error = "Invalid email address"
            mobile.isEmpty() -> mobileEditText.error = "Mobile number is required"
            password.isEmpty() -> passwordEditText.error = "Password is required"
            password != confirmPassword -> confirmPasswordEditText.error = "Passwords do not match"
            else -> registerUser(email, password, name, mobile)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun registerUser(email: String, password: String, name: String, mobile: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    saveUserToDatabase(it.uid, name, email, mobile)
                }
            } else {
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToDatabase(uid: String, name: String, email: String, mobile: String) {
        val userMap = mapOf(
            "name" to name,
            "email" to email,
            "mobile" to mobile
        )
        database.child("Users").child(uid).setValue(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
