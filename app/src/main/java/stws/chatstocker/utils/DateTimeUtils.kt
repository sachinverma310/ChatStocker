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

    fun convertDateTimetoDay( dateTime:Long,dateFormat:String):String{
        val formatter = SimpleDateFormat(dateFormat)
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(dateTime)
//        val inFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//        val date=inFormat.parse(dateTime.toString())
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        return formatter.format(dateTime)
    }

    fun convertStringtoMillis(dateString: String,format:String):Long{

        val sdf = SimpleDateFormat(format)
        val date = sdf.parse(dateString)
        val millis = date.time
        return millis
    }

//    fun convertDatepormatTimetoDay( dateTime:Long,dateFormat:String):String{
//        val formatter = SimpleDateFormat(dateFormat)
//        val calendar = Calendar.getInstance()
//        calendar.time(dateTime)
//        val inFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//        val date=inFormat.parse(dateTime.toString())
//        // Create a calendar object that will convert the date and time value in milliseconds to date.
//        return formatter.format(date)
//    }
}
}