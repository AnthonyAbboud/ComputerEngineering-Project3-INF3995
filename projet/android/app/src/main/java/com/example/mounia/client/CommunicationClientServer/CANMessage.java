package com.example.mounia.client.CommunicationClientServer;

public class CANMessage
{
	public Object data1; //Long ou Double (seulement Double si type original FLOAT)
	public Object data2; //Long ou Double (seulement Double si type original FLOAT)
	public short msgID;
	public byte destID;
	public byte destSerial;
	public byte srcID;
	public byte srcSerial;
	public boolean messageIsValid;
}
