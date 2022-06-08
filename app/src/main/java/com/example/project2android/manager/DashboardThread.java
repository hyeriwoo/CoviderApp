package com.example.project2android.manager;

import java.util.concurrent.locks.ReentrantLock;

public class DashboardThread extends Thread {
    private DashboardManager dashboardManager;
    private ReentrantLock lock;
    private char type;

    public DashboardThread(DashboardManager dashboardManager, ReentrantLock lock, char type) {
        this.dashboardManager = dashboardManager;
        this.lock = lock;
        this.type = type;
    }

    public char getType() { return this.type; }

    @Override
    public void run() {


        lock.lock();

        try {
            dashboardManager.setQueryType(type);

            if (type == 0) {
                dashboardManager.updateBuildings();
            } else if (type == 1) {
                dashboardManager.updateCourses();
            }

            while(!dashboardManager.isQueryFinish()) {
                Thread.sleep(500);
            }

            dashboardManager.setQueryFinish(false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
