import java.util.Scanner;

import interfaces.Sample;
import interfaces.SampleIO;

import java.io.IOException;

// Importation des classes pour le capteur
import sensors.SampleListener;
import sensors.SensorConnector;
import utils.*;

// Importation des classes Push Bullet
import pushbullet.PushListener;
import pushbullet.PushbulletClient;

public class SMART 
{
	
	public static void main(String[] args) throws IOException 
	{
		Scanner saisieUtilisateur = new Scanner(System.in);
		
		System.out.println("Inserez la cle PushBullet s'il vous plait...");
		String bulletKey = saisieUtilisateur.nextLine();
		System.out.println(bulletKey);
		
		System.out.println("Quel est le temps de rafraichissement en secondes ? (valeur minimum : 2)");
		int refresh = saisieUtilisateur.nextInt();
		if (refresh < 2){
			System.out.println("Erreur...");
			System.out.println("Veuillez introduire une valeur egale ou superieure a 2.");
			int refresh = saisieUtilisateur.nextInt();
		}
		System.out.println("Le temps de rafraichissement est de " + refresh + "secondes");
		
		
		System.out.println("Souhaitez-vous ecraser les precedentes donnees ? (true or false)");
		boolean erase = saisieUtilisateur.nextBoolean();
		if (erase != true || erase != false){
			System.out.println("Erreur...");
			System.out.println("Veuillez introduire 'true' ou 'false' s'il vous plait...");
			boolean erase = saisieUtilisateur.nextBoolean();
		}
		System.out.println("Vous avez indiqué : " + erase);

		saisieUtilisateur.close();
		
		Sensor(refresh, erase);	
		Pushbullet(bulletKey);
	}


/**
 * Methode ayant pour but de recuperer les donnees du capteur & de les inscrire dans un fichier texte.
 * 
 * @pre : Ecrasement des donnees (TRUE OR FALSE) : Variable Booleen Erase.
 * @pre : Intervalle de temps entre deux mesures (INT) : Variable int Interval.
 * 
 * @author Gauthier Fossion, Melvin Campos Casares, Pablo Wauthelet, Crispin Mutani.
 * 
 * @source : Pierre Schaus, Maxime Piraux.
 */
	public static void Sensor(int refresh, boolean erase) throws IOException 
	{    
    
		// Creation d'un utilitaire de fichier pour ecriture/lecture de donnees
		SampleIO sampleIO = new MySampleFileIO("sensor.txt", erase);

		// Connexion au senseur en lui indiquant l'utilitaire de fichier pour l'ecriture de donnees
		SensorConnector sensor = new SensorConnector(sampleIO);
    
		// Modification de l'intervalle entre deux mesures du senseur ?? 5 secondes
		sensor.setSamplingDelay(refresh);
    
		// Ajout d'un listener sur le senseur
		// A chaque fois qu'une mesure est prise par le senseur
		// La methode sampleTaken() sera appelee avec la nouvelle mesure en argument
		sensor.addListener(new SampleListener() 
		{
			@Override
			public void sampleTaken(Sample sample) 
				{
					try 
					{
						System.out.println("Mesure : "+ sample.getTemperature() + "C / " + sample.getHumidity());
					}
					catch (Exception ex) 
					{
						System.err.println(ex);
						System.exit(-1);
					}
				}
		});
    
		// Lancement de la collecte de mesure.
		sensor.start();
	}


	/**
	* Mï¿½thode ayant pour de connecter votre station avec vos telephones via pushbullet.
	* 
	* @pre : ClePushBullet (STRING)
	* 
	* @author Gauthier Fossion, Melvin Campos Casares, Pablo Wauthelet, Crispin Mutani.
	* 
	* @source : Pierre Schaus, Maxime Piraux.
	*/
	
	public static void Pushbullet(String bulletKey) throws IOException 
	{
		// Creation du client Pushbullet.
        final PushbulletClient client = new PushbulletClient(bulletKey);
        
        // Ajout d'un listener en instanciant une classe anonyme implementant PushListener.
        // Un PushListener verra sa methode pushReceived appelee lorsque l'utilisateur 
        // Ecrit un message sur pushBullet  
        client.addListener(new PushListener() 
        {
        	@Override
            public void pushReceived(String title, String body) 
        	{
            	// si le message contient un "?" ...
                if (body.contains("?")) 
                {
                    try 
                    {
                    	// ... alors on repond avec une reponse simple ;-)
                        client.pushNote("I have the answer !", "42");
                        
                        // notez que client.pushFile permet d'envoyer des fichiers
                        // par exemple client.pushFile("dayGraph.png", null);
                    } 
                    catch (IOException ex) 
                    {
                        System.err.println(ex);
                        System.exit(-1);
                    }
                }
                if (body.contains("deg") || body.contains("humid")){
                	Stry 
					{
						System.out.println("Mesure : "+ sample.getTemperature() + "C / " + sample.getHumidity());
					}
					catch (Exception ex) 
					{
						System.err.println(ex);
						System.exit(-1);
					}
                }
            }
        });

    }
}
