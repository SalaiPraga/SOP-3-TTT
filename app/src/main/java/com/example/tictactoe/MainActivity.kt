package com.example.tictactoe

import android.os.Bundle
import android.text.Editable
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.TextView.BufferType
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.player_names.*

const val RC_SIGN_IN = 1
const val ANONYMOUS = "anonymous"

     class MainActivity : AppCompatActivity(), PlayerNameDialog.mDialogListener {

         private lateinit var mFirebaseAuth : FirebaseAuth
         private lateinit var mAuthStateListener : FirebaseAuth.AuthStateListener
         lateinit var mUsername : String




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mUsername = ANONYMOUS

        openDialog()

        fab.setOnClickListener { view ->
            if (!MyDrawView.singlePlayerMode) {
                if(MyDrawView.boxElementsList.size!=0){
                    MyDrawView.boxElementsList.removeAt(MyDrawView.boxElementsList.size-1)
                }
            }else{
                if(MyDrawView.boxElementsList.size>=2){
                    MyDrawView.availableOptions.add(MyDrawView.boxElementsList[MyDrawView.boxElementsList.size-1])
                    MyDrawView.boxElementsList.removeAt(MyDrawView.boxElementsList.size-1)
                    MyDrawView.availableOptions.add(MyDrawView.boxElementsList[MyDrawView.boxElementsList.size-1])
                    MyDrawView.boxElementsList.removeAt(MyDrawView.boxElementsList.size-1)
                }
            }
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





         override fun applytexts(nameA: String?, nameB: String?) {
             if(nameA!="") {
                 playerA.text = nameA
             }
             else{
                 playerA.text = "Player A"
             }
             if(!MyDrawView.singlePlayerMode) {
                 if (nameB != "") {
                     playerB.text = nameB
                 } else {
                     playerB.text = "Player B"
                 }
             }
             else{
                 playerB.text = "Computer"
             }
         }


         private fun openDialog() {

             var playerNameDialog = PlayerNameDialog()
             playerNameDialog.show(supportFragmentManager, "Player Names")
//             if(MyDrawView.singlePlayerMode){
////                 playerBName.setText("Computer")
////
////             }
////             else{
////                 playerBName.setText("Player B")
////             }

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
            R.id.reset -> {
                MyDrawView.boxElementsList.clear()
                return true
            }
            R.id.switchMode -> {
                MyDrawView.boxElementsList.clear()
                MyDrawView.singlePlayerMode = !MyDrawView.singlePlayerMode
                openDialog()
                if(MyDrawView.singlePlayerMode){
                    Toast.makeText(this, "Switched to Single Player Mode", Toast.LENGTH_SHORT).show()
                    playerB.text = "Computer"
                }else{
                    Toast.makeText(this, "Switched to Dual Player Mode", Toast.LENGTH_SHORT).show()
                    playerA.text = "Player A"
                    playerB.text = "Player B"
                }
                return true
            }

            R.id.changeNames -> {
                MyDrawView.boxElementsList.clear()
                openDialog()
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
