package stws.chatstocker.utils


import java.text.SimpleDateFormat
import java.util.*


class DateTimeUtils {
companion object{
    fun convertMillisecondtodate( millis:Long,dateFormat:String):String{
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(millis)
        return formatter.format(calendar.getTime())
    }
}
}