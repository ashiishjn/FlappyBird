package com.example.flappybird;

import android.util.Log;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class GameScreen extends Game {

    SpriteBatch batch;
    Texture background1;
    Texture[] background2;
    float[] backX = new float[2];
    float bottomBackgroundHeight;
    float topBackgroundHeight;
	ShapeRenderer shapeRenderer;

    int elixir_points_earned;

    Sound tap_sound;
    Music music;

    int flapState = 0;
    Texture[] birds;
    Texture[] birdLife;
    Stage[] stagesBird;
    Image image1, image2;
    float birdY;
    float velocity = 0;
    Circle birdCircle;
    int flag=0;
    int gameState = 0;
    int score = 0;
    int scoringTube = 0;
    BitmapFont font;
    BitmapFont finalScore;
    BitmapFont highScore;
    GlyphLayout layout;

    Texture gameOver;
    Texture final_score;
    Texture high_score;

    int numOfTubes = 3;
    Texture topTube;
    Texture bottomTube;
    Texture elixirBottle;
    float gap[] = {550,570,540};
    float[] tubeOffset = new float[numOfTubes];
    float tubeVelocity;
    float maxtubeVelocity;
    float[] tubeX = new float[numOfTubes];
    float distanceBetweenTubes;
    Rectangle[] topTubeRectangle;
    Rectangle[] bottomTubeRectangle;
    Circle[] elixirBottleCircle;
    Random random;

    ImageButton restart;
    ImageButton home;
    ImageButton revive;
    int temp = 0;

    float dropVelocity;
    float velocityDecrement;

    FitViewport gameViewport;
    Stage stage;

    AndroidLauncher androidLauncher;

    int birdLifePos[][] = new int[4][2];

    boolean elixirBottlesActive[] = new boolean[numOfTubes];
    float elixirBottlePositionY[] = new float[numOfTubes];

    public GameScreen(AndroidLauncher androidLauncher) {
        this.androidLauncher = androidLauncher;
    }

    @Override
    public void create()
    {
        HEIGHT = Gdx.graphics.getHeight();

        batch = new SpriteBatch();

        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        elixirBottle = new Texture("elixir_bottle.png");

        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");

        birdLife = new Texture[4];
        birdLife[0] = new Texture("bird.png");
        birdLife[1] = new Texture("bird.png");
        birdLife[2] = new Texture("bird.png");
        birdLife[3] = new Texture("bird.png");

        gameOver = new Texture("gameover.png");
        final_score = new Texture("score.png");
        high_score = new Texture("high_score.png");

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        finalScore = new BitmapFont();
        finalScore.setColor(Color.WHITE);

        highScore = new BitmapFont();
        highScore.setColor(Color.WHITE);
        if(HEIGHT >= 2400) {
            font.getData().setScale(13);
            finalScore.getData().setScale(11);
            highScore.getData().setScale(11);
        }
        else if(HEIGHT >= 1750) {
            font.getData().setScale(10);
            finalScore.getData().setScale(8);
            highScore.getData().setScale(8);
        }
        else {
            font.getData().setScale(7);
            finalScore.getData().setScale(5);
            highScore.getData().setScale(5);
        }

        setDimensions();

        background1 = new Texture(GameInfo.background_top[GameInfo.background_selected]);
        background2 = new Texture[2];
        background2[0] = new Texture(GameInfo.background_base[GameInfo.background_selected]);
        background2[1] = new Texture(GameInfo.background_base[GameInfo.background_selected]);

        gap[0] = HEIGHT * 0.231f;
        gap[1] = HEIGHT * 0.250f;
        gap[2] = HEIGHT * 0.236f;

        stagesBird = new Stage[2];
        stagesBird[0] = new Stage(new ScreenViewport());
        stagesBird[1] = new Stage(new ScreenViewport());

        image2 = new Image(birds[0]);
        image2.setWidth(birdImageWidth);
        image2.setHeight(birdImageHeight);
        image2.setPosition(Gdx.graphics.getWidth()/2f-birdImageWidth/2f,Gdx.graphics.getHeight()/2f-birdImageHeight/2f+bottomBackgroundHeight/2f);
        image2.setOrigin(birdImageWidth/2f,birdImageHeight/2f);
        stagesBird[0].addActor(image2);

        image1 = new Image(birds[1]);
        image1.setWidth(birdImageWidth);
        image1.setHeight(birdImageHeight);
        image1.setPosition(Gdx.graphics.getWidth()/2f-birdImageWidth/2f,Gdx.graphics.getHeight()/2f-birdImageHeight/2f+bottomBackgroundHeight/2f);
        image1.setOrigin(birdImageWidth/2f,birdImageHeight/2f);
        stagesBird[1].addActor(image1);

        gameViewport = new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),
                new OrthographicCamera());

        stage = new Stage(gameViewport, batch);

        random = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 0.85f;

        topTubeRectangle = new Rectangle[numOfTubes];
        bottomTubeRectangle = new Rectangle[numOfTubes];
        elixirBottleCircle = new Circle[numOfTubes];

		shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();



        tap_sound = Gdx.audio.newSound(Gdx.files.internal(GameInfo.sound_GamePlay[GameInfo.sound_selected]));
        if(GameInfo.volume) {
            music = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3"));
            music.setLooping(true);
            music.play();
        }
