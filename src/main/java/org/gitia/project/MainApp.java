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
package org.gitia.project;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import org.gitia.ag.fitness.Fitness;
import org.gitia.froog.Feedforward;
import org.gitia.froog.layer.Dense;
import org.gitia.froog.transferfunction.TransferFunction;
import org.gitia.froog.util.Open;
import org.gitia.project.flappygame.FlappyBirdTrain;
import org.gitia.project.flappygame.FlappyBirdGame;
import org.gitia.project.flappygame.FlappyBirdTestNet;
import org.gitia.project.flappygame.FlappyBird_Experiment;
import org.gitia.project.flappygame.layer.RLayer;
import org.gitia.project.genetic.AG_FlappyMNUMP;
import org.gitia.project.genetic.AG_FlappyMNUMPNN;
import org.gitia.project.genetic.fitness.FitnessFlappyBird;

/**
 * 
 * @author Matías Roodschild <mroodschild@gmail.com>
 */
public class MainApp extends Application {

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage theStage) throws Exception {
        String quienJuega = "train";//human, net, train, reduced
        switch (quienJuega) {
            case "human":
                FlappyBirdGame fbh = new FlappyBirdGame();
                fbh.start();
                break;
            case "net":
                Feedforward bird = Open.getNet("net_gen_29_it_4_score_2.874.xml", "src/main/resources/results/13");
                System.out.println("" + bird.toString());
                
                int L = bird.getLayers().size()-1;
                
                RLayer rLayer = new RLayer(10, 1, TransferFunction.LOGSIG, 10);
                
                rLayer.setW(bird.getLayers().get(L).getW());
                rLayer.setB(bird.getLayers().get(L).getB());
                
                bird.getLayers().set(L, rLayer);
                
                FlappyBirdTestNet bird_Net = new FlappyBirdTestNet(bird, 400000000.0);
                bird_Net.start();
                break;
            case "train":
               //configuración de la red neuronal
                Feedforward net = new Feedforward();
                
                net.addLayer(new Dense(1305, 10, TransferFunction.TANSIG));//29*45 = 1305
                net.addLayer(new Dense(10, 10, TransferFunction.TANSIG));
                //net.addLayer(new RLayer(10, 10, TransferFunction.LOGSIG, 2));
                net.addLayer(new RLayer(10, 1, TransferFunction.LOGSIG, 10));

                Fitness fitness1 = new FitnessFlappyBird();
                ((FitnessFlappyBird) fitness1).setNet(net);

                int sizeDNA = net.getParameters().getNumElements();

                // configuración del genético
                int epocas = 300;
                int individuos = 20;
                double offspring = 0.8;//16
                double elite = 0.1;//2
                double mutacion_porcentaje = 0.05;//1
                double porcentaje_PesosMutar = 0.10;
                int capa_mutar = 1;
                int torneo_tamano = (int) (individuos * 0.04);//4%
                if (torneo_tamano <= 2) {
                    torneo_tamano = 3;
                }

                boolean mutar_bias = true;
                //aqui le indicamos al genetico donde guardar los resultados
                //REVISAR que existe la carpeta!
                //String folder = "D:\\resultados\\45x29\\03";
                String folder = "src/main/resources/results/14";

                AG_FlappyMNUMPNN ag_flappy = new AG_FlappyMNUMPNN(epocas, individuos,
                        sizeDNA, offspring, elite, mutacion_porcentaje, torneo_tamano,
                        net, porcentaje_PesosMutar, capa_mutar, mutar_bias);

                //1000000000.0
                //FlappyBirdTrain flappyBird = new FlappyBirdTrain(net, ag_flappy, 5, 300000000.0, folder);
                FlappyBirdTrain flappyBird = new FlappyBirdTrain(net, ag_flappy, 3, 400000000.0, folder);

                flappyBird.start();
                break;
            case "reduced":
                Feedforward net_reduced = new Feedforward();
                net_reduced.addLayer(new Dense(1000, 100, TransferFunction.TANSIG));//70*108 = 7560
                net_reduced.addLayer(new Dense(100, 1, TransferFunction.LOGSIG));

                Fitness fitness2 = new FitnessFlappyBird();
                ((FitnessFlappyBird) fitness2).setNet(net_reduced);

                AG_FlappyMNUMP ag_flappy2 = new AG_FlappyMNUMP(150, 100, net_reduced.getParameters().getNumElements(), 0.6, 0.02, 0.15, 10, -1, 1);
                FlappyBird_Experiment flappyBird_mid = new FlappyBird_Experiment(net_reduced, ag_flappy2);

                flappyBird_mid.start();
                break;
        }
    }
}
