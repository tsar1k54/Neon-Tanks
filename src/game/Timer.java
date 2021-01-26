package game;

public class Timer {
    private long lastTime;
    private float elapsedTime;
    private boolean stopped = false;

    public Timer(){
        elapsedTime = 0.0f;
        lastTime = System.currentTimeMillis();
    }

    public float getElapsedTimeSec(){
        if (!stopped)
            return (float) (System.currentTimeMillis() - lastTime) / 1000.0f + elapsedTime;
        else return elapsedTime;
    }

    public void start(){
        lastTime = System.currentTimeMillis();
        stopped = false;
    }

    public void stop(){
        elapsedTime = getElapsedTimeSec();
        stopped = true;
    }

    public void restart(){
        elapsedTime = 0.0f;
        start();
    }
}
