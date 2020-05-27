package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_leader_board.*
import java.util.ArrayList

class LeaderBoardActivity : AppCompatActivity() {

    private lateinit var mFirebaseDatabase : FirebaseDatabase
    private lateinit var mDatabaseReference : DatabaseReference
    private var players = ArrayList<PlayerStatictics>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)

        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase.reference.child("Leader Board")


        var mChildEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                var playerStatictics =
                    dataSnapshot.getValue<PlayerStatictics>(PlayerStatictics::class.java)
                if (playerStatictics != null) {
                  players.add(playerStatictics)
                }
                var mLeaderBoardAdapter = LeaderBoardAdapter(players, applicationContext)
                leaderBoardListView.adapter = mLeaderBoardAdapter
                leaderBoardListView.layoutManager = LinearLayoutManager(applicationContext)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        mDatabaseReference!!.addChildEventListener(mChildEventListener)


//        players.add(PlayerStatictics("salai", 1, 2, 3))
//        players.add(PlayerStatictics("helo", 1, 2, 3))
//        players.add(PlayerStatictics("heya", 4,2, 5))
//
//        var mLeaderBoardAdapter = LeaderBoardAdapter(players, this)
//        leaderBoardListView.adapter = mLeaderBoardAdapter
//        leaderBoardListView.layoutManager = LinearLayoutManager(this)

    }

}
