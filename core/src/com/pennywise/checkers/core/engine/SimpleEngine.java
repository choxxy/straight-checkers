package com.pennywise.checkers.core.engine;//
// Created by CHOXXY on 10/10/2015.
//


/*______________________________________________________________________________

----------> name: simple checkers with enhancements
----------> author: martin fierz
----------> purpose: platform independent checkers engine
----------> version: 1.15
----------> date: 4th february 2011
----------> description: simplech.c contains a simple but fast checkers engine
			and some routines to interface to checkerboard. simplech.c contains three
			main parts: interface, search and move generation. these parts are
			separated in the code.

board representation: the standard checkers notation is

     (white)
  32  31  30  29
28  27  26  25
  24  23  22  21
20  19  18  17
  16  15  14  13
12  11  10   9
  8   7   6   5
4   3   2   1
    (black)

the internal representation of the board is different, it is a
array of int with length 46, the checkers board is numbered
like this:

    (white)
  37  38  39  40
32  33  34  35
  28  29  30  31
23  24  25  26
  19  20  21  22
14  15  16  17
  10  11  12  13
5   6   7   8
    (black)

let's say, you would like to teach the program that it is
important to keep a back rank guard. you can for instance
add the following (not very sophisticated) code for this:

if(b[6] & (BLACK|MAN)) eval++;
if(b[8] & (BLACK|MAN)) eval++;
if(b[37] & (WHITE|MAN)) eval--;
if(b[39] & (WHITE|MAN)) eval--;

the evaluation function is seen from the point of view of the
black player, so you increase the value v if you think the
position is good for black.

simple checkers is free for anyone to use, in any way, explicitly also
in commercial products without the need for asking me. Naturally, I would
appreciate if you tell me that you are using it, and if you acknowledge
my contribution to your project.

questions, comments, suggestions to:

Martin Fierz
checkers@fierz.ch


/*----------> includes */

public class SimpleEngine {

    /*----------> definitions */
    public static final int OCCUPIED = 0;
    public static final int WHITE = 1;
    public static final int BLACK = 2;
    public static final int MAN = 4;
    public static final int KING = 8;
    public static final int FREE = 16;
    public static final int CHANGECOLOR = 3;
    private static final int MAXDEPTH = 99;
    private final long CLK_TCK = 1000;

    /* return values */
    private static final int DRAW = 0;
    private static final int WIN = 1;
    private static final int LOSS = 2;
    private static final int UNKNOWN = 3;
    private static final int MAXMOVES = 28;


    int[] value = new int[]{0, 0, 0, 0, 0, 1, 256, 0, 0, 16, 4096, 0, 0, 0, 0, 0, 0};
    boolean play;

    /*----------> structure definitions  */
    static class Move2 {
        int n;
        int[] m = new int[8];
    }

/*----------> function prototypes  */
/*----------> part I: interface to CheckerBoard: CheckerBoard requires that
                at getmove and enginename are present in the dll. the
				functions help, options and about are optional. if you
				do not provide them, CheckerBoard will display a
				MessageBox stating that this option is in fact not an option*/


    /*----------> globals  */
   /* #
    ifdef STATISTICS
    int alphabetas, generatemovelists, evaluations, generatecapturelists, testcaptures;
    #endif
*/

    /**
     * getmove is what checkerboard calls. you get 6 parameters:
     * #param b[8][8] 	is the current position. the values in the array are determined by
     * the #defined values of BLACK, WHITE, KING, MAN. a black king for
     * instance is represented by BLACK|KING.
     * color		    is the side to make a move. BLACK or WHITE.
     * maxtime	        is the time your program should use to make a move. this is
     * what you specify as level in checkerboard. so if you exceed
     * this time it's not too bad - just don't exceed it too much...
     * str		        is a pointer to the output string of the checkerboard status bar.
     * you can use sprintf(str,"information"); to print any information you
     * want into the status bar.
     * playnow	        is a pointer to the playnow variable of checkerboard. if the user
     * would like your engine to play immediately, this value is nonzero,
     * else zero. you should respond to a nonzero value of playnow by
     * interrupting your search IMMEDIATELY.
     */

    public int getMove(int[][] b, int color, double maxtime,
                       String str, boolean playnow, int info, int unused) {


        int i;
        int value;
        int[] board = new int[46];

    /* initialize board */
        for (i = 0; i < 46; i++)
            board[i] = OCCUPIED;
        for (i = 5; i <= 40; i++)
            board[i] = FREE;
    /*    (white)
    37  38  39  40
    32  33  34  35
    28  29  30  31
    23  24  25  26
    19  20  21  22
    14  15  16  17
    10  11  12  13
    5   6   7   8
    (black)   */
        board[5] = b[0][0];
        board[6] = b[2][0];
        board[7] = b[4][0];
        board[8] = b[6][0];
        board[10] = b[1][1];
        board[11] = b[3][1];
        board[12] = b[5][1];
        board[13] = b[7][1];
        board[14] = b[0][2];
        board[15] = b[2][2];
        board[16] = b[4][2];
        board[17] = b[6][2];
        board[19] = b[1][3];
        board[20] = b[3][3];
        board[21] = b[5][3];
        board[22] = b[7][3];
        board[23] = b[0][4];
        board[24] = b[2][4];
        board[25] = b[4][4];
        board[26] = b[6][4];
        board[28] = b[1][5];
        board[29] = b[3][5];
        board[30] = b[5][5];
        board[31] = b[7][5];
        board[32] = b[0][6];
        board[33] = b[2][6];
        board[34] = b[4][6];
        board[35] = b[6][6];
        board[37] = b[1][7];
        board[38] = b[3][7];
        board[39] = b[5][7];
        board[40] = b[7][7];
        for (i = 5; i <= 40; i++)
            if (board[i] == 0)
                board[i] = FREE;
        for (i = 9; i <= 36; i += 9)
            board[i] = OCCUPIED;
        play = playnow;

        value = checkers(board, color, maxtime, str);
        for (i = 5; i <= 40; i++)
            if (board[i] == FREE) board[i] = 0;
    /* return the board */
        b[0][0] = board[5];
        b[2][0] = board[6];
        b[4][0] = board[7];
        b[6][0] = board[8];
        b[1][1] = board[10];
        b[3][1] = board[11];
        b[5][1] = board[12];
        b[7][1] = board[13];
        b[0][2] = board[14];
        b[2][2] = board[15];
        b[4][2] = board[16];
        b[6][2] = board[17];
        b[1][3] = board[19];
        b[3][3] = board[20];
        b[5][3] = board[21];
        b[7][3] = board[22];
        b[0][4] = board[23];
        b[2][4] = board[24];
        b[4][4] = board[25];
        b[6][4] = board[26];
        b[1][5] = board[28];
        b[3][5] = board[29];
        b[5][5] = board[30];
        b[7][5] = board[31];
        b[0][6] = board[32];
        b[2][6] = board[33];
        b[4][6] = board[34];
        b[6][6] = board[35];
        b[1][7] = board[37];
        b[3][7] = board[38];
        b[5][7] = board[39];
        b[7][7] = board[40];
        if (color == BLACK) {
            if (value > 4000) return WIN;
            if (value < -4000) return LOSS;
        }
        if (color == WHITE) {
            if (value > 4000) return LOSS;
            if (value < -4000) return WIN;
        }
        return UNKNOWN;
    }


