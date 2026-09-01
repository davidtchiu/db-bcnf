import java.util.*;

public class Main {
  public static void main(String[] args) {
    /*
     * Examples from the DB theory slides
     */

    // U(A,B,C,D,E)
    Set<String> U = new HashSet<>(Set.of("A", "B", "C", "D", "E"));
    FD f1 = new FD(Set.of("A", "E"), Set.of("D")); // AE --> D
    FD f2 = new FD(Set.of("A", "B"), Set.of("C")); // AB --> C
    FD f3 = new FD(Set.of("D"), Set.of("B")); // D --> B
    FDSet fdsetU = new FDSet(f1, f2, f3);
    System.out.println("Final BCNF Schemas: " + Normalizer.BCNFDecompose(U, fdsetU));

    // R(A,B,C)
    Set<String> R = new HashSet<>(Set.of("A", "B", "C"));
    FD g1 = new FD(Set.of("A"), Set.of("B", "C")); // A --> BC
    FD g2 = new FD(Set.of("B"), Set.of("C")); // B --> C
    FD g3 = new FD(Set.of("A"), Set.of("B")); // A --> B
    FD g4 = new FD(Set.of("A", "B"), Set.of("C")); // AB --> C
    FDSet fdsetR = new FDSet(g1, g2, g3, g4);
    System.out.println("Final BCNF Schemas: " + Normalizer.BCNFDecompose(R, fdsetR));
  }
}
