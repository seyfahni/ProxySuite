package de.sabbertran.proxysuite.bungee.handlers;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import de.sabbertran.proxysuite.bungee.ProxySuite;

public class AnnouncementHandler {
    private ProxySuite main;
    private ArrayList<String> announcements;
    private int currentAnnouncement;

    public AnnouncementHandler(ProxySuite main) {
        this.main = main;
        announcements = new ArrayList<String>();
        currentAnnouncement = -1;
    }

    public void readAnnouncementsFromFile() {
        announcements = new ArrayList<String>();
        File f = new File(main.getDataFolder(), "announcements.yml");
        try (BufferedReader read = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"))) {
            String line;
            while ((line = read.readLine()) != null) {
                line = line.trim();
                if (line.equals("''"))
                    announcements.add("");
                else if (!line.equals("") && !line.startsWith("#"))
                    announcements.add(line);
            }
        } catch (IOException e) {
            main.getLogger().log(Level.SEVERE, null, e);
        }
        currentAnnouncement = 0;
    }

    public void startScheduler() {
        int interval = main.getConfig().getInt("ProxySuite.Announcements.Interval");
        main.getProxy().getScheduler().schedule(main, new Runnable() {
            public void run() {
                broadcastMessage();
            }
        }, interval, interval, TimeUnit.SECONDS);
    }

    private void broadcastMessage() {
        if (!announcements.isEmpty() && main.getProxy().getPlayers().size() > 0) {
            String message;
            if (!main.getConfig().getBoolean("ProxySuite.Announcements.Random")) {
                message = announcements.get(currentAnnouncement);
                currentAnnouncement++;
                if (currentAnnouncement == announcements.size())
                    currentAnnouncement = 0;
            } else {
                Random r = new Random();
                message = announcements.get(r.nextInt(announcements.size()));
            }

            if (!message.trim().equals("")) {
                if (message.startsWith("{") && message.endsWith("}"))
                    message = "[" + main.getConfig().getString("ProxySuite.Announcements.PrefixJson") + "," +
                            message + "]";
                else
                    message = main.getConfig().getString("ProxySuite.Announcements.Prefix") + message;

                main.getMessageHandler().broadcast(message);
            }
        }
    }
}
