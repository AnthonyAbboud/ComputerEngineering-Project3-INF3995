package com.example.mounia.client.CommunicationClientServer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.zip.CRC32;
import android.util.Base64;

public class DecodeCAN
{
	/*
	*	classe utilitaire
	*	decode un paquet en une structure nommée Result
	*	celle-ci conient un tableau de messages CAN
	*/
	public static class Result
	{
		public long sequenceNumber;
		public CANMessage[] data;
		public String status;
		public Result(long sequenceNumber, CANMessage[] data, String status) { this.sequenceNumber = sequenceNumber; this.data = data; this.status = status; }
		public boolean ok() { return data != null; }
	}
	public static CANMessage decodeOneMessage(String encodedMessage)
	{
		byte[] raw = B64.decode(encodedMessage);
		CANMessage msg = new CANMessage();
		msg.msgID = (short)((short)raw[0] | ((short)(raw[1] & (byte)0b00000111) << 8)); //11 bits
		msg.destSerial = (byte)((raw[1] & (byte)0b01111000) >> 3); //4 bits
		msg.destID = (byte)((((short)raw[1] & (short)0b10000000) >> 7) | ((short)(raw[2] & (byte)0b00001111) << 1)); //5 bits
		msg.srcSerial = (byte)((raw[2] & (byte)0b11110000) >> 4); //4 bits
		msg.srcID = (byte)(raw[3] & (byte)0b00011111); //5 bits
		ByteBuffer bb = ByteBuffer.wrap(raw);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		CANEnums.CANMsgDataTypes.Pair types = CANEnums.CANMsgDataTypes.typesof((int)msg.msgID);
		msg.data1 = CANEnums.CANDataType.parse(bb, 4, types.first);
		msg.data2 = CANEnums.CANDataType.parse(bb, 8, types.second);
		CRC32 crc32 = new CRC32();
		crc32.update(Arrays.copyOfRange(raw, 0, 12));
		msg.messageIsValid = crc32.getValue() == bb.getInt(12);
		return msg;
	}
	public static DecodeCAN.Result decodeEntirePacket(byte[] packet)
	{
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet)));
			if (!reader.readLine().equals("OK"))
				return new DecodeCAN.Result(0, null, "HEADER FAIL");
			long sequenceNumber = Long.valueOf(reader.readLine());
			CANMessage[] data = new CANMessage[Integer.valueOf(reader.readLine())];
			for (int i = 0; i < data.length; ++i){
				String datum = reader.readLine();
				if (datum.length() != 24) {
					//Log.d("ERROR", datum);
					return new DecodeCAN.Result(0, null, "LINE " + Integer.toString(i) + " WITH WRONG SIZE (expected 24, got " + Integer.toString(datum.length()) + ")");
				}
				data[i] = DecodeCAN.decodeOneMessage(datum);
			}
			return new DecodeCAN.Result(sequenceNumber, data, "OK");
		} catch (IOException ex) {
			return new DecodeCAN.Result(0, null, "FAILED TO READ A LINE");
		}
	}
}
