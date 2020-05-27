package com.example.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.round;

public class MyDrawView extends View {


    float gap ;
    float canvasSize;
    Paint marginPaint ;
    public static int win = 0;
    public static int loss = 0;
    public static int draw = 0;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    public DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("Leader Board");

    public static ArrayList<BoxElement> boxElementsList = new ArrayList<BoxElement>() ;
    public static ArrayList<BoxElement> availableOptions = new ArrayList<BoxElement>() ;
    public static boolean singlePlayerMode = true;
    boolean computerFirstTurn = false;
    MediaPlayer clickSound = MediaPlayer.create(getContext(), R.raw.burst);
    boolean clearScreenTimerRunning = false;
    CountDownTimer clearScreenTimer = new CountDownTimer(1000, 500) {
        @Override
        public void onTick(long millisUntilFinished) {
            //do nothing
        }

        @Override
        public void onFinish() {
            boxElementsList.clear();
            if (singlePlayerMode) {
                computerFirstTurn = !computerFirstTurn;
                if(computerFirstTurn){
                    initializeAvailableOptions();
                    makeComputerMove(true);
                }
            }
            swapNames();
            clearScreenTimerRunning = false;
        }
    };

    public MyDrawView(Context context) {
        super(context);
        init(null);
    }

    public MyDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MyDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public MyDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {

        marginPaint = new Paint();
        marginPaint.setColor(getResources().getColor(R.color.margin));
        marginPaint.isAntiAlias();
        marginPaint.setStyle(Paint.Style.STROKE);
        marginPaint.setStrokeWidth(30);


    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvasSize = canvas.getHeight();
        gap = round(canvasSize / (3));
        canvas.drawColor(getResources().getColor(R.color.canvasBg));
        drawMargins(canvas);
        drawAllElements(canvas);
        postInvalidate();

    }

    private void drawAllElements(Canvas canvas) {
        boolean firstPlayerTurn = true;
        float z = 40;// TODO: make this responsive
        for(int i = 0; i<boxElementsList.size(); i++){

            if(firstPlayerTurn){
                float x = boxElementsList.get(i).getX();
                float y = boxElementsList.get(i).getY();
                canvas.drawLine(x, y, x+z, y-z, marginPaint);
                canvas.drawLine(x, y, x+z, y+z, marginPaint);
                canvas.drawLine(x, y, x-z, y-z, marginPaint);
                canvas.drawLine(x, y, x-z, y+z, marginPaint);
                firstPlayerTurn = false;
            }
            else {
                canvas.drawCircle(boxElementsList.get(i).getX(), boxElementsList.get(i).getY(), 50, marginPaint);
                firstPlayerTurn = true;
            }

        }
        TextView txtView = (TextView) ((MainActivity)getContext()).findViewById(R.id.playerA);
        if((txtView.getText().toString()=="Computer")&&(boxElementsList.size()==0)){ // to avoid the reset bug
            initializeAvailableOptions();
            makeComputerMove(true);
        }

    }

