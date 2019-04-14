package com.example.instagr.screens

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.instagr.R
import com.example.instagr.models.User
import com.example.instagr.screens.common.coordinateBtnAndInputs
import com.example.instagr.screens.common.showToast
import com.example.instagr.screens.home.HomeActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_namepass.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, NamePassFragment.Listener,
    KeyboardVisibilityEventListener {
    private val TAG = "RegisterActivity"

    private var mEmail: String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        KeyboardVisibilityEvent.setEventListener(this, this)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment())
                .commit()
        }
    }

    override fun onNext(email: String) {
        if (email.isNotEmpty()) {
            mEmail = email
            mAuth.fetchSignInMethodsForEmail(email) { signInMethods ->
                if (signInMethods.isEmpty()) {
                    supportFragmentManager.beginTransaction().replace(R.id.frame_layout, NamePassFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    showToast(getString(R.string.this_email_already_exists))
                }
            }
        } else {
            showToast(getString(R.string.please_enter_an_email))
        }
    }

    override fun onRegister(fullName: String, password: String) {
        if (fullName.isNotEmpty() && password.isNotEmpty()) {
            val email = mEmail
            if (email != null) {
                mAuth.createUserWithEmailAndPassword(email, password) {
                    mDatabase.createUser(it!!.user.uid, mkUser(fullName, email)) {
                        startHomeActivity()
                    }
                }
            } else {
                Log.e(TAG, "onRegister: email is null")
                showToast(getString(R.string.please_enter_an_email))
                supportFragmentManager.popBackStack()
            }

        } else {
            showToast(getString(R.string.please_enter_fullname_and_password))
        }
    }

    private fun unknownRegisterError(it: Task<*>) {
        Log.e(TAG, "onRegister: failed to create user profile", it.exception)
        showToast(getString(R.string.something_wrong_happened))
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen) {
            scroll_view.scrollTo(0, scroll_view.bottom)
        } else {
            scroll_view.scrollTo(0, scroll_view.top)
        }
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun mkUser(fullName: String, email: String): User {
        val username = mkUsername(fullName)
        return User(name = fullName, username = username, email = email)
    }

    private fun mkUsername(fullName: String) =
        fullName.toLowerCase().replace(" ", ".")

    private fun FirebaseAuth.fetchSignInMethodsForEmail(email: String, onSuccess: (List<String>) -> Unit) {
        fetchSignInMethodsForEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess(it.result?.signInMethods ?: emptyList<String>())
            } else {
                showToast(it.exception!!.message!!)
            }
        }
    }

    private fun DatabaseReference.createUser(uid: String, user: User, onSuccess: () -> Unit) {
        val reference = child("users").child(uid)
        reference.setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                unknownRegisterError(it)
            }
        }
    }

    private fun FirebaseAuth.createUserWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (AuthResult?) -> Unit
    ) {
        createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess(it.result)
            } else {
                unknownRegisterError(it)
            }
        }
    }
}


class EmailFragment : Fragment() {
    private lateinit var mListener: Listener

    interface Listener {
        fun onNext(email: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnAndInputs(next_btn, email_input)
        next_btn.setOnClickListener {
            val email = email_input.text.toString()
            mListener.onNext(email)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }
}

class NamePassFragment : Fragment() {
    private lateinit var mListener: Listener

    interface Listener {
        fun onRegister(fullName: String, password: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_namepass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnAndInputs(register_btn, full_name_input, password_input)
        register_btn.setOnClickListener {
            val fullName = full_name_input.text.toString()
            val password = password_input.text.toString()
            mListener.onRegister(fullName, password)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }
}