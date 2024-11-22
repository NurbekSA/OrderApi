package amanzat.com.persistence.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    private final Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String save(String key, String value) {
        logger.info("SAVE: Начало сохранения данных в Redis. Ключ: {}", key);

        JSONObject jsonObject = new JSONObject(value);
        String role = jsonObject.getString("role");
        Long space = jsonObject.getLong("expired_at");

        logger.debug("SAVE: Извлечены данные из JSON. Role: {}, Space: {}", role, space);

        // Сохраняем объект в Redis с указанием времени жизни
        redisTemplate.opsForValue().set(key, jsonObject.toString(), space, java.util.concurrent.TimeUnit.MILLISECONDS);
        logger.info("SAVE: Данные успешно сохранены в Redis. Ключ: {}, Время жизни: {} ms", key, space);

        return role;
    }

    public String get(String key) {
        logger.info("GET: Запрос данных из Redis по ключу: {}", key);

        String value = redisTemplate.opsForValue().get(key); // Тип данных String
        if (value != null) {
            logger.debug("GET: Данные найдены в Redis. Ключ: {}, Значение: {}", key, value);
        } else {
            logger.warn("GET: Данные не найдены в Redis по ключу: {}", key);
        }

        return value;
    }
}
