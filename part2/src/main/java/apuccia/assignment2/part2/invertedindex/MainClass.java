/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apuccia.assignment2.part2.invertedindex;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alessandro Puccia
 */
public class MainClass {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        InvertedIndex invertedIndex = new InvertedIndex();
        
        try {
            invertedIndex.templateMethod(Path.of("books"), Path.of("invertedindexresult.csv"));
        } catch (IOException ex) {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }
    
}
