package utils;

import interfaces.Sample;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * LSINF1102 P3 Projet demo
 *
 * @author Maxime Piraux, maxime.piraux@student.uclouvain.be
 */
public class MySampleFileIO implements interfaces.SampleFileIO {

    private BufferedWriter writer;
    private String path;
    private boolean append;

    public MySampleFileIO(String path, boolean append) throws IOException {
        this.writer = null;
        open(path);
        this.append = append;
    }

    @Override
    public void writeSample(Sample sample) {
        try {
            if (writer == null) {
                this.writer = new BufferedWriter(new FileWriter(path, append));
            }
            this.writer.write(new MySample(sample).toString());
            this.writer.newLine();
            this.writer.flush();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public List<Sample> readSample(Instant from, Instant to) {
        try {
            BufferedReader bf = new BufferedReader(new FileReader(path));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            String line = null;
            boolean collect = false;
            bf.mark(500);
            while (!collect && null != ((line = bf.readLine()))) {
                Instant time = new Instant(sdf.parse(line.split("\t")[0], new ParsePosition(0)));
                if (time.isAfter(from)) {
                    collect = true;
                } else {
                    bf.mark(500);
                }
            }
            bf.reset();
            boolean t = true;
            MySample sample = null;
            List<Sample> samples = new ArrayList<Sample>();
            boolean stop = false;
            while (collect && !stop && null != ((line = bf.readLine()))) {
                if (new Instant(sdf.parse(line.split("\t")[0], new ParsePosition(0))).isAfter(to)) {
                    stop = true;
                }
                String[] split = line.split("\t");
                if (t && MySample.isValidString(line)) {
                    sample = new MySample(Double.parseDouble(split[3]), 0, new Instant(sdf.parse(split[0], new ParsePosition(0))), Integer.parseInt(split[2]) == 0);
                } else {
                    if (sample != null && MySample.isValidString(line)) {
                        sample.setHum((float) Double.parseDouble(line.split("\t")[2]));
                        samples.add(sample);
                    }
                    sample = null;
                }
                t = !t;
            }
            return samples;
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return null;
    }

    @Override
    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    private void setFile(String string) {
        this.path = string;
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    @Override
    public void open(String string) {
        setFile(string);
    }

    @Override
    public List<Sample> readSample() {
        return readSample(Instant.MIN, Instant.MAX);
    }
}
