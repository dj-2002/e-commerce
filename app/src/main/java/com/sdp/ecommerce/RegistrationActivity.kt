package com.sdp.ecommerce

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast




private const val TAG = "RegistrationActivity"
class RegistrationActivity : AppCompatActivity() {
    private lateinit var logo: ImageView
    private  lateinit var joinus: ImageView
    private  lateinit var username: AutoCompleteTextView
    private  lateinit var email: AutoCompleteTextView
    private lateinit var password: AutoCompleteTextView
    private lateinit var signup: Button
    private lateinit var signin: TextView
    private lateinit var progressDialog: ProgressDialog
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        initializeGUI()
        signup!!.setOnClickListener {
            val inputName: String = username.getText().toString().trim { it <= ' ' }
            val inputPw: String = password.getText().toString().trim { it <= ' ' }
            val inputEmail: String = email.getText().toString().trim { it <= ' ' }
            if (validateInput(inputName, inputPw, inputEmail)) registerUser(
                inputName,
                inputPw,
                inputEmail
            )
        }
        signin.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@RegistrationActivity,
                    LoginActivity::class.java
                )
            )
        })
    }

    private fun initializeGUI() {
        logo = findViewById(R.id.ivRegLogo)
        joinus = findViewById(R.id.ivJoinUs)
        username = findViewById(R.id.atvUsernameReg)
        email = findViewById(R.id.atvEmailReg)
        password = findViewById(R.id.atvPasswordReg)
        signin = findViewById(R.id.tvSignIn)
        signup = findViewById(R.id.btnSignUp)
        progressDialog = ProgressDialog(this)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun registerUser(inputName: String, inputPw: String, inputEmail: String) {
        progressDialog.setMessage("Verificating...")
        progressDialog.show()
        firebaseAuth?.createUserWithEmailAndPassword(inputEmail, inputPw)
            ?.addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                override fun onComplete(p0: Task<AuthResult?>) {
                    if (p0.isSuccessful()) {
                        val fUser = firebaseAuth?.currentUser
                        fUser?.sendEmailVerification()?.addOnCompleteListener(this@RegistrationActivity) {

                            // Re-enable button
//                                    findViewById<View>(R.id.verify_email_button).isEnabled = true
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Verification email sent to " + fUser!!.getEmail(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Log.e(TAG, "sendEmailVerification", it.exception)
                                Toast.makeText(
                                    applicationContext,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                        }



                        progressDialog.dismiss()
                        sendUserData(inputName, inputPw)
                        Toast.makeText(
                            this@RegistrationActivity,
                            "You've been registered successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Email already exists.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            })


//        val actionCodeSettings =
//            ActionCodeSettings.newBuilder()
//                // URL you want to redirect back to. The domain (www.example.com) for this
//                // URL must be whitelisted in the Firebase Console.
//                .setUrl("https://www.example.com/finishSignUp?cartId=1234")
//                // This must be true
//                .setHandleCodeInApp(true)
//                .setIOSBundleId("com.example.ios")
//                .setAndroidPackageName(
//                    "com.sdp.ecommerce",
//                    true,  /* installIfNotAvailable */
//                    "17" /* minimumVersion */
//                )
//                .build()
//
//
//        FirebaseAuth.getInstance().sendSignInLinkToEmail(inputEmail, actionCodeSettings)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "Email sent.")
//                }
//            }



    }

    private fun sendUserData(username: String, password: String) {
        firebaseDatabase = FirebaseDatabase.getInstance()
        val users: DatabaseReference = firebaseDatabase!!.getReference("users")
        val user = UserProfile(username, password)
        users.push().setValue(user)
    }

    private fun validateInput(inName: String, inPw: String, inEmail: String): Boolean {
        if (inName.isEmpty()) {
            username.setError("Username is empty.")
            return false
        }
        if (inPw.isEmpty()) {
            password.setError("Password is empty.")
            return false
        }
        if (inEmail.isEmpty()) {
            email.setError("Email is empty.")
            return false
        }
        return true
    }
}

