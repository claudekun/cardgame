package mate.com.cardgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    //USING BUTTERKNIFE LIBRARY FOR EASIER INITIALIZATION OF THE VIEWS
    @BindView(R.id.btn_start)
    Button btn_start;

    @BindView(R.id.et_name)
    EditText et_name;

    @BindView(R.id.ll_container)
    LinearLayout ll_container;

    TransitionDrawable transitiondrawable;

    public static final String PNAME = "players_name";

    private ColorDrawable[] backGroundColor = {
            new ColorDrawable(Color.parseColor("#003d5d")),
            new ColorDrawable(Color.parseColor("#8c3d5d"))
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SET THE FLAG TO FULLSCREEN
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start);

        //INITIALIZE BUTTERKNIFE
        ButterKnife.bind(this);

        //SET THE BACKGROUND OF THIS ACTIVITY
        transitiondrawable = new TransitionDrawable(backGroundColor);
        ll_container.setBackground(transitiondrawable);

        btn_start.setOnClickListener(this);

    }

    //METHOD FOR THE ANIMATION OF THE BACKGROUND
    private void startColorAnimation() {

        //START THE ANIMATION
        transitiondrawable.startTransition(300);

        //SET A COUNTDOWN TIMER TO PERFORM A TASK RIGHT AFTER THE ANIMATION
        new CountDownTimer(600, 600) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                //ONCE THE TIMER IS FINISH, GO TO THE NEXT ACTIVITY
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra(PNAME, et_name.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //RESET THE VIEWS BEFORE LEAVING THIS ACTIVITY
        ll_container.setBackground(backGroundColor[0]);
        btn_start.setEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        //START THE ANIMATION AFTER CLICKING THE BUTTON
        if (et_name.getText().length() > 0) {
            btn_start.setEnabled(false);
            transitiondrawable = new TransitionDrawable(backGroundColor);
            ll_container.setBackground(transitiondrawable);
            startColorAnimation();

        } else { //IF THE PLAYER'S NAME HASN'T BEEN ENCODED
            Toast.makeText(this, "Please input your name", Toast.LENGTH_SHORT).show();
        }
    }
}
