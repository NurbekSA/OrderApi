package amanzat.com;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class);
        System.out.println("Hello world!");
    }
}

// todo: Запросы на бокс сервис -
// Бронирование бокса и получение стоймости за неделю
// Получение стоумости заказа

// todo: Запросы на платежный сервис
// Запрос на платеж с результатом



// метод пост работает
// метод гет по юзер аиди работает
// Get Bu Box id working
// Get All work
// Extend work


// todo нужен ли гет олл с правами админа