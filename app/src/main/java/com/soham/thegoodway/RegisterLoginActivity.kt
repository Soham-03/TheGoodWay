package com.soham.thegoodway
import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soham.thegoodway.databinding.ActivityRegisterLoginBinding
import java.text.DateFormat

class RegisterLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var heading: String
    private lateinit var accName: String
    private lateinit var accPhone: String
    @androidx.core.os.BuildCompat.PrereleaseSdkCheck
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterLoginBinding.inflate(layoutInflater)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        heading = intent.getStringExtra("task")!!
        if(heading == "Register"){
            binding.txtSignIn.text = "Create An Account"
            binding.txt.text = heading
        }
        binding.btnSignUpGoogle.setOnClickListener {
            if(!TextUtils.isEmpty(binding.edtTxtName.text) && !TextUtils.isEmpty(binding.edtTxtPhoneNumber.text)){
                accName = binding.edtTxtName.text.toString()
                accPhone = binding.edtTxtPhoneNumber.text.toString()
                signIn()
            }
            else{
                Toast.makeText(this@RegisterLoginActivity, "Please Enter name and phone above", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLoginWithEmail.setOnClickListener {
            if(!TextUtils.isEmpty(binding.edtTxtEmail.text) && !TextUtils.isEmpty(binding.edtTxtPassword.text)
                && !TextUtils.isEmpty(binding.edtTxtName.text) && !TextUtils.isEmpty(binding.edtTxtPhoneNumber.text)){
                if(heading=="Register"){
                    createUserWithEmailAndPass(binding.edtTxtEmail.text.toString(),binding.edtTxtPassword.text.toString(),
                        binding.edtTxtName.text.toString(),binding.edtTxtPhoneNumber.text.toString()
                    )
                }
                else{
                    signInWithEmailAndPass(binding.edtTxtEmail.text.toString(),binding.edtTxtPassword.text.toString())
                }
            }
            else{
                Toast.makeText(this@RegisterLoginActivity, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                // Back is pressed... Finishing the activity
                finish()
            }
        } else {
            onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Back is pressed... Finishing the activity
                    finish()
                }
            })
        }

        setContentView(binding.root)
    }


    private fun signInWithEmailAndPass(email:String,password:String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    println("Failed:${task.exception}")
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }
    }

    private fun createUserWithEmailAndPass(
        email: String,
        password: String,
        name: String,
        phone: String
    ){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val hashMap =  HashMap<String,String>()
                    hashMap["name"] = name
                    hashMap["phone"] = phone
                    hashMap["dateJoined"] = DateFormat.getDateInstance().format("dd MMMM yyyy")
                    db.collection("users").document(auth.currentUser!!.uid).set(hashMap)
                        .addOnSuccessListener {
                            updateUI(user)
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String,heading:String) {
        if(heading=="Register"){
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        val hashMap =  HashMap<String,String>()
                        hashMap["name"] = accName
                        hashMap["phone"] = accPhone
                        hashMap["dateJoined"] = DateFormat.getDateInstance().format("dd MMMM yyyy")
                        db.collection("users")
                            .document(user!!.uid)
                            .set(hashMap)
                            .addOnSuccessListener {
                                updateUI(user)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                        updateUI(null)
                    }
                }
        }
        else{
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                        updateUI(null)
                    }
                }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
//        else {
//            binding.txtMasterPasswordAgain.error = "Passwords Don't Match"
//        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!, heading = heading)
            } catch (e: ApiException) {
                Log.w(ContentValues.TAG, "Google sign in failed", e)
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user!=null){
            finish()
            val intent = Intent(this@RegisterLoginActivity, DonorVolunteerActivity::class.java)
            intent.putExtra("userId",user.uid)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            finishAffinity()
            startActivity(intent)
        }
    }

}