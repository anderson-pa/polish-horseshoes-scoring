package info.andersonpa.polishhorseshoesscoring;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import info.andersonpa.polishhorseshoesscoring.backend.Activity_Base;

public class GameTracker extends Activity_Base {
//        implements
//        GestureDetector.OnGestureListener,
//        GestureDetector.OnDoubleTapListener{
    private static final String DEBUG_TAG = "GameTracker";
    GestureLibrary gestureLibrary = null;
    GestureOverlayView gestureOverlayView;
    TextView gestureResult;
    ImageView imageView;
//    private GestureDetectorCompat mDetector;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_tracker);
        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
//        mDetector = new GestureDetectorCompat(this,this);
        // Set the gesture detector as the double tap
        // listener.
//        mDetector.setOnDoubleTapListener(this);

//        gestureResult = (TextView)findViewById(R.id.tv_result);
//        gestureOverlayView = (GestureOverlayView)findViewById(R.id.gestures);

//        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gesture);
//        gestureLibrary.load();

//        gestureOverlayView.addOnGesturePerformedListener(gesturePerformedListener);

//        ImageView imView = (ImageView) findViewById(R.id.imageView);
//        imView.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                gestureResult.setText(event.toString());
//                return gesturePerformedListener.onTouchEvent(event);
                // ... Respond to touch events
//                return true;
//            }
//        });
    }

    GestureOverlayView.OnGesturePerformedListener gesturePerformedListener
            = new GestureOverlayView.OnGesturePerformedListener(){

        @Override
        public void onGesturePerformed(GestureOverlayView view, Gesture gesture) {
            // TODO Auto-generated method stub
            ArrayList<Prediction> prediction = gestureLibrary.recognize(gesture);
            if(prediction.size() > 0){
                gestureResult.setText(prediction.get(0).name);
            }

        }};


//    @Override
//    public boolean onTouchEvent(MotionEvent event){
//        this.mDetector.onTouchEvent(event);
//        // Be sure to call the superclass implementation
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    public boolean onDown(MotionEvent event) {
//        Log.d(DEBUG_TAG,"onDown: " + event.toString());
//        return true;
//    }
//
//    @Override
//    public boolean onFling(MotionEvent event1, MotionEvent event2,
//                           float velocityX, float velocityY) {
//        Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
//        return true;
//    }
//
//    @Override
//    public void onLongPress(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
//    }
//
//    @Override
//    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
//                            float distanceY) {
//        Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
//        return true;
//    }

//    @Override
//    public void onShowPress(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
//    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
//        return true;
//    }
//
//    @Override
//    public boolean onDoubleTap(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
//        return true;
//    }
//
//    @Override
//    public boolean onDoubleTapEvent(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
//        return true;
//    }
//
//    @Override
//    public boolean onSingleTapConfirmed(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
//        return true;
//    }



//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        int action = MotionEventCompat.getActionMasked(event);
//
//        switch (action) {
//            case (MotionEvent.ACTION_DOWN):
//                Log.d(DEBUG_TAG, "Action was DOWN");
//                return true;
//            case (MotionEvent.ACTION_MOVE):
//                Log.d(DEBUG_TAG, "Action was MOVE");
//                return true;
//            case (MotionEvent.ACTION_UP):
//                Log.d(DEBUG_TAG, "Action was UP");
//                return true;
//            case (MotionEvent.ACTION_CANCEL):
//                Log.d(DEBUG_TAG, "Action was CANCEL");
//                return true;
//            case (MotionEvent.ACTION_OUTSIDE):
//                Log.d(DEBUG_TAG, "Movement occurred outside bounds " +
//                        "of current screen element");
//                return true;
//            default:
//                return super.onTouchEvent(event);
//        }
//    }


//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_game_tracker);
//
//        View myView = findViewById(R.id.button);
//
//        myView.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                // ... Respond to touch events
//                return true;
//            }
//        });
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
}
