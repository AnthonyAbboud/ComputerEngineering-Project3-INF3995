package com.example.mounia.client.Utilitaires;

import android.content.Context;

import com.example.mounia.client.Factory.CreateurWidget;
import com.example.mounia.client.Fragments.Widgets.FragmentWidget;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by mounianordine on 18-03-11.
 */

public class LectureXML {

    // https://stackoverflow.com/questions/23664906/static-fragments-vs-dynamic-fragments?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    // http://wptrafficanalyzer.in/blog/dynamically-add-fragments-to-an-activity-in-android/
    // https://developer.android.com/guide/components/fragments.html
    // https://developer.android.com/about/versions/android-4.2.html
    // http://rootzwiki.com/topic/36433-nested-fragments-example-for-android-42-api-v17/
    // https://www.programcreek.com/java-api-examples/?class=android.support.v4.app.Fragment&method=getChildFragmentManager
    // https://gist.github.com/noxi515/4259065
    // https://www.codota.com/android/methods/android.app.Fragment/getChildFragmentManager
    // https://stackoverflow.com/questions/18935505/where-to-call-getchildfragmentmanager?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    // https://www.youtube.com/watch?v=1XoPsySgZ_A

    private static List<String> tagsToIgnore = new ArrayList<>();

    private LectureXML() {
        tagsToIgnore.add(CreateurWidget.BUTTON_ARRAY);
        tagsToIgnore.add(CreateurWidget.DEBUG_OPTIONS);
        tagsToIgnore.add(CreateurWidget.RADIO_STATUS);
        tagsToIgnore.add(CreateurWidget.CUSTOM_CAN_SENDER);
    }

    private static LectureXML instance = null;

    public static LectureXML getInstance() {
        if (instance == null) instance = new LectureXML();
        return instance;
    }

    // https://stackoverflow.com/questions/7607327/how-to-create-a-xml-object-from-string-in-java?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
    public Document XMLStringToDocument(String xmlString) {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return document;
    }

