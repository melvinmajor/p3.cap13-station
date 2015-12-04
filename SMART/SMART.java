import java.util.Scanner;

import chart.Chart;
import chart.DataSet;

import time.Instant;

import java.util.Date;
import java.util.List;

import regression.LinearPseudoInverseRegression;
import regression.LinearPseudoInverseRegression.PolynomialEquation;

import interfaces.Sample;
import interfaces.SampleIO;

import java.io.*;

// Importation des classes pour le capteur
import sensors.SampleListener;
import sensors.SensorConnector;
import utils.*;

// Importation des classes Push Bullet
import pushbullet.PushListener;
import pushbullet.PushbulletClient;

public class SMART 
{

	public static PushbulletClient client;

	public static double hum;
	public static double temp;
	public static double tempMax;
	public static double tempMin;
	public static double humMax;
	public static double humMin;
	public static String bulletKey;
	
	/**
	* Méthode permettant de demander les informations à l'utilisateur & lancement pushbullet, alerte, mesure
	* 
	* @pre : None
	* 
	* @author Gauthier Fossion, Melvin Campos Casares, Pablo Wauthelet, Crispin Mutani.
	*/
	
	public static void main(String[] args) throws IOException 
	{
		Scanner saisieUtilisateur = new Scanner(System.in);
		System.out.println("Welcome to our SMART");
		System.out.println();
		System.out.println("Inserez la cle PushBullet s'il vous plait...");
		bulletKey = saisieUtilisateur.nextLine();		
		System.out.println("Quel est le temps de rafraichissement en secondes ? (valeur minimum : 2)");
		int refresh = saisieUtilisateur.nextInt();
		while (refresh < 2){
			System.out.println("Erreur...");
			System.out.println("Veuillez introduire une valeur egale ou superieure a 2.");
			refresh = saisieUtilisateur.nextInt();
		}
		System.out.println("Le temps de rafraichissement est de " + refresh + "secondes");
		
		
		System.out.println();
		System.out.println("Quel est le seuil maximum de t° sans avertissement ? (max 80)");
		tempMax = saisieUtilisateur.nextDouble();
		while (tempMax >= 80 && tempMax <= -40){
			System.out.println("Erreur...");
			System.out.println("Veuillez introduire une valeur comprise en -40 & 80 °C.");
			tempMax = saisieUtilisateur.nextDouble();
		}
		
		System.out.println("Vous serez averti si la temperature dépasse "+tempMax+" °C");
		
		System.out.println();
		System.out.println("Quel est le seuil minimum de t° sans avertissement ? (max -40)");
		tempMin = saisieUtilisateur.nextDouble();
		while (tempMin >= 80 && tempMin <= -40){
			System.out.println("Erreur...");
			System.out.println("Veuillez introduire une valeur comprise en -40 & 80 °C.");
			tempMin = saisieUtilisateur.nextDouble();
		}
		System.out.println("Vous serez averti si la temperature descend en dessous de "+tempMin+" °C");
		
		System.out.println();
		System.out.println("Quel est le seuil maximum d'humidite sans avertissement ? (max 100)");
		humMax = saisieUtilisateur.nextDouble();
		while (humMax >= 100 && humMax <= 0){
			System.out.println("Erreur...");
			System.out.println("Veuillez introduire une valeur comprise en 0 & 100.");
			humMax = saisieUtilisateur.nextDouble();
		}
		
		System.out.println("Vous serez averti si l'humidite dépasse "+humMax+" %");
		
		System.out.println();
		System.out.println("Quel est le seuil minimum d'humidite sans avertissement ? (max 0)");
		humMin = saisieUtilisateur.nextDouble();
		while (humMin >= 100 && humMin <= 0){
			System.out.println("Erreur...");
			System.out.println("Veuillez introduire une valeur comprise en 100 & 0.");
			humMin = saisieUtilisateur.nextDouble();
		}
		System.out.println("Vous serez averti si la temperature descend en dessous de "+humMin+" %");
		
		
		System.out.println();
		System.out.println("Souhaitez-vous ecraser les precedentes donnees ?");
		System.out.println("[0] Oui / Yes");
		System.out.println("[1] Non / No");
		int eraseChoice = saisieUtilisateur.nextInt();
		while (eraseChoice != 0 && eraseChoice != 1){
			System.out.println("Erreur...");
			System.out.println("Veuillez introduire '0' ou '1' s'il vous plait...");
			eraseChoice = saisieUtilisateur.nextInt();
		}
		
		boolean erase;
		
		if(eraseChoice == 0)
		{
			erase = false;
		}
		else
		{
			erase = true;
		}
		
		System.out.println("Vous avez indiqué : " + eraseChoice);

		saisieUtilisateur.close();
		
		client = new PushbulletClient(bulletKey);
		
		Sensor(refresh, erase);	
		Pushbullet(bulletKey);
		Alerte();
	}

