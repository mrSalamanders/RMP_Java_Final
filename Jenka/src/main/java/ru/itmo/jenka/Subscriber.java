package ru.itmo.jenka;

public class Subscriber {
    public Long chatId;
    public Boolean isSubscribed;
    public Float latitude;
    public Float longitude;
    public Subscriber(Long chatId, Boolean isSubscribed, Float latitude, Float longitude) {
        this.chatId = chatId;
        this.isSubscribed = isSubscribed;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
