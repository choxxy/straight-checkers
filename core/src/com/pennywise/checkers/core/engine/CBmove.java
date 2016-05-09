package com.pennywise.checkers.core.engine;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Joshua.Nabongo on 3/29/2016.
 */
public class CBMove implements Serializable {
    int jumps;				/* how many jumps are there in this move? */
    int newpiece;        /* what type of piece appears on to */
    int oldpiece;        /* what disappears on from */
    public Point from, to; /* coordinates of the piece - in 8x8 notation!*/
    public Point[] path = new Point[12]; /* intermediate path coordinates of the moving piece */
    public Point[] del = new Point[12];  /* squares whose pieces are deleted after the move */
    public int[] delpiece = new int[12]; /* what is on these squares */
    public boolean forcedCapture;
    public boolean onlyMove;
    public boolean noLegalMove;

    @Override
    public String toString() {
        return "CBMove{" +
                "jumps=" + jumps +
                ", newpiece=" + newpiece +
                ", oldpiece=" + oldpiece +
                ", from=" + from +
                ", to=" + to +
                ", path=" + Arrays.toString(path) +
                ", del=" + Arrays.toString(del) +
                ", delpiece=" + Arrays.toString(delpiece) +
                ", forcedCapture=" + forcedCapture +
                ", onlyMove=" + onlyMove +
                ", noLegalMove=" + noLegalMove +
                '}';
    }
}
