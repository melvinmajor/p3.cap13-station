package student.examples;


import java.io.IOException;
import pushbullet.PushListener;
import pushbullet.PushbulletClient;


/**
 * @author Maxime Piraux, maxime.piraux@student.uclouvain.be
 * @author Pierre Schaus, pierre.schaus@uclouvain.be
 */
public class StudentDemoPushBulletAnsweringQuestions {

    public static void main(String[] args) throws IOException {
        // Cr??ation du client Pushbullet.
        final PushbulletClient client = new PushbulletClient("xxx pushbullet key xxx");
        // Ajout d'un listener en instanciant une classe anonyme impl??mentant PushListener.
        // Un PushListener verra sa m??thode pushReceived appel??e lorsque l'utilisateur 
        // ??crit un message sur pushBullet  
        client.addListener(new PushListener() {
            @Override
            public void pushReceived(String title, String body) {
            	// si le message contient un "?" ...
                if (body.contains("?")) {
                    try {
                    	// ... alors on r??pond avec une r??ponse simple ;-)
                        client.pushNote("I have the answer !", "42");
                        
                        // notez que client.pushFile permet d'envoyer des fichiers
                        // par exemple client.pushFile("dayGraph.png", null);
                    } catch (IOException ex) {
                        System.err.println(ex);
                        System.exit(-1);
                    }
                }
            }
        });

    }
}