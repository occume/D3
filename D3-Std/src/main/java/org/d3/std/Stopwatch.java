package org.d3.std;

/*************************************************************************
 *  Compilation:  javac Stopwatch.java
 *
 *
 *************************************************************************/

/**
 *  The <tt>Stopwatch</tt> data type is for measuring
 *  the time that elapses between the start and end of a
 *  programming task (wall-clock time).
 *
 *  See {@link StopwatchCPU} for a version that measures CPU time.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */


public class Stopwatch { 

    private final long start;

    /**
     * Initialize a stopwatch object.
     */
    private Stopwatch() {
        start = System.currentTimeMillis();
    }

    public static Stopwatch newStopwatch(){
    	return new Stopwatch();
    }

    /**
     * Returns the elapsed time (in seconds) since this object was created.
     */
    public double elapsedTime() {
        long now = System.currentTimeMillis();
        return (now - start) / 1000.0;
    }
    
    /**
     * Returns the elapsed time (in seconds) since this object was created.
     */
    public long longTime() {
        long now = System.currentTimeMillis();
        return (now - start);
    }

} 