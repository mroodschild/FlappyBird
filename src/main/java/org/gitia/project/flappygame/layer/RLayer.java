/*
 * Copyright 2019 Matías Roodschild <mroodschild@gmail.com>.
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
package org.gitia.project.flappygame.layer;

import org.ejml.simple.SimpleMatrix;
import org.gitia.froog.layer.Dense;
import org.gitia.froog.transferfunction.TransferFunction;

/**
 *
 * @author Matías Roodschild <mroodschild@gmail.com>
 */
public class RLayer extends Dense {

    int recurrencia = 0; // cantidad de pasos hacia atrás que serán considerados
    int dimensionEntrada;// dimensión que terminará teniendo la entrada
    double[] salidasPrevias;// vector con las salidas previas
    int dimOutput; // dimensión de las salidas

    /**
     *
     * @param input entrada a la capa (sin incluir la recurrencia)
     * @param output cantidad de neuronas que tendrá la capa
     * @param funcion función de transferencia
     * @param recurrencia cantidad de pasos hacia atrás a recordar
     */
    public RLayer(int input, int output, String funcion, int recurrencia) {
        super(input + recurrencia * output, output, funcion);
        dimOutput = output;
        //la entrada esta compuesta por la recurrencia y la cantidad de salidas
        dimensionEntrada = input + recurrencia * output;
        this.recurrencia = recurrencia;
        salidasPrevias = new double[recurrencia * output];
    }

    /**
     *
     * @param a [out prev layer - m] where m is the amount of data
     * @return a<sub>L(i)</sub> = f(a<sub>L(i)-1</sub>)
     */
    @Override
    public SimpleMatrix output(SimpleMatrix a) {
        //salidas previas
        SimpleMatrix rec = new SimpleMatrix(salidasPrevias.length, 1, true, salidasPrevias);
        //formamos la entrada
        //rec.printDimensions();
//        System.out.println("salida previa");
//        rec.print();
        SimpleMatrix input = a.concatRows(rec);
        //obtenemos la salida
        SimpleMatrix output = getFunction().output(this.outputZ(input));
        copyNewData(output);
        return output;
    }

    private void copyNewData(SimpleMatrix output) {
        if (recurrencia > 0) {
            double[] aux = new double[salidasPrevias.length];
            double[] s = output.getDDRM().getData();
            int cont = dimOutput;
            for (int i = 0; i < salidasPrevias.length - dimOutput; i++) {
                aux[i] = salidasPrevias[cont++];
            }
            cont = 0;
            for (int i = salidasPrevias.length - dimOutput; i < salidasPrevias.length; i++) {
                aux[i] = s[cont++];
            }
            salidasPrevias = aux;
        }
    }

//    public void printArray(double[] a) {
//        //System.out.println(a.length);
//        for (int i = 0; i < a.length; i++) {
//            System.out.printf("%.1f ", a[i]);
//        }
//        System.out.println("");
//    }
//
//    public static void main(String[] args) {
//        RLayer layer = new RLayer(1, 1, TransferFunction.PURELIM, 4);
//        double[] data = {5};
//        SimpleMatrix a = new SimpleMatrix(1, 1, true, data);
//
//        SimpleMatrix s = layer.output(a);
//        System.out.println("salida actual");
//        s.print();
//        s = layer.output(a);
//        System.out.println("salida actual");
//        s.print();
//        s = layer.output(a);
//        System.out.println("salida actual");
//        s.print();
//        s = layer.output(a);
//        System.out.println("salida actual");
//        s.print();
//        s = layer.output(a);
//        System.out.println("salida actual");
//        s.print();
//
//    }
}
