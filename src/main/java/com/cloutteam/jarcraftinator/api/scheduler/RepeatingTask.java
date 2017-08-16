/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cloutteam.jarcraftinator.api.scheduler;

/**
 *
 * @author masterdoctor
 */
public interface RepeatingTask {
    
    /**
     * The code that you want to repeat.
     */
    public void run();
    
}
