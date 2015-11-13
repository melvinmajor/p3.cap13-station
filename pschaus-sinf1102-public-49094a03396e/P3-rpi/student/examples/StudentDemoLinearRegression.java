package student.examples;

import chart.Chart;
import chart.DataSet;
import data.Generator;
import interfaces.Sample;

import java.io.IOException;

import time.Instant;
import utils.*;


import java.util.Date;
import java.util.List;

import pushbullet.PushListener;
import pushbullet.PushbulletClient;
import regression.LinearPseudoInverseRegression;
import regression.LinearPseudoInverseRegression.PolynomialEquation;
import sensors.SensorConnector;

/**
 * LSINF1102 P3 Projet demo
 * 
 * Cette demo viste ?? montrer comment predire l'??volution de la temp??rature 
 * sur base d'une r??gression d'un polynome de degre 3 sur les donn??es historiques
 * 
 *
 * @author Maxime Piraux, maxime.piraux@student.uclouvain.be
 * @author Pierre Schaus, pierre.schaus@uclouvain.be
 */
public class StudentDemoLinearRegression {

    public static void main(String[] args) throws IOException, InterruptedException {
    	
    	// Cr??ation d'un utilitaire d'??criture/lecture de donn??es
    	MySampleFileIO genIO = new MySampleFileIO("perlin.txt", false);
    	// Sauvegarde du temps (Instant) pr??sent
    	Instant now = Instant.now();
    	// Generation al??atoire d'un fichier de mesures (temp??rature et humidit??)
    	// pendant 24h (1440 minutes)  depuis now-24h jusqu'?? now
    	Generator.generateFile(genIO, 1440);
    	// Fermeture du fichier
    	genIO.close();
    	
    	// Lecture des ??chantillons de temp??ratures et humidit??s
    	List<Sample> points = genIO.readSample(now.minus(1, Instant.CHRONO_UNIT_HOURS),now);
    	
    	// Nous allons tenter de predire l'??volution de la temp??rature 
    	// sur base d'une r??gression d'un polynome de degre 3 sur les donn??es historiques
    	
    	int degree = 3;
    	boolean temperature = true;
    	
    	// Creation de l'objet permettant de calculer une r??gression
    	PolynomialEquation eq = LinearPseudoInverseRegression.findPseudoInverseRegression(points,degree,temperature);

    	// Calcul de l'heure suivante
    	Instant nextHour = now.plus(1, Instant.CHRONO_UNIT_HOURS);
    	// Evaluation du polynome d'interpolation ?? l'heure suivante
    	double temperatureNextHour =  eq.evaluate(nextHour.toEpochSecond());
    	// Affichage de la pr??diction de temp??rature
        System.out.println("Prediction of Temperature for next hour  : " + temperatureNextHour);
       

    }
}
