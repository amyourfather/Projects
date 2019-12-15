package tablut;
import java.util.ArrayList;
import static java.lang.Math.*;
import static tablut.Piece.*;

/** A Player that automatically generates moves.
 *  @author Yu Jia Xu
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;
    /** the score of black. */
    private static final int EACH_BLACK = -10;
    /** the score of white. */
    private static final int EACH_WHITE = 5;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        /** Return either a String denoting either a legal move for me
         *  or another command (which may be invalid).  Always returns the
         *  latter if board().turn() is not myPiece() or if board.winner()
         *  is not null. */
        Move mv = findMove();
        _controller.reportMove(mv);
        return mv.toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        int sense;
        if (b.turn() == BLACK) {
            sense = -1;
        } else {
            sense = 1;
        }
        findMove(b, 3, true, sense, -1 * INFTY, INFTY);
        System.out.println(_lastFoundMove);
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        ArrayList<Move> lastMoves = new ArrayList<>();
        if (depth == 0 || board.winner() != null) {
            return staticScore(board, depth);
        }
        int bestsofar = -1 * sense * INFTY;
        if (sense == 1) {
            for (Move mo : board.legalMoves(board.turn())) {
                board.makeMove(mo);
                int response = findMove(board, depth - 1,
                        false, -1 * sense, alpha, beta);
                if (bestsofar <= response) {
                    if (bestsofar < response) {
                        lastMoves.clear();
                    }
                    lastMoves.add(mo);
                    bestsofar = response;
                }
                alpha = max(alpha, response);
                if (beta <= alpha) {
                    board.undo();
                    break;
                }
                board.undo();
            }
            if (saveMove) {
                _lastFoundMove = lastMoves.get(
                        _controller.randInt(lastMoves.size()));
            }
        } else {
            for (Move mo : board.legalMoves(board.turn())) {
                board.makeMove(mo);
                int response = findMove(board, depth - 1,
                        false, -1 * sense, alpha, beta);
                if (bestsofar >= response) {
                    if (bestsofar > response) {
                        lastMoves.clear();
                    }
                    lastMoves.add(mo);
                    bestsofar = response;

                }
                beta = min(beta, response);
                if (beta <= alpha) {
                    board.undo();
                    break;
                }
                board.undo();
            }
            if (saveMove) {
                _lastFoundMove = lastMoves.get(
                        _controller.randInt(lastMoves.size()));
            }
        }
        return bestsofar;
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {
        return 4;
    }

    /** Return a heuristic value for BOARD and DEPTH. */
    private int staticScore(Board board, int depth) {
        if (board.winner() == WHITE) {
            return WINNING_VALUE + depth;
        } else if (board.winner() == BLACK) {
            return -1 * WINNING_VALUE - depth;
        } else {
            return board.numblack() * EACH_BLACK
                    + board.numwhite() * EACH_WHITE;
        }
    }

}
