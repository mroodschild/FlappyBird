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
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 *
 * @author Matías Rodschild <mroodschild@gmail.com>
 */
public class Pipe {
    private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    private double maxPosX;
    private double maxPosY;
    private double minPosX;
    private double minPosY;
    ArrayList<Sprite> pipe = new ArrayList<>();

    public Pipe() {
        positionX = 0;
        positionY = 0;
        velocityX = 0;
        velocityY = 0;
    }

    public void setImage(Image i) {
        image = i;
        width = i.getWidth();
        height = i.getHeight();
    }

    public void setImage(String filename) {
        Image i = new Image(getClass().getResource(filename).toExternalForm());
        setImage(i);
    }

    public void setPosition(double x, double y) {
        positionX = x;
        positionY = y;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setVelocity(double x, double y) {
        velocityX = x;
        velocityY = y;
    }

    public void addVelocity(double x, double y) {
        velocityX += x;
        velocityY += y;
    }

    public void update(double time) {
        positionX += velocityX * time;
        positionY += velocityY * time;
        
        if (positionX < minPosX) {
            positionX = minPosX;
        }
        if (positionY < minPosY) {
            positionY = minPosY;
        }
        if (positionX > maxPosX-image.getWidth()) {
            positionX = maxPosX-image.getWidth();
        }
        if (positionY > maxPosY - image.getHeight()) {
            positionY = maxPosY - image.getHeight();
        }
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY);
    }

    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, width, height);
    }

    public boolean intersects(Sprite s) {
        return s.getBoundary().intersects(this.getBoundary());
    }

    @Override
    public String toString() {
        return " Position: [" + positionX + "," + positionY + "]"
                + " Velocity: [" + velocityX + "," + velocityY + "]";
    }

    public void setBoundLimits(double minPosX, double maxPosX, double minPosY, double maxPosY) {
        setMaxPosX(maxPosX);
        setMaxPosY(maxPosY);
        setMinPosX(minPosX);
        setMinPosY(minPosY);
    }

    public void setMaxPosX(double maxPosX) {
        this.maxPosX = maxPosX;
    }

    public void setMaxPosY(double maxPosY) {
        this.maxPosY = maxPosY;
    }

    public void setMinPosX(double minPosX) {
        this.minPosX = minPosX;
    }

    public void setMinPosY(double minPosY) {
        this.minPosY = minPosY;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getMaxPosX() {
        return maxPosX;
    }

    public double getMaxPosY() {
        return maxPosY;
    }

    public double getMinPosX() {
        return minPosX;
    }

    public double getMinPosY() {
        return minPosY;
    }
}