    void movetonotation(Move2 move, String str) {
        int j, from, to;
        char c;

        from = move.m[0] % 256;
        to = move.m[1] % 256;
        from = from - (from / 9);
        to = to - (to / 9);
        from -= 5;
        to -= 5;
        j = from % 4;
        from -= j;
        j = 3 - j;
        from += j;
        j = to % 4;
        to -= j;
        j = 3 - j;
        to += j;
        from++;
        to++;
        c = '-';
        if (move.n > 2)
            c = 'x';
        str = String.format("%2li%c%2li", from, c, to);
    }


/*-------------- PART II: SEARCH ---------------------------------------------*/


    int checkers(int[] b, int color, double maxtime, String str)
/*--------> purpose: entry point to checkers. find a move on board b for color
---------->          in the time specified by maxtime, write the best move in
---------->          board, returns information on the search in str
----------> returns 1 if a move is found & executed, 0, if there is no legal
----------> move in this position.
----------> version: 1.1
----------> date: 9th october 98 */ {
        int i, numberofmoves;
        long start;
        int eval;
        Move2 best = null;
        Move2 lastbest = null;
        Move2[] movelist = new Move2[MAXMOVES];
        String str2 = "";
        double secondsused;

    /*--------> check if there is only one move */
        numberofmoves = generatecapturelist(b, movelist, color);
        if (numberofmoves == 1) {
            domove(b, movelist[0]);
            str = "forced capture";
            return 1;
        } else if (numberofmoves == 0) {
            numberofmoves = generatemovelist(b, movelist, color);
            if (numberofmoves == 1) {
                domove(b, movelist[0]);
                str = "only move";
                return 1;
            }
            if (numberofmoves == 0) {
                str = "no legal moves in this position";
                return 0;
            }
        }
        start = System.currentTimeMillis();

        eval = firstalphabeta(b, 1, -10000, 10000, color, best);

        for (i = 2; i <= MAXDEPTH; i++) {
            lastbest = best;
            eval = firstalphabeta(b, i, -10000, 10000, color, best);
            secondsused = (double) (System.currentTimeMillis() - start) / CLK_TCK;
            movetonotation(best, str2);
            if (play)
                break;
            if (eval == 5000)
                break;
            if (eval == -5000)
                break;
            if (secondsused > maxtime)
                break;
        }
        i--;
        if (play)
            movetonotation(lastbest, str2);
        else
            movetonotation(best, str2);

        /*
        str = String.format(
                "best:%s time %2.2f, depth %2li, value %4li  nodes %li, gms %li, gcs %li, evals %li",
                str2, secondsused, i, eval, alphabetas, generatemovelists, generatecapturelists,
                evaluations);*/

        if (play)
            domove(b, lastbest);
        else
            domove(b, best);
        return eval;
    }


    int firstalphabeta(int[] b, int depth, int alpha, int beta, int color, Move2 best)
/*----------> purpose: search the game tree and find the best move.
----------> version: 1.0
----------> date: 25th october 97 */ {
        int i;
        int value;
        int numberofmoves;
        int capture;
        Move2[] movelist = new Move2[MAXMOVES];

        if (play) return 0;
    /*----------> test if captures are possible */
        capture = testcapture(b, color);

    /*----------> recursion termination if no captures and depth=0*/
        if (depth == 0) {
            if (capture == 0)
                return (evaluation(b, color));
            else
                depth = 1;
        }

    /*----------> generate all possible moves in the position */
        if (capture == 0) {
            numberofmoves = generatemovelist(b, movelist, color);
        /*----------> if there are no possible moves, we lose: */
            if (numberofmoves == 0) {
                if (color == BLACK) return (-5000);
                else return (5000);
            }
        } else
            numberofmoves = generatecapturelist(b, movelist, color);

    /*----------> for all moves: execute the move, search tree, undo move. */
        for (i = 0; i < numberofmoves; i++) {
            domove(b, movelist[i]);

            value = alphabeta(b, depth - 1, alpha, beta, (color ^ CHANGECOLOR));

            undomove(b, movelist[i]);
            if (color == BLACK) {
                if (value >= beta) return (value);
                if (value > alpha) {
                    alpha = value;
                    best = movelist[i];
                }
            }
            if (color == WHITE) {
                if (value <= alpha) return (value);
                if (value < beta) {
                    beta = value;
                    best = movelist[i];
                }
            }
        }
        if (color == BLACK)
            return (alpha);
        return (beta);
    }

    int alphabeta(int[] b, int depth, int alpha, int beta, int color)
/*--------> purpose: search the game tree and find the best move.
----------> version: 1.0
----------> date: 24th october 97 */ {
        int i;
        int value;
        int capture;
        int numberofmoves;
        Move2[] movelist = new Move2[MAXMOVES];

        /*
        #ifdef STATISTICS
        alphabetas++;
        #endif*/
        if (play) return 0;
    /*----------> test if captures are possible */
        capture = testcapture(b, color);

    /*----------> recursion termination if no captures and depth=0*/
        if (depth == 0) {
            if (capture == 0)
                return (evaluation(b, color));
            else
                depth = 1;
        }

    /*----------> generate all possible moves in the position */
        if (capture == 0) {
            numberofmoves = generatemovelist(b, movelist, color);
        /*----------> if there are no possible moves, we lose: */
            if (numberofmoves == 0) {
                if (color == BLACK) return (-5000);
                else return (5000);
            }
        } else
            numberofmoves = generatecapturelist(b, movelist, color);

    /*----------> for all moves: execute the move, search tree, undo move. */
        for (i = 0; i < numberofmoves; i++) {
            domove(b, movelist[i]);

            value = alphabeta(b, depth - 1, alpha, beta, color ^ CHANGECOLOR);

            undomove(b, movelist[i]);

            if (color == BLACK) {
                if (value >= beta) return (value);
                if (value > alpha) alpha = value;
            }
            if (color == WHITE) {
                if (value <= alpha) return (value);
                if (value < beta) beta = value;
            }
        }
        if (color == BLACK)
            return (alpha);
        return (beta);
    }

