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

import javafx.scene.image.Image;

public class SpritePipeTop extends SpritePipe {

    public SpritePipeTop(double backgroundWidth, double backgroundHeight) {
        super.setImage("/images/pipetop.png");
        super.setPosition(backgroundWidth, 0);
        super.setBoundLimits(0 - super.getWidth() + 15, backgroundWidth, 0, backgroundHeight);
    }

    public SpritePipeTop(double backgroundWidth, double backgroundHeight, Image image) {
        super.setImage(image);
        super.setPosition(backgroundWidth, 0);
        super.setBoundLimits(0 - super.getWidth() + 15, backgroundWidth, 0, backgroundHeight);
    }

    public SpritePipeTop(double backgroundWidth, double backgroundHeight, Image image, double posY) {
        super.setImage(image);
        super.setPosition(backgroundWidth, posY);
        super.setBoundLimits(0 - super.getWidth() + 15, backgroundWidth, 0, backgroundHeight);
    }

    public SpritePipeTop(double backgroundWidth, double backgroundHeight, Image image, double posX, double posY) {
        super.setImage(image);
        super.setPosition(posX, posY);
        super.setBoundLimits(0 - super.getWidth() + 15, backgroundWidth, 0, backgroundHeight);
    }

}
