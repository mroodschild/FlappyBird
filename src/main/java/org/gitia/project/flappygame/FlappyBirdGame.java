/*
 * Copyright 2018 Matías Roodschild <mroodschild@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gitia.project.flappygame;

import java.util.ArrayList;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.gitia.froog.statistics.Clock;

/**
 *
 * @author Matías Roodschild <mroodschild@gmail.com>
 */
public class FlappyBirdGame extends AnimationTimer {

    Image iBird = new Image(getClass().getResource("/images/flappy.png").toExternalForm());
    Image iPipeBaseBot = new Image(getClass().getResource("/images/pipebase.png").toExternalForm());
    Image iPipeBot = new Image(getClass().getResource("/images/pipebot.png").toExternalForm());
    Image iPipeTop = new Image(getClass().getResource("/images/pipetop.png").toExternalForm());

    boolean print = true;
    boolean hit = false;
    boolean isStart = false;
    boolean firstIt = true;
    boolean finalizado = false;

    double distanciaTubos = 350;

    Stage theStage = new Stage();
    Sprite background = new Sprite();
    Random r = new Random();
    GraphicsContext gc;
    Sprite bird;

    ArrayList<Sprite> pipeList = new ArrayList<>();

    LongValue lastNanoTime = new LongValue(System.nanoTime());

    IntValue score = new IntValue(0);
    IntValue scoreTotal = new IntValue(0);
    double recorrido = 0;
    Time time = new Time();
    //Time presstime = new Time();
    ArrayList<String> input = new ArrayList<>();
    protected double velocidad = 800000000.0;

    Clock clock = new Clock();
    Clock clockTotal = new Clock();

    double fps = 25;
    double fpsTime = 1 / fps;

    public FlappyBirdGame() {
        //this.velocidad = velocidad;
        initEnvironment();
    }

    protected void initEnvironment() {
        theStage.setTitle("Flappy Bird!");
        Group root = new Group();
        Scene theScene = new Scene(root);
        theStage.setScene(theScene);
        background.setImage("/images/backgraund720x540-black.png");
        background.setPosition(0, 0);
        bird = new SpriteBird(background.getWidth(), background.getHeight(), iBird);

        Canvas canvas = new Canvas(background.getWidth(), background.getHeight());
        root.getChildren().add(canvas);

        theScene.setOnKeyPressed((KeyEvent e) -> {
            String code = e.getCode().toString();
            if (!input.contains(code)) {
                input.add(code);
            }
        });

        theScene.setOnKeyReleased((KeyEvent e) -> {
            String code = e.getCode().toString();
            input.remove(code);
        });

        gc = canvas.getGraphicsContext2D();
        Font theFont = Font.font("Helvetica", FontWeight.BOLD, 24);
        gc.setFont(theFont);
        gc.setFill(Color.BLACK);
        //gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
    }

    @Override
    public void handle(long currentNanoTime) {
            double elapsedTime = (currentNanoTime - lastNanoTime.value) / velocidad;
        if (elapsedTime > fpsTime) {
            if (isStart) {
                lastNanoTime.value = currentNanoTime;
                action();
                updateObjects(elapsedTime);
                detectarColision(elapsedTime);
                crearTuberias();
                renderizarObjetos();
                evaluarJuego();
                //System.out.println("ElapsedTime: " + elapsedTime);
            } else {
                refresh(currentNanoTime);
            }
        }
//        double elapsedTime = (currentNanoTime - lastNanoTime.value) / velocidad;
//        if (elapsedTime > fpsTime) {
//            if (isStart) {
//                lastNanoTime.value = currentNanoTime;
//                action();
//                updateObjects(elapsedTime);
//                detectarColision(elapsedTime);
//                crearTuberias();
//                renderizarObjetos();
//                evaluarJuego();
////                System.out.println("ElapsedTime: " + elapsedTime);
//            } else {
//                refresh(currentNanoTime);
//            }
//        }
    }

    /**
     * removemos las cañerias que salieron del mapa, actualizamos el espacio
     * recorrido por la cañeria en base a X, actualizamos la posición de las
     * cañerías
     *
     * @param elapsedTime
     */
    private void updateObjects(double elapsedTime) {
        bird.update(elapsedTime);
        pipeList.removeIf(p -> p.getPositionX() < p.getMinPosX() + 1);
        int pipeSize = pipeList.size();
        if (pipeSize > 0) {
            recorrido = pipeList.get(0).getPositionX();
        }
        for (int i = 0; i < pipeSize; i++) {
            Sprite pipe = pipeList.get(i);
            pipe.setVelocity(-200, 0);
            pipe.update(elapsedTime);
        }
        if (pipeSize > 0) {
            recorrido -= pipeList.get(0).getPositionX();
        }
    }