    void domove(int[] b, Move2 move)
/*--------> purpose: execute move on board
----------> version: 1.1
----------> date: 25th october 97 */ {
        int square, after;
        int i;

        for (i = 0; i < move.n; i++) {
            square = (move.m[i] % 256);
            after = ((move.m[i] >> 16) % 256);
            b[square] = after;
        }
    }

    void undomove(int[] b, Move2 move)
/*--------> purpose:
----------> version: 1.1
----------> date: 25th october 97 */ {
        int square, before;
        int i;

        for (i = 0; i < move.n; i++) {
            square = (move.m[i] % 256);
            before = ((move.m[i] >> 8) % 256);
            b[square] = before;
        }
    }

    int evaluation(int[] b, int color)
/*--------> purpose:
----------> version: 1.1
----------> date: 18th april 98 */ {
        int i, j;
        int eval;
        int v1, v2;
        int nbm, nbk, nwm, nwk;
        int nbmc = 0, nbkc = 0, nwmc = 0, nwkc = 0;
        int nbme = 0, nbke = 0, nwme = 0, nwke = 0;
        int code = 0;
        int[] value = new int[]{
                0, 0, 0, 0, 0, 1, 256, 0, 0, 16, 4096, 0, 0, 0, 0, 0, 0
        };
        int[] edge = new int[]{
                5, 6, 7, 8, 13, 14, 22, 23, 31, 32, 37, 38, 39, 40
        };
        int[] center = new int[]{
                15, 16, 20, 21, 24, 25, 29, 30
        };
        int[] row = new int[]{
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 0, 3, 3, 3, 3, 4, 4,
                4, 4, 0, 5, 5, 5, 5, 6, 6, 6, 6, 0, 7, 7, 7, 7
        };
        int[] safeedge = new int[]{
                8, 13, 32, 37
        };

        int tempo = 0;
        int nm, nk;

        int turn = 2;   //color to move gets +turn
        int brv = 3;    //multiplier for back rank
        int kcv = 5;    //multiplier for kings in center
        int mcv = 1;    //multiplier for men in center

        int mev = 1;    //multiplier for men on edge
        int kev = 5;    //multiplier for kings on edge
        int cramp = 5;  //multiplier for cramp

        int opening = -2; // multipliers for tempo
        int midgame = -1;
        int endgame = 2;
        int intactdoublecorner = 3;


        int backrank;

        int stonesinsystem = 0;

        /*
        #ifdef STATISTICS
        evaluations++;
        #endif
*/

        for (i = 5; i <= 40; i++)
            code += value[b[i]];

        nwm = code % 16;
        nwk = (code >> 4) % 16;
        nbm = (code >> 8) % 16;
        nbk = (code >> 12) % 16;


        v1 = 100 * nbm + 130 * nbk;
        v2 = 100 * nwm + 130 * nwk;

        eval = v1 - v2;                       /*material values*/
        eval += (250 * (v1 - v2)) / (v1 + v2);      /*favor exchanges if in material plus*/

        nm = nbm + nwm;
        nk = nbk + nwk;
    /*--------- fine evaluation below -------------*/

        if (color == BLACK) eval += turn;
        else eval -= turn;
    /*    (white)
    37  38  39  40
    32  33  34  35
    28  29  30  31
    23  24  25  26
    19  20  21  22
    14  15  16  17
    10  11  12  13
    5   6   7   8
    (black)   */
    /* cramp */
        if (b[23] == (BLACK | MAN) && b[28] == (WHITE | MAN)) eval += cramp;
        if (b[22] == (WHITE | MAN) && b[17] == (BLACK | MAN)) eval -= cramp;

    /* back rank guard */

        code = 0;
        if (b[5] == MAN) code++;
        if (b[6] == MAN) code += 2;
        if (b[7] == MAN) code += 4;
        if (b[8] == MAN) code += 8;
        switch (code) {
            case 0:
                code = 0;
                break;
            case 1:
                code = -1;
                break;
            case 2:
                code = 1;
                break;
            case 3:
                code = 0;
                break;
            case 4:
                code = 1;
                break;
            case 5:
                code = 1;
                break;
            case 6:
                code = 2;
                break;
            case 7:
                code = 1;
                break;
            case 8:
                code = 1;
                break;
            case 9:
                code = 0;
                break;
            case 10:
                code = 7;
                break;
            case 11:
                code = 4;
                break;
            case 12:
                code = 2;
                break;
            case 13:
                code = 2;
                break;
            case 14:
                code = 9;
                break;
            case 15:
                code = 8;
                break;
        }
        backrank = code;


        code = 0;
        if (b[37] == MAN) code += 8;
        if (b[38] == MAN) code += 4;
        if (b[39] == MAN) code += 2;
        if (b[40] == MAN) code++;
        switch (code) {
            case 0:
                code = 0;
                break;
            case 1:
                code = -1;
                break;
            case 2:
                code = 1;
                break;
            case 3:
                code = 0;
                break;
            case 4:
                code = 1;
                break;
            case 5:
                code = 1;
                break;
            case 6:
                code = 2;
                break;
            case 7:
                code = 1;
                break;
            case 8:
                code = 1;
                break;
            case 9:
                code = 0;
                break;
            case 10:
                code = 7;
                break;
            case 11:
                code = 4;
                break;
            case 12:
                code = 2;
                break;
            case 13:
                code = 2;
                break;
            case 14:
                code = 9;
                break;
            case 15:
                code = 8;
                break;
        }
        backrank -= code;
        eval += brv * backrank;


    /* intact double corner */
        if (b[8] == (BLACK | MAN)) {
            if (b[12] == (BLACK | MAN) || b[13] == (BLACK | MAN))
                eval += intactdoublecorner;
        }

        if (b[37] == (WHITE | MAN)) {
            if (b[32] == (WHITE | MAN) || b[33] == (WHITE | MAN))
                eval -= intactdoublecorner;
        }
    /*    (white)
    37  38  39  40
    32  33  34  35
    28  29  30  31
    23  24  25  26
    19  20  21  22
    14  15  16  17
    10  11  12  13
    5   6   7   8
    (black)   */

    /* center control */
        for (i = 0; i < 8; i++) {
            if (b[center[i]] != FREE) {
                if (b[center[i]] == (BLACK | MAN)) nbmc++;
                if (b[center[i]] == (BLACK | KING)) nbkc++;
                if (b[center[i]] == (WHITE | MAN)) nwmc++;
                if (b[center[i]] == (WHITE | KING)) nwkc++;
            }
        }
        eval += (nbmc - nwmc) * mcv;
        eval += (nbkc - nwkc) * kcv;

    /*edge*/
        for (i = 0; i < 14; i++) {
            if (b[edge[i]] != FREE) {
                if (b[edge[i]] == (BLACK | MAN)) nbme++;
                if (b[edge[i]] == (BLACK | KING)) nbke++;
                if (b[edge[i]] == (WHITE | MAN)) nwme++;
                if (b[edge[i]] == (WHITE | KING)) nwke++;
            }
        }
        eval -= (nbme - nwme) * mev;
        eval -= (nbke - nwke) * kev;



    /* tempo */
        for (i = 5; i < 41; i++) {
            if (b[i] == (BLACK | MAN))
                tempo += row[i];
            if (b[i] == (WHITE | MAN))
                tempo -= 7 - row[i];
        }

        if (nm >= 16) eval += opening * tempo;
        if ((nm <= 15) && (nm >= 12)) eval += midgame * tempo;
        if (nm < 9) eval += endgame * tempo;


        for (i = 0; i < 4; i++) {
            if (nbk + nbm > nwk + nwm && nwk < 3) {
                if (b[safeedge[i]] == (WHITE | KING))
                    eval -= 15;
            }
            if (nwk + nwm > nbk + nbm && nbk < 3) {
                if (b[safeedge[i]] == (BLACK | KING))
                    eval += 15;
            }
        }

    /* the move */
        if (nwm + nwk - nbk - nbm == 0) {
            if (color == BLACK) {
                for (i = 5; i <= 8; i++) {
                    for (j = 0; j < 4; j++) {
                        if (b[i + 9 * j] != FREE) stonesinsystem++;
                    }
                }
                if ((stonesinsystem % 2) != 0) {
                    if (nm + nk <= 12) eval++;
                    if (nm + nk <= 10) eval++;
                    if (nm + nk <= 8) eval += 2;
                    if (nm + nk <= 6) eval += 2;
                } else {
                    if (nm + nk <= 12) eval--;
                    if (nm + nk <= 10) eval--;
                    if (nm + nk <= 8) eval -= 2;
                    if (nm + nk <= 6) eval -= 2;
                }
            } else {
                for (i = 10; i <= 13; i++) {
                    for (j = 0; j < 4; j++) {
                        if (b[i + 9 * j] != FREE) stonesinsystem++;
                    }
                }
                if ((stonesinsystem % 2) == 0) {
                    if (nm + nk <= 12) eval++;
                    if (nm + nk <= 10) eval++;
                    if (nm + nk <= 8) eval += 2;
                    if (nm + nk <= 6) eval += 2;
                } else {
                    if (nm + nk <= 12) eval--;
                    if (nm + nk <= 10) eval--;
                    if (nm + nk <= 8) eval -= 2;
                    if (nm + nk <= 6) eval -= 2;
                }
            }
        }


        return (eval);
    }


/*-------------- PART III: MOVE GENERATION -----------------------------------*/

