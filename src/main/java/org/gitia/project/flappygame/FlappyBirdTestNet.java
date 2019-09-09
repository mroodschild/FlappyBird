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
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import org.ejml.simple.SimpleMatrix;
import org.gitia.froog.Feedforward;
import org.gitia.jdataanalysis.ImageReader;

/**
 *
 * @author Matías Roodschild <mroodschild@gmail.com>
 */
public class FlappyBirdTestNet extends FlappyBirdGame {

    //int iteracion = 0;
    Feedforward net;
    ArrayList<String> input = new ArrayList<>();

    public FlappyBirdTestNet(Feedforward net, double velocidad) {
        this.net = net;
        this.velocidad = velocidad;
        initEnvironment();
    }

    @Override
    protected void action() {
        ////                 decidimos con la RNA
//        if (presstime.time >= 0.01) {
        //obtenemos el entorno actual
        //y normalizamos entre -1 y 1
        SimpleMatrix inputMap = getEnviroment(gc).divide(127).minus(1);
        //le preguntamos a la RNA si debemos volar
        double volar = net.output(inputMap).getDDRM().get(0);
        //evaluamos la decisión
//            System.out.println("volar:\t" + volar);
        if (volar > 0.5) {
            bird.addVelocity(0, -1000);
        }
        //actualizamos el tiempo de vuelo
//            presstime.setTime(0);
//        }
    }

    public SimpleMatrix getEnviroment(GraphicsContext gc) {
        WritableImage wImage = gc.getCanvas().snapshot(new SnapshotParameters(), null);
        ImageView image = new ImageView(wImage);
        image.setPreserveRatio(true);
        image.setCache(true);
        image.setFitWidth(60);//144
        
        BufferedImage image1 = SwingFXUtils.fromFXImage(image.snapshot(new SnapshotParameters(), null), null);
        //BufferedImage reduced = ImageReader.crop(image1, 60, 0, 70, 108);//para 144
        BufferedImage reduced = ImageReader.crop(image1, 25, 0, 29, 45);//para 60
        
        ImageReader.save(reduced, "D:\\resultados\\45x29\\01\\imgFlappy.jpg");
        SimpleMatrix map = new SimpleMatrix(ImageReader.convertTo1DDouble(reduced));
        // map.printDimensions();
        //System.exit(0);
        return map;
    }

}
