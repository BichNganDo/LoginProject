
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TestTimestampToDate {
    public static void main(String[] args) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        long currentTimeMillis = System.currentTimeMillis();
        String date = sdf.format(currentTimeMillis); 
        System.out.println(date); //Prints 26/10/2015
    }
    
}
