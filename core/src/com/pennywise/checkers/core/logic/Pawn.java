package com.pennywise.checkers.core.logic;

import com.pennywise.checkers.core.logic.enums.CellEntry;
import com.pennywise.checkers.core.logic.enums.Owner;
import com.pennywise.checkers.core.logic.enums.Player;

import java.util.Vector;

/**
 * Created by Joshua.Nabongo on 10/7/2015.
 */
public class Pawn {


    private final Player player;
    private final Owner owner;
    private final CellEntry playerPawn;
    private final CellEntry opponentPawn;
    private final CellEntry opponentKing;
    private boolean allowBackstep;

    public Pawn(Player player, Owner owner, boolean allowBackstep) {
        this.player = player;
        this.owner = owner;
        this.allowBackstep = allowBackstep;

        playerPawn = player.equals(Player.black) ? CellEntry.black : CellEntry.white;
        opponentPawn = player.equals(Player.black) ? CellEntry.white : CellEntry.black;
        opponentKing = player.equals(Player.black) ? CellEntry.whiteKing : CellEntry.blackKing;
    }

    public Vector<Step> getForcedMoves(int r, int c, Board board) {
        Vector<Step> furtherCaptures = new Vector<Step>();

        if (board.cell[r][c].equals(playerPawn)) {
            if (forwardLeftCapture(r, c, board) != null)
                furtherCaptures.add(forwardLeftCapture(r, c, board));
            if (forwardRightCapture(r, c, board) != null)
                furtherCaptures.add(forwardRightCapture(r, c, board));
        }

        if (allowBackstep) {
            if (backwardLeftCapture(r, c, board) != null)
                furtherCaptures.add(backwardLeftCapture(r, c, board));
            if (backwardRightCapture(r, c, board) != null)
                furtherCaptures.add(backwardRightCapture(r, c, board));
        }

        return furtherCaptures;
    }

    public Vector<Step> calculateAllForcedMoves(Board board) {
        Vector<Step> forcedMoves = new Vector<Step>();


        // Scan across the board
        for (int r = 0; r < Board.rows; r++) {
            // Check only valid cols
            int c = (r % 2 == 0) ? 0 : 1;
            for (; c < Board.cols; c += 2) {
                assert (!board.cell[r][c].equals(CellEntry.inValid));

                // Forward Capture
                if (board.cell[r][c].equals(playerPawn)) {
                    // Boundary Condition for forward capture for black
                    if (r >= 2) {
                        // Forward Left Capture
                        if (forwardLeftCapture(r, c, board) != null)
                            forcedMoves.add(forwardLeftCapture(r, c, board));

                        // Forward Right Capture for black
                        if (forwardRightCapture(r, c, board) != null)
                            forcedMoves.add(forwardRightCapture(r, c, board));
                    }
                }
                // Backward Capture for Black King
                if (allowBackstep) {
                    // Boundary Condition for backward capture
                    if (r < Board.rows - 2) {
                        // Backward Left Capture for black king
                        if (backwardLeftCapture(r, c, board) != null)
                            forcedMoves.add(backwardLeftCapture(r, c, board));

                        // Backward Right Capture for black king
                        if (backwardRightCapture(r, c, board) != null)
                            forcedMoves.add(backwardRightCapture(r, c, board));
                    }
                }
            }
        }

        return forcedMoves;
    }

