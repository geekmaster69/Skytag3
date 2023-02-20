package com.example.skytag3.base

object Constans {
    const val BASE_URL = "https://www.poderonline.net/paginas/skymeduza2/aplicacion/servicio/web/"
    const val TAG_KEY_LOCATIO_PATH = "posicion/PositionService.php"
    const val TAG_KEY_LOGIN_PATH = "tagkey/TagkeyService.php"

    @JvmField val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
        "Verbose WorkManager Notifications"
    const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
        "Shows notifications whenever work starts"
    @JvmField val NOTIFICATION_TITLE: CharSequence = "Skytag"
    const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
    const val NOTIFICATION_ID = 1

    // The name of the image manipulation work
    const val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"

    // Other keys
    const val OUTPUT_PATH = "blur_filter_outputs"
    const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
    const val TAG_OUTPUT = "OUTPUT"

    const val DELAY_TIME_MILLIS: Long = 3000
}