/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apuccia.assignment2.part2.wordscount;

import apuccia.assignment2.part2.framework.MapReduce;
import apuccia.assignment2.part2.utils.Pair;
import apuccia.assignment2.part2.utils.Reader;
import apuccia.assignment2.part2.utils.Writer;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Alessandro Puccia
 */
public class WordsCount extends MapReduce<String, List<String>, String, Integer, Pair<String, Integer>> {

    @Override
    protected Stream<? extends Pair<String, List<String>>> read(Path fileName) throws IOException {
        
        Reader reader = new Reader(fileName);
        
        return reader.read();
    }

    @Override
    protected Stream<? extends Pair<String, Integer>> map(
            Stream<? extends Pair<String, List<String>>> readPairs) {
        return null;
    }

    @Override
    protected Pair<String, Integer> reduce(
            Stream<? extends Pair<String, List<Integer>>> groupedPairs) {
        return null;
    }

    @Override
    protected void write(Pair<String, Integer> output, Path fileName) throws
            IOException {
        
    }

    @Override
    protected int compare(String s1, String s2) {
        return s1.compareTo(s2);
    }
}
