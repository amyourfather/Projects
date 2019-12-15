package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Yu Jia Xu
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        _setting = 0;
        for (int i = 0; i < cycles.length(); i++) {
            if (cycles.charAt(i) == '(') {
                i++;
                while (cycles.charAt(i) != ')') {
                    if (!_alphabet.contains(cycles.charAt(i))) {
                        throw new EnigmaException("not in alphabet");
                    }
                    i++;
                }
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles = _cycles + cycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int real = wrap(p + _setting);
        char alp = alphabet().toChar(real);
        int ind = _cycles.indexOf(alp);
        if (ind != -1 && !_cycles.equals("")) {
            if (_cycles.charAt(ind + 1) != ')') {
                return wrap(alphabet().toInt
                        (_cycles.charAt(ind + 1)) - _setting);
            } else {
                while (_cycles.charAt(ind) != '(') {
                    ind--;
                }
                return wrap(alphabet().toInt
                        (_cycles.charAt(ind + 1)) - _setting);
            }
        }
        return wrap(real - _setting);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int real = wrap(c +  _setting);
        char alp = alphabet().toChar(real);
        int ind = _cycles.indexOf(alp);
        if (ind != -1) {
            if (_cycles.charAt(ind - 1) != '(') {
                return wrap(alphabet().toInt
                        (_cycles.charAt(ind - 1)) - _setting);
            } else {
                while (_cycles.charAt(ind) != ')') {
                    ind++;
                }
                return wrap(alphabet().toInt
                        (_cycles.charAt(ind - 1)) - _setting);
            }
        }
        return wrap(real - _setting);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return alphabet().toChar(permute(alphabet().toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return alphabet().toChar(invert(alphabet().toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _cycles.length(); i++) {
            if (_cycles.charAt(i) == '(') {
                if (_cycles.charAt(i + 2) == ')') {
                    return false;
                }
            } else {
                if (_cycles.charAt(i) != ')'
                        && !alphabet().contains(_cycles.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** Cycles in this permutation. */
    private String _cycles;
    /** setting of this permutation. */
    private int _setting;

    /** Setting the SET. */
    void setsetting(int set) {
        _setting = set;
    }

    /** Return the setting of this permutation. */
    int getsetting() {
        return _setting;
    }
}
