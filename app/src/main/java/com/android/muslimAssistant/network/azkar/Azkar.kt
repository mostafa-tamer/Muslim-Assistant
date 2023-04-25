package com.android.muslimAssistant.network.azkar

import azkar.*

data class Azkar(
    val messengersDuaa: List<أدعيةالأنبياء>,
    val quraanDoaa: List<أدعيةقرآنية>,
    val wakingAzkar: List<أذكارالاستيقاظ>,
    val morningAzkar: List<Any>,
    val eveningAzkar: List<أذكارالمساء>,
    val sleepingAzkar: List<أذكارالنوم>,
    val azkarAfterPrayer: List<أذكاربعدالسلاممنالصلاةالمفروضة>,
    val tasabeeh: List<تسابيح>
)