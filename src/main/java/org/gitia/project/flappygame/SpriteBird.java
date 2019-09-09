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

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class SpriteBird extends Sprite {

    double heightRotate;
    double widthRotate;
    double angle = 0;
    
    //double translationX=0;
    

    public SpriteBird(double backgroundWidth, double backgroundHeight) {
        super.setImage("/images/flappy.png");
        super.setPosition(300, 400);
        super.setBoundLimits(0, backgroundWidth, 0, backgroundHeight - 45);
        
    }

    public SpriteBird(double backgroundWidth, double backgroundHeight, Image image) {
        super.setImage(image);
        super.setPosition(300, 200);
        super.setBoundLimits(0, backgroundWidth, 0, backgroundHeight - 45);
       
    }

    @Override
    public void update(double time) {

        double acceletarion = 980;//600 le gusta a jorge
        // positionX += velocityX * time;
        positionY += velocityY * time + (acceletarion * Math.pow(time, 2)) / 2;
        //System.out.println("Vi:\t"+velocityY+"\tVf:\t"+(velocityY + acceletarion * time)+"\ttime:\t"+time);
        velocityY = velocityY + acceletarion * time;

        if (positionX < minPosX) {
            positionX = minPosX;
        }
        if (positionY < minPosY) {
            positionY = minPosY;
            velocityY = 0;
        }
        if (positionX > maxPosX - image.getWidth()) {
            positionX = maxPosX - image.getWidth();
        }
        if (positionY > maxPosY - image.getHeight()) {
            positionY = maxPosY - image.getHeight();
            velocityY = 0;
        }
    }

    @Override
    public void addVelocity(double x, double y) {
        //velocityX += x;
        if (velocityY + y > -350) {
            velocityY += y;
        } else {
            velocityY = -350;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        ImageView iv = new ImageView(this.image);
//        if (velocityY < 0) {
//            this.angle = -45;
//        } else {
//            if (angle < 60) {
//                angle += 3;
//            }
//        }
//        iv.setRotate(angle);
        
        SnapshotParameters params = new SnapshotParameters();

//        params.setFill(Color.RED);
        params.setFill(Color.TRANSPARENT);

        Image rotatedImage = iv.snapshot(params, null);
        heightRotate = rotatedImage.getHeight();
        widthRotate = rotatedImage.getWidth();
        gc.drawImage(rotatedImage, positionX, positionY);
//        gc.drawImage(this.image, positionX, positionY);
    }

    @Override
    public boolean intersects(Sprite s) {
        return s.getBoundary().intersects(this.getBoundary());
    }

    @Override
    public Rectangle2D getBoundary() {
        //return new Rectangle2D(positionX, positionY, width, height);
        double h, h1, h2;
        double w, w1, w2;
        double deg = Math.toDegrees(angle);
        double sin = Math.sin(deg);
        double cos = Math.cos(deg);
        h1 = Math.abs(width * sin);
        h2 = Math.abs(height * cos);
        h = (h1 > h2) ? h1 : h2;
        w1 = Math.abs(width * cos);
        w2 = Math.abs(height * sin);
        w = (w1 > w2) ? w1 : w2;

        double x2, y2;
        x2 = positionX + (widthRotate - w) / 2;
        y2 = positionY + (heightRotate - h) / 2;
        return new Rectangle2D(x2, y2, w, h);
    }

}
