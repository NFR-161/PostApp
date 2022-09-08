package com.exampleone.postapp.accounthelper

import android.util.Log
import android.widget.Toast
import com.exampleone.postapp.MainActivity
import com.exampleone.postapp.R
import com.exampleone.postapp.constants.FireBaseAuthConstants
import com.exampleone.postapp.dialoghelper.GoogleAccConst
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*

class AccountHelper(act: MainActivity) {
    private val act = act
    private lateinit var signInClient: GoogleSignInClient


    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendEmailVerification(task.result?.user!!)
                        act.uiUpdate(task.result?.user)
                    } else {
                        Log.d("MyLog", "Exeption : ${task.exception}")
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            val exeption = task.exception as FirebaseAuthUserCollisionException
                            if (exeption.errorCode == FireBaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                                linkEmailToG(email, password)
                            }
                        } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            val exeption = task.exception as FirebaseAuthInvalidCredentialsException
                            if (exeption.errorCode == FireBaseAuthConstants.ERROR_INVALID_EMAIL) {
                                Toast.makeText(
                                    act,
                                    FireBaseAuthConstants.ERROR_INVALID_EMAIL,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        if (task.exception is FirebaseAuthWeakPasswordException) {
                            val exeption = task.exception as FirebaseAuthWeakPasswordException
                            Log.d("MyLog", "Exeption : ${exeption.errorCode}")
                            if (exeption.errorCode == FireBaseAuthConstants.ERROR_WEAK_PASSWORD) {
                                Toast.makeText(
                                    act,
                                    FireBaseAuthConstants.ERROR_WEAK_PASSWORD,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
        }
    }

    private fun linkEmailToG(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (act.mAuth.currentUser != null) {
            act.mAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        act,
                        act.resources.getString(R.string.link_done),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }
        } else {
            Toast.makeText(
                act,
                act.resources.getString(R.string.enter_to_g),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id)).requestEmail()
            .build()
        return GoogleSignIn.getClient(act, gso)
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        act.mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(act, "Sign in done", Toast.LENGTH_LONG).show()
                act.uiUpdate(task.result?.user)
            } else {
                Log.d("MyLog", "Google Sign in Exeption : ${task.exception}")
            }

        }

    }

    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.startActivityForResult(intent, GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    fun signOutG() {
        getSignInClient().signOut()
    }


    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.send_verification_done),
                    Toast.LENGTH_LONG
                ).show()

            } else {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.send_verification_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        act.uiUpdate(task.result?.user)
                    } else {
                        Log.d("MyLog", "Google Sign in Exception : ${task.exception}")
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            val exception =
                                task.exception as FirebaseAuthInvalidCredentialsException
                            if (exception.errorCode == FireBaseAuthConstants.ERROR_INVALID_EMAIL) {
                                Toast.makeText(
                                    act,
                                    FireBaseAuthConstants.ERROR_INVALID_EMAIL,
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (exception.errorCode == FireBaseAuthConstants.ERROR_WRONG_PASSWORD) {
                                Toast.makeText(
                                    act,
                                    FireBaseAuthConstants.ERROR_WRONG_PASSWORD,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else if (task.exception is FirebaseAuthInvalidUserException) {
                            val exception = task.exception as FirebaseAuthInvalidUserException
                            if (exception.errorCode == FireBaseAuthConstants.ERROR_USER_NOT_FOUND) {
                                Toast.makeText(
                                    act,
                                    FireBaseAuthConstants.ERROR_USER_NOT_FOUND,
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }

                    }
                }
        }
    }
}