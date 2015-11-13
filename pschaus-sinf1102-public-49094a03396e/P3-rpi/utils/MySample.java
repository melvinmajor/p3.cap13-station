/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import interfaces.Sample;
import java.text.SimpleDateFormat;
import java.util.Locale;
import time.Instant;

/**
 * LSINF1102 P3 Projet demo
 *
 * @author Maxime Piraux, maxime.piraux@student.uclouvain.be
 */
public class MySample implements interfaces.Sample {

    private float temp;
    private float hum;
    private Instant time;
    private boolean isCelsius;

    public MySample(double temp, double hum, Instant time, boolean isCelsius) {
        this.temp = (float) temp;
        this.hum = (float) hum;
        this.time = time;
        this.isCelsius = isCelsius;
    }

    public MySample(Sample sample) {
        this.temp = sample.getTemperature();
        this.hum = sample.getHumidity();
        this.time = sample.getTime();
        this.isCelsius = sample.isCelsius();
    }

    @Override
    public Instant getTime() {
        return time;
    }

    @Override
    public float getTemperature() {
        return temp;
    }

    @Override
    public float getHumidity() {
        return hum;
    }

    @Override
    public boolean isCelsius() {
        return isCelsius;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public void setHum(float hum) {
        this.hum = hum;
    }

    @Override
    public String toString() {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(this.time.toDate());
        StringBuilder builder = new StringBuilder();
        builder.append(timestamp);
        builder.append("\t0");
        builder.append('\t').append(isCelsius() ? "0" : "1");
        builder.append('\t').append(String.format(Locale.ENGLISH, "%3.1f", getTemperature()));
        builder.append('\t').append(getSuffix(builder.toString()));
        String line1 = builder.toString();
        builder = new StringBuilder();
        builder.append(timestamp);
        builder.append("\t1");
        builder.append('\t').append(String.format(Locale.ENGLISH, "%3.1f", getHumidity()));
        builder.append('\t').append(getSuffix(builder.toString()));
        String line2 = builder.toString();
        return new StringBuilder().append(line1).append('\n').append(line2).toString();
    }

    static int getSuffix(String str) {
        int sum = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isDigit(c)) {
                sum += Character.digit(c, 10);
            }
        }
        return sum % 13;
    }

    static boolean isValidString(String str) {
        return getSuffix(str.substring(0, str.length() - 1)) == getSuffix(str.substring(str.length() - 1));
    }
}
