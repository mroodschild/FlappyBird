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
import java.util.Random;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.ejml.simple.SimpleMatrix;
import org.gitia.ag.population.Individuo;
import org.gitia.froog.Feedforward;
import org.gitia.froog.util.Save;
import org.gitia.jdataanalysis.ImageReader;
import org.gitia.project.genetic.AG_Flappy;

/**
 *
 * @author Matías Roodschild <mroodschild@gmail.com>
 */
public class FlappyBirdTrain extends FlappyBirdGame {

    AG_Flappy ag;
    //int iteracion = 0;
    double mejor = -1;
    int intentos = 0;
    int nro_intentos = 1;
    //double velocidad = 1000000000.0;
    String folder;
    int img_num = 0;

    Feedforward net;

    /**
     *
     * @param net arquitectura de la red neuronal
     * @param ag Genetico que se utilizará para entrenar la RNA
     * @param nro_intentos Cantidad de veces que se lanzará el individuo para
     * calcular su score
     * @param velocidad velocidad de ejecución del juego 1000000000.0 (normal),
     * 500000000.0 (medio), 250000000.0 (rápido), muy rápido
     * @param folder carpeta donde guardar los resultados @param folder carpeta
     * donde guardar los resultados
     */
    public FlappyBirdTrain(Feedforward net, AG_Flappy ag, int nro_intentos,
            double velocidad, String folder) {
        this.ag = ag;
        this.net = net;
        this.nro_intentos = nro_intentos;
        this.velocidad = velocidad;
        this.folder = folder;
        initEnvironment();
    }

    @Override
    public void refresh(long currentNanoTime) {
        if (intentos++ >= nro_intentos - 1) {
            //System.out.println("0");
            Individuo ind = ag.getCurrentIndividuo();
            String pantalla = "";
            pantalla += "epoca:\t" + ag.getGenActual();
            pantalla += "\tit:\t" + ag.getCurrentIt();
            pantalla += "\tscore:\t" + ind.getFitness();
            if (ind.getFit().size() > 1) {
                pantalla += "\tHist_Mean:\t" + Math.rint(ind.getFitnessMean() * 1000) / 1000;
            }
            System.out.println(pantalla);
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
        r = new Random(intentos);//para que todos los pajaritos tengan los mismos escenarios
        renderizarObjetos();
    }

    @Override
    public void start() {
        super.start();
        theStage.show();
    }

    @Override
    public void stop() {
        super.stop();
        finalizado = true;
    }

//    @Override
    public SimpleMatrix getEnviroment(GraphicsContext gc) {
        WritableImage wImage = gc.getCanvas().snapshot(new SnapshotParameters(), null);
        ImageView image = new ImageView(wImage);
        image.setPreserveRatio(true);
        image.setCache(true);
        image.setFitWidth(60);//144

        BufferedImage image1 = SwingFXUtils.fromFXImage(image.snapshot(new SnapshotParameters(), null), null);
        //BufferedImage reduced = ImageReader.crop(image1, 60, 0, 70, 108);//para 144
        BufferedImage reduced = ImageReader.crop(image1, 25, 0, 29, 45);//para 60

        //ImageReader.save(reduced, "D:\\resultados\\45x29\\01\\imgFlappy.jpg");
        SimpleMatrix map = new SimpleMatrix(ImageReader.convertTo1DDouble(reduced));
        //ImageReader.save(reduced, "src/main/resources/ae_images/" + img_num++ + ".jpg");
        // map.printDimensions();
        //System.exit(0);
        return map;
    }

    @Override
    protected void action() {
        //obtenemos el entorno actual
        //y normalizamos entre -1 y 1
        SimpleMatrix inputMap = getEnviroment(gc).divide(127).minus(1);
        //le preguntamos a la RNA si debemos volar
        double volar = net.output(inputMap).getDDRM().get(0);
        if (volar > 0.5) {
            bird.addVelocity(0, -900);
        }
    }

    @Override
    protected void evaluarJuego() {
        String pointsText = "Score:\t" + (score.getValue());
        double ini = 36;
        double esp = 25;
        gc.setFill(Color.DARKGRAY);
        gc.fillText(pointsText, 30, ini + esp * 0);

        double total = Math.rint(1000 * (scoreTotal.getValue() + score.getValue())) / 1000;
        String totalText = "Total:\t" + total;
        gc.fillText(totalText, 30, ini + esp * 1);

        double promedio = Math.rint(1000 * ((scoreTotal.getValue() + score.getValue()) / (intentos + 1))) / 1000;
        String promedioText = "Mean:\t" + promedio;
        gc.fillText(promedioText, 30, ini + esp * 2);

        String intentosText = (intentos + 1) + "/" + nro_intentos;
        gc.fillText(intentosText, 30, ini + esp * 3);

        boolean contar = true;
        score.add(recorrido * 0.001);//siempre damos puntos

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
            //String mje = "Bird: " + ag.getCurrentIt() + " SCORE:\t" + score.getValue();
            isStart = false;
            scoreTotal.add(score.getValue());
            //se acabaron los intentos?
            if (intentos == nro_intentos - 1) {
                double puntaje = Math.rint(scoreTotal.getValue() / nro_intentos * 1000) / 1000;
                ag.getCurrentIndividuo().setFitness(puntaje);
                //el puntaje es superior al mejor?
                if (puntaje > mejor) {
                    mejor = puntaje;
                    if (puntaje > 1) {
                        Save.saveNet(net, "net_gen_" + ag.getGenActual() + "_it_" + ag.getCurrentIt() + "_score_" + puntaje,
                                folder);
                    }
                }
            }
        }
    }

}
