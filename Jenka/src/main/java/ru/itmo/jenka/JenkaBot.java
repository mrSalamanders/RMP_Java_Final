package ru.itmo.jenka;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

//Написать бота для Telegram, который будет уметь:
//        1. Сообщать текущую погоду и прогноз на сутки в ответ на присланное
//        местоположение.
//        2. Присылать прогноз погоды на сутки каждый день, если пользователь подписался
//        на ежедневную рассылку. Подписка должна происходить с помощью команды
//        /subscribe. Также должна быть предусмотрена возможность отписки (/unsubscribe).
//        My chat ID 947638429

public class JenkaBot extends TelegramLongPollingBot {

    public DbHandler dbh = new DbHandler();

    public void onUpdateReceived(Update update) {
        new Thread(() -> {

            Long currentChatId = update.getMessage().getChatId();
            String currentText = update.getMessage().getText();
            OpenWeatherMapJsonParser wp = new OpenWeatherMapJsonParser();

            if (update.hasMessage()) {
                if (update.getMessage().hasLocation()) {
                    Float currentLat = update.getMessage().getLocation().getLatitude();
                    Float currentLon = update.getMessage().getLocation().getLongitude();

                    System.out.println(update.getMessage().getLocation().toString());
                    try {
                        dbh.updateSub(currentChatId, currentLat, currentLon);
                        execute(new SendMessage().setText(wp.getReadyForecast(currentLat, currentLon)).setChatId(currentChatId));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                if (update.getMessage().hasText()) {
                    if (currentText.equals("/start")) {
                        try {
                            dbh.insertSub(currentChatId);
                            execute(new SendMessage().setChatId(currentChatId).setText("Здравствуй =)! Дай мне МЕСТОПОЛОЖЕНИЕ, а Я пришлю тебе прогноз. Можешь подписаться на прогноз командой /subscribe (/unsubscribe – отписаться)"));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    if (currentText.equals("/subscribe")) {
                        try {
                            dbh.updateSub(currentChatId, true);
                            execute(new SendMessage().setChatId(currentChatId).setText("Я записал тебя в книжечку =) (" + currentChatId + ")"));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        System.out.println(currentChatId + " subscribed");
                    }
                    if (currentText.equals("/unsubscribe")) {
                        try {
                            dbh.updateSub(currentChatId, false);
                            execute(new SendMessage().setChatId(currentChatId).setText("Я вычеркнул тебя из завещания =( (" + currentChatId + ")"));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        System.out.println(currentChatId + " unsubscribed");
                    }
                    if (currentText.equals("/broadcast 25565")) {
                        System.out.println("Start broadcasting");
                        broadcast();
                        System.out.println("Done broadcasting");
                    }
                }
            }
        }).start();
    }

    public synchronized void broadcast() {
        OpenWeatherMapJsonParser wp = new OpenWeatherMapJsonParser();
        List<Subscriber> subs = dbh.getSubscribers();
        if (subs.isEmpty()) {
            return;
        }
        for (Subscriber s : subs) {
            if (s.isSubscribed) {
                if (s.latitude == 200 || s.longitude == 200) {
                    try {
                        execute(new SendMessage().setChatId(s.chatId).setText("Я не могу прислать тебе прогноз, так как ты ни разу не присылал МЕСТОПОЛОЖЕНИЕ =("));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        execute(new SendMessage().setChatId(s.chatId).setText(wp.getReadyForecast(s.latitude, s.longitude)));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String getBotUsername() {
        return "JenkaWeatherBot";
    }

    public String getBotToken() {
        return "1061672052:AAEPiJUAJqktnuE5u4m8Z7lZJ4CXk3Z2Fnk";
    }

}