	/**
	* Méthode ayant pour but de récuperer les données du capteur & de les inscrire dans un fichier texte.

 * 
 * @pre : Ecrasement des données (TRUE OR FALSE) : Variable Booleen Erase.
 * @pre : Intervalle de temps entre deux mesures (INT) : Variable int Interval.
 * 
 * @author Gauthier Fossion, Melvin Campos Casares, Pablo Wauthelet, Crispin Mutani.
 * 
 * @source : Pierre Schaus, Maxime Piraux.
 */
	
	public static void Sensor(int refresh, boolean erase) throws IOException 
	{    
    
		// Creation d'un utilitaire de fichier pour d'ecriture/lecture de donnees
		SampleIO sampleIO = new MySampleFileIO("sensor.txt", erase);

		// Connexion au senseur en lui indiquant l'utilitaire de fichier pour l'ecriture de donnees
		SensorConnector sensor = new SensorConnector(sampleIO);
    
		// Modification de l'intervalle entre deux mesures du senseur ?? 5 secondes
		sensor.setSamplingDelay(refresh);
    
		// Ajout d'un listener sur le senseur
		// A chaque fois qu'une mesure est prise par le senseur
		// La methode sampleTaken() sera appel??e avec la nouvelle mesure en argument
		sensor.addListener(new SampleListener() 
		{
			@Override
			public void sampleTaken(Sample sample) 
				{
					try 
					{
						System.out.println("Mesure : "+ sample.getTemperature() + "°C / " + sample.getHumidity());
						temp = sample.getTemperature();
						hum = sample.getHumidity();
						System.out.println(temp);
						
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
	* Méthode ayant pour de connecter votre station avec vos téléphones via pushbullet.
	* 
	* @pre : Clé PushBullet (STRING )
	* 
	* @author Gauthier Fossion, Melvin Campos Casares, Pablo Wauthelet, Crispin Mutani.
	* 
	* @source : Pierre Schaus, Maxime Piraux.
	*/
	
	public static void Pushbullet(String bulletKey) throws IOException 
	{
		// Creation du client Pushbullet.        
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
                        client.pushNote("TempNow", "Connaitre la temperature actuelle");
                        client.pushNote("HumNow", "Connaitre le taux d'humidite actuel");
                        client.pushNote("GraphHumHour", "Graphique de l'humidite de la derniere heure");
                        client.pushNote("GraphTempHour", "Graphique de la temperature de la derniere heure");
                        client.pushNote("GraphHumDay", "Graphique de l'humidite des dernieres 24 heures");
                        client.pushNote("GraphTempDay", "Graphique de la temperature des dernieres 24 heures");
                        client.pushNote("GraphHumWeek", "Graphique de l'humidite de la semaine");
                        client.pushNote("GraphTempWeek", "Graphique de la temperature de la semaine");
                        client.pushNote("PredTemp", "Temperature dans une heure");
                        
                        
                        // notez que client.pushFile permet d'envoyer des fichiers
                        // par exemple client.pushFile("dayGraph.png", null);
                    } 

                    catch (IOException ex) 
                    {
                        System.err.println(ex);
                        System.exit(-1);
                    }
                }
                
                if (body.contains("TempNow")) 
                {
                    try 
                    {
                    	double fah = ((9/5)*temp+32);
                    	String tempS = String.valueOf(temp)+" Celsius / "+fah+" Fahrenheit";
                    	client.pushNote("Reponse :", tempS);
                        
                        // notez que client.pushFile permet d'envoyer des fichiers
                        // par exemple client.pushFile("dayGraph.png", null);
                    } 

                    catch (IOException ex) 
                    {
                        System.err.println(ex);
                        System.exit(-1);
                    } catch (Exception e) {
						e.printStackTrace();
					}
                }
                
                if (body.contains("HumNow")) 
                {
                    try 
                    {
                    	String humS = String.valueOf(hum)+"%";
                    	// ... alors on repond avec une reponse simple ;-)
                        client.pushNote("Reponse :", humS);
                        
                        // notez que client.pushFile permet d'envoyer des fichiers
                        // par exemple client.pushFile("dayGraph.png", null);
                    } 

                    catch (IOException ex) 
                    {
                        System.err.println(ex);
                        System.exit(-1);
                    } catch (Exception e) {
						e.printStackTrace();
					}
                }
                
                if (body.contains("GraphHumHour")) 
                {
                    try 
                    {   
                    	              	
                    	GraphHumHour();
                    	
                    	// ... alors on repond avec une reponse simple ;-)
                        client.pushNote("Reponse :", null);
                        client.pushFile("hourHumGraph.png", null);
                        // notez que client.pushFile permet d'envoyer des fichiers
                        // par exemple client.pushFile("dayGraph.png", null);
                    } 

                    catch (IOException ex) 
                    {
                        System.err.println(ex);
                        System.exit(-1);
                    } catch (Exception e) {
						e.printStackTrace();
					}
                }
                
                if (body.contains("GraphTempHour")) 
                {
                    try 
                    {   
                    	              	
                    	GraphTempHour();
                    	
                    	// ... alors on repond avec une reponse simple ;-)
                        client.pushNote("Reponse :", null);
                        client.pushFile("hourTempGraph.png", null);
                        // notez que client.pushFile permet d'envoyer des fichiers
                        // par exemple client.pushFile("dayGraph.png", null);
                    } 

                    catch (IOException ex) 
                    {
                        System.err.println(ex);
                        System.exit(-1);
                    } catch (Exception e) {
						e.printStackTrace();
					}
                }
                
                if (body.contains("GraphHumDay")) 
                {
                    try 
                    {   
                    	
                    	GraphHumDay();
                    	
                    	// ... alors on repond avec une reponse simple ;-)
                        client.pushNote("Reponse :", null);
                        client.pushFile("dayHumGraph.png", null);
                        // notez que client.pushFile permet d'envoyer des fichiers
                        // par exemple client.pushFile("dayGraph.png", null);
                    } 

                    catch (IOException ex) 
                    {
                        System.err.println(ex);
                        System.exit(-1);
                    } catch (Exception e) {
						e.printStackTrace();
					}
                }
                
                if (body.contains("GraphTempDay")) 
                {
                    try 
                    {   
                    	
                    	GraphTempDay();
                    	
                    	// ... alors on repond avec une reponse simple ;-)
                        client.pushNote("Reponse :", null);
                        client.pushFile("dayTempGraph.png", null);
                        // notez que client.pushFile permet d'envoyer des fichiers
                        // par exemple client.pushFile("dayGraph.png", null);
                    } 

                    catch (IOException ex) 
                    {
                        System.err.println(ex);
                        System.exit(-1);
                    } catch (Exception e) {
						e.printStackTrace();
					}
                }
                
                if (body.contains("GraphHumWeek")) 
                {
                    try 
                    {   
                    	
                    	GraphHumWeek();
                    	
                    	// ... alors on repond avec une reponse simple ;-)
                        client.pushNote("Reponse :", null);
                        client.pushFile("weekHumGraph.png", null);
                        // notez que client.pushFile permet d'envoyer des fichiers
                        // par exemple client.pushFile("dayGraph.png", null);
                    } 

                    catch (IOException ex) 
                    {
                        System.err.println(ex);
                        System.exit(-1);
                    } catch (Exception e) {
						e.printStackTrace();
					}
                }
                
                if (body.contains("GraphTempWeek")) 
                {
                    try 
                    {   
                    	
                    	GraphTempWeek();
                    	
                    	// ... alors on repond avec une reponse simple ;-)
                        client.pushNote("Reponse :", null);
                        client.pushFile("weekTempGraph.png", null);
                        // notez que client.pushFile permet d'envoyer des fichiers
                        // par exemple client.pushFile("dayGraph.png", null);
                    } 

                    catch (IOException ex) 
                    {
                        System.err.println(ex);
                        System.exit(-1);
                    } catch (Exception e) {
						e.printStackTrace();
					}
                }
                
                if (body.contains("PredTemp")) 
                {
                    try 
                    {   
                    	               	
                    	double reponse = PredTemp();
                    	double fah = ((9/5)*reponse+32);
                    	
                    	String str = String.valueOf(reponse)+" Celsius & "+String.valueOf(fah)+" Fahrenheit";
                    	
                    	// ... alors on repond avec une reponse simple ;-)
                        client.pushNote("Reponse :", str);
                        // notez que client.pushFile permet d'envoyer des fichiers
                        // par exemple client.pushFile("dayGraph.png", null);
                    } 

                    catch (IOException ex) 
                    {
                        System.err.println(ex);
                        System.exit(-1);
                    } catch (Exception e) {
						e.printStackTrace();
					}
                }
                
               
               
            }
        });

    }

