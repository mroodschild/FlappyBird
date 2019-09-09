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
import org.gitia.ag.mutation.MultiNonUniformMutationPercentNeurona;
import org.gitia.ag.population.Individuo;
import org.gitia.ag.population.IndividuoComparator;
import org.gitia.ag.population.Population;
import org.gitia.froog.Feedforward;
import org.gitia.froog.layer.Dense;

/**
 *
 * @author Matías Roodschild <mroodschild@gmail.com>
 */
public class AG_FlappyMNUMPNN extends AG_Flappy {

    Feedforward net;
    int[] neuronas_por_capa;// indicamos cuantas neuronas hay en cada capa
    int[] pesos_por_neurona_capa;// indicamos cuantos pesos tienen las neuronas en la capa
    int layerMutar;
    boolean mutarBias;
    double porcentajePesosMutar;

//    int genActual = 0;
//    int currentIt = 0;
    public AG_FlappyMNUMPNN() {
    }

    /**
     *
     * @param epoch cantidad de epocas
     * @param populationSize tamaño de la población
     * @param dnaSize tamaño del adn
     * @param offspring porcentaje de numOffspring
     * @param elite porcentaje de elite
     * @param porcentajePobMutar porcentaje de mutación
     * @param tournament_size tamaño del torneo (no mayor al diez por ciento) en
     * cantidad
     * @param net estructura de la red neuronal
     * @param porcentajePesosMutar
     * @param layerMutar capa seleccionada para mutar
     * @param mutarBias mutar bias?
     */
    public AG_FlappyMNUMPNN(
            int epoch,
            int populationSize,
            int dnaSize,
            double offspring,
            double elite,
            double porcentajePobMutar,
            int tournament_size,
            Feedforward net,
            double porcentajePesosMutar,
            int layerMutar,
            boolean mutarBias
    ) {
        this.genMax = epoch;
        this.popSize = populationSize;
        this.dnaSize = dnaSize;
        this.elite_porcentaje = elite;
        this.mutation = porcentajePobMutar;
        this.tournament_size = tournament_size;
        this.offspring_porcentaje = offspring;
        this.porcentajePesosMutar = porcentajePesosMutar;
        this.net = net;
        this.layerMutar = layerMutar;
        this.mutarBias = mutarBias;

        neuronas_por_capa = new int[net.getLayers().size()];
        pesos_por_neurona_capa = new int[net.getLayers().size()];

        for (int i = 0; i < net.getLayers().size(); i++) {
            Dense l = net.getLayers().get(i);
            neuronas_por_capa[i] = l.numNeuron();
            pesos_por_neurona_capa[i] = l.getW().numCols();
        }

        //initial population
        numElite = (int) (popSize * elite_porcentaje);
        numMutacion = (int) (popSize * porcentajePobMutar);
        numOffspring = (int) (popSize * offspring_porcentaje);
        numSeleccion = popSize - (numElite + numMutacion + numOffspring);
        population = Population.generate(popSize, dnaSize, min, max);

        System.out.println("elite porcentaje:\t" + elite_porcentaje
                + "\tmutación porcentaje\t" + porcentajePobMutar
        );
        System.out.println("Poblacion\t" + population.size() + "\telite\t" + numElite
                + "\tnumMutacion\t" + numMutacion
                + "\tnumSeleccion\t" + numSeleccion
                + "\tnumOffspring\t" + numOffspring
        );
    }

    @Override
    public void printResume() {
        double mean;
        double sum = 0;
        double best = population.get(0).getFitnessMean();
        int idxBest = 0;
        for (int i = 0; i < population.size(); i++) {
            Individuo gen = population.get(i);
            sum += gen.getFitnessMean();
            if (best < gen.getFitnessMean()) {
                best = gen.getFitnessMean();
                idxBest = i;
            }
        }
        mean = sum / (double) population.size();
        System.out.println("epoch:\t" + genActual + "\tBest:\t" + best + "\tmean:\t" + mean + "\tidx best:\t" + idxBest);
    }

    @Override
    public void addEpoca() {
        genActual++;
    }

    @Override
    public SimpleMatrix getCurrentDNA() {
        return population.get(currentIt).getDna();
    }

    @Override
    public void run() {
        //System.out.printf("\nstart -");
        //fit y orden de menor a mayor
        Collections.sort(population, new IndividuoComparator());//ok
        //printPoblacion(population);
        listElite.clear();
        separarElite(population, listElite, numElite, listPoblacionSeleccionada);//ok
        //elite
        //seleccionamos para hacer el cruzamiento
        int[][] paring = Tournament.getParing(numOffspring, population, tournament_size);//ok
        //printPareo(paring);
        //separarOffspringSelNoCruzada(paring, population, listPoblacionSeleccionada, listCrossOver, listNoCruzada);
        //realizamos el cruzamiento
        //guardamos el cruzamiento
        //de la parte que no se cruzará
        //seleccionamos al azar los que se mutarán
        //mutamos y dejamos pasar a los que no se mutan
        //unimos el numOffspring, con los mutados, los seleccionados y la elite
        listOffspring = CubeCrossOver.crossover(population, paring);//ok
        // a la poblacion le quitamos la elite, quitamos los que se cruzaron
        // y indicamos cuantos deben pasar (num Seleccion + num mutacion)
        listNoCruzada = separarSeleccionNoCruzada(paring, population, numElite, (popSize - (listOffspring.size() + numElite)));
        //listNoCruzada = separarSeleccionNoCruzada(paring, population, numElite, numSeleccion + numMutacion);//revisar
        //MultiNonUniformMutationPercent.mutacion(listNoCruzada, numMutacion, min, max, genActual, genMax);
        // first_gen y last_gen pertenecen a las posiciones correspondientes a la neurona a mutar
        MultiNonUniformMutationPercentNeurona
                .mutacion(listNoCruzada, numMutacion,
                        neuronas_por_capa, pesos_por_neurona_capa,
                        porcentajePesosMutar, layerMutar, mutarBias);//ok
        //unimos las partes y reemplazamos por la nueva generación de individuos
        population.clear();
        //System.out.println("offspring: "+listOffspring.size()+"\tMutacion y no Cruzada\t"+listNoCruzada.size()+"\tElite:\t"+listElite.size());
        population = joinListas(listOffspring, listNoCruzada, listElite);
        cleanListas(listOffspring, listNoCruzada, listElite, listPoblacionSeleccionada);
        currentIt = 0;
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
//        System.err.println("\tepoca\t" + getGenActual() + "\tIt:\t" + getCurrentIt());
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
//    private void printPoblacion(List<Individuo> population) {
//        for (int i = 0; i < this.population.size(); i++) {
//            System.out.println(i + ":\t" + population.get(i).getFitness());
//        }
//    }
//
//    private void printPareo(int[][] paring) {
//        for (int i = 0; i < paring.length; i++) {
//            System.out.println(paring[i][0] + "\t" + paring[i][1]);
//        }
//    }
}