    int generatemovelist(int[] b, Move2[] movelist, int color)
/*--------> purpose:generates all moves. no captures. returns number of moves
----------> version: 1.0
----------> date: 25th october 97 */ {
        int n = 0, m = 0;
        int i;

        if (color == BLACK) {
            for (i = 5; i <= 40; i++) {
                if ((b[i] & BLACK) != 0) {
                    if ((b[i] & MAN) != 0) {
                        if ((b[i + 4] & FREE) != 0) {
                            movelist[n].n = 2;
                            if (i >= 32)
                                m = (BLACK | KING);
                            else
                                m = (BLACK | MAN);
                            m = m << 8;
                            m += FREE;
                            m = m << 8;
                            m += i + 4;
                            movelist[n].m[1] = m;
                            m = FREE;
                            m = m << 8;
                            m += (BLACK | MAN);
                            m = m << 8;
                            m += i;
                            movelist[n].m[0] = m;
                            n++;
                        }
                        if ((b[i + 5] & FREE) != 0) {
                            movelist[n].n = 2;
                            if (i >= 32)
                                m = (BLACK | KING);
                            else
                                m = (BLACK | MAN);
                            m = m << 8;
                            m += FREE;
                            m = m << 8;
                            m += i + 5;
                            movelist[n].m[1] = m;
                            m = FREE;
                            m = m << 8;
                            m += (BLACK | MAN);
                            m = m << 8;
                            m += i;
                            movelist[n].m[0] = m;
                            n++;
                        }
                    }
                    if ((b[i] & KING) != 0) {
                        if ((b[i + 4] & FREE) != 0) {
                            movelist[n].n = 2;
                            m = (BLACK | KING);
                            m = m << 8;
                            m += FREE;
                            m = m << 8;
                            m += i + 4;
                            movelist[n].m[1] = m;
                            m = FREE;
                            m = m << 8;
                            m += (BLACK | KING);
                            m = m << 8;
                            m += i;
                            movelist[n].m[0] = m;
                            n++;
                        }
                        if ((b[i + 5] & FREE) != 0) {
                            movelist[n].n = 2;
                            m = (BLACK | KING);
                            m = m << 8;
                            m += FREE;
                            m = m << 8;
                            m += i + 5;
                            movelist[n].m[1] = m;
                            m = FREE;
                            m = m << 8;
                            m += (BLACK | KING);
                            m = m << 8;
                            m += i;
                            movelist[n].m[0] = m;
                            n++;
                        }
                        if ((b[i - 4] & FREE) != 0) {
                            movelist[n].n = 2;
                            m = (BLACK | KING);
                            m = m << 8;
                            m += FREE;
                            m = m << 8;
                            m += i - 4;
                            movelist[n].m[1] = m;
                            m = FREE;
                            m = m << 8;
                            m += (BLACK | KING);
                            m = m << 8;
                            m += i;
                            movelist[n].m[0] = m;
                            n++;
                        }
                        if ((b[i - 5] & FREE) != 0) {
                            movelist[n].n = 2;
                            m = (BLACK | KING);
                            m = m << 8;
                            m += FREE;
                            m = m << 8;
                            m += i - 5;
                            movelist[n].m[1] = m;
                            m = FREE;
                            m = m << 8;
                            m += (BLACK | KING);
                            m = m << 8;
                            m += i;
                            movelist[n].m[0] = m;
                            n++;
                        }
                    }
                }
            }
        } else    /* color = WHITE */ {
            for (i = 5; i <= 40; i++) {
                if ((b[i] & WHITE) != 0) {
                    if ((b[i] & MAN) != 0) {
                        if ((b[i - 4] & FREE) != 0) {
                            movelist[n].n = 2;
                            if (i <= 13)
                                m = (WHITE | KING);
                            else
                                m = (WHITE | MAN);
                            m = m << 8;
                            m += FREE;
                            m = m << 8;
                            m += i - 4;
                            movelist[n].m[1] = m;
                            m = FREE;
                            m = m << 8;
                            m += (WHITE | MAN);
                            m = m << 8;
                            m += i;
                            movelist[n].m[0] = m;
                            n++;
                        }
                        if ((b[i - 5] & FREE) != 0) {
                            movelist[n].n = 2;
                            if (i <= 13)
                                m = (WHITE | KING);
                            else
                                m = (WHITE | MAN);
                            m = m << 8;
                            m += FREE;
                            m = m << 8;
                            m += i - 5;
                            movelist[n].m[1] = m;
                            m = FREE;
                            m = m << 8;
                            m += (WHITE | MAN);
                            m = m << 8;
                            m += i;
                            movelist[n].m[0] = m;
                            n++;
                        }
                    }
                    if ((b[i] & KING) != 0)  /* or else */ {
                        if ((b[i + 4] & FREE) != 0) {
                            movelist[n].n = 2;
                            m = (WHITE | KING);
                            m = m << 8;
                            m += FREE;
                            m = m << 8;
                            m += i + 4;
                            movelist[n].m[1] = m;
                            m = FREE;
                            m = m << 8;
                            m += (WHITE | KING);
                            m = m << 8;
                            m += i;
                            movelist[n].m[0] = m;
                            n++;
                        }
                        if ((b[i + 5] & FREE) != 0) {
                            movelist[n].n = 2;
                            m = (WHITE | KING);
                            m = m << 8;
                            m += FREE;
                            m = m << 8;
                            m += i + 5;
                            movelist[n].m[1] = m;
                            m = FREE;
                            m = m << 8;
                            m += (WHITE | KING);
                            m = m << 8;
                            m += i;
                            movelist[n].m[0] = m;
                            n++;
                        }
                        if ((b[i - 4] & FREE) != 0) {
                            movelist[n].n = 2;
                            m = (WHITE | KING);
                            m = m << 8;
                            m += FREE;
                            m = m << 8;
                            m += i - 4;
                            movelist[n].m[1] = m;
                            m = FREE;
                            m = m << 8;
                            m += (WHITE | KING);
                            m = m << 8;
                            m += i;
                            movelist[n].m[0] = m;
                            n++;
                        }
                        if ((b[i - 5] & FREE) != 0) {
                            movelist[n].n = 2;
                            m = (WHITE | KING);
                            m = m << 8;
                            m += FREE;
                            m = m << 8;
                            m += i - 5;
                            movelist[n].m[1] = m;
                            m = FREE;
                            m = m << 8;
                            m += (WHITE | KING);
                            m = m << 8;
                            m += i;
                            movelist[n].m[0] = m;
                            n++;
                        }
                    }
                }
            }
        }
        return (n);
    }

