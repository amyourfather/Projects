package enigma;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Yu Jia Xu
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }
        _config = getInput(args[0]);
        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }
        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machine = readConfig();
        String plugboard = "";
        String msg = "";
        while (_input.hasNextLine()) {
            String temp = _input.nextLine();
            msg = "";
            if (temp.length() == 0) {
                _output.println();
            } else if (temp.charAt(0) == '*') {
                plugboard = "";
                machine.setPlugboard(null);
                String[] set = temp.split(" ");
                String[] therotors = new String[machine.numRotors()];
                System.arraycopy(set, 1, therotors, 0, machine.numRotors());
                machine.insertRotors(therotors);
                machine.setRotors(set[therotors.length + 1]);
                for (int i = therotors.length + 2; i < set.length; i++) {
                    plugboard += set[i];
                }
                machine.setPlugboard(new Permutation(plugboard, _alphabet));
            } else {
                String[] set = temp.split(" ");
                for (String ele: set) {
                    msg += ele;
                }
                printMessageLine(machine.convert(msg));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String first = _config.next();
            if (first.equals("")) {
                throw new EnigmaException("nothing");
            }
            _alphabet = new Alphabet(first);
            ArrayList<Rotor> allRotors = new ArrayList<Rotor>();
            int numRotors = _config.nextInt(), pawls = _config.nextInt();
            while (_config.hasNext()) {
                if (_nextconfig == null) {
                    _nextconfig = _config.next();
                }
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name;
            if (_nextconfig != null) {
                name = _nextconfig;
            } else {
                name = _config.next();
            }
            String style = _config.next();
            String cycle = "";
            while (_config.hasNext()) {
                String temp = _config.next();
                if (temp.charAt(0) == '(') {
                    cycle += temp;
                } else {
                    _nextconfig = temp;
                    break;
                }
            }
            Permutation P = new Permutation(cycle, _alphabet);
            if (style.charAt(0) == 'M') {
                return new MovingRotor(name, P, style.substring(1));
            } else if (style.charAt(0) == 'N') {
                return new FixedRotor(name, P);
            }
            return new Reflector(name, P);
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        M.setRotors(settings);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            _output.print(msg.charAt(i));
            if ((i + 1) % 5 == 0) {
                _output.print(" ");
            }
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
    /** String for next element for configuation. */
    private String _nextconfig = null;
}
