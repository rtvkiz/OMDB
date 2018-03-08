package com.example.android.imdb;

/**
 * Created by Ritvik Arya on 29-01-2018.
 */

public class Event {
    public String name1;
    public String runtime;
    public String genre;
    public String url;
    public String plot;
    public String resp;
    public boolean yes=false;
    public Event(String name_1,String run_1,String gen_1,String url,String plot){
        this.name1=name_1;
        this.runtime=run_1;
        this.genre=gen_1;
        this.url=url;
        this.plot=plot;
       // this.resp=res;
        //this.yes=no;

    }
}
