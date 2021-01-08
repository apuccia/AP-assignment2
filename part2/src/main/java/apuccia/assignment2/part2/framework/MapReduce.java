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
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 *
 * @author Alessandro Puccia
 */
public abstract class MapReduce<K, V, MK, MV, O> {
    protected abstract Stream<Pair<K, V>> read(Path fileName) throws IOException;
    protected abstract Stream<Pair<MK, MV>> map(Stream<Pair<K, V>> readPairs);
    protected Stream<Pair<MK, List<MV>>> group(Stream<Pair<MK, MV>> mappedPairs) {
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
    
    protected abstract O reduce(Stream<Pair<MK, List<MV>>> groupedPairs);
    protected abstract void write(O output, Path fileName) throws IOException;
    
    protected abstract int compare(MK s1, MK s2);
    
    public final void templateMethod(Path inFile, Path outFile) throws IOException {
        Stream<Pair<K, V>> readPairs = read(inFile);
        Stream<Pair<MK, MV>> mappedPairs = map(readPairs);
        
        Stream<Pair<MK, List<MV>>> groupedPairs = group(mappedPairs);
        
        O output = reduce(groupedPairs);
        write(output, outFile);
    }
}
