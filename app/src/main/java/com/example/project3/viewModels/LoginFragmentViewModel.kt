package com.example.project3.viewModels

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.animatediconbutton.AnimatedButton
import com.example.project3.Completion
import com.example.project3.constants.Constants
import com.example.project3.databinding.FragmentLoginBinding
import com.example.project3.models.User
import com.example.project3.uiComponents.ProgressButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginFragmentViewModel() : ViewModel() {
    private val mAuth = Firebase.auth
    val email = MutableLiveData("")
    val pass = MutableLiveData("")
    private lateinit var binding: FragmentLoginBinding
    private lateinit var gButton: AnimatedButton
    private lateinit var progressButton: ProgressButton

    private val gso =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(Constants.WebClientId)
            .requestEmail().build()


    fun setUi(binding: FragmentLoginBinding, a: AnimatedButton, b: ProgressButton) {
        this.binding = binding
        this.gButton = a
        this.progressButton = b
    }

    fun login(comp: Completion) {
        if (email.value.isNullOrBlank()) {
            binding.email.error = "Email required"
            progressButton.deactivate()
            return
        } else if (pass.value.isNullOrBlank()) {
            binding.pass.error = "Password required"
            progressButton.deactivate()
            return
        } else {
            Firebase.auth
                .signInWithEmailAndPassword(email.value!!.trim(), pass.value!!.trim())
                .addOnSuccessListener {
                    comp.onComplete()
                    progressButton.deactivate()
                }
                .addOnFailureListener {
                    Snackbar.make(binding.root,it.message.toString(),Snackbar.LENGTH_LONG).show()
                    comp.onCancelled("login Error", it.message.toString())
                    progressButton.deactivate()
                }
        }
    }

    fun loginWithGoogle(activity: AppCompatActivity): Intent {
        gButton.activateButton()
        val googleSignInClient = GoogleSignIn.getClient(activity, gso)
        return googleSignInClient.signInIntent

    }

    fun handleResult(
        task: Task<GoogleSignInAccount>,
        activity: AppCompatActivity,
        comp: Completion
    ) {
        if (task.isSuccessful) {
            val account = task.result
            if (account != null)
                updateUI(account, activity, comp)
            else
                Snackbar.make(
                    binding.root,
                    task.exception?.message.toString(),
                    Snackbar.LENGTH_LONG
                ).show()
        }

    }

    private fun updateUI(
        account: GoogleSignInAccount,
        activity: AppCompatActivity,
        comp: Completion
    ) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful)
                saveUserData(it.result.user!!, activity, comp)
            else {
                Snackbar.make(binding.root, it.exception?.message.toString(), Snackbar.LENGTH_LONG)
                    .show()
                gButton.deactivateButton()
            }
        }
    }

    private fun saveUserData(
        fUser: FirebaseUser,
        activity: AppCompatActivity,
        comp: Completion
    ) {
        val user = User(fUser.email!!, fUser.uid)
        Constants.userRef
            .document(fUser.uid)
            .set(user)
            .addOnSuccessListener {
                comp.onComplete()
            }
            .addOnFailureListener {
                Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_LONG).show()
                comp.onCancelled("database", it.message.toString())
            }


    }


}