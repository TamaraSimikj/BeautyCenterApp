package app.beautycenter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnGoogleSignIn: SignInButton
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var firebaseAuth: FirebaseAuth

    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        etName = findViewById(R.id.etName)
        etSurname = findViewById(R.id.etSurname)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build GoogleSignInClient
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set OnClickListener for the register button
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val surname = etSurname.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Perform any necessary validation checks here

            registerUser(name, surname, phoneNumber, email, password)
        }

        // Set OnClickListener for the Google Sign-In button
        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun registerUser(
        name: String,
        surname: String,
        phoneNumber: String,
        email: String,
        password: String
    ) {
        // Create user with email and password
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful, update user profile
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
                    // Registration failed
                    val errorCode = (task.exception as FirebaseAuthException).errorCode
                    Toast.makeText(this, "Registration failed: $errorCode", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in with Google successful, update user profile
                    val user = firebaseAuth.currentUser
                    val name = account?.displayName
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
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
                                // Redirect to the home activity or any other desired activity
                                val intent = Intent(this, HomeActivity::class.java)
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
                    // Sign-in with Google failed
                    Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