    int generatecapturelist(int[] b, Move2[] movelist, int color)
/*--------> purpose: generate all possible captures
----------> version: 1.0
----------> date: 25th october 97 */ {
        int n = 0;
        int m;
        int i;
        int tmp;

        if (color == BLACK) {
            for (i = 5; i <= 40; i++) {
                if ((b[i] & BLACK) != 0) {
                    if ((b[i] & MAN) != 0) {
                        if ((b[i + 4] & WHITE) != 0) {
                            if ((b[i + 8] & FREE) != 0) {
                                movelist[n].n = 3;
                                if (i >= 28)
                                    m = (BLACK | KING);
                                else
                                    m = (BLACK | MAN);
                                m = m << 8;
                                m += FREE;
                                m = m << 8;
                                m += i + 8;
                                movelist[n].m[1] = m;
                                m = FREE;
                                m = m << 8;
                                m += (BLACK | MAN);
                                m = m << 8;
                                m += i;
                                movelist[n].m[0] = m;
                                m = FREE;
                                m = m << 8;
                                m += b[i + 4];
                                m = m << 8;
                                m += i + 4;
                                movelist[n].m[2] = m;
                                n = blackmancapture(b, n, movelist, i + 8);
                            }
                        }
                        if ((b[i + 5] & WHITE) != 0) {
                            if ((b[i + 10] & FREE) != 0) {
                                movelist[n].n = 3;
                                if (i >= 28)
                                    m = (BLACK | KING);
                                else
                                    m = (BLACK | MAN);
                                m = m << 8;
                                m += FREE;
                                m = m << 8;
                                m += i + 10;
                                movelist[n].m[1] = m;
                                m = FREE;
                                m = m << 8;
                                m += (BLACK | MAN);
                                m = m << 8;
                                m += i;
                                movelist[n].m[0] = m;
                                m = FREE;
                                m = m << 8;
                                m += b[i + 5];
                                m = m << 8;
                                m += i + 5;
                                movelist[n].m[2] = m;
                                n = blackmancapture(b, n, movelist, i + 10);
                            }
                        }
                    } else /* b[i] is a KING */ {
                        if ((b[i + 4] & WHITE) != 0) {
                            if ((b[i + 8] & FREE) != 0) {
                                movelist[n].n = 3;
                                m = (BLACK | KING);
                                m = m << 8;
                                m += FREE;
                                m = m << 8;
                                m += i + 8;
                                movelist[n].m[1] = m;
                                m = FREE;
                                m = m << 8;
                                m += (BLACK | KING);
                                m = m << 8;
                                m += i;
                                movelist[n].m[0] = m;
                                m = FREE;
                                m = m << 8;
                                m += b[i + 4];
                                m = m << 8;
                                m += i + 4;
                                movelist[n].m[2] = m;
                                tmp = b[i + 4];
                                b[i + 4] = FREE;
                                b[i] = FREE;
                                n = blackkingcapture(b, n, movelist, i + 8);
                                b[i + 4] = tmp;
                                b[i] = BLACK | KING;
                            }
                        }
                        if ((b[i + 5] & WHITE) != 0) {
                            if ((b[i + 10] & FREE) != 0) {
                                movelist[n].n = 3;
                                m = (BLACK | KING);
                                m = m << 8;
                                m += FREE;
                                m = m << 8;
                                m += i + 10;
                                movelist[n].m[1] = m;
                                m = FREE;
                                m = m << 8;
                                m += (BLACK | KING);
                                m = m << 8;
                                m += i;
                                movelist[n].m[0] = m;
                                m = FREE;
                                m = m << 8;
                                m += b[i + 5];
                                m = m << 8;
                                m += i + 5;
                                movelist[n].m[2] = m;
                                tmp = b[i + 5];
                                b[i + 5] = FREE;
                                b[i] = FREE;
                                n = blackkingcapture(b, n, movelist, i + 10);
                                b[i + 5] = tmp;
                                b[i] = BLACK | KING;
                            }
                        }
                        if ((b[i - 4] & WHITE) != 0) {
                            if ((b[i - 8] & FREE) != 0) {
                                movelist[n].n = 3;
                                m = (BLACK | KING);
                                m = m << 8;
                                m += FREE;
                                m = m << 8;
                                m += i - 8;
                                movelist[n].m[1] = m;
                                m = FREE;
                                m = m << 8;
                                m += (BLACK | KING);
                                m = m << 8;
                                m += i;
                                movelist[n].m[0] = m;
                                m = FREE;
                                m = m << 8;
                                m += b[i - 4];
                                m = m << 8;
                                m += i - 4;
                                movelist[n].m[2] = m;
                                tmp = b[i - 4];
                                b[i - 4] = FREE;
                                b[i] = FREE;
                                n = blackkingcapture(b, n, movelist, i - 8);
                                b[i - 4] = tmp;
                                b[i] = BLACK | KING;
                            }
                        }
                        if ((b[i - 5] & WHITE) != 0) {
                            if ((b[i - 10] & FREE) != 0) {
                                movelist[n].n = 3;
                                m = (BLACK | KING);
                                m = m << 8;
                                m += FREE;
                                m = m << 8;
                                m += i - 10;
                                movelist[n].m[1] = m;
                                m = FREE;
                                m = m << 8;
                                m += (BLACK | KING);
                                m = m << 8;
                                m += i;
                                movelist[n].m[0] = m;
                                m = FREE;
                                m = m << 8;
                                m += b[i - 5];
                                m = m << 8;
                                m += i - 5;
                                movelist[n].m[2] = m;
                                tmp = b[i - 5];
                                b[i - 5] = FREE;
                                b[i] = FREE;
                                n = blackkingcapture(b, n, movelist, i - 10);
                                b[i - 5] = tmp;
                                b[i] = BLACK | KING;
                            }
                        }
                    }
                }
            }
        } else /* color is WHITE */ {
            for (i = 5; i <= 40; i++) {
                if ((b[i] & WHITE) != 0) {
                    if ((b[i] & MAN) != 0) {
                        if ((b[i - 4] & BLACK) != 0) {
                            if ((b[i - 8] & FREE) != 0) {
                                movelist[n].n = 3;
                                if (i <= 17)
                                    m = (WHITE | KING);
                                else
                                    m = (WHITE | MAN);
                                m = m << 8;
                                m += FREE;
                                m = m << 8;
                                m += i - 8;
                                movelist[n].m[1] = m;
                                m = FREE;
                                m = m << 8;
                                m += (WHITE | MAN);
                                m = m << 8;
                                m += i;
                                movelist[n].m[0] = m;
                                m = FREE;
                                m = m << 8;
                                m += b[i - 4];
                                m = m << 8;
                                m += i - 4;
                                movelist[n].m[2] = m;
                                n = whitemancapture(b, n, movelist, i - 8);
                            }
                        }
                        if ((b[i - 5] & BLACK) != 0) {
                            if ((b[i - 10] & FREE) != 0) {
                                movelist[n].n = 3;
                                if (i <= 17)
                                    m = (WHITE | KING);
                                else
                                    m = (WHITE | MAN);
                                m = m << 8;
                                m += FREE;
                                m = m << 8;
                                m += i - 10;
                                movelist[n].m[1] = m;
                                m = FREE;
                                m = m << 8;
                                m += (WHITE | MAN);
                                m = m << 8;
                                m += i;
                                movelist[n].m[0] = m;
                                m = FREE;
                                m = m << 8;
                                m += b[i - 5];
                                m = m << 8;
                                m += i - 5;
                                movelist[n].m[2] = m;
                                n = whitemancapture(b, n, movelist, i - 10);
                            }
                        }
                    } else /* b[i] is a KING */ {
                        if ((b[i + 4] & BLACK) != 0) {
                            if ((b[i + 8] & FREE) != 0) {
                                movelist[n].n = 3;
                                m = (WHITE | KING);
                                m = m << 8;
                                m += FREE;
                                m = m << 8;
                                m += i + 8;
                                movelist[n].m[1] = m;
                                m = FREE;
                                m = m << 8;
                                m += (WHITE | KING);
                                m = m << 8;
                                m += i;
                                movelist[n].m[0] = m;
                                m = FREE;
                                m = m << 8;
                                m += b[i + 4];
                                m = m << 8;
                                m += i + 4;
                                movelist[n].m[2] = m;
                                tmp = b[i + 4];
                                b[i + 4] = FREE;
                                b[i] = FREE;
                                n = whitekingcapture(b, n, movelist, i + 8);
                                b[i + 4] = tmp;
                                b[i] = WHITE | KING;
                            }
                        }
                        if ((b[i + 5] & BLACK) != 0) {
                            if ((b[i + 10] & FREE) != 0) {
                                movelist[n].n = 3;
                                m = (WHITE | KING);
                                m = m << 8;
                                m += FREE;
                                m = m << 8;
                                m += i + 10;
                                movelist[n].m[1] = m;
                                m = FREE;
                                m = m << 8;
                                m += (WHITE | KING);
                                m = m << 8;
                                m += i;
                                movelist[n].m[0] = m;
                                m = FREE;
                                m = m << 8;
                                m += b[i + 5];
                                m = m << 8;
                                m += i + 5;
                                movelist[n].m[2] = m;
                                tmp = b[i + 5];
                                b[i + 5] = FREE;
                                b[i] = FREE;
                                n = whitekingcapture(b, n, movelist, i + 10);
                                b[i + 5] = tmp;
                                b[i] = WHITE | KING;
                            }
                        }
                        if ((b[i - 4] & BLACK) != 0) {
                            if ((b[i - 8] & FREE) != 0) {
                                movelist[n].n = 3;
                                m = (WHITE | KING);
                                m = m << 8;
                                m += FREE;
                                m = m << 8;
                                m += i - 8;
                                movelist[n].m[1] = m;
                                m = FREE;
                                m = m << 8;
                                m += (WHITE | KING);
                                m = m << 8;
                                m += i;
                                movelist[n].m[0] = m;
                                m = FREE;
                                m = m << 8;
                                m += b[i - 4];
                                m = m << 8;
                                m += i - 4;
                                movelist[n].m[2] = m;
                                tmp = b[i - 4];
                                b[i - 4] = FREE;
                                b[i] = FREE;
                                n = whitekingcapture(b, n, movelist, i - 8);
                                b[i - 4] = tmp;
                                b[i] = WHITE | KING;
                            }
                        }
                        if ((b[i - 5] & BLACK) != 0) {
                            if ((b[i - 10] & FREE) != 0) {
                                movelist[n].n = 3;
                                m = (WHITE | KING);
                                m = m << 8;
                                m += FREE;
                                m = m << 8;
                                m += i - 10;
                                movelist[n].m[1] = m;
                                m = FREE;
                                m = m << 8;
                                m += (WHITE | KING);
                                m = m << 8;
                                m += i;
                                movelist[n].m[0] = m;
                                m = FREE;
                                m = m << 8;
                                m += b[i - 5];
                                m = m << 8;
                                m += i - 5;
                                movelist[n].m[2] = m;
                                tmp = b[i - 5];
                                b[i - 5] = FREE;
                                b[i] = FREE;
                                n = whitekingcapture(b, n, movelist, i - 10);
                                b[i - 5] = tmp;
                                b[i] = WHITE | KING;
                            }
                        }
                    }
                }
            }
        }
        return (n);
    }

