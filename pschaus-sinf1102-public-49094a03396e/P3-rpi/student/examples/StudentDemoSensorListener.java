package student.examples;


import interfaces.Sample;
import interfaces.SampleIO;

import java.io.IOException;

import sensors.SampleListener;
import sensors.SensorConnector;
import utils.*;


/**
 * @author Maxime Piraux, maxime.piraux@student.uclouvain.be
 * @author Pierre Schaus, pierre.schaus@uclouvain.be
 */
public class StudentDemoSensorListener {

    public static void main(String[] args) throws IOException {    
        
    	// Cr??ation d'un utilitaire de fichier pour d'??criture/lecture de donn??es
    	SampleIO sampleIO = new MySampleFileIO("sensor.txt", true);

    	// Connexion au senseur en lui indiquant l'utilitaire de fichier pour l'??criture de donn??es
        SensorConnector sensor = new SensorConnector(sampleIO);
        
        // Modification de l'intervalle entre deux mesures du senseur ?? 5 secondes
        sensor.setSamplingDelay(5);
        
        // Ajout d'un listener sur le senseur
        // a chaque fois qu'une mesure est prise par le senseur
        // la m??thode sampleTaken() sera appel??e avec la nouvelle mesure en argument
        sensor.addListener(new SampleListener() {
            @Override
            public void sampleTaken(Sample sample) {
                try {
                    System.out.println("Sample:"+ sample.getTemperature() + "??C/" + sample.getHumidity());
                } catch (Exception ex) {
                    System.err.println(ex);
                    System.exit(-1);
                }
            }
        });
        
        // Lancement de la collecte de mesure.
        sensor.start();
    }
}