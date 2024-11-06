package com.example.agroeasy

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
            onBackPressed()  // Navigate back to the previous activity
        }

        // Toggle password visibility for password field
        togglePasswordVisibility.setOnClickListener {
            togglePasswordVisibility(passwordEditText, togglePasswordVisibility)
        }

        // Toggle password visibility for confirm password field
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

    // Function to toggle password visibility
    private fun togglePasswordVisibility(editText: EditText, toggleIcon: ImageView) {
        if (editText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggleIcon.setImageResource(R.drawable.ic_eye_closed) // Change to closed eye icon
        } else {
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            toggleIcon.setImageResource(R.drawable.ic_eye_open) // Change to open eye icon
        }
        editText.setSelection(editText.text.length) // Set cursor to the end of the text
    }

    // Function to validate user input and register
    private fun validateAndRegisterUser() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val mobile = mobileEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        // Validate fields
        when {
            name.isEmpty() -> nameEditText.error = "Name is required"
            !isValidEmail(email) -> emailEditText.error = "Invalid email address"
            mobile.isEmpty() -> mobileEditText.error = "Mobile number is required"
            password.isEmpty() -> passwordEditText.error = "Password is required"
            password != confirmPassword -> confirmPasswordEditText.error = "Passwords do not match"
            else -> checkEmailInFirebaseAuth(email, name, mobile, password)
        }
    }

    // Function to validate email format
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Function to check if the email is registered in Firebase Authentication
    private fun checkEmailInFirebaseAuth(email: String, name: String, mobile: String, password: String) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList<String>()
                if (signInMethods.isNotEmpty()) {
                    // Email already exists, show dialog to prompt login
                    showEmailExistsDialog()
                } else {
                    // Email doesn't exist, create new account
                    registerUserWithFirebaseAuth(email, password, name, mobile)
                }
            } else {
                Toast.makeText(this, "Failed to check email. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to show a dialog if the email already exists
    private fun showEmailExistsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("User Exists")
        builder.setMessage("This email is already registered. Please log in.")
        builder.setPositiveButton("Login") { dialog, _ ->
            startActivity(Intent(this, LoginActivity::class.java))
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    // Function to register a new user with Firebase Authentication
    private fun registerUserWithFirebaseAuth(email: String, password: String, name: String, mobile: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Account created successfully, save name and mobile in Firebase Realtime Database
                saveUserToFirebaseDatabase(name, email, mobile)
            } else {
                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to save the user's name and mobile to Firebase Realtime Database
    private fun saveUserToFirebaseDatabase(name: String, email: String, mobile: String) {
        val userId = email.replace(".", "_") // Firebase keys cannot contain dots
        val userMap = hashMapOf(
            "name" to name,
            "mobile" to mobile
        )

        database.child("users").child(userId).setValue(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                // Redirect to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
                finish() // Close the Registration activity
            } else {
                Toast.makeText(this, "Failed to save user data. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
