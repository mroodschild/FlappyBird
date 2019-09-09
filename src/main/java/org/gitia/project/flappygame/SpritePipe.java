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

public class SpritePipe extends Sprite {

    boolean count = false;

    public void setCount(boolean count) {
        this.count = count;
    }

    public boolean getCount() {
        return count;
    }

//    @Override
//    public void render(GraphicsContext gc) {
////        Rectangle rectangle = new Rectangle(40, 40, 100, 300);
////        rectangle.setFill(Color.GREEN);
////        rectangle.setStrokeWidth(5);
////        rectangle.setStroke(Color.BLACK);
//
//        gc.setFill(Color.DARKGREEN);
//        //gc.setStroke(Color.BLACK);
//        //gc.setLineWidth(2);
//        //gc.strokeRect(positionX, positionY, width, height);
//        gc.fillRect(positionX, positionY, width, height);
//        //gc.drawImage(image, positionX, positionY);
//    }
//
//    @Override
//    public void update(double time) {
//        positionX += velocityX * time;
//        positionY += velocityY * time;
//
//        if (positionX < minPosX) {
//            positionX = minPosX;
//        }
//        if (positionY < minPosY) {
//            positionY = minPosY;
//        }
//        if (positionX > maxPosX - 88) {
//            positionX = maxPosX - 88;
//        }
//        if (positionY > maxPosY - 88) {
//            positionY = maxPosY - 88;
//        }
//    }

}
