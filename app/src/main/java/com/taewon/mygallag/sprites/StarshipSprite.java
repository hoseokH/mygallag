package com.taewon.mygallag.sprites;

 //맡은거
import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.taewon.mygallag.MainActivity;
import com.taewon.mygallag.R;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.items.HealitemSprite;
import com.taewon.mygallag.items.PowerItemSprite;
import com.taewon.mygallag.items.SpeedItemSprite;

import java.util.ArrayList;

public class StarshipSprite extends Sprite{
    Context context;
    SpaceInvadersView game;
    public float speed;
    private int bullets, life=3, powerLevel;
    private int specialShotCount;
    private boolean isSpecialShooting;
    private static ArrayList<Integer> bulletSprites = new ArrayList<Integer>();
    private final static float MAX_SPEED = 3.5f;
    private final static int MAX_HEART = 3;

    private RectF rectF;
    private boolean isReloading = false;

    public StarshipSprite(Context context, SpaceInvadersView game, int resId, int x, int y, float speed){
        super(context, resId, x, y);
        this.context = context;
        this.game = game;
        this.speed = speed;
        init();
    }

    public void init(){
        dx=dy=0;  //플레이어 시작 위치
        bullets=30;  //시작할떄 들고있는 총알
        life=3;  //시작할때 들고있는 생명
        specialShotCount = 2; //시작할때 사용가능한 특수 총알

        powerLevel=0;  //플레이어 총알 레벨(?) 이미지
        Integer [] shots = {R.drawable.shot_001,R.drawable.shot_002,R.drawable.
                shot_003,R.drawable.shot_004,R.drawable.shot_005,R.drawable.shot_006,R.drawable.shot_007};

        for(int i =0; i<shots.length; i++){
            bulletSprites.add(shots[i]);
        }
    }

    @Override
    public void move() {
        //플레이어가 화면에서 탈출하지 못하게 부딫치
        if((dx<0) && (x <120)) return;
        if((dx>0) && (x > game.screenW - 120)) return;
        if((dy<0) && (y <120)) return;
        if((dy>0) && (y > game.screenH-120)) return;
        super.move(); //슈퍼클래스
    }

    //총알 개수 리턴
    public int getBulletsCount() {return bullets;}

    //스피드 제어
    public void plusSpeed(float speed) { this.speed += speed;}


    //
    public void moveRight(double force) {setDx((float)(1*force*speed));}
    public void moveLeft(double force) {setDx((float)(-1*force*speed));}
    public void moveDown(double force) {setDy((float)(1*force*speed));}
    public void moveUp(double force) {setDy((float)(-1*force*speed));}
    public void resetDx(){ setDx(0); }
    public void resetDy(){ setDy(0); }





    public void fire(){
        if(isReloading | isSpecialShooting){return;}
        //        //ShotSprite 생성자구현
        ShotSprite shot = new ShotSprite(context, game, bulletSprites.get(powerLevel),
                getX()+10, getY()-30, -16 );
        MainActivity.effectSound(MainActivity.PLAYER_SHOT);
        //SpaceInvadersView 의 getSprites()구현
        game.getSprites().add(shot);
        bullets--;

        MainActivity.bulletCount.setText(bullets + "/30");
        Log.d("bullets", bullets + "/30");
        if(bullets ==0){
            reloadBullets();
            return;
        }
    }

    public void powerUp(){
        if(powerLevel >= bulletSprites.size() - 1){
            // game.setScore(game.getScore() + 1);
            //  MainActivity.scoreTv.setText(Integer.toString(game.getScore()));
            return;
        }
        powerLevel++;
        MainActivity.fireBtn.setImageResource(bulletSprites.get(powerLevel));
        MainActivity.fireBtn.setBackgroundResource(R.drawable.round_button_shape);
    }


