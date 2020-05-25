package com.example.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.random;
import static java.lang.Math.round;

public class MyDrawView extends View {


    float gap ;
    float canvasSize;
    Paint marginPaint ;
    public static ArrayList<BoxElement> boxElementsList = new ArrayList<BoxElement>() ;
    public static ArrayList<BoxElement> availableOptions = new ArrayList<BoxElement>() ;
    public static boolean singlePlayerMode = true;
    boolean computerFirstTurn = false;
    MediaPlayer clickSound = MediaPlayer.create(getContext(), R.raw.burst);
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
        float z = 40;//make this responsive
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

        if(boxElementsList.size()==9){
            announceWinner(-1);
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
            if(notPresentInList(x, y)) {
                if(boxElementsList.size()!=0) {
                    boxElementsList.add(new BoxElement(x, y, !(boxElementsList.get(boxElementsList.size() - 1).isFirstPlayerTurn())));
                }else{
                    boxElementsList.add(new BoxElement(x, y, true));
                }
                removeFromAvailableOptions(x, y);
                clickSound.start();
                checkIfGameOver(x, y, (boxElementsList.get(boxElementsList.size() - 1).isFirstPlayerTurn()));
                if((boxElementsList.size()!=0)&&(singlePlayerMode)){
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
            Toast.makeText(getContext(), "Match Draw", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(getContext(), msg+" Wins!", Toast.LENGTH_SHORT).show();
        clearScreenTimer.start();
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
        Random random = new Random();
        int index = random.nextInt(availableOptions.size());
        boxElementsList.add(new BoxElement(availableOptions.get(index).getX(), availableOptions.get(index).getY(), turn));
        availableOptions.remove(index);
    }

    private void removeFromAvailableOptions(float x, float y) {
        for(int i=0; i < availableOptions.size() ; i++){
            if( (x==availableOptions.get(i).getX()) && (y==availableOptions.get(i).getY()) ){
                availableOptions.remove(i);
            }
        }
    }
}
