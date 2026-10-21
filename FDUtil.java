import java.util.HashSet;
import java.util.Set;

/**
 * This utility class is not meant to be instantitated, and just provides some
 * useful methods on FD sets.
 * 
 * @author David
 * @version 5/18/2022
 */
public final class FDUtil {
  /**
   * Generates all trivial FDs over the given attribute set.
   * FDs with empty right-hand sides are omitted.
   *
   * @param attrs the relation's attributes; must not be modified
   * @return a new set containing all trivial FDs over the given attributes
   */
  public static FDSet trivial(final Set<String> attrs) {
    FDSet result = new FDSet();

    for (Set<String> left : FDUtil.powerSet(attrs)) {
      if (!left.isEmpty()) {
        for (Set<String> right : FDUtil.powerSet(left)) {
          if (!right.isEmpty()) {
            result.add(new FD(left, right));
          }
        }
      }
    }

    return result;
  }

  /**
   * Augments every FD in the given set of FDs with the given attributes
   * 
   * @param fdset FD Set (Immutable)
   * @param attrs a set of attributes with which to augment FDs (Immutable)
   * @return a set of augmented FDs
   */
  public static FDSet augment(final FDSet fdset, final Set<String> attrs) {
    FDSet result = new FDSet();
    for (FD fd : fdset) {
      FD newFD = new FD(fd);
      newFD.addToLeft(attrs);
      newFD.addToRight(attrs);
      result.add(newFD);
    }
    return result;
  }

  /**
   * Exhaustively resolves transitive FDs with respect to the given set of FDs
   * 
   * @param fdset (Immutable) FD Set
   * @return all transitive FDs with respect to the input FD set
   */
  public static FDSet transitive(final FDSet fdset) {
    int last;
    FDSet result = new FDSet();
    do {
      last = result.size();

      // take the union between the result and the given fdset
      FDSet union = new FDSet(fdset);
      union.addAll(result);

      // examine every pair of FDs in the union and check for transitivity
      for (FD alpha : union) {
        for (FD beta : union) {
          if (alpha != beta && alpha.getRight().containsAll(beta.getLeft())) {
            result.add(new FD(alpha.getLeft(), beta.getRight()));
          }
        }
      }
    } while (result.size() != last);
    return result;
  }

  /**
   * Generates the closure of the input FD set, omitting FDs with empty
   * right-hand sides. The relation's attributes are assumed to be exactly
   * those appearing on either side of the input FDs.
   *
   * @param fdset input FD set; must not be modified
   * @return a new FD set containing the closure
   */
  public static FDSet fdSetClosure(final FDSet fdset) {
    FDSet current = new FDSet(fdset);

    // Collect all attributes appearing in the input FDs.
    Set<String> attrs = new HashSet<>();
    for (FD fd : fdset) {
      attrs.addAll(fd.getLeft());
      attrs.addAll(fd.getRight());
    }

    Set<Set<String>> subsets = FDUtil.powerSet(attrs);

    // Add all trivial FDs once.
    current.addAll(FDUtil.trivial(attrs));

    // Repeat augmentation and transitivity until no new FDs are found.
    int last;
    do {
      last = current.size();

      for (Set<String> subset : subsets) {
        current.addAll(FDUtil.augment(current, subset));
      }

      current.addAll(FDUtil.transitive(current));
    } while (current.size() != last);

    return current;
  }

  /**
   * Generates all subsets of the input set, including the empty subset.
   *
   * @param set input set; must not be modified
   * @return the power set of the input set
   */
  public static <E> Set<Set<E>> powerSet(final Set<E> set) {
    if (set.isEmpty()) {
      Set<Set<E>> basePset = new HashSet<>();
      basePset.add(new HashSet<>());
      return basePset;
    }

    // Choose an element and remove it from a copy.
    E first = set.iterator().next();
    Set<E> reduced = new HashSet<>(set);
    reduced.remove(first);

    // Generate subsets without the chosen element.
    Set<Set<E>> currentPset = FDUtil.powerSet(reduced);

    // Generate new subsets containing the chosen element.
    Set<Set<E>> otherPset = new HashSet<>();
    for (Set<E> subset : currentPset) {
      Set<E> copy = new HashSet<>(subset);
      copy.add(first);
      otherPset.add(copy);
    }

    currentPset.addAll(otherPset);
    return currentPset;
  }
}