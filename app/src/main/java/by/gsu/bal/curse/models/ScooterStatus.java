package by.gsu.bal.curse.models;

/**
 * Scooter status.<br>
 * FREE - свободный самокат. Можно букать или арендовать.<br>
 * BOOKED - Забронированный самокат, арендовать модет только кто забукал.<br>
 * BUSY - Кем-то арендован.<br>
 * INACTIVE - Деактивирован. Нельзя забукать или арендовать.
 */
public enum ScooterStatus {
    FREE,
    BOOKED,
    BUSY,
    INACTIVE
}
