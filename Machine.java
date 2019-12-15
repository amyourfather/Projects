package enigma;

import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Yu Jia Xu
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _rotors = new Rotor[numRotors()];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        int mov = 0;
        if (rotors.length != numRotors()) {
            throw new EnigmaException("Wrong number rotor");
        }
        Rotor[] newrotors = new Rotor[numRotors()];
        int i = 0;
        for (String ele: rotors) {
            for (Rotor x: _allRotors) {
                if (x.name().equals(ele)) {
                    if (x instanceof MovingRotor) {
                        mov++;
                    }
                    if (i == 0 && !(x instanceof Reflector)) {
                        throw new EnigmaException("Reflector in wrong place");
                    }
                    for (int j = 1; j <= numRotors() - numPawls() - 1; j++) {
                        if (i == j) {
                            if (!(x instanceof FixedRotor)) {
                                throw new EnigmaException("FixedRotor wrong");
                            }
                        }
                    }

                    newrotors[i] = x;
                    i++;
                    break;
                }
            }
        }
        if (!(newrotors[i - 1] instanceof MovingRotor)) {
            throw new EnigmaException("1st not mov");
        }
        if (mov != _pawls) {
            throw new EnigmaException("pawls diff");
        }
        _rotors = newrotors;
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 1; i < _rotors.length; i++) {
            if (!_alphabet.contains(setting.charAt(i - 1))) {
                throw new EnigmaException("wrong setting");
            }
            this._rotors[i].set(setting.charAt(i - 1));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        int result = _plugboard.permute(c);
        boolean checknotch = _rotors[numRotors() - 1].atNotch();
        boolean previousadvance = true;
        _rotors[numRotors() - 1].advance();
        for (int i = numRotors() - 2; i >= 0; i--) {
            if (checknotch && previousadvance) {
                checknotch = _rotors[i].atNotch();
                _rotors[i].advance();
            } else {
                previousadvance = false;
            }
        }
        for (int i = numRotors() - 1; i >= 0; i--) {
            result = _rotors[i].convertForward(result);
        }
        for (int i = 1; i < numRotors(); i++) {
            result = _rotors[i].convertBackward(result);
        }
        result = _plugboard.permute(result);
        return result;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            char c = _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
            result += Character.toString(c);
        }
        return result;
    }

    /** Returns the plugboard. */
    Permutation getplugboard() {
        return _plugboard;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** the number of rotors. */
    private int _numRotors;
    /** number of pawls. */
    private int _pawls;
    /** all kinds of rotors that can be used. */
    private Collection<Rotor> _allRotors;
    /** plugboard that needs to be used. */
    private Permutation _plugboard = null;
    /** the rotors that will be used in this time. */
    private Rotor[] _rotors;
}