//        music.stop();

        GameInfo.totalLife = 2;
        startgame();
        setVelocity();

    }

    public static float background_Width;
    public static float background_Height;

    public static int WIDTH;
    public static int HEIGHT;

    public static float restart_button_X;
    public static float restart_button_y;
    public static float home_button_X;
    public static float home_button_Y;

    public static float restart_button_width;
    public static float restart_button_height;
    public static float home_button_width;
    public static float home_button_height;

    public static float gameOverX;
    public static float gameOverY;
    public static float gameOverwidth;
    public static float gameOverHeight;

    public static float ScoreImageX;
    public static float ScoreImageY;
    public static float ScoreImagewidth;
    public static float ScoreImageHeight;

    public static float highScoreImageX;
    public static float highScoreImageY;
    public static float highScoreImagewidth;
    public static float highScoreImageHeight;

    public static float ScoreNumberY;
    public static float highScoreNumberY;

    public static float birdImageWidth;
    public static float birdImageHeight;

    public static float topPipesWidth;
    public static float topPipesHeight;

    public static float bottomPipesWidth;
    public static float bottomPipesHeight;

    float actual_X;
    float actual_Y;

    public void setDimensions() {

        WIDTH = Gdx.graphics.getWidth();
        HEIGHT = Gdx.graphics.getHeight();

        actual_X = Gdx.graphics.getWidth();
        actual_Y = Gdx.graphics.getHeight();

        float image_X = 1440;
        float image_Y = 3088;

        float ratio_X = actual_X / image_X;
        float ratio_Y = actual_Y / image_Y;

        if(ratio_X > ratio_Y){
            background_Height = image_Y * ratio_X;
            background_Width = image_X * ratio_X;
        }
        else {
            background_Height = image_Y * ratio_Y;
            background_Width = image_X * ratio_Y;
        }

        bottomBackgroundHeight = HEIGHT * 0.2f;
        topBackgroundHeight = HEIGHT * 0.8f;

        gameOverwidth = WIDTH * 0.8f;
        gameOverHeight = (WIDTH * 0.8f * gameOver.getHeight()) / gameOver.getWidth();
        gameOverX = WIDTH * 0.1f;
        gameOverY = 2 * topBackgroundHeight / 3f + bottomBackgroundHeight;

        ScoreImagewidth = WIDTH * 0.4f;
        ScoreImageHeight = (WIDTH * 0.4f * final_score.getHeight()) / final_score.getWidth();
        ScoreImageX = WIDTH * 0.3f;
        ScoreImageY = gameOverY - ScoreImageHeight - 50;

        if(HEIGHT >= 2400)
            layout = new GlyphLayout(finalScore,String.valueOf(11));
        else if(HEIGHT >= 1750)
            layout = new GlyphLayout(finalScore,String.valueOf(8));
        else
            layout = new GlyphLayout(finalScore,String.valueOf(5));

        ScoreNumberY = ScoreImageY - 20;

        highScoreImagewidth = WIDTH * 0.6f;
        highScoreImageHeight = (WIDTH * 0.6f * high_score.getHeight()) / high_score.getWidth();
        highScoreImageX = WIDTH * 0.2f;
        highScoreImageY = ScoreNumberY - highScoreImageHeight - layout.height - 60;

        highScoreNumberY = highScoreImageY - 20;

        restart_button_width = WIDTH * 0.35f;
        home_button_width = WIDTH * 0.35f;
        restart_button_height = restart_button_width / 3f;
        home_button_height = home_button_width / 3f;

        restart_button_X = WIDTH * 0.1f;
        home_button_X = WIDTH * 0.55f;
        restart_button_y = highScoreNumberY - layout.height - 60 - restart_button_height;
        home_button_Y = highScoreNumberY - layout.height - 60 - restart_button_height;

        birdImageHeight = actual_Y * 0.0842f;
        birdImageWidth = actual_Y * 0.0947f;

        topPipesWidth = actual_Y * 0.0908f;
        topPipesHeight = actual_Y * 0.9548f;
        bottomPipesWidth = actual_Y * 0.0908f;
        bottomPipesHeight = actual_Y * 0.9548f;

        for(int i = 0; i< 4; i++){
            birdLifePos[i][0] = HEIGHT - 110;
            birdLifePos[i][1] = 30 + 90 * i;
        }
    }

    public void rotate(int x){
        image2.rotateBy(x);
        image1.rotateBy(x);
    }

    public void setPosition(){
        image2.setPosition(Gdx.graphics.getWidth()/2f-birdImageWidth/2f,birdY);
        image1.setPosition(Gdx.graphics.getWidth()/2f-birdImageWidth/2f,birdY);
    }

    public void startgame()
    {
        temp_flag = false;
        birdY = Gdx.graphics.getHeight()/2f-birdImageHeight/2f + bottomBackgroundHeight/2f;
        for(int i=0;i<numOfTubes;i++)
        {
            elixirBottlePositionY[i] = bottomBackgroundHeight + (random.nextFloat() * (topBackgroundHeight - Gdx.graphics.getWidth() * 0.3f)) + Gdx.graphics.getWidth() * 0.05f;
            elixirBottlesActive[i] = true;
            tubeOffset[i] = (random.nextFloat()-0.5f)*(topBackgroundHeight-gap[i]-topBackgroundHeight * 0.165f);
            tubeX[i] = Gdx.graphics.getWidth()/2f-topPipesWidth/2f +Gdx.graphics.getWidth()+ i*distanceBetweenTubes;

            topTubeRectangle[i] = new Rectangle();
            bottomTubeRectangle[i] = new Rectangle();
            elixirBottleCircle[i] = new Circle();
        }

        backX[0]=0;
        backX[1]=Gdx.graphics.getWidth();
    }

    public void setVelocity(){
//        if(HEIGHT > 2300){
//            tubeVelocity = 10;
//            maxtubeVelocity = 17;
//        }
//        else if(HEIGHT >= 1440) {
//            tubeVelocity = 8;
//            maxtubeVelocity = 15;
//        }
//        else {
//            tubeVelocity = 7;
//            maxtubeVelocity = 14;
//        }

        tubeVelocity = (int)(WIDTH * 0.007407f);
        maxtubeVelocity = tubeVelocity + 7;
        velocityDecrement = HEIGHT * 0.0008772f;
        dropVelocity = (int) (HEIGHT * 0.01315f);

//        Log.i("Hello", Float.toString(tubeVelocity));
//        Log.i("Hello", Float.toString(maxtubeVelocity));
//        Log.i("Hello", Float.toString(velocityDecrement));
//        Log.i("Hello", Float.toString(dropVelocity));

//        if(dropVelocity > 35)
//            dropVelocity = 35;
//        else if(dropVelocity < 25)
//            dropVelocity = 25;
    }

    public void buttons()
    {
        restart = new ImageButton(new SpriteDrawable(new Sprite(
                new Texture("restart.png"))));
        restart.setPosition(restart_button_X, restart_button_y);

        restart.setWidth(restart_button_width);
        restart.setHeight(restart_button_height);

        restart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                tap_sound.play();
                stage.clear();
                stage.dispose();
                gameState = 0;
                score = 0;
                scoringTube = 0;
                velocity = 0;
//                tubeVelocity = 8;
                temp = 0;
                GameInfo.totalLife = 2;
                setVelocity();
                startgame();
            }
        });

        stage.addActor(restart);

        //********************************************************

        home = new ImageButton(new SpriteDrawable(new Sprite(
                new Texture("home.png"))));
        home.setPosition(home_button_X, home_button_Y);

        home.setWidth(home_button_width);
        home.setHeight(home_button_height);

        home.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameInfo.totalLife = 2;
                if(GameInfo.volume) {
                    music.stop();
                    music.dispose();
                }
