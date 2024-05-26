package model.utils;

import model.CellModel;

import java.util.HashSet;
import java.util.Set;

public class CycleDetector {

    public static boolean hasCycle(CellModel cell) {
        Set<CellModel> visited = new HashSet<>();
        Set<CellModel> stack = new HashSet<>();
        return hasCycleHelper(cell, visited, stack);
    }

    private static boolean hasCycleHelper(CellModel cell, Set<CellModel> visited, Set<CellModel> stack) {
        if (stack.contains(cell)) {
            return true; // cycle detected
        }
        if (visited.contains(cell)) {
            return false; // already visited
        }

        visited.add(cell);
        stack.add(cell);

        for (CellModel dependency : cell.getDependsOnMe()) {
            if (hasCycleHelper(dependency, visited, stack)) {
                return true;
            }
        }

        stack.remove(cell);
        return false;
    }
}
