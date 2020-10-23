package dev.ragnarok.fenrir.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({PhotoSize.S, PhotoSize.M, PhotoSize.X, PhotoSize.Y,
        PhotoSize.Z, PhotoSize.W, PhotoSize.O, PhotoSize.P, PhotoSize.Q, PhotoSize.R})
@Retention(RetentionPolicy.SOURCE)
public @interface PhotoSize {
    /**
     * пропорциональная копия изображения с максимальной шириной 75px
     */
    int S = 1;

    /**
     * пропорциональная копия изображения с максимальной шириной 130px
     */
    int M = 2;

    /**
     * пропорциональная копия изображения с максимальной шириной 604px
     */
    int X = 3;

    /**
     * пропорциональная копия изображения с максимальной стороной 807px
     */
    int Y = 4;

    /**
     * пропорциональная копия изображения с максимальным размером 1280x1024
     */
    int Z = 5;

    /**
     * пропорциональная копия изображения с максимальным размером 2560x2048px
     */
    int W = 6;

    /**
     * если соотношение "ширина/высота" исходного изображения меньше или равно 3:2,
     * то пропорциональная копия с максимальной шириной 130px.
     * Если соотношение "ширина/высота" больше 3:2, то копия обрезанного слева изображения
     * с максимальной шириной 130px и соотношением сторон 3:2
     */
    int O = 7;

    /**
     * если соотношение "ширина/высота" исходного изображения меньше или равно 3:2,
     * то пропорциональная копия с максимальной шириной 200px.
     * Если соотношение "ширина/высота" больше 3:2, то копия обрезанного слева и
     * справа изображения с максимальной шириной 200px и соотношением сторон 3:2
     */
    int P = 8;

    /**
     * если соотношение "ширина/высота" исходного изображения меньше или равно 3:2,
     * то пропорциональная копия с максимальной шириной 320px.
     * Если соотношение "ширина/высота" больше 3:2, то копия обрезанного слева и
     * справа изображения с максимальной шириной 320px и соотношением сторон 3:2
     */
    int Q = 9;

    /**
     * если соотношение "ширина/высота" исходного изображения меньше или равно 3:2,
     * то пропорциональная копия с максимальной шириной 510px.
     * Если соотношение "ширина/высота" больше 3:2, то копия обрезанного слева
     * и справа изображения с максимальной шириной 510px и соотношением сторон 3:2
     */
    int R = 10;
}