    public Document lireFichierXML(String filepath) {
        File file = new File(filepath);
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = dBuilder.parse(file);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public Element obtenirRacineDocument(Document document) {
        return document.getDocumentElement();
    }

    public static void main(String args[]) {
        LectureXML lectureXML = LectureXML.getInstance();
        try {
            String filepath = "C:/WorkspaceTPsINF3995/inf3995-09/Livrable1/Client/name_10_polarislaris.xml";
            Document document = lectureXML.lireFichierXML(filepath);
            Node racine = lectureXML.obtenirRacineDocument(document);
            lectureXML.afficher(racine);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Fonction qui sert juste à parcourir un node de Document XML et l'afficher
    public void afficher(Node node) {
        if (node instanceof Element) {
            print("Node : " + node.getNodeName());
            if (node.hasChildNodes()) {
                NodeList enfants = node.getChildNodes();
                for (int i = 0; i < enfants.getLength(); i++) afficher(enfants.item(i));
            }
        }
    }

    public void construireUI(Node node, FragmentWidget parentWidget) {
        if (node instanceof Element) {

            if (node.hasAttributes()) {
                Map<String, String> attributs = new HashMap<>();
                NamedNodeMap attributes = node.getAttributes();
                // /!\ Les attributs sont rendus minuscules
                for (int j = 0; j < attributes.getLength(); j++)
                    attributs.put(attributes.item(j).getNodeName().toLowerCase(), attributes.item(j).getNodeValue());
                parentWidget.assignerAttributs(attributs);
            }

            if (node.hasChildNodes()) {
                for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                    Node childNode = node.getChildNodes().item(i);
                    if (childNode instanceof Element && !mustIgnore(childNode.getNodeName())) {
                        FragmentWidget childWidget = CreateurWidget.newInstance(childNode.getNodeName(), parentWidget);
                        construireUI(childNode, childWidget);
                    }
                }
            }
        }
    }

    public static boolean mustIgnore(String tag) {
        return tagsToIgnore.contains(tag.toLowerCase());
    }

    public static void print(String message) { System.out.println(message); }

    private Context context = null;

    public Context getContext() { return context; }

    public void setContext(Context context) { this.context = context; }

    public static String getRocketFileContent() {
        return new String("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Rocket name=\"Valkyrie Mark 2\" id=\"11\">\n" +
                "\t<GridContainer>\n" +
                "\t\t<Grid col=\"0\" row=\"0\">\n" +
                "\t\t\t<TabContainer>\n" +
                "\t\t\t\t<Tab name=\"Batteries, SD\">\n" +
                "\t\t\t\t\t<DualVWidget>\n" +
                "\t\t\t\t\t\t<DataDisplayer>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ABMM A\" id=\"PUM_VBAT1\" display=\"__DATA1__ V\" minAcceptable=\"6.0\" maxAcceptable=\"12.5\" chiffresSign=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ABMM B\" id=\"PUM_VBAT2\" display=\"__DATA1__ V\" minAcceptable=\"6.0\" maxAcceptable=\"12.5\" chiffresSign=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ABMM C\" id=\"PUM_VBAT3\" display=\"__DATA1__ V\" minAcceptable=\"6.0\" maxAcceptable=\"12.5\" chiffresSign=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ABMM A\" id=\"PUM_BAT1_CURRENT\" display=\"__DATA1__ A\" minAcceptable=\"0.1\" maxAcceptable=\"7.0\" chiffresSign=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ABMM B\" id=\"PUM_BAT2_CURRENT\" display=\"__DATA1__ A\" minAcceptable=\"0.1\" maxAcceptable=\"7.0\" chiffresSign=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ABMM C\" id=\"PUM_BAT3_CURRENT\" display=\"__DATA1__ A\" minAcceptable=\"0.1\" maxAcceptable=\"7.0\" chiffresSign=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"PUM 0 3V3\" id=\"PUM_3V3_CURRENT\" display=\"__DATA1__ A\" minAcceptable=\"0.1\" maxAcceptable=\"2.0\" chiffresSign=\"2\" specificSource=\"PUM\" serialNb=\"0\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"PUM 0 5V\" id=\"PUM_5V_CURRENT\" display=\"__DATA1__ A\" minAcceptable=\"0.1\" maxAcceptable=\"2.0\" chiffresSign=\"2\" specificSource=\"PUM\" serialNb=\"0\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"PUM 2 3V3\" id=\"PUM_3V3_CURRENT\" display=\"__DATA1__ A\" minAcceptable=\"0.1\" maxAcceptable=\"2.0\" chiffresSign=\"2\" specificSource=\"PUM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"PUM 2 5V\" id=\"PUM_5V_CURRENT\" display=\"__DATA1__ A\" minAcceptable=\"0.1\" maxAcceptable=\"2.0\" chiffresSign=\"2\" specificSource=\"PUM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 1 45V\" id=\"RPM_45V\" display=\"__DATA1__ V\" minAcceptable=\"0\" maxAcceptable=\"8\" chiffresSign=\"2\" specificSource=\"ADM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 2 45V\" id=\"RPM_45V\" display=\"__DATA1__ V\" minAcceptable=\"0\" maxAcceptable=\"8\" chiffresSign=\"2\" specificSource=\"ADM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 1 chgd\" id=\"LVDM_CHARGED\" display=\"__DATA1__ V\" minAcceptable=\"0\" maxAcceptable=\"0\" chiffresSign=\"0\" specificSource=\"ADM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 2 chgd\" id=\"LVDM_CHARGED\" display=\"__DATA1__ V\" minAcceptable=\"0\" maxAcceptable=\"0\" chiffresSign=\"0\" specificSource=\"ADM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t</DataDisplayer>\n" +
                "\t\t\t\t\t\t<DataDisplayer>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADIRM 0 msg lost\" id=\"SD_CARD_WRITE_ERROR\" display=\"__DATA1__\" minAcceptable=\"0\" maxAcceptable=\"0\" chiffresSign=\"0\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADLM 2 msg lost\" id=\"SD_CARD_WRITE_ERROR\" display=\"__DATA1__\" minAcceptable=\"0\" maxAcceptable=\"0\" chiffresSign=\"0\" specificSource=\"DLM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADLM 3 msg lost\" id=\"SD_CARD_WRITE_ERROR\" display=\"__DATA1__\" minAcceptable=\"0\" maxAcceptable=\"0\" chiffresSign=\"0\" specificSource=\"DLM\" serialNb=\"3\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADIRM left\" id=\"SD_CARD_SPACE_LEFT\" minAcceptable=\"1048576\" customUpdate=\"SDSpaceLeft\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADLM 2 left\" id=\"SD_CARD_SPACE_LEFT\" minAcceptable=\"1048576\" customUpdate=\"SDSpaceLeft\" specificSource=\"DLM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADLM 3 left\" id=\"SD_CARD_SPACE_LEFT\" minAcceptable=\"1048576\" customUpdate=\"SDSpaceLeft\"  specificSource=\"DLM\" serialNb=\"3\"/>7\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADIRM wr.\" id=\"SD_TOTAL_BYTES_WRITTEN\" minAcceptable=\"0\" maxAcceptable=\"3500000000\" customUpdate=\"SDBytesWritten\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADLM 2 wr.\" id=\"SD_TOTAL_BYTES_WRITTEN\" minAcceptable=\"0\" maxAcceptable=\"3500000000\" customUpdate=\"SDBytesWritten\" specificSource=\"DLM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADLM 3 wr.\" id=\"SD_TOTAL_BYTES_WRITTEN\" minAcceptable=\"0\" maxAcceptable=\"3500000000\" customUpdate=\"SDBytesWritten\"  specificSource=\"DLM\" serialNb=\"3\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"GS UART write error\" id=\"GS_UART_WRITE_ERROR\" display=\"__DATA1__\" minAcceptable=\"0\" maxAcceptable=\"0\" chiffresSign=\"0\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"GS UART read error\" id=\"GS_UART_READ_ERROR\" display=\"__DATA1__\" minAcceptable=\"0\" maxAcceptable=\"0\" chiffresSign=\"0\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADIRM data queue full\" id=\"ADIRM_DATA_QUEUE_FULL\" display=\"__DATA1__\" minAcceptable=\"1\" maxAcceptable=\"0\" chiffresSign=\"0\"  specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t</DataDisplayer>\n" +
                "\t\t\t\t\t</DualVWidget>\n" +
                "\t\t\t\t</Tab>\n" +
                "\t\t\t\t<Tab name=\"GPS and Temperature\">\n" +
                "\t\t\t\t\t<DualVWidget>\n" +
                "\t\t\t\t\t\t<DataDisplayer>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"1 - LAT\" id=\"GPS1_LATITUDE\" display=\"__DATA1__\" minAcceptable=\"30\" maxAcceptable=\"35\" chiffresSign=\"6\" specificSource=\"AGRUM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"2 - LAT\" id=\"GPS1_LATITUDE\" display=\"__DATA1__\" minAcceptable=\"30\" maxAcceptable=\"35\" chiffresSign=\"6\" specificSource=\"AGRUM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"1 - LONG\" id=\"GPS1_LONGITUDE\" display=\"__DATA1__\" minAcceptable=\"100\" maxAcceptable=\"110\" chiffresSign=\"6\" specificSource=\"AGRUM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"2 - LONG\" id=\"GPS1_LONGITUDE\" display=\"__DATA1__\" minAcceptable=\"100\" maxAcceptable=\"110\" chiffresSign=\"6\" specificSource=\"AGRUM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"1 - ALT\" id=\"GPS1_ALT_MSL\" display=\"__DATA1__\" customUpdate=\"meterToFoot\" minAcceptable=\"1000\" maxAcceptable=\"12000\" specificSource=\"AGRUM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"2 - ALT\" id=\"GPS1_ALT_MSL\" display=\"__DATA1__\" customUpdate=\"meterToFoot\" minAcceptable=\"1000\" maxAcceptable=\"12000\" specificSource=\"AGRUM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"1 - Gnd speed\" id=\"GPS1_GND_SPEED\" display=\"__DATA1__\" chiffresSign=\"2\" specificSource=\"AGRUM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"2 - Gnd speed\" id=\"GPS1_GND_SPEED\" display=\"__DATA1__\" chiffresSign=\"2\" specificSource=\"AGRUM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"1 - Nb sat\" id=\"GPS1_NB_SAT\" display=\"__DATA1__\" chiffresSign=\"0\" specificSource=\"AGRUM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"2 - Nb sat\" id=\"GPS1_NB_SAT\" display=\"__DATA1__\" chiffresSign=\"0\" specificSource=\"AGRUM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"1 - UTC\" id=\"GPS1_DATE_TIME\" display=\"__DATA1__\" chiffresSign=\"0\" specificSource=\"AGRUM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"2 - UTC\" id=\"GPS1_DATE_TIME\" display=\"__DATA1__\" chiffresSign=\"0\" specificSource=\"AGRUM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"1 - Horiz. precision\" id=\"GPS1_FIX_QUAL\" display=\"__DATA1__\" chiffresSign=\"2\" specificSource=\"AGRUM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"2 - Horiz. precision\" id=\"GPS1_FIX_QUAL\" display=\"__DATA1__\" chiffresSign=\"2\" specificSource=\"AGRUM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"1 - Course over Ground\" id=\"GPS1_TRACK_ANG\" display=\"__DATA1__\" chiffresSign=\"2\" specificSource=\"AGRUM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"2 - Course over Ground\" id=\"GPS1_TRACK_ANG\" display=\"__DATA1__\" chiffresSign=\"2\" specificSource=\"AGRUM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t</DataDisplayer>\n" +
                "\t\t\t\t\t\t<DataDisplayer>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"PUM 0\" id=\"ONE_WIRE_TEMPERATURE\" display=\"__DATA2__\" customUpdate=\"oneWire\" customUpdateParam=\"0xE9CAE728\" customAcceptable=\"oneWireAcceptable\" chiffresSign=\"2\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"PUM 2\" id=\"ONE_WIRE_TEMPERATURE\" display=\"__DATA2__\" customUpdate=\"oneWire\" customUpdateParam=\"0xEACBE028\" customAcceptable=\"oneWireAcceptable\" chiffresSign=\"2\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ABMM A\" id=\"ONE_WIRE_TEMPERATURE\" display=\"__DATA2__\" customUpdate=\"oneWire\" customUpdateParam=\"0xEAEB5728\" customAcceptable=\"oneWireAcceptable\" chiffresSign=\"2\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ABMM B\" id=\"ONE_WIRE_TEMPERATURE\" display=\"__DATA2__\" customUpdate=\"oneWire\" customUpdateParam=\"0x1AA08B28\" customAcceptable=\"oneWireAcceptable\" chiffresSign=\"2\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ABMM C\" id=\"ONE_WIRE_TEMPERATURE\" display=\"__DATA2__\" customUpdate=\"oneWire\" customUpdateParam=\"0x1AA14628\" customAcceptable=\"oneWireAcceptable\" chiffresSign=\"2\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"AGRUM 1\" id=\"ONE_WIRE_TEMPERATURE\" display=\"__DATA2__\" customUpdate=\"oneWire\" customUpdateParam=\"0x50C17728\" customAcceptable=\"oneWireAcceptable\" chiffresSign=\"2\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"AGRUM 2\" id=\"ONE_WIRE_TEMPERATURE\" display=\"__DATA2__\" customUpdate=\"oneWire\" customUpdateParam=\"0x50883228\" customAcceptable=\"oneWireAcceptable\" chiffresSign=\"2\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"AGRUM 3\" id=\"ONE_WIRE_TEMPERATURE\" display=\"__DATA2__\" customUpdate=\"oneWire\" customUpdateParam=\"0x50e29128\" customAcceptable=\"oneWireAcceptable\" chiffresSign=\"2\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"AGRUM 4\" id=\"ONE_WIRE_TEMPERATURE\" display=\"__DATA2__\" customUpdate=\"oneWire\" customUpdateParam=\"0x50f8e128\" customAcceptable=\"oneWireAcceptable\" chiffresSign=\"2\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"MOBO_FTDI\" id=\"ONE_WIRE_TEMPERATURE\" display=\"__DATA2__\" customUpdate=\"oneWire\" customUpdateParam=\"0x50C52B28\" customAcceptable=\"oneWireAcceptable\" chiffresSign=\"2\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"MOBO_AGRUM\" id=\"ONE_WIRE_TEMPERATURE\" display=\"__DATA2__\" customUpdate=\"oneWire\" customUpdateParam=\"0x5095C528\" customAcceptable=\"oneWireAcceptable\" chiffresSign=\"2\" specificSource=\"ADIRM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 1\" id=\"BAR_TEMPERATURE1\" display=\"__DATA1__ C\" chiffresSign=\"2\" specificSource=\"ADM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 2\" id=\"BAR_TEMPERATURE1\" display=\"__DATA1__ C\" chiffresSign=\"2\" specificSource=\"ADM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADIRM 0\" id=\"BAR_TEMPERATURE1\" display=\"__DATA1__ C\" chiffresSign=\"2\" specificSource=\"ADIRM\" serialNb=\"0\"/>\n" +
                "\t\t\t\t\t\t</DataDisplayer>\n" +
                "\t\t\t\t\t</DualVWidget>\n" +
                "\t\t\t\t</Tab>\n" +
                "\t\t\t\t<Tab name=\"Debug\"><DebugOptions/></Tab>\n" +
                "\t\t\t\t<Tab name=\"Buttons\">\n" +
                "\t\t\t\t\t<ButtonArray nbColumns=\"1\">\n" +
                "\t\t\t\t\t\t<CAN name=\"Drogue\" id=\"COMMAND_EMERGENCY_DEPLOY_DROGUE\" popup=\"True\"/>\t\t\t\t\t\n" +
                "\t\t\t\t\t\t<CAN name=\"Main\" id=\"COMMAND_EMERGENCY_DEPLOY_MAIN\" popup=\"True\"/>\t\t\t\t\t\n" +
                "\t\t\t\t\t\t<CAN name=\"Arm\" id=\"COMMAND_ARM\" popup=\"True\"/>\t\t\t\t\t\n" +
                "\t\t\t\t\t\t<CAN name=\"Disarm\" id=\"COMMAND_DISARM\" popup=\"True\"/>\t\t\t\t\t\n" +
                "\t\t\t\t\t\t<CAN name=\"Lock\" id=\"COMMAND_LOCK\" popup=\"True\"/>\t\t\t\t\t\n" +
                "\t\t\t\t\t\t<CAN name=\"Unlock\" id=\"COMMAND_UNLOCK\" popup=\"True\"/>\t\t\t\t\t\n" +
                "\t\t\t\t\t</ButtonArray>\n" +
                "\t\t\t\t</Tab>\n" +
                "\t\t\t\t<Tab name=\"ModuleStatus\">\n" +
                "\t\t\t\t\t\t<Modulestatus nGrid=\"15\" nColumns=\"5\" />\n" +
                "\t\t\t\t</Tab>\n" +
                "\t\t\t</TabContainer>\n" +
                "\t\t</Grid>\n" +
                "\n" +
                "\t\t<Grid col=\"1\" row=\"1\">\n" +
                "\t\t\t<TabContainer>\n" +
                "\t\t\t\t<Tab name=\"Plots\">\n" +
                "\t\t\t\t\t<DualVWidget>\n" +
                "\t\t\t\t\t\t<Plot name=\"\" unit=\"G\" axis=\"Magnetic-field\">\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LSM_MAGN_FIELD_X\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LSM_MAGN_FIELD_Y\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LSM_MAGN_FIELD_Z\"/>\n" +
                "\t\t\t\t\t\t</Plot>\n" +
                "\t\t\t\t\t\t<Plot name=\"\" unit=\"g\" axis=\"Acceleration\">\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LSM_ACC_X\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LSM_ACC_Y\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LSM_ACC_Z\"/>\n" +
                "\t\t\t\t\t\t</Plot>\n" +
                "\t\t\t\t\t</DualVWidget>\n" +
                "\t\t\t\t</Tab>\n" +
                "\t\t\t\t<Tab name=\"Map and FindMe\">\n" +
                "\t\t\t\t\t<DualVWidget>\n" +
                "\t\t\t\t\t\t<Map/>\n" +
                "\t\t\t\t\t\t<FindMe/>\n" +
                "\t\t\t\t\t</DualVWidget>\n" +
                "\t\t\t\t</Tab>\n" +
                "\t\t\t\t<Tab name=\"Radio Status\">\n" +
                "\t\t\t\t\t<RadioStatus/>\n" +
                "\t\t\t\t</Tab>\n" +
                "\t\t\t\t<Tab name=\"Send CAN Msg\">\n" +
                "\t\t\t\t\t<CustomCANSender/>\n" +
                "\t\t\t\t</Tab>\n" +
                "\t\t\t\t<Tab name=\"Main stats - Logs\">\n" +
                "\t\t\t\t\t<DualHWidget>\n" +
                "\t\t\t\t\t\t<DataDisplayer>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"AGRUM 1 LAT\" id=\"GPS1_LATITUDE\" display=\"__DATA1__\" minAcceptable=\"30\" maxAcceptable=\"35\" chiffresSign=\"6\" specificSource=\"AGRUM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"AGRUM 2 LAT\" id=\"GPS1_LATITUDE\" display=\"__DATA1__\" minAcceptable=\"30\" maxAcceptable=\"35\" chiffresSign=\"6\" specificSource=\"AGRUM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"AGRUM 1 LONG\" id=\"GPS1_LONGITUDE\" display=\"__DATA1__\" minAcceptable=\"100\" maxAcceptable=\"110\" chiffresSign=\"6\" specificSource=\"AGRUM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"AGRUM 2 LONG\" id=\"GPS1_LONGITUDE\" display=\"__DATA1__\" minAcceptable=\"100\" maxAcceptable=\"110\" chiffresSign=\"6\" specificSource=\"AGRUM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"AGRUM 1 ALT GPS\" id=\"GPS1_ALT_MSL\" display=\"__DATA1__\" customUpdate=\"meterToFoot\" minAcceptable=\"1000\" maxAcceptable=\"12000\" specificSource=\"AGRUM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"AGRUM 2 ALT GPS\" id=\"GPS1_ALT_MSL\" display=\"__DATA1__\" customUpdate=\"meterToFoot\" minAcceptable=\"1000\" maxAcceptable=\"12000\" specificSource=\"AGRUM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 1 DROGUE\" id=\"LVDM_BRIDGEWIRE_RES_DROGUE\" customUpdate=\"admBWVoltsToOhmsString\" customAcceptable=\"isBWOhmAcceptable\" specificSource=\"ADM\" serialNb=\"1\" updateEach=\"10\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 2 DROGUE\" id=\"LVDM_BRIDGEWIRE_RES_DROGUE\" customUpdate=\"admBWVoltsToOhmsString\" customAcceptable=\"isBWOhmAcceptable\" specificSource=\"ADM\" serialNb=\"2\" updateEach=\"10\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 1 MAIN\" id=\"LVDM_BRIDGEWIRE_RES_MAIN\" customUpdate=\"admBWVoltsToOhmsString\" customAcceptable=\"isBWOhmAcceptable\" specificSource=\"ADM\" serialNb=\"1\" updateEach=\"10\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 2 MAIN\" id=\"LVDM_BRIDGEWIRE_RES_MAIN\" customUpdate=\"admBWVoltsToOhmsString\" customAcceptable=\"isBWOhmAcceptable\" specificSource=\"ADM\" serialNb=\"2\" updateEach=\"10\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 1\" id=\"LVDM_ARMING_STATUS\" customUpdate=\"armingStatusUpdate\" chiffresSign=\"0\" minAcceptable=\"0\" maxAcceptable=\"0\" specificSource=\"ADM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 2\" id=\"LVDM_ARMING_STATUS\" customUpdate=\"armingStatusUpdate\" chiffresSign=\"0\" minAcceptable=\"0\" maxAcceptable=\"0\" specificSource=\"ADM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"Nxt Rec. St.\" id=\"LVDM_STATE\" customUpdate=\"admStateUpdate\" chiffresSign=\"0\" minAcceptable=\"0\" maxAcceptable=\"6\" specificSource=\"ADM\" serialNb=\"1\" />\n" +
                "\t\t\t\t\t\t\t<CAN name=\"Nxt Rec. St.\" id=\"LVDM_STATE\" customUpdate=\"admStateUpdate\" chiffresSign=\"0\" minAcceptable=\"0\" maxAcceptable=\"6\" specificSource=\"ADM\" serialNb=\"2\" />\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 1 Alt\" id=\"BAR_PRESS1\" minAcceptable=\"25000\" maxAcceptable=\"95000\" customUpdate=\"pressToAlt\" specificSource=\"ADM\" serialNb=\"1\" updateEach=\"10\" />\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 2 Alt\" id=\"BAR_PRESS1\" minAcceptable=\"25000\" maxAcceptable=\"95000\" customUpdate=\"pressToAlt\" specificSource=\"ADM\" serialNb=\"2\" updateEach=\"10\" />\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADIRM 0 Alt\" id=\"BAR_PRESS1\" minAcceptable=\"25000\" maxAcceptable=\"95000\" customUpdate=\"pressToAlt\" specificSource=\"ADIRM\" serialNb=\"0\" updateEach=\"10\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"Apogée (ADM 1)\" id=\"BAR_PRESS1\" minAcceptable=\"25000\" maxAcceptable=\"95000\" customUpdate=\"apogeeDetect\" specificSource=\"ADM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"Apogée (ADM 2)\" id=\"BAR_PRESS1\" minAcceptable=\"25000\" maxAcceptable=\"95000\" customUpdate=\"apogeeDetect\" specificSource=\"ADM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"Apogée (ADIRM 0)\" id=\"BAR_PRESS1\" minAcceptable=\"25000\" maxAcceptable=\"95000\" customUpdate=\"apogeeDetect\" specificSource=\"ADIRM\" serialNb=\"0\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"RAMP_ALT (ADM 1)\" id=\"LVDM_RAMP_ALT\" minAcceptable=\"1000\" maxAcceptable=\"1500\" customUpdate=\"rampAlt\" specificSource=\"ADM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"RAMP_ALT (ADM 2)\" id=\"LVDM_RAMP_ALT\" minAcceptable=\"1000\" maxAcceptable=\"1500\" customUpdate=\"rampAlt\" specificSource=\"ADM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 1 Lock\" id=\"LVDM_LOCKED\" chiffresSign=\"0\" minAcceptable=\"0\" maxAcceptable=\"0\" specificSource=\"ADM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 2 Lock\" id=\"LVDM_LOCKED\" chiffresSign=\"0\" minAcceptable=\"0\" maxAcceptable=\"0\" specificSource=\"ADM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 1 45V\" id=\"RPM_45V\" display=\"__DATA1__ V\" minAcceptable=\"0\" maxAcceptable=\"8\" chiffresSign=\"2\" specificSource=\"ADM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 2 45V\" id=\"RPM_45V\" display=\"__DATA1__ V\" minAcceptable=\"0\" maxAcceptable=\"8\" chiffresSign=\"2\" specificSource=\"ADM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 1 chgd\" id=\"LVDM_CHARGED\" minAcceptable=\"0\" maxAcceptable=\"0\" chiffresSign=\"0\" specificSource=\"ADM\" serialNb=\"1\"/>\n" +
                "\t\t\t\t\t\t\t<CAN name=\"ADM 2 chgd\" id=\"LVDM_CHARGED\" minAcceptable=\"0\" maxAcceptable=\"0\" chiffresSign=\"0\" specificSource=\"ADM\" serialNb=\"2\"/>\n" +
                "\t\t\t\t\t\t</DataDisplayer>\n" +
                "\t\t\t\t\t\t<DisplayLogWidget>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LVDM_CANNOT_TRIGGER_NOT_ARMED\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LVDM_LOCKED_CANNOT_DISARM\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LVDM_EMERGENCY_KABOOM_CALLED\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LVDM_TRIGGERED\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LVDM_CHARGES_BURNT\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LVDM_DIAGNOSTIC_FAILURE\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LVDM_ACKNOWLEDGE\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"VOTING_VSTRUCT_OVERFLOW\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"VOTING_VSTRUCT_UPDATE_FAILURE\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"MODULE_CRITICAL_FAILURE\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"RADIO_ACKNOWLEDGE\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LOGGING_ACKNOWLEDGE\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"ERR_CAN_GOT_BAD_MAGIC_BYTES\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"ERR_CAN_SWITCHING_BUS\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"MODULE_BASE_INIT_SUCCESS\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"MODULE_SPECIFIC_INIT_SUCCESS\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"MODULE_SPECIFIC_INIT_RECOVERABLE_ERROR\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"MODULE_SPECIFIC_INIT_FAILURE\"/>\n" +
                "\t\t\t\t\t\t\t<CAN id=\"LVDM_HALL_ACKNOWLEDGE\"/>\n" +
                "\t\t\t\t\t\t</DisplayLogWidget>\n" +
                "\t\t\t\t\t</DualHWidget>\n" +
                "\t\t\t\t</Tab>\n" +
                "\t\t\t</TabContainer>\n" +
                "\t\t</Grid>\n" +
                "\t</GridContainer>\n" +
                "</Rocket>\n");
    }

// public static String getRocketFileContent() {
//        return new String("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                "<Rocket name=\"Polaris\" id=\"10\">\n" +
//                    "<GridContainer>" +
//                        "<Grid>" +
//                "<DualVWidget>\n" +
//                "<Plot name=\"\" unit=\"G\" axis=\"Magnetic-field\">\n" +
//                "<CAN id=\"LSM_MAGN_FIELD_X\"/>\n" +
//                "<CAN id=\"LSM_MAGN_FIELD_Y\"/>\n" +
//                "<CAN id=\"LSM_MAGN_FIELD_Z\"/>\n" +
//                "</Plot>\n" +
//                "<Plot name=\"\" unit=\"g\" axis=\"Acceleration\">\n" +
//                "<CAN id=\"LSM_ACC_X\"/>\n" +
//                "<CAN id=\"LSM_ACC_Y\"/>\n" +
//                "<CAN id=\"LSM_ACC_Z\"/>\n" +
//                "</Plot>\n" +
//                "</DualVWidget>" +
//                        "</Grid>" +
//                    "</GridContainer>" +
//                "</Rocket>");
//    }

}
