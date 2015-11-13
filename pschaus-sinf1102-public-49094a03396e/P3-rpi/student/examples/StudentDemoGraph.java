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
import sensors.SensorConnector;

/**
 * LSINF1102 P3 Projet demo
 * 
 * Cette demo viste ?? montrer comment generer un graphique (plot)
 * au d??part de mesures d'humidit?? (plot) 
 *
 * @author Maxime Piraux, maxime.piraux@student.uclouvain.be
 * @author Pierre Schaus, pierre.schaus@uclouvain.be
 */
public class StudentDemoGraph {

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
    	// Extraction des ??chantillons de mesures sur une fen??tre de temps [now-24h,now]
        List<Sample> samples = genIO.readSample(now.minus(1, Instant.CHRONO_UNIT_DAYS), now);
        
        System.out.println(samples.size());
        // Cr??ation d'un ensemble de donn??es avec une precision ?? la minutes
        DataSet hum = new DataSet("Humidity", DataSet.PRECISION_MINUTES);
        // Iteration sur tous les ??chantillons dans l'ordre chroissant
        for (Sample sample : samples) {
        	// ajout d'un point (x,y)  ?? l'ensemble de donn??es correspondant ?? l'??chantillon 
        	// les coordonn??es sont x = la date en milli seconde,  y = la mesure d'humidit?? 
            hum.addPoint(new Date(sample.getTime().toEpochMilli()), sample.getHumidity());
        }
        // Cr??ation du graphique au d??part de l'ensemble des points (x,y)
        Chart chart = new Chart("Humidity over the last 24 hours", hum);
        // Sauvegarde du graphique sous forme de fichier image png avec une r??solution 1200x400
        chart.saveChartAsPNG("dayGraph.png", 1200, 400);

    }
}