    protected int blackmancapture(int[] b, int n, Move2[] movelist, int i) {
        int m;
        boolean found = false;
        Move2 move;
        Move2 orgmove;

        orgmove = movelist[n];
        move = orgmove;

        if ((b[i + 4] & WHITE) != 0) {
            if ((b[i + 8] & FREE) != 0) {
                move.n++;
                if (i >= 28)
                    m = (BLACK | KING);
                else
                    m = (BLACK | MAN);
                m = m << 8;
                m += FREE;
                m = m << 8;
                m += (i + 8);
                move.m[1] = m;
                m = FREE;
                m = m << 8;
                m += b[i + 4];
                m = m << 8;
                m += (i + 4);
                move.m[move.n - 1] = m;
                found = true;
                movelist[n] = move;
                blackmancapture(b, n, movelist, i + 8);
            }
        }
        move = orgmove;
        if ((b[i + 5] & WHITE) != 0) {
            if ((b[i + 10] & FREE) != 0) {
                move.n++;
                if (i >= 28)
                    m = (BLACK | KING);
                else
                    m = (BLACK | MAN);
                m = m << 8;
                m += FREE;
                m = m << 8;
                m += (i + 10);
                move.m[1] = m;
                m = FREE;
                m = m << 8;
                m += b[i + 5];
                m = m << 8;
                m += (i + 5);
                move.m[move.n - 1] = m;
                found = true;
                movelist[n] = move;
                blackmancapture(b, n, movelist, i + 10);
            }
        }
        if (!found) n++;

        return n;
    }

