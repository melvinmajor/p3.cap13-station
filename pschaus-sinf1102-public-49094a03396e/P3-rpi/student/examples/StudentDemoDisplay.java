package student.examples;

import display.Display;
import display.Frame;
import display.FrameManager;

import java.io.IOException;


/**
 * @author Maxime Piraux, maxime.piraux@student.uclouvain.be
 * @author Pierre Schaus, pierre.schaus@uclouvain.be
 */
public class StudentDemoDisplay {
	
	/**
	 * Cr??e une fen??tre qui affiche toujours le m??me texte en argument
	 * @param txt
	 * @return
	 */
	public static Frame createFrame(String txt) {
	    final String msg = txt;
    	return new Frame() {
            @Override
            public String getText() {
            	return msg;
            }
    	};
	}
	
	static int x = 0;
	static int y = 0;		
			

    public static void main(String[] args) throws IOException {
 	
    	//Creation de 4 Frame's
    	
    	final Frame [][] frames = new Frame[][] {
    			{ createFrame("(0,0)"), createFrame("(0,1)"), createFrame("(0,2)")},
    			{ createFrame("(1,0)"), createFrame("(1,1)"), createFrame("(1,2)")},
    			{ createFrame("(2,0)"), createFrame("(2,1)"), createFrame("(2,2)")}
    		
    	};


        // Cr??ation du display en instanciant une classe anonyme impl??mentant FrameManager.
    	// La m??thode getFrame est appel??e ?? chaque fois que l'utilisateur pousse sur une 
    	// touche "fl??che" de son clavier. Le param??tre movement permet de savoir sur quelle 
    	// fl??che ils s'agit.
        Display display = new Display(new FrameManager() {
            @Override
            public Frame getFrame(int movement) {
            	// le movement indique sur quelle fleche du clavier l'utilisateur a pouss??
            	// on adapte x, y pour naviger dans la matrice des ??crans
            	switch (movement) {
				case 0: // haut
					y = (3 + (y - 1)) % 3;
					break;
				case 1: // bas
					y = (y + 1) % 3; 
					break;
				case 2: // gauche
					x = (3 + (x - 1)) % 3;
					break;
				case 3: // droite
					x = (x + 1) % 3;
					break;
				default:
					// ecran par defaut
					x = 0;
					y = 0;
					break;
				}
            	// on retourne l'??cran de la position courante
                return frames[x][y];
            }
        });
    }
}