    /**
     * Returns a vector of all possible steps which Black can make at the state of the game given by board.
     * <p/>
     * Should only be called if no forced steps exist.
     *
     * @param board
     * @return
     */
    public Vector<Step> calculateAllNonForcedMoves(Board board) {

        Vector<Step> allNonForcedMoves = new Vector<Step>();

        if (owner == Owner.ROBOT) {
            // Scan across the board
            for (int r = 0; r < Board.rows; r++) {
                // Check only valid cols
                int c = (r % 2 == 0) ? 0 : 1;
                for (; c < Board.cols; c += 2) {
                    assert (!board.cell[r][c].equals(CellEntry.inValid));

                    // Forward Step for normal.
                    if (board.cell[r][c].equals(playerPawn)) {

                        Step step = null;
                        step = forwardLeftCapture(r, c, board);
                        assert (step == null);
                        step = forwardRightCapture(r, c, board);
                        assert (step == null);
                        step = forwardLeft(r, c, board);
                        if (step != null)
                            allNonForcedMoves.add(step);

                        step = forwardRight(r, c, board);

                        if (step != null)
                            allNonForcedMoves.add(step);

                    }

                    //Forward and Backward Step for black king piece.
                    if (allowBackstep) {
                        Step step = null;
                        step = backwardLeftCapture(r, c, board);
                        assert (step == null);
                        step = backwardRightCapture(r, c, board);
                        assert (step == null);
                    }
                }
            }
        } else {

            for (int r = 0; r < Board.rows; r++) {
                // Check only valid cols
                int c = (r % 2 == 0) ? 0 : 1;
                for (; c < Board.cols; c += 2) {
                    assert (!board.cell[r][c].equals(CellEntry.inValid));

                    // Forward Step for normal white piece.
                    if (board.cell[r][c].equals(playerPawn)) {

                        Step step = null;
                        step = forwardLeftCapture(r, c, board);
                        assert (step == null);
                        step = forwardRightCapture(r, c, board);
                        assert (step == null);

                        step = forwardLeft(r, c, board);
                        if (step != null) {
                            allNonForcedMoves.add(step);
                        }

                        step = forwardRight(r, c, board);
                        if (step != null) {
                            allNonForcedMoves.add(step);
                        }
                    }

                    //Forward and Backward Step for black king piece.
                    if (allowBackstep) {
                        Step step = null;

                        step = backwardLeft(r, c, board);
                        if (step != null) {
                            allNonForcedMoves.add(step);
                        }

                        step = backwardRight(r, c, board);
                        if (step != null) {
                            allNonForcedMoves.add(step);
                        }

                    }


                }
            }
        }
        return allNonForcedMoves;
    }

    private Step forwardLeft(int r, int c, Board board) {
        Step forwardLeft = null;

        if (owner == Owner.ROBOT) {

            if (r >= 1 && c < Board.cols - 1 &&
                    board.cell[r - 1][c + 1] == CellEntry.empty

                    ) {
                forwardLeft = new Step(r, c, r - 1, c + 1);
            }
        } else {
            if (r < Board.rows - 1 && c >= 1 &&
                    board.cell[r + 1][c - 1] == CellEntry.empty
                    ) {
                forwardLeft = new Step(r, c, r + 1, c - 1);
            }
        }
        return forwardLeft;
    }

    // Forward Left Capture
    private Step forwardLeftCapture(int r, int c, Board board) {
        Step forwardLeftCapture = null;

        if (owner == Owner.ROBOT) {
            if (r >= 2 && c < Board.cols - 2 &&
                    (
                            board.cell[r - 1][c + 1].equals(opponentPawn)
                                    || board.cell[r - 1][c + 1].equals(opponentKing)
                    )
                    && board.cell[r - 2][c + 2].equals(CellEntry.empty)
                    ) {
                forwardLeftCapture = new Step(r, c, r - 2, c + 2);
            }
        } else {
            if (r < Board.rows - 2 && c >= 2 &&
                    (
                            board.cell[r + 1][c - 1].equals(opponentPawn)
                                    || board.cell[r + 1][c - 1].equals(opponentKing)
                    )
                    && board.cell[r + 2][c - 2].equals(CellEntry.empty)
                    ) {
                forwardLeftCapture = new Step(r, c, r + 2, c - 2);
            }
        }
        return forwardLeftCapture;
    }

    //Forward Right
    private Step forwardRight(int r, int c, Board board) {
        Step forwardRight = null;

        if (owner == Owner.ROBOT) {
            if (r >= 1 && c >= 1 &&
                    board.cell[r - 1][c - 1] == CellEntry.empty
                    ) {
                forwardRight = new Step(r, c, r - 1, c - 1);
            }
        } else {
            if (r < Board.rows - 1 && c < Board.cols - 1 &&
                    board.cell[r + 1][c + 1] == CellEntry.empty
                    ) {
                forwardRight = new Step(r, c, r + 1, c + 1);
            }
        }
        return forwardRight;
    }

