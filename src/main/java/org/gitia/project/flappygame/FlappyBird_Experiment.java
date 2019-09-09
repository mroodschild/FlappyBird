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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.ejml.simple.SimpleMatrix;
import org.gitia.froog.Feedforward;
import org.gitia.froog.layer.Dense;
import org.gitia.froog.transferfunction.TransferFunction;
import org.gitia.froog.util.Save;
import org.gitia.jdataanalysis.ImageReader;
import org.gitia.project.genetic.AG_Flappy;
//import org.gitia.project.genetic.AG;

/**
 *
 * @author Matías Roodschild <mroodschild@gmail.com>
 */
public class FlappyBird_Experiment extends AnimationTimer {

    AG_Flappy ag;
    //int iteracion = 0;
    double mejor = -1;
    int intentos = 0;
    int nro_intentos = 3;

    Image iBird = new Image(getClass().getResource("/images/flappy.png").toExternalForm());
    Image iPipeBaseBot = new Image(getClass().getResource("/images/pipebase.png").toExternalForm());
    Image iPipeBot = new Image(getClass().getResource("/images/pipebot.png").toExternalForm());
    Image iPipeTop = new Image(getClass().getResource("/images/pipetop.png").toExternalForm());

    boolean print = true;
    boolean hit = false;
    boolean isStart = false;
    boolean firstIt = true;
    boolean finalizado = false;

    double distancia = 1.7;

    Stage theStage = new Stage();
    Sprite background = new Sprite();
    Random r = new Random();
    GraphicsContext gc;
    Sprite bird;

    ArrayList<Sprite> pipeList = new ArrayList<>();

    LongValue lastNanoTime = new LongValue(System.nanoTime());

    IntValue score = new IntValue(0);
    IntValue scoreTotal = new IntValue(0);
    Time time = new Time();
    Time presstime = new Time();
    Feedforward net;
    Dense inputLayer = new Dense(7560, 1000, TransferFunction.TANSIG, new Random(1));
    
    //ArrayList<String> inputLayer = new ArrayList<>();

    public FlappyBird_Experiment(Feedforward net, AG_Flappy ag) {
        this.ag = ag;
        theStage.setTitle("Flappy Bird!");
        Group root = new Group();
        Scene theScene = new Scene(root);
        theStage.setScene(theScene);
        background.setImage("/images/backgraund720x540.png");
        background.setPosition(0, 0);
        bird = new SpriteBird(background.getWidth(), background.getHeight(), iBird);
        this.net = net;

        Canvas canvas = new Canvas(background.getWidth(), background.getHeight());
        root.getChildren().add(canvas);

//        theScene.setOnKeyPressed((KeyEvent e) -> {
//            String code = e.getCode().toString();
//            if (!inputLayer.contains(code)) {
//                inputLayer.add(code);
//            }
//        });
//
//        theScene.setOnKeyReleased((KeyEvent e) -> {
//            String code = e.getCode().toString();
//            inputLayer.remove(code);
//        });
        gc = canvas.getGraphicsContext2D();
        Font theFont = Font.font("Helvetica", FontWeight.BOLD, 24);
        gc.setFont(theFont);
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
    }

    @Override
    public void handle(long currentNanoTime) {
        if (isStart) {
            //double elapsedTime = (currentNanoTime - lastNanoTime.value) / 1000000000.0;
            double elapsedTime = (currentNanoTime - lastNanoTime.value) / 500000000.0;
            //double elapsedTime = (currentNanoTime - lastNanoTime.value) / 250000000.0;
            //double elapsedTime = (currentNanoTime - lastNanoTime.value) / 125000000.0;
            lastNanoTime.value = currentNanoTime;
            //jugamos manualmente
            //presionoSpace();
            redNeuronalDecide();

            presstime.addTime(elapsedTime);
            bird.update(elapsedTime);

            //collision detection
            detectarColision(elapsedTime);

            pipeList.removeIf(p -> p.getPositionX() < p.getMinPosX() + 1);
            ///System.out.println("tuberias:\t"+pipeList.size());
            crearTuberias();

            //render
            renderizarObjetos();

            evaluarJuego();

            getEnviroment(gc);

            time.addTime(elapsedTime);
        } else {
            genetico(currentNanoTime);
            //manual(currentNanoTime);

        }
    }

    public void genetico(long currentNanoTime) {
        if (intentos++ >= nro_intentos - 1) {
            //System.out.println("0");
            System.out.println("epoca:\t" + ag.getGenActual() + "\tit:\t" + ag.getCurrentIt()+"\tscore:\t"+ag.getCurrentIndividuo().getFitness());
            if (!ag.nuevoIndividuo()) {
                if (ag.nuevaEpoca()) {
                    ag.printResume();
                    ag.run();
                } else {
                    ag.printResume();
                    System.exit(0);
                }
            }
            scoreTotal.setValue(0);
            intentos = 0;
        }
        //System.out.println("score total:\t "+scoreTotal.getValue());
        pipeList.clear();
        net.setParameters(ag.getCurrentDNA());
        //falta guardar el fitness
        reiniciar(currentNanoTime);
        renderizarObjetos();
    }

//    public void manual(long currentNanoTime) {
//        if (inputLayer.contains("SPACE")) {
//            reiniciar(currentNanoTime);
//            renderizarObjetos();
//        }
//        if (firstIt) {
//            renderizarObjetos();
//            firstIt = false;
//        }
//    }
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

