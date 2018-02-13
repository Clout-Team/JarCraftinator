package com.cloutteam.jarcraftinator.world.terrain.noise;

public abstract class Noise {
    public int octaves = 0;
    public double scale = 0;
    public double persistence = 0;

    public Noise () {
    }

    public void set (int octaves, double scale, double persistence) {
        this.octaves = octaves;
        this.scale = scale;
        this.persistence = persistence;
    }

    public double octavedNoise(int x, int z, int y) {
        double noiseSum = 0;
        double weight = 1;
        double max = 0;
        double freq = 1;
        for (int oct = 0; oct < octaves; oct++) {
            noiseSum += noise((double) x*scale*freq, (double) z*scale*freq, (double) y*scale*freq) * weight;
            max += weight;
            weight *= persistence;
            freq *= 2;
        }
        return noiseSum/max;
    }

    public abstract double noise(double x, double y, double z);
}