    public void refresh(long currentNanoTime) {
        if (input.contains("SPACE")) {
            reiniciar(currentNanoTime);
            renderizarObjetos();
        }
        if (firstIt) {
            renderizarObjetos();
            firstIt = false;
        }
    }

    @Override
    public void start() {
        super.start();
        theStage.show();
    }

    @Override
    public void stop() {
        super.stop();
        //theStage.close();
        finalizado = true;
    }
    
    /**
     * se determina el movimiento del pajarito
     */
    protected void action() {
        if (input.contains("SPACE")) {
            bird.addVelocity(0, -900);
        }
    }

    protected void evaluarJuego() {
        String pointsText = "Score: " + (score.getValue());

        gc.fillText(pointsText, 30, 36);

        boolean contar = true;
        //score.add(0.002);//siempre damos puntos
        score.add(recorrido * 0.001);//siempre damos puntos
        for (int i = 0; i < pipeList.size(); i++) {
            SpritePipe pipe = (SpritePipe) pipeList.get(i);
            if (bird.getPositionX() + bird.getWidth() / 2 > pipe.positionX+pipe.getWidth()) {
                if (contar && !pipe.getCount()) {
                    score.add(1);//si paso un tubo damos premio
                    contar = false;
                }
                pipe.setCount(true);
            }
        }

        if (hit) {
            //Aqui el Genetico Inicializa nuevamente el 
            //juego y pone la siguiente red neuronal

            System.out.println("SCORE:\t" + score.getValue());
            isStart = false;
        }
    }

    /**
     * reiniciamos todas las variables del juego
     *
     * @param currentNanoTime
     */
    protected void reiniciar(long currentNanoTime) {
        time.time = distanciaTubos;
        lastNanoTime.value = currentNanoTime;
        score.value = 0;
        pipeList.clear();
        bird = new SpriteBird(background.getWidth(), background.getHeight(), iBird);
        isStart = true;
        hit = false;
        //r = new Random(1);
        this.start();
    }

    protected void crearTuberias() {

        int last = pipeList.size() - 1;
        double X;
        if (last > -1) {
            X = pipeList.get(last).getPositionX() + distanciaTubos;
        } else {
            X = background.getWidth();
        }

        //if (time.time >= distancia) {
        if (X <= background.getWidth()) {
            //time.setTime(time.getTime() - distancia);

            //int pos = r.nextInt(6) + 1;
            int pos = 3;
            int hueco = 4;

            for (int i = 0; i < 11; i++) {
                if (i + 1 == pos) {
                    Sprite pipetop = new SpritePipeTop(background.getWidth() + 70, background.getHeight(), iPipeTop, X, 45 * i);
                    pipeList.add(pipetop);
                } else if (i - hueco == pos) {
                    Sprite pipebot = new SpritePipeBot(background.getWidth() + 70, background.getHeight(), iPipeBot, X, 45 * i);
                    pipeList.add(pipebot);
                } else if (i >= pos + hueco || i < pos) {
                    Sprite pipeBase = new SpritePipeBaseBot(background.getWidth() + 70, background.getHeight(), iPipeBaseBot, X, 45 * i);
                    pipeList.add(pipeBase);
                }
            }
            
////     public SpritePipeTop(double backgroundWidth, double backgroundHeight, Image image, double posX, double posY) {
////        super.setImage(image);
////        super.setPosition(posX, posY);
////        super.setBoundLimits(0 - super.getWidth() + 15, backgroundWidth, 0, backgroundHeight);
////     }
//            
//            SpritePipe pipeTop = new SpritePipe();
//            pipeTop.setWidth(88);
//            pipeTop.setHeight(45 * pos);
//            pipeTop.setPosition(X, 0);
//            
//            pipeList.add(pipeTop);

        }
    }

    protected void detectarColision(double elapsedTime) {
        if (bird.getPositionY() + bird.height == background.height - 45) {
            hit = true;
        } else {
            int pipeSize = pipeList.size();
            for (int i = 0; i < pipeSize; i++) {
                Sprite pipe = pipeList.get(i);
                if (bird.intersects(pipe)) {
                    hit = true;
                }
            }
        }
    }

    protected void renderizarObjetos() {
        background.render(gc);
        bird.render(gc);
        int size = pipeList.size();
        for (int i = 0; i < size; i++) {
            pipeList.get(i).render(gc);
        }
    }
}
