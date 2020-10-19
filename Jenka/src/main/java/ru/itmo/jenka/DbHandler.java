package ru.itmo.jenka;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DbHandler {
    Connection connection = null;

    public void updateSub(Long chatId, Boolean isSubscribed) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:subscribers.db");
            PreparedStatement statement = this.connection.prepareStatement("update person set isSubscribed = ? where chatId = ?");
            statement.setObject(1, isSubscribed);
            statement.setObject(2, chatId);
            statement.execute();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally {
            try {
                if(connection != null)
                    connection.close();
            } catch(SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void updateSub(Long chatId, Float latitude, Float longitude) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:subscribers.db");
            PreparedStatement statement = this.connection.prepareStatement("update person set latitude = ?, longitude = ? where chatId = ?");
            statement.setObject(1, latitude);
            statement.setObject(2, longitude);
            statement.setObject(3, chatId);
            statement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally {
            try {
                if(connection != null)
                    connection.close();
            } catch(SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void insertSub(Long chatId) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:subscribers.db");
            PreparedStatement statement = this.connection.prepareStatement("insert into person values (?, false, 200, 200)");
            statement.setObject(1, chatId);
            statement.execute();
            if(connection != null)
                connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally {
            try {
                if(connection != null)
                    connection.close();
            } catch(SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public List<Subscriber> getSubscribers() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:subscribers.db");
            Statement statement = this.connection.createStatement();
            List<Subscriber> Subscribers = new ArrayList<Subscriber>();
            ResultSet resultSet = statement.executeQuery("select * from person");
            while (resultSet.next()) {
                Subscribers.add(new Subscriber(resultSet.getLong("chatId"),
                        resultSet.getBoolean("isSubscribed"),
                        resultSet.getFloat("latitude"),
                        resultSet.getFloat("longitude")));
            }
            return Subscribers;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