    protected int blackkingcapture(int[] b, int n, Move2[] movelist, int i) {
        int m;
        int tmp;
        boolean found = false;
        Move2 move;
        Move2 orgmove;

        orgmove = movelist[n];
        move = orgmove;

        if ((b[i - 4] & WHITE) != 0) {
            if ((b[i - 8] & FREE) != 0) {
                move.n++;
                m = (BLACK | KING);
                m = m << 8;
                m += FREE;
                m = m << 8;
                m += i - 8;
                move.m[1] = m;
                m = FREE;
                m = m << 8;
                m += b[i - 4];
                m = m << 8;
                m += i - 4;
                move.m[move.n - 1] = m;
                found = true;
                movelist[n] = move;
                tmp = b[i - 4];
                b[i - 4] = FREE;
                blackkingcapture(b, n, movelist, i - 8);
                b[i - 4] = tmp;
            }
        }
        move = orgmove;
        if ((b[i - 5] & WHITE) != 0) {
            if ((b[i - 10] & FREE) != 0) {
                move.n++;
                m = (BLACK | KING);
                m = m << 8;
                m += FREE;
                m = m << 8;
                m += i - 10;
                move.m[1] = m;
                m = FREE;
                m = m << 8;
                m += b[i - 5];
                m = m << 8;
                m += i - 5;
                move.m[move.n - 1] = m;
                found = true;
                movelist[n] = move;
                tmp = b[i - 5];
                b[i - 5] = FREE;
                blackkingcapture(b, n, movelist, i - 10);
                b[i - 5] = tmp;
            }
        }
        move = orgmove;
        if ((b[i + 4] & WHITE) != 0) {
            if ((b[i + 8] & FREE) != 0) {
                move.n++;
                m = (BLACK | KING);
                m = m << 8;
                m += FREE;
                m = m << 8;
                m += i + 8;
                move.m[1] = m;
                m = FREE;
                m = m << 8;
                m += b[i + 4];
                m = m << 8;
                m += i + 4;
                move.m[move.n - 1] = m;
                found = true;
                movelist[n] = move;
                tmp = b[i + 4];
                b[i + 4] = FREE;
                blackkingcapture(b, n, movelist, i + 8);
                b[i + 4] = tmp;
            }
        }
        move = orgmove;
        if ((b[i + 5] & WHITE) != 0) {
            if ((b[i + 10] & FREE) != 0) {
                move.n++;
                m = (BLACK | KING);
                m = m << 8;
                m += FREE;
                m = m << 8;
                m += i + 10;
                move.m[1] = m;
                m = FREE;
                m = m << 8;
                m += b[i + 5];
                m = m << 8;
                m += i + 5;
                move.m[move.n - 1] = m;
                found = true;
                movelist[n] = move;
                tmp = b[i + 5];
                b[i + 5] = FREE;
                blackkingcapture(b, n, movelist, i + 10);
                b[i + 5] = tmp;
            }
        }
        if (!found) n++;

        return n;
    }