    // Forward Right Capture
    private Step forwardRightCapture(int r, int c, Board board) {

        Step forwardRightCapture = null;

        if (owner == Owner.ROBOT) {
            if (r >= 2 && c >= 2 && (
                    board.cell[r - 1][c - 1].equals(opponentPawn)
                            || board.cell[r - 1][c - 1].equals(opponentKing)
            )
                    && board.cell[r - 2][c - 2].equals(CellEntry.empty)
                    ) {
                forwardRightCapture = new Step(r, c, r - 2, c - 2);

            }
        } else {
            if (r < Board.rows - 2 && c < Board.cols - 2 &&
                    (
                            board.cell[r + 1][c + 1].equals(opponentPawn)
                                    || board.cell[r + 1][c + 1].equals(opponentKing)
                    )
                    && board.cell[r + 2][c + 2].equals(CellEntry.empty)
                    ) {
                forwardRightCapture = new Step(r, c, r + 2, c + 2);
                //System.out.println("Forward Right Capture");
            }
        }
        return forwardRightCapture;
    }

    private Step backwardLeft(int r, int c, Board board) {
        Step backwardLeft = null;

        if (owner == Owner.ROBOT) {
            if (r < Board.rows - 1 && c < Board.cols - 1 &&
                    board.cell[r + 1][c + 1] == CellEntry.empty
                    ) {
                backwardLeft = new Step(r, c, r + 1, c + 1);
            }
        } else {
            if (r >= 1 && c >= 1 &&
                    board.cell[r - 1][c - 1] == CellEntry.empty
                    ) {
                backwardLeft = new Step(r, c, r - 1, c - 1);
            }
        }

        return backwardLeft;
    }

    // Backward Left Capture
    private Step backwardLeftCapture(int r, int c, Board board) {

        Step backwardLeftCapture = null;

        if (owner == Owner.ROBOT) {

            if (r < Board.rows - 2 && c < Board.cols - 2 && (
                    board.cell[r + 1][c + 1].equals(opponentPawn)
                            || board.cell[r + 1][c + 1].equals(opponentKing)
            )
                    && board.cell[r + 2][c + 2].equals(CellEntry.empty)
                    ) {
                backwardLeftCapture = new Step(r, c, r + 2, c + 2);

            }
        } else {

            if (r >= 2 && c >= 2 && (
                    board.cell[r - 1][c - 1].equals(opponentPawn)
                            || board.cell[r - 1][c - 1].equals(opponentKing)
            )
                    && board.cell[r - 2][c - 2].equals(CellEntry.empty)
                    ) {
                backwardLeftCapture = new Step(r, c, r - 2, c - 2);
            }
        }

        return backwardLeftCapture;
    }

    private Step backwardRight(int r, int c, Board board) {
        Step backwardRight = null;


        if (owner == Owner.ROBOT) {
            if (r < Board.rows - 1 && c >= 1 &&
                    board.cell[r + 1][c - 1].equals(CellEntry.empty)
                    ) {
                backwardRight = new Step(r, c, r + 1, c - 1);
            }
        } else {

            if (r >= 1 && c < Board.cols - 1 &&
                    board.cell[r - 1][c + 1] == CellEntry.empty
                    ) {
                backwardRight = new Step(r, c, r - 1, c + 1);
            }
        }

        return backwardRight;
    }

    // Backward Right Capture
    private Step backwardRightCapture(int r, int c, Board board) {

        Step backwardRightCapture = null;

        if (owner == Owner.ROBOT) {

            if (r < Board.rows - 2 && c >= 2 && (
                    board.cell[r + 1][c - 1].equals(opponentPawn) ||
                            board.cell[r + 1][c - 1].equals(opponentKing)
            )
                    && board.cell[r + 2][c - 2].equals(CellEntry.empty)
                    ) {
                backwardRightCapture = new Step(r, c, r + 2, c - 2);
            }
        } else {

            if (r >= 2 && c < Board.cols - 2 && (
                    board.cell[r - 1][c + 1].equals(opponentPawn) ||
                            board.cell[r - 1][c + 1].equals(opponentKing)
            )
                    && board.cell[r - 2][c + 2].equals(CellEntry.empty)
                    ) {
                backwardRightCapture = new Step(r, c, r - 2, c + 2);
            }
        }

        return backwardRightCapture;
    }

}