	/**
	* Méthode permettant de fournir un graphique de l'humidité selon les heures.
	* 
	* @pre : None
	* 
	* @author Gauthier Fossion, Melvin Campos Casares, Pablo Wauthelet, Crispin Mutani.
	*/
	
	public static void GraphHumHour() throws IOException, InterruptedException {
    	
    	// Cr??ation d'un utilitaire d'??criture/lecture de donn??es
    	MySampleFileIO genIO = new MySampleFileIO("sensor.txt", true);
    	
    	Instant now = Instant.now();
    	
    	// Extraction des ??chantillons de mesures sur une fen??tre de temps [now-24h,now]
        List<Sample> samples = genIO.readSample(now.minus(1, Instant.CHRONO_UNIT_HOURS), now);
        genIO.close();
        
        System.out.println("Graphique done !");
        // Cr??ation d'un ensemble de donn??es avec une precision ?? la minutes
        DataSet hum = new DataSet("Humidite", DataSet.PRECISION_SECONDS);
        // Iteration sur tous les ??chantillons dans l'ordre chroissant
        for (Sample sample : samples) {
        	// ajout d'un point (x,y)  ?? l'ensemble de donn??es correspondant ?? l'??chantillon 
        	// les coordonn??es sont x = la date en milli seconde,  y = la mesure d'humidit?? 
            hum.addPoint(new Date(sample.getTime().toEpochMilli()), sample.getHumidity());
        }
        // Cr??ation du graphique au d??part de l'ensemble des points (x,y)
        Chart chart = new Chart("Humidite sur la derniere heure", hum);
        // Sauvegarde du graphique sous forme de fichier image png avec une r??solution 1200x400
        chart.saveChartAsPNG("hourHumGraph.png", 1200, 400);
    }
    