//                tap_sound.play();
                stage.clear();
                stage.dispose();
                Gdx.app.exit();

            }
        });

        stage.addActor(home);
    }

    int count = 0;

    int x=0;
    @Override
    public void render() {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(
                background1,0,HEIGHT - background_Height,background_Width,background_Height);
//        batch.draw(
//                    background2[0],
//                    0,
//                    0,
//                    Gdx.graphics.getWidth(),
//                    bottomBackgroundHeight);

//        Gdx.app.log("Hello45", String.valueOf(topBackgroundHeight / 2 - gap[0] / 2 - bottomPipesHeight + tubeOffset[0]+bottomBackgroundHeight));


        //********************************************************************
        //************************ GAME PLAY *********************************

        if(gameState == 1)
        {
            if(tubeX[scoringTube]<Gdx.graphics.getWidth()/2f-birdImageWidth/2f-topPipesWidth)
            {
                scoringTube++;
                if(scoringTube == numOfTubes)
                    scoringTube = 0;
                score++;
                if(score % 5 == 0 && tubeVelocity != maxtubeVelocity)
                    tubeVelocity++;
            }
            if(Gdx.input.justTouched())
            {
                if(GameInfo.volume)
                    tap_sound.play();
                velocity = -dropVelocity;
                count+=20;
            }
            for(int i=0;i<numOfTubes;i++) {
                if(tubeX[i]<-(topPipesWidth))
                {
                    tubeOffset[i] = (random.nextFloat()-0.5f)*(topBackgroundHeight-gap[i]-topBackgroundHeight * 0.165f);
                    tubeX[i] += distanceBetweenTubes * numOfTubes;

                    elixirBottlesActive[i] = true;
                    elixirBottlePositionY[i] = bottomBackgroundHeight + (random.nextFloat() * (topBackgroundHeight - Gdx.graphics.getWidth() * 0.3f)) + Gdx.graphics.getWidth() * 0.05f;
                }
                else {
                    tubeX[i] -= tubeVelocity;
                }

                //******************
                batch.draw(
                        topTube,
                        tubeX[i],
                        topBackgroundHeight / 2 + gap[i] / 2  + tubeOffset[i]+bottomBackgroundHeight,
                        topPipesWidth,
                        topPipesHeight);

                topTubeRectangle[i] = new Rectangle(
                        tubeX[i],
                        topBackgroundHeight / 2 + gap[i] / 2 + tubeOffset[i]+bottomBackgroundHeight,
                        topPipesWidth,
                        topPipesHeight);

                batch.draw(
                        bottomTube,
                        tubeX[i],
                        topBackgroundHeight / 2 - gap[i] / 2 - bottomPipesHeight + tubeOffset[i]+bottomBackgroundHeight,
                        bottomPipesWidth,
                        bottomPipesHeight);

                bottomTubeRectangle[i] = new Rectangle(
                        tubeX[i],
                        topBackgroundHeight / 2 - gap[i] / 2 - bottomPipesHeight + tubeOffset[i]+bottomBackgroundHeight,
                        bottomPipesWidth,
                        bottomPipesHeight);

                //******************************************
                if(elixirBottlesActive[i]) {

                    batch.draw(elixirBottle,
                            tubeX[i] + distanceBetweenTubes / 2f - Gdx.graphics.getWidth() * 0.08f + topPipesWidth / 2f,
                            elixirBottlePositionY[i], Gdx.graphics.getWidth() * 0.16f, Gdx.graphics.getWidth() * 0.16f);

                    elixirBottleCircle[i] = new Circle(
                            tubeX[i] + distanceBetweenTubes / 2f + topPipesWidth / 2f,
                            elixirBottlePositionY[i] + Gdx.graphics.getWidth() * 0.08f,
                            Gdx.graphics.getWidth() * 0.08f
                    );
                }

//                birdCircle.set(
//                        Gdx.graphics.getWidth()/2f,
//                        birdY+birdImageHeight/2f,
//                        birdImageWidth/2f);
            }
            if(birdY>bottomBackgroundHeight) {
                velocity += velocityDecrement;
                birdY -= velocity;
                if(birdY < bottomBackgroundHeight)
                    birdY=bottomBackgroundHeight;
                else if(birdY > Gdx.graphics.getHeight())
                    birdY = Gdx.graphics.getHeight();
            }
            else {
                if(GameInfo.totalLife == 0){
                    elixir_points_earned = score;
                    rotate((count-x) * 18);
                    x = count;
                    soundActiveUpdateOnDatabase(elixir_points_earned);
                    gameState=2;
                }

                else {
                    velocity = 0;
                    gameState = 0;
                    rotate((count-x) * 18);
                    x = count;
                    startgame();
                    GameInfo.totalLife--;
                    scoringTube = 0;
                }
            }
        }
        else if(gameState == 0)
        {
            if(Gdx.input.justTouched())
            {
//                tap_sound.play();
                gameState = 1;
            }
        }
        else
        {
            if(birdY>bottomBackgroundHeight) {
                velocity += velocityDecrement;
                birdY -= velocity;
                if(birdY < bottomBackgroundHeight)
                    birdY=bottomBackgroundHeight;
            }

            batch.draw(
                    gameOver,
                    gameOverX,
                    gameOverY,
                    gameOverwidth,
                    gameOverHeight);

            batch.draw(
                    final_score,
                    ScoreImageX,
                    ScoreImageY,
                    ScoreImagewidth,
                    ScoreImageHeight);

            batch.draw(
                    high_score,
                    highScoreImageX,
                    highScoreImageY,
                    highScoreImagewidth,
                    highScoreImageHeight);

            layout = new GlyphLayout(finalScore,String.valueOf(score));

            finalScore.draw(
                    batch,
                    String.valueOf(score),
                    Gdx.graphics.getWidth()/2f-layout.width/2f,
                    ScoreNumberY);

            layout = new GlyphLayout(highScore,String.valueOf(GameInfo.highestScores));

            highScore.draw(
                    batch,
                    String.valueOf(GameInfo.highestScores),
                    Gdx.graphics.getWidth()/2f-layout.width/2f,
                    highScoreNumberY);

        }
        flag++;
        if(flag == 4) {
            flag=0;
            if (flapState == 0)
                flapState = 1;
            else
                flapState = 0;
        }
        if(gameState == 1) {
            backX[0]-=tubeVelocity;
            backX[1]-=tubeVelocity;

            if(backX[0] <= -Gdx.graphics.getWidth())
                backX[0]+=2*Gdx.graphics.getWidth();
            if(backX[1] <= -Gdx.graphics.getWidth())
                backX[1]+=2*Gdx.graphics.getWidth();

            batch.draw(
                    background2[0],
                    backX[0],
                    0,
                    Gdx.graphics.getWidth(),
                    bottomBackgroundHeight );

            batch.draw(
                    background2[1],
                    backX[1],
                    0,
                    Gdx.graphics.getWidth(),
                    bottomBackgroundHeight);
        }
        else
            batch.draw(
                    background2[0],
                    0,
                    0,
                    Gdx.graphics.getWidth(),
                    bottomBackgroundHeight);

        if(gameState!=2) {

//            batch.draw(
//                    birds[flapState],
//                    Gdx.graphics.getWidth() / 2f - birdImageWidth / 2f,
//                    birdY);

            layout = new GlyphLayout(font,String.valueOf(score));

//            finalScore.draw(
//                    batch,
//                    String.valueOf(score),
//                    Gdx.graphics.getWidth()/2f-layout.width/2f,
//                    ScoreNumberY);

            font.draw(batch, String.valueOf(score),WIDTH * 0.1f,HEIGHT * 0.02f + layout.height);
}
//        else {

//            batch.draw(
//                    birds[1],
//                    Gdx.graphics.getWidth() / 2f - birdImageWidth / 2f,
//                    birdY);
//        }

        birdCircle.set(
                Gdx.graphics.getWidth()/2f,
                birdY+birdImageHeight/2f,
                birdImageHeight/2f);

//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);
//		shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);
        for(int i=0;i<numOfTubes;i++)
        {
//			shapeRenderer.rect(
//			        tubeX[i],
//                    topBackgroundHeight / 2 + gap[i] / 2 + tubeOffset[i]+bottomBackgroundHeight,
//					topPipesWidth,
//                    topPipesHeight);
//
//			shapeRenderer.rect(
//			        tubeX[i],
//                    topBackgroundHeight / 2 - gap[i] / 2 - bottomPipesHeight + tubeOffset[i]+bottomBackgroundHeight,
//					bottomPipesWidth,
//                    bottomPipesHeight);
//
//            shapeRenderer.circle(
//                    tubeX[i] + distanceBetweenTubes / 2f + topPipesWidth / 2f,
//                    1000 + Gdx.graphics.getWidth() * 0.1f,
//                    Gdx.graphics.getWidth() * 0.1f);



            if(Intersector.overlaps(birdCircle,topTubeRectangle[i]) || Intersector.overlaps(birdCircle,bottomTubeRectangle[i])){
                if(GameInfo.totalLife == 0) {
                    elixir_points_earned = score;
                    rotate((count-x) * 18);
                    x = count;
                    soundActiveUpdateOnDatabase(elixir_points_earned);
                    gameState = 2;
                }
                else {
                    velocity = 0;
                    gameState = 0;
                    rotate((count-x) * 18);
                    x = count;
                    startgame();
                    scoringTube = 0;
                    GameInfo.totalLife--;
                }
            }

            if(Intersector.overlaps(birdCircle, elixirBottleCircle[i])){
                if(elixirBottlesActive[i]) {
                    score++;
                    elixirBottlesActive[i] = false;
                }
            }
        }

        for(int i = 0; i< GameInfo.totalLife; i++){
            batch.draw(
                    birdLife[i],
                    birdLifePos[i][1], birdLifePos[i][0],80,80);
        }

        batch.end();
//		shapeRenderer.end();

        if(x<count)
        {
            rotate(18);
            x++;
        }
        setPosition();
        if(gameState!=2) {
            if (flapState == 0) {
                stagesBird[0].act();
                stagesBird[0].draw();
            } else {
                stagesBird[1].act();
                stagesBird[1].draw();
            }
        }
        else {
            stagesBird[0].act();
            stagesBird[0].draw();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK )|| Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)){
            GameInfo.totalLife = 2;
            stage.clear();
            if(GameInfo.volume) {
                music.stop();
                music.dispose();
            }

//            game.dispose();
        }


        if(gameState == 2)
        {
            if(temp == 0) {
                buttons();
                Gdx.input.setInputProcessor(stage);
                temp = 1;
            }
//            game.batch.setProjectionMatrix(stage.getCamera().combined);
            stage.draw();
        }


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    boolean temp_flag = false;
    public void soundActiveUpdateOnDatabase(int points_earned){
        if(temp_flag)
            return;
        temp_flag = true;

        GameInfo.elixir += points_earned;
        if(GameInfo.highestScores < points_earned)
            GameInfo.highestScores = points_earned;
        if(!GameInfo.guestUser) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.child("Elixir").setValue(String.valueOf(GameInfo.elixir));
            reference.child("Highest Score").setValue(String.valueOf(GameInfo.highestScores));
        }
    }
}
