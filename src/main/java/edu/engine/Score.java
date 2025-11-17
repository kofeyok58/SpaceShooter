package edu.engine;

public final class Score {
    private int value = 0;

    public void add(int delta) { value += delta; }
    public int  get()          { return value;    }
    public void reset()        { value = 0;       }
}