	/**
	* Méthode permettant de fournir un graphique de la temperature selon les heures.
	* 
	* @pre : None
	* 
	* @author Gauthier Fossion, Melvin Campos Casares, Pablo Wauthelet, Crispin Mutani.
	*/
	
    public static void GraphTempHour() throws IOException, InterruptedException {
    	
    	// Cr??ation d'un utilitaire d'??criture/lecture de donn??es
    	MySampleFileIO genIO = new MySampleFileIO("sensor.txt", true);
    	
    	Instant now = Instant.now();
    	
    	// Extraction des ??chantillons de mesures sur une fen??tre de temps [now-24h,now]
        List<Sample> samples = genIO.readSample(now.minus(1, Instant.CHRONO_UNIT_HOURS), now);
        genIO.close();
        
        System.out.println("Graphique done !");
        // Cr??ation d'un ensemble de donn??es avec une precision ?? la minutes
        DataSet hum = new DataSet("Temperature", DataSet.PRECISION_SECONDS);
        // Iteration sur tous les ??chantillons dans l'ordre chroissant
        for (Sample sample : samples) {
        	// ajout d'un point (x,y)  ?? l'ensemble de donn??es correspondant ?? l'??chantillon 
        	// les coordonn??es sont x = la date en milli seconde,  y = la mesure d'humidit?? 
            hum.addPoint(new Date(sample.getTime().toEpochMilli()), sample.getTemperature());
        }
        // Cr??ation du graphique au d??part de l'ensemble des points (x,y)
        Chart chart = new Chart("Temperature sur la derniere heure", hum);
        // Sauvegarde du graphique sous forme de fichier image png avec une r??solution 1200x400
        chart.saveChartAsPNG("hourTempGraph.png", 1200, 400);
    }
    
    /**
	* Méthode permettant de fournir un graphique de l'humidité selon les jours.
	* 
	* @pre : None
	* 
	* @author Gauthier Fossion, Melvin Campos Casares, Pablo Wauthelet, Crispin Mutani.
	*/
    
