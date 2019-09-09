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
package org.gitia.project.genetic;

import java.util.Collections;
import java.util.List;
import org.ejml.simple.SimpleMatrix;
import org.gitia.ag.compite.Tournament;
import org.gitia.ag.crossover.CubeCrossOver;
import org.gitia.ag.mutation.MultiNonUniformMutationPercent;
import org.gitia.ag.population.Individuo;
import org.gitia.ag.population.IndividuoComparator;
import org.gitia.froog.Feedforward;

/**
 *
 * @author Matías Roodschild <mroodschild@gmail.com>
 */
public class AG_FlappyTest extends AG_Flappy {

    Feedforward net;
//    int genActual = 0;
//    int currentIt = 0;

    public AG_FlappyTest() {
    }

    public AG_FlappyTest(Feedforward net) {
        this.net = net;
    }

    @Override
    public void printResume() {
//        double mean;
//        double sum = 0;
//        double best = population.get(0).getFitness();
//        int idxBest = 0;
//        for (int i = 0; i < population.size(); i++) {
//            Individuo gen = population.get(i);
//            sum += gen.getFitness();
//            if (best < gen.getFitness()) {
//                best = gen.getFitness();
//                idxBest = i;
//            }
//        }
//        mean = sum / (double) population.size();
//        System.out.println("epoch:\t" + genActual + "\tBest:\t" + best + "\tmean:\t" + mean + "\tidx best:\t" + idxBest);
    }

    @Override
    public void addEpoca() {

    }

    @Override
    public SimpleMatrix getCurrentDNA() {
        return null;
    }

    /**
     *
     */
    @Override
    public void run() {

        //System.out.printf("\nstart -");
        //fit y orden de menor a mayor
        //Collections.sort(population, new IndividuoComparator());
        //listElite.clear();
        //separarElite(population, listElite, numElite, listPoblacionSeleccionada);
        //elite
        //seleccionamos para hacer el cruzamiento
        //int[][] paring = Tournament.getParing(numOffspring, population, tournament_size);
        //separarOffspringSelNoCruzada(paring, population, listPoblacionSeleccionada, listCrossOver, listNoCruzada);
        //realizamos el cruzamiento
        //guardamos el cruzamiento
        //de la parte que no se cruzará
        //seleccionamos al azar los que se mutarán
        //mutamos y dejamos pasar a los que no se mutan
        //unimos el numOffspring, con los mutados, los seleccionados y la elite
        //listOffspring = CubeCrossOver.crossover(population, paring);
        // a la poblacion le quitamos la elite, quitamos los que se cruzaron
        // y indicamos cuantos deben pasar (num Seleccion + num mutacion)
        //listNoCruzada = separarSeleccionNoCruzada(paring, population, numElite, numSeleccion + numMutacion);
        //MultiNonUniformMutationPercent.mutacion(listNoCruzada, numMutacion, min, max, genActual, genMax);

        //unimos las partes y reemplazamos por la nueva generación de individuos
        //population.clear();
        //population = joinListas(listOffspring, listNoCruzada, listElite);
        //cleanListas(listOffspring, listNoCruzada, listElite, listPoblacionSeleccionada);
        //currentIt = 0;
        //System.out.printf("/finish\n");
        //System.out.println(population.size() + " " + listElite.size() + " " + listPoblacionSeleccionada.size() + " " + listOffspring.size() + " " + listNoCruzada.size());

    }

    /**
     * quedan epocas que entrenar?
     *
     * @return true si hay epocas que seguir entrenando, false si hay q
     * finalizar
     */
    @Override
    public boolean nuevaEpoca() {
        if (genActual < genMax) {
            genActual++;
        }
        return genActual + 1 <= genMax;
    }

    /**
     * evaluamos si es el ultimo individuo de la población caso contrario lo
     * incrementamos
     *
     * @return true si hay nuevos individuos, falso si ya paso toda la población
     */
    @Override
    public boolean nuevoIndividuo() {
        System.err.println("\tepoca\t" + getGenActual() + "\tIt:\t" + getCurrentIt());
        if (currentIt + 1 < population.size()) {
            currentIt++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Individuo> getPopulation() {
        return population;
    }

    @Override
    public Individuo getCurrentIndividuo() {
        return population.get(currentIt);
    }

//    @Override
//    public int getCurrentIt() {
//        return currentIt;
//    }
//
//    @Override
//    public int getGenActual() {
//        return genActual;
//    }
}
