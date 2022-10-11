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
            act.mAuth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    act.mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signUpWithEmailSuccessful(task.result.user!!)
                            } else {
                                signUpWithEmailException(task.exception!!, email, password)
                            }
                        }
                }
            }
        }
    }

    private fun signUpWithEmailException(e: Exception, email: String, password: String) {
        if (e is FirebaseAuthUserCollisionException) {
            if (e.errorCode == FireBaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                linkEmailToG(email, password)
            }
        } else if (e is FirebaseAuthInvalidCredentialsException) {
            if (e.errorCode == FireBaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(
                    act,
                    FireBaseAuthConstants.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        if (e is FirebaseAuthWeakPasswordException) {
            if (e.errorCode == FireBaseAuthConstants.ERROR_WEAK_PASSWORD) {
                Toast.makeText(
                    act,
                    FireBaseAuthConstants.ERROR_WEAK_PASSWORD,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun signUpWithEmailSuccessful(user: FirebaseUser) {
        sendEmailVerification(user)
        act.uiUpdate(user)
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
        act.mAuth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                act.mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(act, "Sign in done", Toast.LENGTH_LONG).show()
                        act.uiUpdate(task.result?.user)
                    } else {
                        Log.d("MyLog", "Google Sign in Exception : ${task.exception}")
                    }

                }
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
            act.mAuth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    act.mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                act.uiUpdate(task.result?.user)
                            } else {
                                signInWithEmailException(task.exception!!, email, password)
                            }
                        }
                }
            }

        }
    }

    private fun signInWithEmailException(e: Exception, email: String, password: String) {
        if (e is FirebaseAuthInvalidCredentialsException) {
            if (e.errorCode == FireBaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(
                    act,
                    FireBaseAuthConstants.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_LONG
                ).show()
            } else if (e.errorCode == FireBaseAuthConstants.ERROR_WRONG_PASSWORD) {
                Toast.makeText(
                    act,
                    FireBaseAuthConstants.ERROR_WRONG_PASSWORD,
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (e is FirebaseAuthInvalidUserException) {
            if (e.errorCode == FireBaseAuthConstants.ERROR_USER_NOT_FOUND) {
                Toast.makeText(
                    act,
                    FireBaseAuthConstants.ERROR_USER_NOT_FOUND,
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }


    fun signInAnonymously(listener: Listener) {
        act.mAuth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener.onComplete()
                Toast.makeText(act, "Вы вошли как гость:)", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(act, "Не удалось войти как гость:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    interface Listener {
        fun onComplete()
    }
}