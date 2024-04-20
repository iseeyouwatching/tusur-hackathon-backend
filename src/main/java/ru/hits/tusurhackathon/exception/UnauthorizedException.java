package ru.hits.tusurhackathon.exception;

/**
 * Исключение, которое выбрасывается, когда пользователь не авторизован.
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Создает новый экземпляр исключения с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public UnauthorizedException(String message) {
        super(message);
    }

}
