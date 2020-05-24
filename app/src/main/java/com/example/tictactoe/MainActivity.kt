package com.example.tictactoe

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*

const val RC_SIGN_IN = 1
const val ANONYMOUS = "anonymous"

     class MainActivity : AppCompatActivity() {

    private lateinit var mFirebaseAuth : FirebaseAuth
    private lateinit var mAuthStateListener : FirebaseAuth.AuthStateListener
    lateinit var mUsername : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mUsername = ANONYMOUS

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                //signed in
                // Toast.makeText(MainActivity.this, "You are now Signed In, Welcome !", Toast.LENGTH_SHORT).show();
                onSignedInInitialize(user.displayName)
            } else {
                //signed out
                onSignedOutCleanUp()
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(
                            listOf(
                                AuthUI.IdpConfig.GoogleBuilder().build(),
                                AuthUI.IdpConfig.EmailBuilder().build()
                            )
                        )
                        .build(),
                    RC_SIGN_IN
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.logout -> {
                AuthUI.getInstance().signOut(this)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onSignedInInitialize(displayName: String?) {
        if (displayName != null) {
            mUsername = displayName
        }
    }


    private fun onSignedOutCleanUp() {
        mUsername = ANONYMOUS
    }

     override fun onResume() {
         super.onResume()
         mFirebaseAuth.addAuthStateListener(mAuthStateListener)
     }

     override fun onPause() {
         super.onPause()
         mFirebaseAuth.removeAuthStateListener(mAuthStateListener)
     }
}
