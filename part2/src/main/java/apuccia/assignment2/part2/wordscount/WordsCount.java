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
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Alessandro Puccia
 */
public class WordsCount extends MapReduce<String, List<String>, String, Integer, Stream<Pair<String, Integer>>> {

    @Override
    protected Stream<Pair<String, List<String>>> read(Path fileName) throws IOException {
        
        Reader reader = new Reader(fileName);
        return reader.read();
    }
    
    @Override
    protected Stream<Pair<String, Integer>> map(
            Stream<Pair<String, List<String>>> readPairs) {
        
        return readPairs.flatMap(
            filePair -> filePair.getValue().stream().flatMap(
                line -> Arrays.stream(line.replaceAll("[^a-zA-Z\\s]", " ").
                                            toLowerCase().
                                            split(" ")).
                        filter(word -> word.length() > 3).
                        collect(Collectors.groupingBy(Function.identity(), 
                                Collectors.summingInt(x -> 1))).
                        entrySet().
                        stream().
                        map(entry -> new Pair<>(entry.getKey(), entry.getValue()))));
    }

    

    @Override
    protected int compare(String s1, String s2) {
        return s1.compareTo(s2);
    }

    @Override
    protected Stream<Pair<String, Integer>> reduce(
            Stream<Pair<String, List<Integer>>> groupedPairs) {
        
        return groupedPairs.map(pair -> new Pair(pair.getKey(), pair.getValue().
                stream().mapToInt(Integer::intValue).sum()));
    }

    @Override
    protected void write(Stream<Pair<String, Integer>> output, Path fileName)
            throws IOException {
        Writer.write(new File(fileName.toString()), output);
    }
}
