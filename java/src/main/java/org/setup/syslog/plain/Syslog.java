package plain;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by a on 4/17/14.
 */

class SyslogMessage {
    String timestamp;
    String hostname;
    String process;
    int    pid;
    String message;

    SyslogMessage(String timestamp, String hostname, String process, int pid, String message) {
        this.timestamp = timestamp;
        this.hostname  = hostname;
        this.process   = process;
        this.pid       = pid;
        this.message   = message;
    }
}

public class Syslog {

    public static void main(String[] args) {

        Pattern reSystemLog = Pattern.compile("(?<timestamp>^[A-Za-z0-9, ]+\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?)\\s+(?<host>\\S+)\\s+(?<process>[^\\[]+)\\[(?<pid>\\d+)\\]\\s*:?\\s*(?<message>.*)");

        // NB provide a sample log
        String systemLogFile = "/var/log/system.log";
        InputStream    fis;
        BufferedReader br;
        String         line;

        List<SyslogMessage> events = new ArrayList<>();

        try {
            fis = new FileInputStream(systemLogFile);
            br  = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
            while ((line = br.readLine()) != null) {
                Matcher m = reSystemLog.matcher(line);
                List<SyslogMessage> result = new ArrayList<>();
                if (m.find()) {
                    try {
                        //DateTime timestamp = formatter.parseDateTime(dt);
                        int pid = Integer.parseInt(m.group("pidS"));
                        SyslogMessage evt = new SyslogMessage(
                                m.group("timestamp"), m.group("host"),
                                m.group("process"), pid, m.group("message"));

                       events.add(evt);

                   } catch (IllegalArgumentException e) {}
                }
            }

            Map<String, Integer> procs = new HashMap<>();
            for (SyslogMessage event: events) {
                String key = event.process;
                procs.put(key,procs.getOrDefault(key,0)+1);
            }
            List<Map.Entry<String,Integer>> nprocs = new ArrayList<>(procs.entrySet());
            Collections.sort(nprocs, new Comparator<Map.Entry<String,Integer>>() {
                public int compare(Map.Entry<String,Integer> a, Map.Entry<String, Integer> b) {
                        return b.getValue().compareTo(a.getValue());
                    }
                }
            );
            int n = 0;
            for (Map.Entry<String,Integer> nproc: nprocs.subList(0,10)) {
                System.out.println((n++)+": "+nproc.getKey()+" ("+nproc.getValue()+")");
            }

        } catch (FileNotFoundException e) {
            System.out.println("no such file:" + "/var/log/system.log");
        } catch (IOException e) {
            System.out.println("IO error");
        }
    }
}
