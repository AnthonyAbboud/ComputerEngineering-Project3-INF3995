package com.example.mounia.client.CommunicationClientServer;

public class Main {
	/*
	*	test du decodage de paquet
	*	Autheur : Gabriel
	*/
	public static void main(String[] args) {
		CANEnums.init();
		DecodeCAN.Result decoded = DecodeCAN.decodeEntirePacket("OK\n1234\n2\nDCoiEPkPSUAIAAAAq5n4Xg==\nDCoiEPkPSUAIAAAAq5n4Xg==".getBytes());
		System.out.println(String.format("%b", decoded.ok()));
		if (decoded.ok()){
			System.out.println(String.format("%d", decoded.sequenceNumber));
			System.out.println(String.format("%d", decoded.data.length));
			for (int i = 0; i < decoded.data.length; ++i){
				System.out.println(String.format("%d %d %d %d %d", decoded.data[i].msgID, (int)decoded.data[i].destID, (int)decoded.data[i].destSerial, (int)decoded.data[i].srcID, (int)decoded.data[i].srcSerial));
				CANEnums.CANMsgDataTypes.Pair types = CANEnums.CANMsgDataTypes.typesof(decoded.data[i].msgID);
				System.out.println(types.first == CANEnums.CANDataType.FLOAT ? String.format("%f", (Double)decoded.data[i].data1) : String.format("%d", (Long)decoded.data[i].data1));
				System.out.println(types.second == CANEnums.CANDataType.FLOAT ? String.format("%f", (Double)decoded.data[i].data2) : String.format("%d", (Long)decoded.data[i].data2));
				System.out.println(CustomUpdate.update("pressToAlt", decoded.data[i], null));
			}
		} else
			System.out.println(String.format("%s", decoded.status));
	}

}
