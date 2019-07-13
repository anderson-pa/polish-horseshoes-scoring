package info.andersonpa.polishhorseshoesscoring;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class GameSelect extends AppCompatActivity {
    Dpad dpad = new Dpad();
    CoordinatorLayout coordinatorLayout;
    TextView tv1, tv2, tv3, tv4, tv5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        tv1 = findViewById(R.id.textView1);
        tv2 = findViewById(R.id.textView2);
        tv3 = findViewById(R.id.textView3);
        tv4 = findViewById(R.id.textView4);
        tv5 = findViewById(R.id.textView5);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Integer> gameControllerDeviceIds = new ArrayList<Integer>();
                int[] deviceIds = InputDevice.getDeviceIds();
                for (int deviceId : deviceIds) {
                    InputDevice dev = InputDevice.getDevice(deviceId);
                    int sources = dev.getSources();

                    // Verify that the device has gamepad buttons, control sticks, or both.
                    if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                            || ((sources & InputDevice.SOURCE_JOYSTICK)
                            == InputDevice.SOURCE_JOYSTICK)) {
                        // This device is a game controller. Store its device ID.
                        if (!gameControllerDeviceIds.contains(deviceId)) {
                            gameControllerDeviceIds.add(deviceId);
                        }
                    }
                }

                Snackbar.make(view, "Device IDs: " + gameControllerDeviceIds.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // Check if this event is from a joystick movement and process accordingly.
        // Check that the event came from a game controller
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {

            // Process all historical movement samples in the batch
            final int historySize = event.getHistorySize();

            // Process the movements starting from the
            // earliest historical position in the batch
//            for (int i = 0; i < historySize; i++) {
//                // Process the event at historical position i
//                processJoystickInput(event, i);
//            }

            // Process the current movement sample in the batch (position -1)
            processJoystickInput(event, -1);
            return true;
        }
        return super.onGenericMotionEvent(event);
//
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        String tv_text = "";
        if ((event.getSource() & InputDevice.SOURCE_GAMEPAD)
                == InputDevice.SOURCE_GAMEPAD) {
            if (event.getRepeatCount() == 0) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BUTTON_A:
                        tv_text = "A";
                        handled = true;
                        break;
                    case KeyEvent.KEYCODE_BUTTON_B:
                        tv_text = "B";
                        handled = true;
                        break;
                    case KeyEvent.KEYCODE_BUTTON_X:
                        tv_text = "X";
                        handled = true;
                        break;
                    case KeyEvent.KEYCODE_BUTTON_Y:
                        tv_text = "Y";
                        handled = true;
                        break;
                    case KeyEvent.KEYCODE_BUTTON_THUMBL:
                        tv_text = "Tl";
                        handled = true;
                        break;
                    case KeyEvent.KEYCODE_BUTTON_THUMBR:
                        tv_text = "Tr";
                        handled = true;
                        break;
                    case KeyEvent.KEYCODE_BUTTON_L1:
                        tv_text = "BL1";
                        handled = true;
                        break;
                    case KeyEvent.KEYCODE_BUTTON_R1:
                        tv_text = "BR1";
                        handled = true;
                        break;
                    case KeyEvent.KEYCODE_BACK:
                        tv_text = "Back";
                        handled = true;
                        break;
                    case KeyEvent.KEYCODE_BUTTON_START:
                        tv_text = "Start";
                        handled = true;
                        break;
                }
            }
            if (handled) {
                tv2.setText(tv_text);
                return true;
            }
        }
        if ((event.getSource() & InputDevice.SOURCE_DPAD)
                == InputDevice.SOURCE_DPAD) {

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    tv_text = "L";
                    handled = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    tv_text = "R";
                    handled = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    tv_text = "U";
                    handled = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    tv_text = "D";
                    handled = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    tv_text = "C";
                    handled = true;
                    break;
            }
            if (handled) {
                tv1.setText(tv_text);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private static float getCenteredAxis(MotionEvent event,
                                         InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis) :
                            event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    private void processJoystickInput(MotionEvent event, int historyPos) {

        InputDevice inputDevice = event.getDevice();

        float x1, y1, x2, y2, x3, y3;
        x1 = historyPos < 0 ? event.getAxisValue(MotionEvent.AXIS_X) :
                event.getAxisValue(MotionEvent.AXIS_X, historyPos);
        y1 = historyPos < 0 ? event.getAxisValue(MotionEvent.AXIS_Y) :
                event.getAxisValue(MotionEvent.AXIS_Y, historyPos);

        x2 = historyPos < 0 ? event.getAxisValue(MotionEvent.AXIS_Z) :
                event.getAxisValue(MotionEvent.AXIS_Z, historyPos);
        y2 = historyPos < 0 ? event.getAxisValue(MotionEvent.AXIS_RZ) :
                event.getAxisValue(MotionEvent.AXIS_RZ, historyPos);

        x3 = historyPos < 0 ? event.getAxisValue(MotionEvent.AXIS_LTRIGGER) :
                event.getAxisValue(MotionEvent.AXIS_LTRIGGER, historyPos);
        y3 = historyPos < 0 ? event.getAxisValue(MotionEvent.AXIS_RTRIGGER) :
                event.getAxisValue(MotionEvent.AXIS_RTRIGGER, historyPos);

        tv3.setText(String.format(Locale.getDefault(), "%.3f", x1) + ", "
                + String.format(Locale.getDefault(), "%.3f", y1));
        tv4.setText(String.format(Locale.getDefault(), "%.3f", x2) + ", "
                + String.format(Locale.getDefault(), "%.3f", y2));
        tv5.setText(String.format(Locale.getDefault(), "%.3f", x3) + ", "
                + String.format(Locale.getDefault(), "%.3f", y3));

        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
//        float x = getCenteredAxis(event, inputDevice,
//                MotionEvent.AXIS_X, historyPos);
//        if (x == 0) {
//            x = getCenteredAxis(event, inputDevice,
//                    MotionEvent.AXIS_HAT_X, historyPos);
//        }
//        if (x == 0) {
//            x = getCenteredAxis(event, inputDevice,
//                    MotionEvent.AXIS_Z, historyPos);
//        }

        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
//        float y = getCenteredAxis(event, inputDevice,
//                MotionEvent.AXIS_Y, historyPos);
//        if (y == 0) {
//            y = getCenteredAxis(event, inputDevice,
//                    MotionEvent.AXIS_HAT_Y, historyPos);
//        }
//        if (y == 0) {
//            y = getCenteredAxis(event, inputDevice,
//                    MotionEvent.AXIS_RZ, historyPos);
//        }

        // Update the ship object based on the new x and y values
    }
}
