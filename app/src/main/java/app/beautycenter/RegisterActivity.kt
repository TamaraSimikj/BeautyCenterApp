package app.beautycenter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etSurname = findViewById(R.id.etSurname)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)


        firebaseAuth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val surname = etSurname.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()


            registerUser(name, surname, phoneNumber, email, password)
        }
    }

    private fun registerUser(
        name: String,
        surname: String,
        phoneNumber: String,
        email: String,
        password: String
    ) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName("$name $surname")
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // User profile updated successfully
                                Toast.makeText(
                                    this,
                                    "Registration Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Redirect to the login activity
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish() // Optional: Finish the current activity if you don't want the user to navigate back to it
                            } else {
                                // Failed to update user profile
                                Toast.makeText(
                                    this,
                                    "Failed to update user profile",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    val errorCode = (task.exception as FirebaseAuthException).errorCode
                    Toast.makeText(this, "Registration failed: $errorCode", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}
