/**
 * @author Arman Radmanesh <radmanesh@gmail.com>
 * @since September 2, 2012
 * @version 0.9.0
 */

package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EmptyStackException;

import javax.swing.text.DateFormatter;
public class JalaliDateConverter {
	private static final long JULIAN_EPOCH_MILLIS = -210866803200000L;
	private static final long ONE_DAY_MILLIS = 24L * 60L * 60L * 1000L;
	public static final long EPOCH = 1948321;

	public JalaliDateConverter(){
		
	}
	public static void main(String[] args){
		String julianString = "9/2/2012";
		//System.out.println(persianToGeorgian(georgianToPersian(julianString)));
	}
	public static String persianToGeorgian(String persianDate) throws EmptyStackException{
		String[] persian=persianDate.split("/");
		
		if(persian.length<3) throw new EmptyStackException();
		
		int day=Integer.valueOf(persian[2]);
		int month=Integer.valueOf(persian[1])-1;
		int year=Integer.valueOf(persian[0]);
		long jj = pj(year,month,day);
		long georgianLong=JULIAN_EPOCH_MILLIS + jj * ONE_DAY_MILLIS + mod(jj - JULIAN_EPOCH_MILLIS, ONE_DAY_MILLIS);
		Date georgianD=new Date(georgianLong);
		String georgianDate=new SimpleDateFormat("MM/dd/yyyy").format(georgianD);
		return georgianDate;
	}
	
	public static String georgianToPersian(String georgianDate) throws ParseException {
		SimpleDateFormat sdf= new SimpleDateFormat("MM/dd/yyyy");
		
		long j=sdf.parse(georgianDate).getTime();
		long julian = (long)Math.floor((j - JULIAN_EPOCH_MILLIS)/ONE_DAY_MILLIS);
		long r = jp(julian);
		int year = (int)y(r);
		int month = m(r) +1;
		int day = d(r)+1;
		String persianDate = year + "/" + month + "/" + day;
		return persianDate;
	}
	public static long pj(long y, int m, int d)
	{
		long a = y - 474L;
		long b = mod(a, 2820D) + 474L;
		return (EPOCH - 1L) + 1029983L * div(a, 2820D) + 365L * (b - 1L) + div(682L * b - 110L, 2816D) + (long)(m > 6? 30 * m + 6: 31 * m) + (long)d;
	}
	public static long jp(long j)
	{
		long a = j - pj(475L, 0, 1);
		long b = div(a, 1029983D);
		long c = mod(a, 1029983D);
		long d = c != 1029982L? div(2816D * (double)c + 1031337D, 1028522D): 2820L;
		long year = 474L + 2820L * b + d;
		long f = (1L + j) - pj(year, 0, 1);
		int month = (int)(f > 186L? Math.ceil((double)(f - 6L) / 30D) - 1: Math.ceil((double)f / 31D) - 1);
		int day = (int)(j - (pj(year, month, 1) - 1L));
		return (year << 16) | (month << 8) | day;
	}
	/**
	Extracts the year from a packed long value.
	
	@param r the packed long value.
	@return the year part of date.
*/
public static long y(long r)
{
	return r >> 16;
}
/**
	Extracts the month from a packed long value.
	
	@param r the packed long value
.
	@return the month part of date.
*/
public static int m(long r)
{
	return (int)(r & 0xff00) >> 8;
}
/**
	Extracts the day from a packed long value.
	
	@param r the packed long value.
	@return the day part of date.
*/
public static int d(long r)
{
	return (int)(r & 0xff);
}
public static long mod(double a, double b)
{
	return (long)(a - b * Math.floor(a / b));
}
/**
An integer division function suitable for our purpose.

@param a the dividend.
@param b the divisor.
@return the quotient of integer division.
*/
public static long div(double a, double b)
{
return (long)Math.floor(a / b);
}

}