    public static void GraphHumDay() throws IOException, InterruptedException {
    	
    	// Cr??ation d'un utilitaire d'??criture/lecture de donn??es
    	MySampleFileIO genIO = new MySampleFileIO("sensor.txt", true);
    	
    	Instant now = Instant.now();
    	
    	// Extraction des ??chantillons de mesures sur une fen??tre de temps [now-24h,now]
        List<Sample> samples = genIO.readSample(now.minus(1, Instant.CHRONO_UNIT_DAYS), now);
        genIO.close();
        
        System.out.println("Graphique done !");
        // Cr??ation d'un ensemble de donn??es avec une precision ?? la minutes
        DataSet hum = new DataSet("Humidite", DataSet.PRECISION_SECONDS);
        // Iteration sur tous les ??chantillons dans l'ordre chroissant
        for (Sample sample : samples) {
        	// ajout d'un point (x,y)  ?? l'ensemble de donn??es correspondant ?? l'??chantillon 
        	// les coordonn??es sont x = la date en milli seconde,  y = la mesure d'humidit?? 
            hum.addPoint(new Date(sample.getTime().toEpochMilli()), sample.getHumidity());
        }
        // Cr??ation du graphique au d??part de l'ensemble des points (x,y)
        Chart chart = new Chart("Humidite sur les dernieres 24 heures", hum);
        // Sauvegarde du graphique sous forme de fichier image png avec une r??solution 1200x400
        chart.saveChartAsPNG("dayHumGraph.png", 1200, 400);
    }
    
    /**
   	* Méthode permettant de fournir un graphique de la temperature selon les jours.
   	* 
   	* @pre : None
   	* 
   	* @author Gauthier Fossion, Melvin Campos Casares, Pablo Wauthelet, Crispin Mutani.
   	*/
       
    public static void GraphTempDay() throws IOException, InterruptedException {
    	
    	// Cr??ation d'un utilitaire d'??criture/lecture de donn??es
    	MySampleFileIO genIO = new MySampleFileIO("sensor.txt", true);
    	
    	Instant now = Instant.now();
    	
    	// Extraction des ??chantillons de mesures sur une fen??tre de temps [now-24h,now]
        List<Sample> samples = genIO.readSample(now.minus(1, Instant.CHRONO_UNIT_DAYS), now);
        genIO.close();
        
        System.out.println("Graphique done !");
        // Cr??ation d'un ensemble de donn??es avec une precision ?? la minutes
        DataSet hum = new DataSet("Temperature", DataSet.PRECISION_SECONDS);
        // Iteration sur tous les ??chantillons dans l'ordre chroissant
        for (Sample sample : samples) {
        	// ajout d'un point (x,y)  ?? l'ensemble de donn??es correspondant ?? l'??chantillon 
        	// les coordonn??es sont x = la date en milli seconde,  y = la mesure d'humidit?? 
            hum.addPoint(new Date(sample.getTime().toEpochMilli()), sample.getTemperature());
        }
        // Cr??ation du graphique au d??part de l'ensemble des points (x,y)
        Chart chart = new Chart("Temperature sur les dernieres 24 heures", hum);
        // Sauvegarde du graphique sous forme de fichier image png avec une r??solution 1200x400
        chart.saveChartAsPNG("dayTempGraph.png", 1200, 400);
    }
    
    /**
   	* Méthode permettant de fournir un graphique de l'humidité selon les semaines.
   	* 
   	* @pre : None
   	* 
   	* @author Gauthier Fossion, Melvin Campos Casares, Pablo Wauthelet, Crispin Mutani.
   	*/
    
    public static void GraphHumWeek() throws IOException, InterruptedException {
    	
    	// Cr??ation d'un utilitaire d'??criture/lecture de donn??es
    	MySampleFileIO genIO = new MySampleFileIO("sensor.txt", true);
    	
    	Instant now = Instant.now();
    	
    	// Extraction des ??chantillons de mesures sur une fen??tre de temps [now-24h,now]
        List<Sample> samples = genIO.readSample(now.minus(1, Instant.CHRONO_UNIT_WEEKS), now);
        genIO.close();
        
        System.out.println("Graphique done !");
        // Cr??ation d'un ensemble de donn??es avec une precision ?? la minutes
        DataSet hum = new DataSet("Humidite", DataSet.PRECISION_SECONDS);
        // Iteration sur tous les ??chantillons dans l'ordre chroissant
        for (Sample sample : samples) {
        	// ajout d'un point (x,y)  ?? l'ensemble de donn??es correspondant ?? l'??chantillon 
        	// les coordonn??es sont x = la date en milli seconde,  y = la mesure d'humidit?? 
            hum.addPoint(new Date(sample.getTime().toEpochMilli()), sample.getHumidity());
        }
        // Cr??ation du graphique au d??part de l'ensemble des points (x,y)
        Chart chart = new Chart("Humidity sur la derniere semaine", hum);
        // Sauvegarde du graphique sous forme de fichier image png avec une r??solution 1200x400
        chart.saveChartAsPNG("weekHumGraph.png", 1200, 400);
    }
    
