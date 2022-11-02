package ir.ayantech.whygoogle.dateTime

data class DateTime(val ISODateTime: String) {
    val gregorianYear: Int
    val gregorianMonth: Int
    val gregorianDay: Int
    val timeHours: Int
    val timeMinutes: Int
    val timeSeconds: Int
    val solarYear: Int
    val solarMonth: Int
    val solarDay: Int

    init {
        ISODateTime.split("T").let {
            it[0].split("-").let {
                gregorianYear = it[0].toInt()
                gregorianMonth = it[1].toInt()
                gregorianDay = it[2].toInt()
            }
            it[1].split(":").let {
                timeHours = it[0].toInt()
                timeMinutes = it[1].toInt()
                timeSeconds = it[2].toInt()
            }
            val differenceBetweenDays: Int

            var date: Int = 0
            var month: Int = 0
            var year: Int = 0

            val notLeapYear = arrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
            val isLeapYear = arrayOf(0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335)

            if ((gregorianYear % 4) != 0) {
                date = notLeapYear[gregorianMonth - 1] + gregorianDay

                if (date > 79) {
                    date -= 79
                    if (date <= 186) {
                        when (date % 31) {
                            0 -> {
                                month = date / 31
                                date = 31
                            }
                            else -> {
                                month = (date / 31) + 1
                                date = (date % 31)
                            }
                        }
                        year = gregorianYear - 621;
                    } else {
                        date -= 186;

                        when (date % 30) {
                            0 -> {
                                month = (date / 30) + 6
                                date = 30
                            }
                            else -> {
                                month = (date / 30) + 7
                                date = (date % 30)
                            }
                        }
                        year = gregorianYear - 621
                    }
                } else {
                    differenceBetweenDays =
                        if ((gregorianYear > 1996) && (gregorianYear % 4) == 1) 11
                        else 10

                    date += differenceBetweenDays

                    when (date % 30) {
                        0 -> {
                            month = (date / 30) + 9
                            date = 30
                        }
                        else -> {
                            month = (date / 30) + 10
                            date = (date % 30)
                        }
                    }
                    year = gregorianYear - 622;
                }
            } else {
                date = isLeapYear[gregorianMonth - 1] + gregorianDay

                differenceBetweenDays =
                    if (gregorianYear >= 1996) 79
                    else 80

                if (date > differenceBetweenDays) {
                    date -= differenceBetweenDays

                    if (date <= 186) {
                        when (date % 31) {
                            0 -> {
                                month = (date / 31)
                                date = 31
                            }
                            else -> {
                                month = (date / 31) + 1
                                date = (date % 31)
                            }
                        }
                        year = gregorianYear - 621
                    } else {
                        date -= 186

                        when (date % 30) {
                            0 -> {
                                month = (date / 30) + 6
                                date = 30
                            }
                            else -> {
                                month = (date / 30) + 7
                                date = (date % 30)
                            }
                        }
                        year = gregorianYear - 621
                    }
                } else {
                    date += 10

                    when (date % 30) {
                        0 -> {
                            month = (date / 30) + 9
                            date = 30
                        }
                        else -> {
                            month = (date / 30) + 10
                            date = (date % 30)
                        }
                    }
                    year = gregorianYear - 622;
                }
            }

            solarYear = year
            solarMonth = month
            solarDay = date
        }
    }

    fun Int.getSolarMonthName() = when (this) {
        1 -> "فرودین"
        2 -> "اردیبهشت"
        3 -> "خرداد"
        4 -> "تیر"
        5 -> "مرداد"
        6 -> "شهریور"
        7 -> "مهر"
        8 -> "آبان"
        9 -> "آذر"
        10 -> "دی"
        11 -> "بهمن"
        else -> "اسفند"
    }
}