    private void drawMargins(Canvas canvas) {

        for(int i = 0 ; i<=3 ; i++){
            canvas.drawLine(gap*i, 0, gap*i, canvasSize, marginPaint);
            canvas.drawLine(0, gap*i, canvasSize, gap*i, marginPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);

        if(event.getAction()==MotionEvent.ACTION_DOWN){
            //check if present already in the list
            if((boxElementsList.size()==0)&&(singlePlayerMode)){
                initializeAvailableOptions();
            }
            float x = ((event.getX()) - (event.getX()%gap) + (gap/2));
            float y = ((event.getY()) - (event.getY()%gap) + (gap/2));
            if(notPresentInList(x, y)&&(!clearScreenTimerRunning)) {
                if(boxElementsList.size()!=0) {
                    boxElementsList.add(new BoxElement(x, y, !(boxElementsList.get(boxElementsList.size() - 1).isFirstPlayerTurn())));
                }else{
                    boxElementsList.add(new BoxElement(x, y, true));
                }
                removeFromAvailableOptions(x, y);
                clickSound.start();
                checkIfGameOver(x, y, (boxElementsList.get(boxElementsList.size() - 1).isFirstPlayerTurn()));
                if((boxElementsList.size()!=0)&&(singlePlayerMode)&&(!clearScreenTimerRunning)){
                    if(!(computerFirstTurn&&(boxElementsList.size()==1))) {
                        makeComputerMove(!(boxElementsList.get(boxElementsList.size() - 1).isFirstPlayerTurn()));
                        checkIfGameOver((boxElementsList.get(boxElementsList.size() - 1).getX()),
                                (boxElementsList.get(boxElementsList.size() - 1).getY()),
                                (boxElementsList.get(boxElementsList.size() - 1).isFirstPlayerTurn()));
                    }
                }
            }

        }

        return value;
    }


    private void checkIfGameOver(float x, float y, boolean firstPlayerTurn) {
        boolean a, b;

        if(((x<gap)||(x>2*gap))&&((y<gap)||(y>2*gap))){
            //corner point

            a = checkifPresentinList(x, (y+gap)%(3*gap), firstPlayerTurn);
            b = checkifPresentinList(x, (y+(2*gap))%(3*gap), firstPlayerTurn);
            if(a&&b){
                announceWinner(firstPlayerTurn?1:0);
                return;
            }
            a = checkifPresentinList((x+gap)%(3*gap), y, firstPlayerTurn);
            b = checkifPresentinList((x+(2*gap))%(3*gap), y, firstPlayerTurn);
            if(a&&b){
                announceWinner(firstPlayerTurn?1:0);
                return;
            }

            if(((x<gap)&&(y<gap))||((x>2*gap)&&y>2*gap)) {
                a = checkifPresentinList((x + gap) % (3 * gap), (y+gap)%(3*gap), firstPlayerTurn);
                b = checkifPresentinList((x + (2 * gap)) % (3 * gap), (y+(2*gap))%(3*gap), firstPlayerTurn);
                if (a && b) {
                    announceWinner(firstPlayerTurn?1:0);
                    return;
                }
            }else{
                a = checkifPresentinList((x + (2*gap)) % (3 * gap), (y+gap)%(3*gap), firstPlayerTurn);
                b = checkifPresentinList((x + (1*gap)) % (3 * gap), (y+(2*gap))%(3*gap), firstPlayerTurn);
                if (a && b) {
                    announceWinner(firstPlayerTurn?1:0);
                    return;
                }
            }

        }
        else if((x==(1.5*gap))&&(y==(1.5*gap))){

            //mid point

            a = checkifPresentinList(x, (y+gap)%(3*gap), firstPlayerTurn);
            b = checkifPresentinList(x, (y+(2*gap))%(3*gap), firstPlayerTurn);
            if(a&&b){
                announceWinner(firstPlayerTurn?1:0);
                return;
            }
            a = checkifPresentinList((x+gap)%(3*gap), y, firstPlayerTurn);
            b = checkifPresentinList((x+(2*gap))%(3*gap), y, firstPlayerTurn);
            if(a&&b){
                announceWinner(firstPlayerTurn?1:0);
                return;
            }
            a = checkifPresentinList((x + gap) % (3 * gap), (y+gap)%(3*gap), firstPlayerTurn);
            b = checkifPresentinList((x + (2 * gap)) % (3 * gap), (y+(2*gap))%(3*gap), firstPlayerTurn);
            if (a && b) {
                announceWinner(firstPlayerTurn?1:0);
                return;
            }
            a = checkifPresentinList((x + (2*gap)) % (3 * gap), (y+gap)%(3*gap), firstPlayerTurn);
            b = checkifPresentinList((x + (1*gap)) % (3 * gap), (y+(2*gap))%(3*gap), firstPlayerTurn);
            if (a && b) {
                announceWinner(firstPlayerTurn?1:0);
                return;
            }
        }
        else{
            //edge point

            a = checkifPresentinList(x, (y+gap)%(3*gap), firstPlayerTurn);
            b = checkifPresentinList(x, (y+(2*gap))%(3*gap), firstPlayerTurn);
            if(a&&b){
                announceWinner(firstPlayerTurn?1:0);
                return;
            }
            a = checkifPresentinList((x+gap)%(3*gap), y, firstPlayerTurn);
            b = checkifPresentinList((x+(2*gap))%(3*gap), y, firstPlayerTurn);
            if(a&&b){
                announceWinner(firstPlayerTurn?1:0);
                return;
            }
        }

        if(boxElementsList.size()==9){
            announceWinner(-1);
        }
    }

    private void announceWinner(int q) {

        String msg;
        if(q==1){
            TextView txtView = (TextView) ((MainActivity)getContext()).findViewById(R.id.playerA);
            msg = txtView.getText().toString();
        }else if(q==0){
            TextView txtView = (TextView) ((MainActivity)getContext()).findViewById(R.id.playerB);
            msg = txtView.getText().toString();
        }
        else{
            if(singlePlayerMode){
                draw++;
                mDatabaseReference.child(MainActivity.emailId).setValue(new PlayerStatictics(MainActivity.mUsername, win, loss, draw));
            }
            Toast.makeText(getContext(), "Match Draw", Toast.LENGTH_SHORT).show();
            clearScreenTimer.start();
            clearScreenTimerRunning = true;
            return;
        }
        if(singlePlayerMode) {
            if (msg != "Computer") {
                win++;
            }
            else{
                loss++;
            }
            mDatabaseReference.child(MainActivity.emailId).setValue(new PlayerStatictics(MainActivity.mUsername, win, loss, draw));
        }
        Toast.makeText(getContext(), msg+" Wins!", Toast.LENGTH_SHORT).show();
        clearScreenTimer.start();
        clearScreenTimerRunning = true;
    }


    private boolean checkifPresentinList(float x, float y, boolean firstPlayerTurn){
        for(int i=0; i < boxElementsList.size() ; i++){
            if((x==boxElementsList.get(i).getX())&&(y==boxElementsList.get(i).getY())
                    &&(firstPlayerTurn==boxElementsList.get(i).isFirstPlayerTurn())){
                return true;
            }
        }

        return false;
    }

    private int indexFromAvailableOptions(float x, float y, int index){
        for(int i=0; i < availableOptions.size() ; i++){
            if((x==availableOptions.get(i).getX())&&(y==availableOptions.get(i).getY())){
                index = i;
                return index;
            }
        }

        return index;
    }

    private boolean notPresentInList(float x, float y) {

        for(int i=0; i < boxElementsList.size() ; i++){
            if((x==boxElementsList.get(i).getX())&&(y==boxElementsList.get(i).getY())){
                return false;
            }
        }

        return true;
    }

    private void swapNames(){

        String nameX, nameO;

        TextView txtView = (TextView) ((MainActivity)getContext()).findViewById(R.id.playerB);
        nameX = txtView.getText().toString();
        txtView = (TextView) ((MainActivity)getContext()).findViewById(R.id.playerA);
        nameO = txtView.getText().toString();
        txtView.setText(nameX);
        txtView = (TextView) ((MainActivity)getContext()).findViewById(R.id.playerB);
        txtView.setText(nameO);

    }


// SPM

    private void initializeAvailableOptions() {
        availableOptions.clear();
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                availableOptions.add(new BoxElement(((i*gap)+(gap/2)), ((j*gap)+(gap/2)), true));
            }
        }
    }


    private void makeComputerMove(boolean turn) {
        int index = 0;
        index = ComputerCanScore(index);
        if(index!=-1){
        }
        else{
            index = opponentCanScore(index);
            if(index!=-1){
            }
            else {
                //strategy
                if(availableOptions.size()!=0) {
                    index = computerStrategy(index);
                    if (index == -1) {
                        Random random = new Random();
                        index = random.nextInt(availableOptions.size());
                    }
                }
            }
        }
        if (availableOptions.size()!=0) {
            boxElementsList.add(new BoxElement(availableOptions.get(index).getX(), availableOptions.get(index).getY(), turn));
            availableOptions.remove(index);
        }
    }

    private int computerStrategy(int index) {
        if(boxElementsList.size()==0){
            Random random = new Random();
            int corner = random.nextInt(4);
            switch (corner){
                case 0: return 0;
                case 1: return 2;
                case 2: return 6;
                case 3: return 8;
            }
        }
        return -1;
    }

    private int opponentCanScore(int index) {
        if(boxElementsList.size()>2){
            if((boxElementsList.get(boxElementsList.size()-1).firstPlayerTurn)){
                index = possibilityOfWinningPresent(index, boxElementsList.get(boxElementsList.size()-1).firstPlayerTurn);
                if(index!=-1){
                    return index;
                }
            }
        }
        if(boxElementsList.size()>3){
            if((!boxElementsList.get(boxElementsList.size()-1).firstPlayerTurn)){
                index = possibilityOfWinningPresent(index, boxElementsList.get(boxElementsList.size()-1).firstPlayerTurn);
                if(index!=-1){
                    return index;
                }
            }
        }
        return -1;
    }

    private int possibilityOfWinningPresent(int index, boolean playerTurn) {
        //check for possibility of scoring and update the index to match that from possible options
        for(int i = (boxElementsList.get(0).firstPlayerTurn==playerTurn)?0:1 ; i<boxElementsList.size() ; i+=2){
            for(int j = i+2 ; j<boxElementsList.size() ; j+=2){
                BoxElement a = boxElementsList.get(i);
                BoxElement b = boxElementsList.get(j);

                if(a.getX()==b.getX()){ // vertical line

                    if(a.getY()<b.getY()){//a above b
                        if((b.getY()-a.getY())==gap){
                            boolean c = checkifPresentinList(b.getX(), (b.getY()+gap)%(3*gap), true);
                            boolean d = checkifPresentinList(b.getX(), (b.getY()+gap)%(3*gap), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions(b.getX(), (b.getY()+gap)%(3*gap), index);
                                return index;
                            }
                        }
                        if((b.getY()-a.getY())==(2*gap)){
                            boolean c = checkifPresentinList(b.getX(), (b.getY()+(2*gap))%(3*gap), true);
                            boolean d = checkifPresentinList(b.getX(), (b.getY()+(2*gap))%(3*gap), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions(b.getX(), (b.getY()+(2*gap))%(3*gap), index);
                                return index;
                            }
                        }
                    }
                    else if(a.getY()>b.getY()){//a below b
                        if((a.getY()-b.getY())==gap){
                            boolean c = checkifPresentinList(a.getX(), (a.getY()+gap)%(3*gap), true);
                            boolean d = checkifPresentinList(a.getX(), (a.getY()+gap)%(3*gap), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions(a.getX(), (a.getY()+gap)%(3*gap), index);
                                return index;
                            }
                        }
                        if((a.getY()-b.getY())==(2*gap)){
                            boolean c = checkifPresentinList(a.getX(), (a.getY()+(2*gap))%(3*gap), true);
                            boolean d = checkifPresentinList(a.getX(), (a.getY()+(2*gap))%(3*gap), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions(a.getX(), (a.getY()+(2*gap))%(3*gap), index);
                                return index;
                            }
                        }
                    }
                }

                else if(a.getY()==b.getY()){ // horizontal line

                    if(a.getX()<b.getX()){//a left of b
                        if((b.getX()-a.getX())==gap){
                            boolean c = checkifPresentinList((b.getX()+gap)%(3*gap), b.getY(), true);
                            boolean d = checkifPresentinList((b.getX()+gap)%(3*gap), b.getY(), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions((b.getX()+gap)%(3*gap), b.getY(), index);
                                return index;
                            }
                        }
                        if((b.getX()-a.getX())==(2*gap)){
                            boolean c = checkifPresentinList((b.getX()+(2*gap))%(3*gap), b.getY(), true);
                            boolean d = checkifPresentinList((b.getX()+(2*gap))%(3*gap), b.getY(), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions((b.getX()+(2*gap))%(3*gap), b.getY(), index);
                                return index;
                            }
                        }
                    }
                    else if(a.getX()>b.getX()){//a right of b
                        if((a.getX()-b.getX())==gap){
                            boolean c = checkifPresentinList((a.getX()+gap)%(3*gap), a.getY(), true);
                            boolean d = checkifPresentinList((a.getX()+gap)%(3*gap), a.getY(), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions((a.getX()+gap)%(3*gap), a.getY(), index);
                                return index;
                            }
                        }
                        if((a.getX()-b.getX())==(2*gap)){
                            boolean c = checkifPresentinList((a.getX()+(2*gap))%(3*gap), a.getY(), true);
                            boolean d = checkifPresentinList((a.getX()+(2*gap))%(3*gap), a.getY(), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions((a.getX()+(2*gap))%(3*gap), a.getY(), index);
                                return index;
                            }
                        }
                    }

                }

                else if ( (a.getX()==a.getY()) && (b.getX()==b.getY()) ){ // principal diagonal
                    if(a.getX()<b.getX()){  // a left of b
                        if((b.getX()-a.getX())==gap){
                            boolean c = checkifPresentinList((b.getX()+gap)%(3*gap), (b.getY()+gap)%(3*gap), true);
                            boolean d = checkifPresentinList((b.getX()+gap)%(3*gap), (b.getY()+gap)%(3*gap), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions((b.getX()+gap)%(3*gap), (b.getY()+gap)%(3*gap), index);
                                return index;
                            }
                        }
                        if((b.getX()-a.getX())==(2*gap)){
                            boolean c = checkifPresentinList((b.getX()+(2*gap))%(3*gap), (b.getY()+(2*gap))%(3*gap), true);
                            boolean d = checkifPresentinList((b.getX()+(2*gap))%(3*gap), (b.getY()+(2*gap))%(3*gap), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions((b.getX()+(2*gap))%(3*gap), (b.getY()+(2*gap))%(3*gap), index);
                                return index;
                            }
                        }
                    }
                    else{  // a right of b
                        if((a.getX()-b.getX())==gap){
                            boolean c = checkifPresentinList((a.getX()+gap)%(3*gap), (a.getY()+gap)%(3*gap), true);
                            boolean d = checkifPresentinList((a.getX()+gap)%(3*gap), (a.getY()+gap)%(3*gap), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions((a.getX()+gap)%(3*gap), (a.getY()+gap)%(3*gap), index);
                                return index;
                            }
                        }
                        if((a.getX()-b.getX())==(2*gap)){
                            boolean c = checkifPresentinList((a.getX()+(2*gap))%(3*gap), (a.getY()+(2*gap))%(3*gap), true);
                            boolean d = checkifPresentinList((a.getX()+(2*gap))%(3*gap), (a.getY()+(2*gap))%(3*gap), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions((a.getX()+(2*gap))%(3*gap), (a.getY()+(2*gap))%(3*gap), index);
                                return index;
                            }
                        }
                    }
                }

                else if( (((3*gap)-a.getX())==a.getY()) && (((3*gap)-b.getX())==b.getY()) ){ // other diagonal

                    if(a.getY()<b.getY()){//a above b
                        if((b.getY()-a.getY())==gap){
                            boolean c = checkifPresentinList((b.getX()+(2*gap))%(3*gap), (b.getY()+gap)%(3*gap), true);
                            boolean d = checkifPresentinList((b.getX()+(2*gap))%(3*gap), (b.getY()+gap)%(3*gap), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions((b.getX()+(2*gap))%(3*gap), (b.getY()+gap)%(3*gap), index);
                                return index;
                            }
                        }
                        if((b.getY()-a.getY())==(2*gap)){
                            boolean c = checkifPresentinList((b.getX()+gap)%(3*gap), (b.getY()+(2*gap))%(3*gap), true);
                            boolean d = checkifPresentinList((b.getX()+gap)%(3*gap), (b.getY()+(2*gap))%(3*gap), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions((b.getX()+gap)%(3*gap), (b.getY()+(2*gap))%(3*gap), index);
                                return index;
                            }
                        }
                    }
                    else if(a.getY()>b.getY()){//a below b
                        if((a.getY()-b.getY())==gap){
                            boolean c = checkifPresentinList((a.getX()+(2*gap))%(3*gap), (a.getY()+gap)%(3*gap), true);
                            boolean d = checkifPresentinList((a.getX()+(2*gap))%(3*gap), (a.getY()+gap)%(3*gap), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions((a.getX()+(2*gap))%(3*gap), (a.getY()+gap)%(3*gap), index);
                                return index;
                            }
                        }
                        if((a.getY()-b.getY())==(2*gap)){
                            boolean c = checkifPresentinList((a.getX()+gap)%(3*gap), (a.getY()+(2*gap))%(3*gap), true);
                            boolean d = checkifPresentinList((a.getX()+gap)%(3*gap), (a.getY()+(2*gap))%(3*gap), false);
                            if(!d&&!c){
                                index = indexFromAvailableOptions((a.getX()+gap)%(3*gap), (a.getY()+(2*gap))%(3*gap), index);
                                return index;
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    private int ComputerCanScore(int index) {
        if(boxElementsList.size()>3){
            if((boxElementsList.get(boxElementsList.size()-2).firstPlayerTurn)){
                index = possibilityOfWinningPresent(index, boxElementsList.get(boxElementsList.size()-2).firstPlayerTurn);
                if(index!=-1) {
                    return index;
                }
            }
        }
        if(boxElementsList.size()>4){
            if((!boxElementsList.get(boxElementsList.size()-2).firstPlayerTurn)){
                index = possibilityOfWinningPresent(index, boxElementsList.get(boxElementsList.size()-2).firstPlayerTurn);
                if(index!=-1) {
                    return index;
                }
            }
        }
        return -1;
    }

    private void removeFromAvailableOptions(float x, float y) {
        for(int i=0; i < availableOptions.size() ; i++){
            if( (x==availableOptions.get(i).getX()) && (y==availableOptions.get(i).getY()) ){
                availableOptions.remove(i);
            }
        }
    }
}
