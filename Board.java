package tablut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Formatter;
import static tablut.Move.ROOK_MOVES;
import static tablut.Piece.*;
import static tablut.Square.*;



/** The state of a Tablut Game.
 *  @author Yu Jia Xu
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 9;

    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
        NTHRONE = sq(4, 5),
        STHRONE = sq(4, 3),
        WTHRONE = sq(3, 4),
        ETHRONE = sq(5, 4);

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
        sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
        sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
        sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
        sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        init();
        _previous.clear();
        _previous.addAll(model._previous);
        _moveCount = model.moveCount();
        _turn = model.turn();
        _winner = model.winner();
        _limit = model.limit();
        _numblack = numblack();
        _numwhite = numwhite();
        for (Square sq : SQUARE_LIST) {
            _board.put(sq, model.get(sq));
        }
    }


    /** Clears the board to the initial position. */
    void init() {
        _board = new HashMap<>();
        _previous = new ArrayList<>();


        for (Square initialAttacker : INITIAL_ATTACKERS) {
            _board.put(initialAttacker, BLACK);
        }

        for (int i = 0; i < INITIAL_DEFENDERS.length; i++) {
            _board.put(INITIAL_DEFENDERS[i], WHITE);
        }

        for (Square sq : SQUARE_LIST) {
            if (!_board.containsKey(sq)) {
                _board.put(sq, EMPTY);
            }
        }
        _turn = BLACK;
        _board.put(THRONE, KING);
        _previous.add(encodedBoard());
        _moveCount = 0;
        _winner = null;
        _limit = Integer.MAX_VALUE;
        _numblack = getnumberpiece('B');
        _numwhite = getnumberpiece('W');

    }


    /** Set the move limit to LIM.  It is an error if 2*LIM <= moveCount(). */
    void setMoveLimit(int lim) {
        if (2 * lim <= moveCount()) {
            throw new IllegalArgumentException();
        }
        _limit = 2 * lim;
    }
    /** Return the move limit. */
    int limit() {
        return _limit;
    }
    /** Return an integer representing the number of black
     *  piece. */
    int numblack() {
        return _numblack;
    }
    /** Return an integer representing the number of white
     *  piece not including with king. */
    int numwhite() {
        return _numwhite;
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        return _repeated;
    }

    /** Return current position and set winner() next mover if the current
     *  position is a repeat. */
    boolean checkRepeated() {
        String currentPos = _previous.remove(_previous.size() - 1);
        if (_previous.contains(currentPos)) {
            _winner = _turn;
            _previous.add(currentPos);
            return true;
        } else {
            _previous.add(currentPos);
            return false;
        }

    }

    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _moveCount;
    }

    /** Return location of the king. */
    Square kingPosition() {
        for (Square sq : SQUARE_LIST) {
            Piece p = get(sq);
            if (p == KING) {
                return sq;
            }
        }
        return null;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return _board.get(sq(col, row));
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        _board.put(s, p);
    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
        if (!from.isRookMove(to)) {
            return false;
        }
        int dir = from.direction(to);
        int steps = 1;
        Square sq = from.rookMove(dir, steps);
        while (sq != to) {
            if (get(sq) != EMPTY) {
                return false;
            }
            steps++;
            sq = from.rookMove(dir, steps);
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return get(from).side() == _turn;
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {
        if (get(to) != EMPTY || (to == THRONE && (get(from) != KING))) {
            return false;
        }
        if (!isLegal(from)) {
            return false;
        }
        return isUnblockedMove(from, to);

    }

    /** Return true iff FROM-TO is a valid move,
     * regardless of the turn. */
    boolean isLegalNoTurn(Square from, Square to) {
        if (get(to) != EMPTY || (to == THRONE && (get(from) != KING))) {
            return false;
        }
        return isUnblockedMove(from, to);
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        assert isLegal(from, to);
        Piece p = get(from);
        put(p, to);
        put(EMPTY, from);
        capAll(to);
        _moveCount++;
        _turn = _turn.opponent();
        _previous.add(encodedBoard());
        if (checkRepeated()) {
            return;
        }
        if (kingPosition() == null) {
            _winner = BLACK;
            return;
        }
        if (kingPosition().isEdge()) {
            _winner = WHITE;
            return;
        }
        if (!hasMove(_turn.side())) {
            _winner = _turn.opponent();
        }
        if (moveCount() > _limit) {
            _winner = _turn;
        }


    }



    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece
     * just moved to SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        Square sq1 = sq0.between(sq2);
        removePiece(sq1);
    }

    /** Remove the piece at square SQ on the board. */
    void removePiece(Square sq) {
        put(EMPTY, sq);
    }

    /** Return true iff OTHER containing enemy piece to SQ.
     * Assuming SQ and OTHER are not null. */
    boolean isEnemy(Square sq, Square other) {
        Piece p1 = get(sq);
        Piece p2 = get(other);
        if (p2 == EMPTY) {
            return false;
        }
        return p1.side() != p2.side();
    }

    /** Return true iff throne is empty. */
    boolean isThroneEmpty() {
        return get(THRONE) == EMPTY;
    }

    /** Return true iff Throne is surrounded by > 3 black. */
    boolean isThroneSurrounded() {
        int count = 0;
        if (get(NTHRONE) == BLACK) {
            count++;
        }
        if (get(STHRONE) == BLACK) {
            count++;
        }
        if (get(ETHRONE) == BLACK) {
            count++;
        }
        if (get(WTHRONE) == BLACK) {
            count++;
        }
        return count > 2;
    }


    /** Return true iff OTHER is hostile to SQ. */
    boolean isHostile(Square sq, Square other) {
        if (isEnemy(sq, other)) {
            return true;
        } else if (other == THRONE && isThroneEmpty()) {
            return true;
        } else {
            return (other == THRONE && !isThroneEmpty()
                    && isThroneSurrounded());
        }
    }

    /** Return true iff capture conditions are met for SQ and OTHER. */
    boolean isCapturable(Square sq, Square other) {
        if (other == null) {
            return false;
        } else {
            Square between = sq.between(other);
            Piece side = get(between);
            if (side == KING
                    && (between == THRONE || between == NTHRONE
                        || between == WTHRONE
                        || between == STHRONE || between == ETHRONE)) {
                Square diag1 = sq.diag1(between);
                Square diag2 = sq.diag2(between);
                return (isHostile(between, sq) && isHostile(between, other)
                    && isHostile(between, diag1) && isHostile(between, diag2));
            }
            if (isHostile(between, sq) && isHostile(between, other)) {
                return true;
            }
            return false;
        }
    }


    /** Capture all enemy pieces adjacent to SQ. */
    void capAll(Square sq) {
        for (int i = 0; i < 4; i++) {
            Square other = sq.rookMove(i, 2);
            if (isCapturable(sq, other)) {
                capture(sq, other);
            }
        }
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            undoPosition();
            _moveCount--;
        }
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        _winner = null;
        _repeated = false;
        _previous.remove(_previous.size() - 1);
        String state = _previous.get(_previous.size() - 1);
        char piece = state.charAt(0);
        if (piece == 'B') {
            _turn = BLACK;
        } else {
            _turn = WHITE;
        }
        int index = 1;
        for (Square sq : SQUARE_LIST) {
            piece = state.charAt(index);
            if (piece == 'B') {
                _board.put(sq, BLACK);
            } else if (piece == 'W') {
                _board.put(sq, WHITE);
            } else if (piece == 'K') {
                _board.put(sq, KING);
            } else {
                _board.put(sq, EMPTY);
            }
            index++;
        }

    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        while (_moveCount > 0) {
            _moveCount--;
            _previous.remove(_previous.size() - 1);
        }
    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        List<Move> lst = new ArrayList<>();
        for (Square sq : SQUARE_LIST) {
            Piece p = get(sq);
            int index = sq.index();
            if (p.side() == side) {
                for (int d = 0; d < 4; d++) {
                    List<Move> L = ROOK_MOVES[index][d];
                    for (Move mv : L) {
                        boolean check;
                        if (get(mv.to()) != EMPTY || (mv.to()
                                == THRONE && (get(mv.from()) != KING))) {
                            check = false;
                        } else {
                            check = isUnblockedMove(mv.from(), mv.to());
                        }
                        if (check) {
                            lst.add(mv);
                        }
                    }
                }
            }
        }

        return lst;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        return !legalMoves(side).isEmpty();
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /** Return the locations of all pieces on SIDE. */
    private HashSet<Square> pieceLocations(Piece side) {
        assert side != EMPTY;
        HashSet<Square> L = new HashSet<>();
        for (Square sq : SQUARE_LIST) {
            Piece p = get(sq);
            if (p.side() == side) {
                L.add(sq);
            }
        }
        return L;
    }

    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }


    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or null if it has not been
     *  computed. */
    private Piece _winner;
    /** Number of (still undone) moves since initial position. */
    private int _moveCount;
    /** True when current board is a repeated position (ending the game). */
    private boolean _repeated;
    /** the BOARD that contains all piece locations. */
    private HashMap<Square, Piece> _board;
    /** the arraylist of all previous stage. especially when u make a
     * move then u save your current state to there. */
    private ArrayList<String> _previous;
    /** The limit of moves. */
    private int _limit;
    /** the number of black pieces. will use in the AI. */
    private int _numblack;
    /** the number of white pieces. will use in the AI. */
    private int _numwhite;


    /** Return the number of pieces in this board by character P where P
     * is the symbol of the piece. */
    public int getnumberpiece(char p) {
        String enco = encodedBoard();
        int result = 0;
        for (int i = 1; i < enco.length(); i++) {
            if (p == enco.charAt(i)) {
                result++;
            }
        }
        return result;
    }
}
