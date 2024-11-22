package amanzat.com.standalone;

import amanzat.com.exception.KafkaResponseFailedException;
import amanzat.com.kafka.proto.tutorial.KafkaMessage;
import amanzat.com.persistence.service.RedisService;
import amanzat.com.kafka.KafkaRequestReply;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationInterceptor.class);
    private final RedisService redisService;
    private final KafkaRequestReply requestReply;

    public JwtAuthenticationInterceptor(RedisService redisService, KafkaRequestReply requestReply) {
        this.redisService = redisService;
        this.requestReply = requestReply;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        logger.info("PRE_HANDLE: Начало обработки запроса");
//        if (handler instanceof HandlerMethod) {
//            String token = null;
//            Cookie[] cookies = request.getCookies();
//
//            if (cookies != null) {
//                for (Cookie cookie : cookies) {
//                    if ("jwtToken".equals(cookie.getName())) {
//                        token = cookie.getValue();
//                        logger.debug("PRE_HANDLE: Найден токен в cookies");
//                        break;  // Прекращаем цикл после нахождения нужного токена
//                    }
//                }
//            }
//
//            if (token != null) {
//                logger.debug("PRE_HANDLE: Проверка токена через VALIDATE_AND_EXTRACT_ROLES");
//                String role = validateAndExtractRoles(token);
//                logger.debug("PRE_HANDLE: Токен успешно валидирован. Роль пользователя: {}", role);
//
//                request.setAttribute("role", role);
//
//                HandlerMethod method = (HandlerMethod) handler;
//                RoleRequired roleRequired = method.getMethodAnnotation(RoleRequired.class);
//
//                if (roleRequired != null) {
//                    logger.debug("PRE_HANDLE: Проверка роли на соответствие аннотации @RoleRequired");
//                    if (!userHasRequiredRole(role, roleRequired.value())) {
//                        logger.warn("PRE_HANDLE: Пользователь не имеет необходимых прав доступа. Доступ запрещен.");
//                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                        return false;
//                    }
//                }
//                logger.info("PRE_HANDLE: Доступ разрешен. Продолжение обработки.");
//                return true;
//            }
//        }
//        logger.warn("PRE_HANDLE: Токен отсутствует или недействителен. Запрос отклонён.");
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        return false;
        validateAndExtractRoles("token");
        return true;

    }

    private boolean userHasRequiredRole(String userRole, String[] requiredRoles) {
        logger.debug("USER_HAS_REQUIRED_ROLE: Проверка наличия роли у пользователя.");
        for (String requiredRole : requiredRoles) {
            if (userRole.equals(requiredRole)) {
                logger.debug("USER_HAS_REQUIRED_ROLE: Роль '{}' соответствует требуемой.", requiredRole);
                return true;
            }
        }
        logger.warn("USER_HAS_REQUIRED_ROLE: Роль '{}' не соответствует ни одной из требуемых.", userRole);
        return false;
    }

    private String validateAndExtractRoles(String token) {
        logger.info("VALIDATE_AND_EXTRACT_ROLES: Попытка найти токен в Redis");
        String value = redisService.get(token);

        if (value != null) {
            logger.debug("VALIDATE_AND_EXTRACT_ROLES: Токен найден в Redis. Извлечение роли.");
            JSONObject jsonObject = new JSONObject(value);
            return jsonObject.getString("role");
        } else {
            logger.info("VALIDATE_AND_EXTRACT_ROLES: Токен не найден в Redis. Запрос в Kafka для проверки токена.");
            CompletableFuture<KafkaMessage> completableUserResponse = requestReply.sendRequest(
                    "",
                    "amanzat.user-api.validateToken",
                    "amanzat.order-api.response"
            );
            KafkaMessage userResponse;
            try {
                userResponse = completableUserResponse.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new KafkaResponseFailedException(
                        "VALIDATE_AND_EXTRACT_ROLES: Прерывание потока (InterruptedException)."
                );
            } catch (ExecutionException e) {
                throw new KafkaResponseFailedException(
                        "VALIDATE_AND_EXTRACT_ROLES: Ошибка выполнения (ExecutionException). Сообщение: "
                                + e.getCause().getMessage()
                );
            }

            if (userResponse.getRequestResult() == KafkaMessage.RequestResult.FAILED) {
                throw new KafkaResponseFailedException("VALIDATE_AND_EXTRACT_ROLES: Токен не может быть проверен.");
            }

            // Сохранение роли в Redis
            String role = redisService.save(token, userResponse.getBody());
            logger.debug("VALIDATE_AND_EXTRACT_ROLES: Токен сохранён в Redis с ролью '{}'.", role);
            return role;
        }
    }
}