    public SimpleMatrix getEnviroment(GraphicsContext gc) {
        WritableImage wImage = gc.getCanvas().snapshot(new SnapshotParameters(), null);
        ImageView image = new ImageView(wImage);
        image.setPreserveRatio(true);
        //image.setSmooth(true);
        image.setCache(true);
        image.setFitWidth(144);
        BufferedImage image1 = SwingFXUtils.fromFXImage(image.snapshot(new SnapshotParameters(), null), null);
        //ImageReader.save(image1, "144.jpg");
//        ImageReader.save(image1.getSubimage(60, 0, 70, 108), "cut144.jpg");
        //System.exit(0);
        BufferedImage reduced = ImageReader.crop(image1, 60, 0, 70, 108);
        //ImageReader.save(reduced, "cut144.jpg");
        //SimpleMatrix map = new SimpleMatrix(ImageReader.convertTo1DDouble(image1));
        SimpleMatrix map = new SimpleMatrix(ImageReader.convertTo1DDouble(reduced));
        return map;
    }

//    private void presionoSpace() {
//        if (inputLayer.contains("SPACE")) {
//            if (presstime.time >= 0.05) {
//                bird.addVelocity(0, -1200);
//                presstime.setTime(0);
//            }
//        }
//    }
    private void redNeuronalDecide() {
        ////                 decidimos con la RNA
        if (presstime.time >= 0.01) {
            //obtenemos el entorno actual
            //y normalizamos entre -1 y 1
            SimpleMatrix inputMap = getEnviroment(gc).divide(127).minus(1);
            //le preguntamos a la RNA si debemos volar
            double volar = net.output(inputLayer.output(inputMap)).getDDRM().get(0);
            //evaluamos la decisión
//            System.out.println("volar:\t" + volar);
            if (volar > 0.5) {
                bird.addVelocity(0, -1000);
            }
            //actualizamos el tiempo de vuelo
            presstime.setTime(0);
        }
    }

    private void evaluarJuego() {
        String pointsText = "Score: " + (score.getValue());

        gc.fillText(pointsText, 30, 36);
        gc.strokeText(pointsText, 30, 36);

        boolean contar = true;
        score.add(0.002);//siempre damos puntos
        for (int i = 0; i < pipeList.size(); i++) {
            SpritePipe pipe = (SpritePipe) pipeList.get(i);
            if (bird.getPositionX() + bird.getWidth() / 2 > pipe.positionX) {
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
            String mje = "Bird: " + ag.getCurrentIt() + " SCORE:\t" + score.getValue();
            //System.out.println("Bird: "+iteracion+" SCORE:\t" + score.getValue());
            isStart = false;
            scoreTotal.add(score.getValue());
            
            double puntaje = Math.rint(scoreTotal.getValue()/nro_intentos*1000)/1000;
            ag.getCurrentIndividuo().setFitness(puntaje);

            //System.out.println(getClass().getResource("/images/").getPath());
            if (puntaje > mejor) {
                //System.out.println("puntaje:\t"+puntaje+"\tmejor:\t"+mejor);
                mejor = puntaje;
                Save.saveNet(net, "net_gen_" + ag.getGenActual() + "_it_" + ag.getCurrentIt() + "_score_" + puntaje,
                        "D:/resultados/");
                //System.out.println("Saved");

            }

            // iteracion++;
        }
    }

    /**
     * reiniciamos todas las variables del juego
     *
     * @param currentNanoTime
     */
    private void reiniciar(long currentNanoTime) {
        time.time = 0;
        lastNanoTime.value = currentNanoTime;
        score.value = 0;
        pipeList.clear();
        bird = new SpriteBird(background.getWidth(), background.getHeight(), iBird);
        isStart = true;
        hit = false;
        //r = new Random(1);
        this.start();
    }

    private void crearTuberias() {
        if (time.time >= distancia) {
            time.setTime(time.getTime() - distancia);

            //int pos = r.nextInt(7) + 1;
            int pos = 3;
            int hueco = 4;
            for (int i = 0; i < 11; i++) {
                if (i + 1 == pos) {
                    Sprite pipetop = new SpritePipeTop(background.getWidth() + 70, background.getHeight(), iPipeTop, 45 * i);
                    pipeList.add(pipetop);
                } else if (i - hueco == pos) {
                    Sprite pipebot = new SpritePipeBot(background.getWidth() + 70, background.getHeight(), iPipeBot, 45 * i);
                    pipeList.add(pipebot);
                } else if (i >= pos + hueco || i < pos) {
//if (i != pos && i != pos + 1 && i != pos + 2) {
                    Sprite pipeBase = new SpritePipeBaseBot(background.getWidth() + 70, background.getHeight(), iPipeBaseBot, 45 * i);
                    pipeList.add(pipeBase);
                }
            }

        }
    }

    private void detectarColision(double elapsedTime) {
        if (bird.getPositionY() + bird.height == background.height - 45) {
            hit = true;
        } else {
            for (Sprite pipe : pipeList) {
                pipe.setVelocity(-200, 0);
                pipe.update(elapsedTime);
                if (bird.intersects(pipe)) {
                    hit = true;
                }
            }
        }
    }

    private void renderizarObjetos() {
        background.render(gc);
        bird.render(gc);

        pipeList.forEach((pipe) -> {
            pipe.render(gc);
        });
    }

}
