package org.trace.utils;

public class StatusStatistics {
    int status2xx = 0;    
    int status4xx = 0;
    int status5xx = 0;

    public void setStatus2xx(int statut){
        this.status2xx = statut;
    }

    public int getStatux2xx(){
        return this.status2xx;
    }

    public void addStatut2xx(){
        this.status2xx += 1;
    }

    public void setStatus4xx(int statut){
        this.status4xx = statut;
    }

    public int getStatux4xx(){
        return this.status4xx;
    }

    public void addStatut4xx(){
        this.status4xx += 1;
    }

    public void setStatus5xx(int statut){
        this.status5xx = statut;
    }

    public int getStatux5xx(){
        return this.status5xx;
    }

    public void addStatut5xx(){
        this.status5xx += 1;
    }
}
