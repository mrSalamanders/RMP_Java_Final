package ru.itmo.jenka;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static final int TIMER_PERIOD = 1000 * 15;
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        Timer t = new Timer();
        try {
            JenkaBot jb = new JenkaBot();
            telegramBotsApi.registerBot(jb);
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    jb.broadcast();
                }
            }, 0, TIMER_PERIOD);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