    int whitemancapture(int[] b, int n, Move2[] movelist, int i) {
        int m;
        boolean found = false;
        Move2 move;
        Move2 orgmove;

        orgmove = movelist[n];
        move = orgmove;

        if ((b[i - 4] & BLACK) != 0) {
            if ((b[i - 8] & FREE) != 0) {
                move.n++;
                if (i <= 17)
                    m = (WHITE | KING);
                else
                    m = (WHITE | MAN);
                m = m << 8;
                m += FREE;
                m = m << 8;
                m += i - 8;
                move.m[1] = m;
                m = FREE;
                m = m << 8;
                m += b[i - 4];
                m = m << 8;
                m += i - 4;
                move.m[move.n - 1] = m;
                found = true;
                movelist[n] = move;
                whitemancapture(b, n, movelist, i - 8);
            }
        }
        move = orgmove;
        if ((b[i - 5] & BLACK) != 0) {
            if ((b[i - 10] & FREE) != 0) {
                move.n++;
                if (i <= 17)
                    m = (WHITE | KING);
                else
                    m = (WHITE | MAN);
                m = m << 8;
                m += FREE;
                m = m << 8;
                m += i - 10;
                move.m[1] = m;
                m = FREE;
                m = m << 8;
                m += b[i - 5];
                m = m << 8;
                m += i - 5;
                move.m[move.n - 1] = m;
                found = true;
                movelist[n] = move;
                whitemancapture(b, n, movelist, i - 10);
            }
        }
        if (!found) n++;

        return n;
    }

    int whitekingcapture(int[] b, int n, Move2[] movelist, int i) {
        int m;
        int tmp;
        boolean found = false;
        Move2 move;
        Move2 orgmove;

        orgmove = movelist[n];
        move = orgmove;

        if ((b[i - 4] & BLACK) != 0) {
            if ((b[i - 8] & FREE) != 0) {
                move.n++;
                m = (WHITE | KING);
                m = m << 8;
                m += FREE;
                m = m << 8;
                m += i - 8;
                move.m[1] = m;
                m = FREE;
                m = m << 8;
                m += b[i - 4];
                m = m << 8;
                m += i - 4;
                move.m[move.n - 1] = m;
                found = true;
                movelist[n] = move;
                tmp = b[i - 4];
                b[i - 4] = FREE;
                whitekingcapture(b, n, movelist, i - 8);
                b[i - 4] = tmp;
            }
        }
        move = orgmove;
        if ((b[i - 5] & BLACK) != 0) {
            if ((b[i - 10] & FREE) != 0) {
                move.n++;
                m = (WHITE | KING);
                m = m << 8;
                m += FREE;
                m = m << 8;
                m += i - 10;
                move.m[1] = m;
                m = FREE;
                m = m << 8;
                m += b[i - 5];
                m = m << 8;
                m += i - 5;
                move.m[move.n - 1] = m;
                found = true;
                movelist[n] = move;
                tmp = b[i - 5];
                b[i - 5] = FREE;
                whitekingcapture(b, n, movelist, i - 10);
                b[i - 5] = tmp;
            }
        }
        move = orgmove;
        if ((b[i + 4] & BLACK) != 0) {
            if ((b[i + 8] & FREE) != 0) {
                move.n++;
                m = (WHITE | KING);
                m = m << 8;
                m += FREE;
                m = m << 8;
                m += i + 8;
                move.m[1] = m;
                m = FREE;
                m = m << 8;
                m += b[i + 4];
                m = m << 8;
                m += i + 4;
                move.m[move.n - 1] = m;
                found = true;
                movelist[n] = move;
                tmp = b[i + 4];
                b[i + 4] = FREE;
                whitekingcapture(b, n, movelist, i + 8);
                b[i + 4] = tmp;
            }
        }
        move = orgmove;
        if ((b[i + 5] & BLACK) != 0) {
            if ((b[i + 10] & FREE) != 0) {
                move.n++;
                m = (WHITE | KING);
                m = m << 8;
                m += FREE;
                m = m << 8;
                m += i + 10;
                move.m[1] = m;
                m = FREE;
                m = m << 8;
                m += b[i + 5];
                m = m << 8;
                m += i + 5;
                move.m[move.n - 1] = m;
                found = true;
                movelist[n] = move;
                tmp = b[i + 5];
                b[i + 5] = FREE;
                whitekingcapture(b, n, movelist, i + 10);
                b[i + 5] = tmp;
            }
        }
        if (!found) n++;

        return n;
    }

    int testcapture(int[] b, int color)
/*----------> purpose: test if color has a capture on b
----------> version: 1.0
----------> date: 25th october 97 */ {
        int i;

        /*
        #ifdef STATISTICS
        testcaptures++;
        #endif*/

        if (color == BLACK) {
            for (i = 5; i <= 40; i++) {
                if ((b[i] & BLACK) != 0) {
                    if ((b[i] & MAN) != 0) {
                        if ((b[i + 4] & WHITE) != 0) {
                            if ((b[i + 8] & FREE) != 0)
                                return (1);
                        }
                        if ((b[i + 5] & WHITE) != 0) {
                            if ((b[i + 10] & FREE) != 0)
                                return (1);
                        }
                    } else /* b[i] is a KING */ {
                        if ((b[i + 4] & WHITE) != 0) {
                            if ((b[i + 8] & FREE) != 0)
                                return (1);
                        }
                        if ((b[i + 5] & WHITE) != 0) {
                            if ((b[i + 10] & FREE) != 0)
                                return (1);
                        }
                        if ((b[i - 4] & WHITE) != 0) {
                            if ((b[i - 8] & FREE) != 0)
                                return (1);
                        }
                        if ((b[i - 5] & WHITE) != 0) {
                            if ((b[i - 10] & FREE) != 0)
                                return (1);
                        }
                    }
                }
            }
        } else /* color is WHITE */ {
            for (i = 5; i <= 40; i++) {
                if ((b[i] & WHITE) != 0) {
                    if ((b[i] & MAN) != 0) {
                        if ((b[i - 4] & BLACK) != 0) {
                            if ((b[i - 8] & FREE) != 0)
                                return (1);
                        }
                        if ((b[i - 5] & BLACK) != 0) {
                            if ((b[i - 10] & FREE) != 0)
                                return (1);
                        }
                    } else /* b[i] is a KING */ {
                        if ((b[i + 4] & BLACK) != 0) {
                            if ((b[i + 8] & FREE) != 0)
                                return (1);
                        }
                        if ((b[i + 5] & BLACK) != 0) {
                            if ((b[i + 10] & FREE) != 0)
                                return (1);
                        }
                        if ((b[i - 4] & BLACK) != 0) {
                            if ((b[i - 8] & FREE) != 0)
                                return (1);
                        }
                        if ((b[i - 5] & BLACK) != 0) {
                            if ((b[i - 10] & FREE) != 0)
                                return (1);
                        }
                    }
                }
            }
        }
        return (0);
    }
}