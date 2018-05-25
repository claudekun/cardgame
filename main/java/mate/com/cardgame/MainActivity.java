package mate.com.cardgame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //USING BUTTERKNIFE
    @BindView(R.id.iv_card1)
    ImageView iv_card1;

    @BindView(R.id.iv_card2)
    ImageView iv_card2;

    @BindView(R.id.iv_back1)
    ImageView iv_back1;

    @BindView(R.id.iv_back2)
    ImageView iv_back2;

    @BindView(R.id.btn_next)
    Button btn_next;

    @BindView(R.id.tv_round)
    TextView tv_round;

    @BindView(R.id.tv_player)
    TextView tv_player;

    @BindView(R.id.tv_player_score)
    TextView tv_player_score;

    @BindView(R.id.tv_ai_score)
    TextView tv_ai_score;

    @BindView(R.id.tv_announce)
    TextView tv_announce;

    @BindView(R.id.ll_judgement)
    LinearLayout ll_judgement;

    @BindView(R.id.tv_judgement)
    TextView tv_judgement;

    @BindView(R.id.btn_quit)
    Button btn_quit;

    @BindView(R.id.btn_play_again)
    Button btn_play_again;

    private String players_name;
    Integer[][] cards;

    //VARIABLES FOR COUNTING THE SCORE OF THE PLAYERS
    private int players_score = 0;
    private int ai_score = 0;

    //VARIABLE FOR COUNTING THE ROUNDS
    private int current_round = 1;
    private final int max_round = 26;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SET THE FLAG TO FULLSCREEN
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //INITIALIZE BUTTERKNIFE
        ButterKnife.bind(this);

        //GET THE DATA FROM EXTRAS
        if (getIntent().hasExtra(StartActivity.PNAME)) {
            players_name = getIntent().getExtras().getString(StartActivity.PNAME);
            tv_player.setText(players_name);
        }

        //METHOD FOR SHUFFLING THE CARDS
        shuffleCards();

        //METHOD FOR DRAWING THE CARDS
        drawCards();

        //VIEWS' LISTENERS
        iv_back1.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_quit.setOnClickListener(this);
        btn_play_again.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //FIRST CARD
        if (v.getId() == R.id.iv_back1) {
            iv_back1.setEnabled(false);

            //METHOD FOR OPENING THE CARD
            flipAnimation(iv_back1, iv_card1, true, false, 180, 0f);
        }

        //NEXT ROUND BUTTON
        else if (v.getId() == R.id.btn_next) {

            //CHECK IF THE ROUND BELOW THE MAX ROUND
            if (current_round < max_round) {
                btn_next.setVisibility(View.GONE);

                //METHOD FOR CLOSING THE CARDS
                flipAnimation(iv_card1, iv_back1, true, true, 0f, 180f);
            }
        }

        //QUIT BUTTON
        else if (v.getId() == R.id.btn_quit) {
            quit();
        }

        //PLAY AGAIN BUTTON
        else if (v.getId() == R.id.btn_play_again) {

            //METHOD FOR SHOWING THE JUDGEMENT VIEW (THE LAST PART OF THE GAME WHICH ANNOUNCES THE WINNER)
            showOrCloseJudgement(null, true);
        }
    }


    //METHOD FOR FLIPPING ANIMATION (OPEN & CLOSE)
    private void flipAnimation(final ImageView viewToFlip, //THE BACK OF THE CARD
                               final ImageView viewToReveal, //THE FRONT OF THE CARD
                               boolean isFirstCard, //CHECKS IF THIS METHOD WAS CALLED FROM THE FIRST CARD
                               final boolean isNext, //CHECKS IF THIS METHOD WAS CALLED FROM THE NEXT ROUND BUTTON
                               float from, //START DEGREE VALUE OF THE FLIPPING ANIMATION
                               float to) { //END DEGREE VALUE OF THE FLIPPING ANIMATION

        //SIMULTANEOUSLY FLIP THE 2 IMAGEVIEWS (BACK AND FRONT)
        ObjectAnimator flip1 = ObjectAnimator.ofFloat(viewToFlip, "rotationY", from, to);
        ObjectAnimator flip2 = ObjectAnimator.ofFloat(viewToReveal, "rotationY", from, to);
        flip1.setDuration(800);
        flip2.setDuration(800);

        //A COUNTDOWN TIMER SET TO THE MIDDLE TIME OF THE DURATION OF THE ANIMATION
        //THE DURATION OF THE ANIMATION IS 800 MILLIS, SO THE TIMER WAS SET TO 400 MILLIS
        //IN THIS WAY WE CAN DETECT THAT THE DEGREE OF THE VIEW IS CURRENTLY SET INTO 90 DEGREES
        //SO WE CAN TAKE THE OPPORTUNITY TO CHANGE THE BACK IMAGEVIEW TO FRONT IMAGEVIEW
        new CountDownTimer(400, 400) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                //HIDE THE BACKVIEW OF THE CRAD
                viewToFlip.setVisibility(View.INVISIBLE);

                //SHOW THE FRONTVIEW OF THE CARD
                viewToReveal.setVisibility(View.VISIBLE);
            }
        }.start();

        //ADD A LISTENER TO THE FIRST CARD
        if (isFirstCard) {

            //AT THE END OF THE ANIMATION OF THE FIRST CARD
            flip1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (isNext) //IF THE NEXT ROUND BUTTON WAS CLICKED

                        //AFTER CLOSING THE FIRST CARD, USING THIS METHOD WILL CLOSE THE NEXT CARD
                        flipAnimation(iv_card2, iv_back2, false, true, 0f, 180f);
                    else {

                        //AFTER OPENING THE FIRST CARD, USING THIS METHOD WILL OPEN THE NEXT CARD
                        flipAnimation(iv_back2, iv_card2, false, false, 180f, 0f);
                    }
                }
            });
        }

        //ADD A LISTENER TO THE SECOND CARD
        else {

            //AT THE END OF ANIMATION OF THE SECOND CARD
            flip2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    //AFTER CLOSING THE SECOND CARD BY CLICKING THE NEXT ROUND BUTTON,
                    //DRAW 2 CARDS TO REPLACE THE CURRENT CARDS AND UPDATE THE ROUND NUMBER
                    if (isNext) {
                        btn_next.setVisibility(View.GONE);
                        drawCards();
                        current_round++;
                        tv_round.setText("Round " + current_round);
                        iv_back1.setEnabled(true);
                    }

                    //AFTER OPENING THE SECOND CARD, ANNOUNCE THE WINNER IN THE CURRENT ROUND
                    else {

                        //CHECK IF THE RESULT WAS DRAW
                        if (Integer.parseInt(iv_card1.getTag().toString())
                                == Integer.parseInt(iv_card2.getTag().toString())) {
                            //ANNOUNCE 'DRAW'
                            announceRoundWinner("Draw!");
                        }
                        //IF NOT DRAW
                        else {
                            //CHECK IF THE PLAYER'S VALUE IS GREATER THAN THE AI
                            if (Integer.parseInt(iv_card1.getTag().toString())
                                    > Integer.parseInt(iv_card2.getTag().toString())) {
                                players_score++;

                                //IF YES, ANNOUNCE 'YOU WIN'
                                announceRoundWinner("You Win!");
                            }
                            //IF AI GOT HIGHER VALUE
                            else {
                                ai_score++;

                                //ANNOUNCE 'YOU LOSE'
                                announceRoundWinner("You Lose!");
                            }

                            //UPDATE THE SCORE BOARD
                            updateScore();
                        }

                    }

                }
            });
        }

        flip1.start();
        flip2.start();
    }

    //METHOD FOR ANNOUNCING THE WINNER IN THE CURRENT ROUND
    void announceRoundWinner(String text) {
        tv_announce.setVisibility(View.VISIBLE);
        tv_announce.setText(text);

        //SET FADING OUT ANIMATION
        final AlphaAnimation aa = new AlphaAnimation(1f, 0f);
        aa.setDuration(500);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            //AT THE END OF FADING OUT ANIMATION
            @Override
            public void onAnimationEnd(Animation animation) {
                tv_announce.setVisibility(View.INVISIBLE);

                //CHECK IF THE CURRENT ROUND IS THE MAX
                if (current_round == 26) {
                    if (players_score == ai_score) {
                        //CLOSE THE JUDGEMENT VIEW
                        showOrCloseJudgement("DRAW!", false);
                    } else {
                        //CHECK IF THE PLAYER'S SCORE IS HIGHER THAN AI
                        if (players_score > ai_score)
                            //IF YES, ANNOUNCE 'YOU WIN'
                            showOrCloseJudgement("YOU WIN!", false);

                            //IF NO, ANNOUNCE 'YOU LOSE'
                        else showOrCloseJudgement("YOU LOSE!", false);
                    }
                } else { //SHOW NEXT ROUND BUTTON
                    btn_next.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //SET FADING IN ANIMATION
        AlphaAnimation aai = new AlphaAnimation(0f, 1f);
        aai.setDuration(500);

        //AT THE END OF FADING IN ANIMATION
        aai.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //START THE FADING OUT ANIMATION
                tv_announce.startAnimation(aa);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //START THE FADING IN ANIMATION
        tv_announce.startAnimation(aai);

    }

    //THIS METHOD SHOWS WHEN THE PLAYERS HAVE REACHED THE MAX ROUND WHICH ANNOUNCES THE WINNER OF THE GAME!!
    void showOrCloseJudgement(String text, boolean isShowing) {

        //CHECK IF ALREADY SHOWING
        //IF SO, MEANING THE PLAYER HAS CLICKED THE PLAY AGAIN BUTTON
        if (isShowing) {

            //SET FADING OUT ANIMATION OF THE JUDGEMENT VIEW
            AlphaAnimation aa = new AlphaAnimation(1f, 0f);
            aa.setDuration(1000);
            aa.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    //AT THE END OF ANIMATION, RESTART THE GAME!
                    ll_judgement.setVisibility(View.INVISIBLE);
                    current_round = 0;
                    tv_round.setText("Round 1");
                    players_score = 0;
                    ai_score = 0;
                    shuffleCards();
                    updateScore();
                    flipAnimation(iv_card1, iv_back1, true, true, 0f, 180f);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            //START THE FADING OUT ANIMATION OF THE JUDGEMENT VIEW
            ll_judgement.startAnimation(aa);
        }
        //IF THE JUDGEMENT VIEW ISN'T CURRENTLY SHOWING, MEANING THIS METHOD AS CALLED TO OPEN
        //THE JUDGEMENT VIEW
        else {
            ll_judgement.setVisibility(View.VISIBLE);
            tv_judgement.setText(text);

            //SET THE FADING IN ANIMATION OF THE JUDGEMENT VIEW
            AlphaAnimation aai = new AlphaAnimation(0f, 1f);
            aai.setDuration(1000);

            //START THE FADING IN ANIMATION
            ll_judgement.startAnimation(aai);
        }

    }

    //METHOD FOR SHUFFLING CARDS
    void shuffleCards() {
        //A MULTIDIMENSIONAL ARRAY THAT CONTAINS THE ID AND THE VALUE OF THE CARD
        cards = new Integer[][] {{R.drawable.c_a, 1}, {R.drawable.c_2, 2}, {R.drawable.c_3, 3}, {R.drawable.c_4, 4}, {R.drawable.c_5, 5}, {R.drawable.c_6, 6}, {R.drawable.c_7, 7}, {R.drawable.c_8, 8}, {R.drawable.c_9, 9}, {R.drawable.c_10, 10}, {R.drawable.c_j, 11}, {R.drawable.c_q, 12}, {R.drawable.c_k, 13}
            , {R.drawable.d_a, 1}, {R.drawable.d_2, 2}, {R.drawable.d_3, 3}, {R.drawable.d_4, 4}, {R.drawable.d_5, 5}, {R.drawable.d_6, 6}, {R.drawable.d_7, 7}, {R.drawable.d_8, 8}, {R.drawable.d_9, 9}, {R.drawable.d_10, 10}, {R.drawable.d_j, 11}, {R.drawable.d_q, 12}, {R.drawable.d_k, 13}
            , {R.drawable.h_a, 1}, {R.drawable.h_2, 2}, {R.drawable.h_3, 3}, {R.drawable.h_4, 4}, {R.drawable.h_5, 5}, {R.drawable.h_6, 6}, {R.drawable.h_7, 7}, {R.drawable.h_8, 8}, {R.drawable.h_9, 9}, {R.drawable.h_10, 10}, {R.drawable.h_j, 11}, {R.drawable.h_q, 12}, {R.drawable.h_k, 13}
            , {R.drawable.s_a, 1}, {R.drawable.s_2, 2}, {R.drawable.s_3, 3}, {R.drawable.s_4, 4}, {R.drawable.s_5, 5}, {R.drawable.s_6, 6}, {R.drawable.s_7, 7}, {R.drawable.s_8, 8}, {R.drawable.s_9, 9}, {R.drawable.s_10, 10}, {R.drawable.s_j, 11}, {R.drawable.s_q, 12}, {R.drawable.s_k, 13}};

        //SHUFFLE
        List<Integer[]> list = new ArrayList<>(Arrays.asList(cards));
        Collections.shuffle(list);

        //CONVERT FROM ARRAYLIST TO ARRAY
        cards = list.toArray(new Integer[list.size()][1]);
    }

    //METHOD FOR FRAWING THE CARDS
    void drawCards() {
        //GET 2 RANDOM RESOURCE IDs FROM THE MULTIDIMENSIONAL ARRAY
        //AND SET TO THE IMAGEVIEWS
        iv_card1.setImageResource(cards[0][0]);
        iv_card2.setImageResource(cards[1][0]);

        //GET THE VALUE OF CARD FROM THE MULTIDIMENSIONAL ARRAY AND
        //TAG IT TO THE IMAGEVIEWS
        iv_card1.setTag(cards[0][1]);
        iv_card2.setTag(cards[1][1]);

        //REMOVE THE CARDS THAT HAVE BEEN DRAWN
        List<Integer[]> list = new ArrayList<>(Arrays.asList(cards));
        list.remove(0);
        list.remove(0);

        //CONVERT FROM ARRAYLIST TO ARRAY
        cards = list.toArray(new Integer[list.size()][1]);
    }

    //METHOD FOR UPDATING THE SCORE BOARD
    void updateScore() {
        tv_player_score.setText(String.valueOf(players_score));
        tv_ai_score.setText(String.valueOf(ai_score));
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to quit this game?");
        builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                quit();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void quit() {
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
