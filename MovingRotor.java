package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Yu Jia Xu
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    void advance() {
        set(setting() + 1);
    }
    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            if (permutation().alphabet().toChar
                    (setting()) == _notches.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    /** the notches in this moving rotor, maybe more than one character. */
    private String _notches;
}
