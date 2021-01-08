/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apuccia.assignment2.part2.invertedindex;

import apuccia.assignment2.part2.framework.MapReduce;
import apuccia.assignment2.part2.utils.Pair;
import apuccia.assignment2.part2.utils.Reader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Alessandro Puccia
 */
public class InvertedIndex extends MapReduce<String, List<String>, String, Pair<String, Integer>, Stream<Pair<String, Pair<String, Integer>>>>{

    @Override
    protected Stream<Pair<String, List<String>>> read(Path fileName)
            throws IOException {
        
        Reader reader = new Reader(fileName);
        return reader.read();
    }

    @Override
    protected Stream<Pair<String, Pair<String, Integer>>> map(
            Stream<Pair<String, List<String>>> readPairs) {
        
        return readPairs.flatMap(
                filePair -> filePair.getValue().stream().flatMap(
                    line -> Arrays.stream(line.replaceAll(
                                "[^a-zA-Z\\s]", " ").toLowerCase().split(" ")).filter(
                        word -> word.length() > 3).map(
                            word -> new Pair<>(word, new Pair(filePair.getKey(), filePair.getValue().indexOf(line))))));
    }

    @Override
    protected int compare(String s1, String s2) {
        return s1.compareTo(s2);
    }

    @Override
    protected Stream<Pair<String, Pair<String, Integer>>> reduce(
            Stream<Pair<String, List<Pair<String, Integer>>>> groupedPairs) {
        return groupedPairs.flatMap(
            filePair -> filePair.getValue().stream().map(
                wordPair -> new Pair<>(filePair.getKey(), new Pair(wordPair.getKey(), wordPair.getValue()))));
    }

    @Override
    protected void write(
            Stream<Pair<String, Pair<String, Integer>>> output, Path fileName)
            throws IOException {
        PrintStream ps = new PrintStream(new File(fileName.toString()));
        output.forEach(p -> ps.println(p.getKey() + ", " + p.getValue().getValue() + ", " + p.getValue().getKey()));
        ps.close();
    }
}
