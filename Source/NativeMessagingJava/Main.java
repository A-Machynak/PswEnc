package com.am.pswenc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Main {
    public static void main(String[] args) throws Exception {
        // Přijme zprávu přes NM protokol
        String msg = receiveMsg();

        // 1 mapper - příliš nákladné vytvářet více
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(new File("../Data/data.json"));
        } catch (FileNotFoundException e) {
            sendMsg(objectNative("File \"data.json\" not found.", mapper));
            System.exit(0);
        }

        // Vytvoříme instanci třídy NativeReceive
        // obsahuje String Hostname, String Webpage
        NativeReceive nat = mapper.readValue(msg, NativeReceive.class);

        // Vytáhneme Hostname a Webpage z instance
        String msgHost = nat.getHostname();
        String msgKey = nat.getMasterKey();

        // Zkontrolujeme, zda-li host s příslušným
        // heslem existuje v data.json
        JsonNode rec = findInTree(msgHost, root);

        // Jestliže nebyl host nalezen v jsonu
        // pošleme zpět "Password not found"
        if (rec == null) {
            sendMsg(objectNative("Password not found", mapper));
            System.exit(0);
        }
        // Dešifrujeme heslo s master klíčem, který byl zaslán
        // a saltem, který je v data.json
        try {
            String psw = rec.path("Password").asText();
            String sal = rec.path("Salt").asText();
            int iterace = root.get("Iteration").asInt();

            // Salt je v Base64, proto je ještě nutné dekódovat
            Crypt crypt = new Crypt(msgKey, Base64.getDecoder().decode(sal), iterace);
            String password = crypt.Decrypt(psw);
            sendMsg(objectNative(password, mapper));
        } catch (javax.crypto.BadPaddingException e) {
            // BadPaddingException nastane, když zadáme špatný master klíč
            // v ~99% případů
            //e.printStackTrace();
            sendMsg(objectNative("Invalid master key", mapper));
        }
        System.exit(0);
    }
    /*
    * Native messaging protokol
    * receiveMsg()
    * První 4 byte = délka zprávy v bytech
    * Následuje zpráva o délce prvních 4 bytů
     */
    private static String receiveMsg() throws IOException {
        byte[] lb = new byte[4];
        System.in.read(lb);
        int lbSize = getInt(lb);
        byte[] msg = new byte[lbSize];
        System.in.read(msg);
        return new String(msg, StandardCharsets.UTF_8);
    }
    /*
     * Native messaging protokol
     * sendMsg(String msg)
     * První 4 byte = délka zprávy v bytech
     * Následuje zpráva o délce prvních 4 bytů
     */
    private static void sendMsg(String msg) throws IOException {
        System.out.write(byteArray(msg.length()));
        System.out.write(msg.getBytes(StandardCharsets.UTF_8));
        System.out.flush();
    }
    /*
    * JSON formátování
    * ...když už používám ten jackson
    */
    private static String objectNative(String text, ObjectMapper mapper) throws IOException {
        ObjectNode rNode = mapper.createObjectNode();
        rNode.put("Password", text);
        return mapper.writeValueAsString(rNode);
    }
    /*
    * Fce pro nalezení hosta
    * Vrací uzel, obsahující Hostname, Webpage, Password
    */
    private static JsonNode findInTree(String host, JsonNode root) throws IOException {
        JsonNode dataNode = root.path("data");
        if (dataNode.isArray()) {
            for (JsonNode node : dataNode) {
                if (node.path("Hostname").asText().equals(host)) {
                    // Vracíme uzel, ve kterém byl host nalezen
                    return node;
                }
            }
        }
        // Hostname nenalezen
        return null;
    }

    // Pomocné funkce k NM protokolu
    private static int getInt(byte[] bytes) {
        return (((bytes[3] << 24) & 0xff000000) | ((bytes[2] << 16) & 0x00ff0000) | ((bytes[1] << 8) & 0x0000ff00) | (bytes[0] & 0x000000ff));
    }
    private static byte[] byteArray(int val) {
        return new byte[] {(byte)(val & 0xff), (byte)((val >> 8) & 0xff), (byte)((val >> 16) & 0xff), (byte)((val >> 24) & 0xff) };
    }
}