package com.example.mounia.client.CommunicationClientServer;

import java.util.HashMap;
import java.lang.RuntimeException;

public class CustomUpdate
{
	private static class Pair
	{
		public byte first;
		public byte second;
		public Pair(byte first, byte second) { this.first = first; this.second = second; }
		@Override public boolean equals(Object thatObj)
		{
			Pair that = (Pair)thatObj;
			return this.first == that.first && this.second == that.second;
		}
		@Override public int hashCode()
		{
			return (int)first | ((int)second << 5);
		}
	}
	private static HashMap<Pair, Double> altMax = new HashMap<Pair, Double>();
	private static HashMap<Pair, Double> lastRampAlt = new HashMap<Pair, Double>();
	
	private static double pascalToFeet(double pascals)
	{
		return Math.pow(1.0 - (pascals / 101325), 0.19026323) * 44330.77 * 3.28084;
	}
	
	public static String armingStatusUpdate(CANMessage CANMsg)
	{
		switch ((int)(long)(Long)CANMsg.data1){
			case 0: return "DISARMED";
			case 1: return "ARMED";
			default: return "UNKNOWN";
		}
	}
	public static String admStateUpdate(CANMessage CANMsg)
	{
		switch ((int)(long)(Long)CANMsg.data1){
			case 0: return "INIT";
			case 1: return "ARMING";
			case 2: return "LAUNCH_DETECT";
			case 3: return "MACH_DELAY";
			case 4: return "APOGEE";
			case 5: return "MAIN_CHUTE_ALTITUDE";
			case 6: return "SAFE_MODE";
			default: return "UNKNOWN";
		}
	}
	public static String pressToAlt(CANMessage CANMsg)
	{
		double alt_f = CustomUpdate.pascalToFeet((Double)CANMsg.data1);
		Double res = CustomUpdate.lastRampAlt.get(new CustomUpdate.Pair(CANMsg.srcID, CANMsg.srcSerial));
		return String.format("%.0f '", alt_f - (res != null ? res : 0));
	}
	public static String rampAlt(CANMessage CANMsg)
	{
		double ramp_alt_f = (Double)CANMsg.data1 * 3.28084;
		CustomUpdate.lastRampAlt.put(new CustomUpdate.Pair(CANMsg.srcID, CANMsg.srcSerial), ramp_alt_f);
		if (CANMsg.srcID == 0 && CANMsg.srcSerial == 1)
			CustomUpdate.lastRampAlt.put(new CustomUpdate.Pair((byte)3, (byte)0), ramp_alt_f);
		return String.format("%.0f '", ramp_alt_f); 
	}
	public static String apogeeDetect(CANMessage CANMsg)
	{
		CustomUpdate.Pair src = new CustomUpdate.Pair(CANMsg.srcID, CANMsg.srcSerial);
		if (!CustomUpdate.altMax.containsKey(src))
			CustomUpdate.altMax.put(src, Double.NEGATIVE_INFINITY);
		double alt_f = Double.NEGATIVE_INFINITY;
		Double lastRampAltVal = CustomUpdate.lastRampAlt.get(src);
		if (lastRampAltVal != null)
			alt_f = CustomUpdate.pascalToFeet((Double)CANMsg.data1) - lastRampAltVal;
		if (alt_f > CustomUpdate.altMax.get(src))
			CustomUpdate.altMax.put(src, alt_f);
		return String.format("%.0f '", CustomUpdate.altMax.get(src)); 
	}
	public static String oneWire(CANMessage CANMsg, String addr) //peut retourner null
	{
//		long d1 = -2000000000;
//		try {
//			d1 = (Long)CANMsg.data1;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		long ad = -2000000001;
//		try {
//			ad = Long.valueOf(addr, 16);
//		} catch (Exception e2) {
//			e2.printStackTrace();
//		}

		long d1 = (Long)CANMsg.data1;
		long ad = Long.valueOf(addr, 16);
		if (d1 == ad)
			return String.format("%.2f C", (Double)CANMsg.data2);
		else
			return null;
	}
	private static double admBWVoltsToOhms(double volts)
	{
		double conv = 3.5 * volts / 1000;
		return conv < 11 ? conv : Double.POSITIVE_INFINITY;
	}
	public static String admBWVoltsToOhmsString(CANMessage CANMsg)
	{
		return String.format("%.1f Ω", CustomUpdate.admBWVoltsToOhms((Double)CANMsg.data1));
	}
	public static String meterToFoot(CANMessage CANMsg)
	{
		return String.format("%.0f '", (Double)CANMsg.data1 * 3.28084);
	}
	public static String footToMeter(CANMessage CANMsg)
	{
		return String.format("%.0f m", (Double)CANMsg.data1 * 0.3048);
	}
	public static String SDSpaceLeft(CANMessage CANMsg)
	{
		double d1 = -1;
		try {
			d1 = (long)(Long)CANMsg.data1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (d1 < 0)
			return "ERREUR";
		else if (d1 < 10 * 1024)
			return String.format("%.0f ko", d1);
		else if (d1 < 1024 * 1024)
			return String.format("%.2f Mo", d1 / 1024);
		else
			return String.format("%.2f Go", d1 / (1024 * 1024));
	}
	public static String SDBytesWritten(CANMessage CANMsg)
	{
		double d1 = (long)(Long)CANMsg.data1;
		if (d1 < 1024 * 1024)
			return String.format("%.0f ko", d1 / 1024);
		else if (d1 < 1024 * 1024 * 1024)
			return String.format("%.2f Mo", d1 / (1024 * 1024));
		else
			return String.format("%.2f Go", d1 / (1024 * 1024 * 1024));
	}
	public static boolean isBWOhmAcceptable(CANMessage CANMsg)
	{
		double val = CustomUpdate.admBWVoltsToOhms((Double)CANMsg.data1);
		return 4.0 < val && val < 6.5;
	}
	public static boolean oneWireAcceptable(CANMessage CANMsg)
	{
		double val = (Double)CANMsg.data2;
		return 15.0 < val && val < 65.0;
	}
	
	public static String update(String functionName, CANMessage CANMsg, String addr) //addr n'est utilisé que pour "oneWire"; ATTENTION: la fonction peut retourner null pour un appel à "oneWire"
	{
		switch (functionName){
		case "armingStatusUpdate": return CustomUpdate.armingStatusUpdate(CANMsg);
		case "admStateUpdate": return CustomUpdate.admStateUpdate(CANMsg);
		case "pressToAlt": return CustomUpdate.pressToAlt(CANMsg);
		case "rampAlt": return CustomUpdate.rampAlt(CANMsg);
		case "apogeeDetect": return CustomUpdate.apogeeDetect(CANMsg);
		case "oneWire": return CustomUpdate.oneWire(CANMsg, addr);
		case "admBWVoltsToOhmsString": return CustomUpdate.admBWVoltsToOhmsString(CANMsg);
		case "meterToFoot": return CustomUpdate.meterToFoot(CANMsg);
		case "footToMeter": return CustomUpdate.footToMeter(CANMsg);
		case "SDSpaceLeft": return CustomUpdate.SDSpaceLeft(CANMsg);
		case "SDBytesWritten": return CustomUpdate.SDBytesWritten(CANMsg);
		default: throw new RuntimeException("CustomUpdate.update: Nom de fonction \"" + functionName + "\" pas reconnu.");
		}
	}
	public static boolean acceptable(String functionName, CANMessage CANMsg)
	{
		switch (functionName){
		case "isBWOhmAcceptable": return CustomUpdate.isBWOhmAcceptable(CANMsg);
		case "oneWireAcceptable": return CustomUpdate.oneWireAcceptable(CANMsg);
		default: throw new RuntimeException("CustomUpdate.acceptable: Nom de fonction \"" + functionName + "\" pas reconnu.");
		}
	}
}