    //총알 다시 셋팅
    public void reloadBullets(){
        isReloading = true;
        MainActivity.effectSound(MainActivity.PLAYER_RELOAD);
        MainActivity.fireBtn.setEnabled(false);
        MainActivity.reloadBtn.setEnabled(false);
        new Handler().postDelayed(new Runnable(){ //메인스레드와 서브스레드 간의 통신할때 Handle
            //가볍게 몇초뒤에 실행시키고 싶을때 postDelayed 사용한다
            @Override
            public void run() {
                bullets=30;
                MainActivity.fireBtn.setEnabled(true);
                MainActivity.reloadBtn.setEnabled(true);
                MainActivity.bulletCount.setText(bullets + "/30");
                MainActivity.bulletCount.invalidate(); //화면새로고침
                isReloading=false;
            }
        }, 2000);
    }



    //특수 총알
    public void specialShot(){
        specialShotCount--;
        //SpecialshotSprite 구현
        SpecialshotSprite shot = new SpecialshotSprite(context,
                game, R.drawable.laser, getRect().right -
                getRect().left, 0);

        //game -> SpaceInvadersView의 getSprites() : sprite에 shot추가하기
        game.getSprites().add(shot);
    }

    public int getSpecialShotCount(){ return specialShotCount; }
    public void setSpecialShooting(boolean specialShooting)
    { isSpecialShooting=specialShooting; }




    //생명 잃었을때
    public int getLife() { return life;}
    public void hurt(){
        life--;
        if(life<=0) {         //생명이 0이되면
            ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource(
                    (R.drawable.ic_baseline_favorite_border_24));
            game.endGame();  // 게임 종료   (endGame => SpaceInvadersView
            return;
        }
        //생명이 깎였는지 확인
        ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource(
                R.drawable.ic_baseline_favorite_border_24);

    }

    //생명 얻었을 때
    public void heal() {
        Log.d("heal", Integer.toString(life));
        if (life < MAX_HEART) {
            game.setScore(game.getScore() + 1);
            MainActivity.scoreTv.setText(Integer.toString(game.getScore()));
            ((ImageView) MainActivity.lifeFrame.getChildAt(life))
                    .setImageResource(R.drawable.ic_baseline_favorite_24);
            life++;
        }
    }

    //생명 얻었을 때
//    public void heal(){
//        Log.d("heal", Integer.toString(life));
//        if(life + 1  > MAX_HEART){
//            game.setScore(game.getScore() +1);
//            MainActivity.scoreTv.setText(Integer.toString(game.getScore()));
//            return;
//        }
//        ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource(
//                R.drawable.ic_baseline_favorite_24);
//        life++;
//    }


    //속도 올리기
    private void speedUp(){  //스피드
        if(MAX_SPEED >= speed + 0.2f) plusSpeed(0.2f);
        else{
            //   game.setScore(game.getScore() + 1);
            MainActivity.scoreTv.setText(Integer.toString((game.getScore())));
        }
    }


    @Override
    public void handleCollision(Sprite other) {    //외계인 충돌및 아이템 처리
        if(other instanceof AlienSprite){
            // 외계인과 충돌
            game.removeSprite(other);
            MainActivity.effectSound((MainActivity.PLAYER_HURT));
            hurt();
        }
        if(other instanceof AlienShotSprite){
            //외계인 쏜 총알 충돌
            MainActivity.effectSound(MainActivity.PLAYER_HURT);
            game.removeSprite(other);
            hurt();
        }
        if(other instanceof SpeedItemSprite){
            //스피드 아이템이면
            game.removeSprite(other);
            MainActivity.effectSound((MainActivity.PLAYER_GET_ITEM));
            speedUp();
        }
        if(other instanceof PowerItemSprite){
            //파워업
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM);
            powerUp();
            game.removeSprite(other);
        }
        if(other instanceof HealitemSprite){
            //힐
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM);
            game.removeSprite(other);
            heal();
        }
    }
    public int getPowerLevel(){ return powerLevel; }
}
