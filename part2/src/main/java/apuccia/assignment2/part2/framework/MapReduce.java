/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apuccia.assignment2.part2.framework;

import apuccia.assignment2.part2.utils.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 *
 * @author Alessandro Puccia
 * @param <K> Type variable for the key that will be used in non-mapped pairs
 * @param <V> Type variable for the value that will be used in non-mapped pairs
 * @param <MK> Type variable for the key that will be used in mapped pairs,
 * allowing the possibility for the user to process and change also the key
 * @param <MV> Type variable for the value that will be used in mapped pairs
 * allowing the possibility for the user to process and change the value
 * @param <O> Type variable for the result of the operations' pipeline
 */
public abstract class MapReduce<K, V, MK, MV, O> {
    // hot spot
    protected abstract Stream<Pair<K, V>> read(Path fileName) throws IOException;
    // hot spot
    protected abstract Stream<Pair<MK, MV>> map(Stream<Pair<K, V>> readPairs);
    
    /**
     * hookup method that can be overloaded in the framework instance. In the
     * default implementation, the pairs are inserted in a ordered way inside a
     * treemap and grouped by equal keys appending the values in a list.
     */
    protected Stream<Pair<MK, Collection<MV>>> group(Stream<Pair<MK, MV>> mappedPairs) {
        Map<MK, List<MV>> groupedByEqualKeys = new TreeMap<>(this::compare);
        
        mappedPairs.forEach(pair -> {
            List<MV> keyValues = groupedByEqualKeys.get(pair.getKey());
            
            if (keyValues == null) {
                ArrayList newList = new ArrayList<MV>();
                newList.add(pair.getValue());
                
                groupedByEqualKeys.put(pair.getKey(), newList);
            }
            else {
                groupedByEqualKeys.get(pair.getKey()).add(pair.getValue());
            }
        });
        
        
        return groupedByEqualKeys.entrySet().stream().map(
                entry -> new Pair(entry.getKey(), entry.getValue()));
    }
    
    // hot spot
    protected abstract O reduce(Stream<Pair<MK, Collection<MV>>> groupedPairs);
    // hot spot
    protected abstract void write(O output, Path fileName) throws IOException;
    // hot spot 
    protected abstract int compare(MK s1, MK s2);
    
    // frozen spot that implements the operations' pipeline
    public final void templateMethod(Path inFile, Path outFile) throws IOException {
        Stream<Pair<K, V>> readPairs = read(inFile);
        Stream<Pair<MK, MV>> mappedPairs = map(readPairs);
        
        Stream<Pair<MK, Collection<MV>>> groupedPairs = group(mappedPairs);
        
        O output = reduce(groupedPairs);
        write(output, outFile);
    }
}