    /**
   	* Méthode permettant de fournir un graphique de la temperature selon les semaines.
   	* 
   	* @pre : None
   	* 
   	* @author Gauthier Fossion, Melvin Campos Casares, Pablo Wauthelet, Crispin Mutani.
   	*/
       
    public static void GraphTempWeek() throws IOException, InterruptedException {
    	
    	// Cr??ation d'un utilitaire d'??criture/lecture de donn??es
    	MySampleFileIO genIO = new MySampleFileIO("sensor.txt", true);
    	
    	Instant now = Instant.now();
    	
    	// Extraction des ??chantillons de mesures sur une fen??tre de temps [now-24h,now]
        List<Sample> samples = genIO.readSample(now.minus(1, Instant.CHRONO_UNIT_WEEKS), now);
        genIO.close();
        
        System.out.println("Graphique done !");
        // Cr??ation d'un ensemble de donn??es avec une precision ?? la minutes
        DataSet hum = new DataSet("Temperature", DataSet.PRECISION_SECONDS);
        // Iteration sur tous les ??chantillons dans l'ordre chroissant
        for (Sample sample : samples) {
        	// ajout d'un point (x,y)  ?? l'ensemble de donn??es correspondant ?? l'??chantillon 
        	// les coordonn??es sont x = la date en milli seconde,  y = la mesure d'humidit?? 
            hum.addPoint(new Date(sample.getTime().toEpochMilli()), sample.getTemperature());
        }
        // Cr??ation du graphique au d??part de l'ensemble des points (x,y)
        Chart chart = new Chart("Temperature sur la derniere semaine", hum);
        // Sauvegarde du graphique sous forme de fichier image png avec une r??solution 1200x400
        chart.saveChartAsPNG("weekTempGraph.png", 1200, 400);
    }

    /**
   	* Méthode permettant de predire la temperature pour l'heure à venir.
   	* 
   	* @pre : None
   	* 
   	* @author Gauthier Fossion, Melvin Campos Casares, Pablo Wauthelet, Crispin Mutani.
   	*/
       
    public static double PredTemp() throws IOException, InterruptedException {
    	
    	// Cr??ation d'un utilitaire d'??criture/lecture de donn??es
    	MySampleFileIO genIO = new MySampleFileIO("sensor.txt", true);
    	// Sauvegarde du temps (Instant) pr??sent
    	Instant now = Instant.now();
    	
    	// Lecture des ??chantillons de temp??ratures et humidit??s
    	List<Sample> points = genIO.readSample(now.minus(7, Instant.CHRONO_UNIT_DAYS),now);
    	
    	// Fermeture du fichier
    	genIO.close();
    	
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
        return temperatureNextHour;
       
    }

    /**
   	* Méthode permettant de fournir des alertes en cas d'atteinte des plafons définis
   	* 
   	* @pre : None
   	* 
   	* @author Gauthier Fossion, Melvin Campos Casares, Pablo Wauthelet, Crispin Mutani.
   	*/
    
    public static void Alerte()
    {
    	 try
         {
             int i = 0;
             while (i != 1)
             {
            	 Thread.sleep (300000);
            	 try 
					{
            	 	if(temp > tempMax)
					{
						 client.pushNote("ALERTE", "Temperature trop haute : "+temp);
					}
					
					if(temp < tempMin)
					{
						 client.pushNote("ALERTE", "Temperature trop basse : "+temp);
					}
					
					if(hum > humMax)
					{
						 client.pushNote("ALERTE", "Taux d'humidite trop haut : "+hum);
					}
					
					if(hum < humMin)
					{
						 client.pushNote("ALERTE", "Taux d'humidite trop bas : "+hum);
					}
					}
					catch (Exception ex) 
					{
						System.err.println(ex);
						System.exit(-1);
					}
             }
         }
         catch (InterruptedException exception){}
     }
   
}