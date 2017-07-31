package com.cloutteam.jarcraftinator.handler;

import com.cloutteam.jarcraftinator.JARCraftinator;
import com.cloutteam.jarcraftinator.packet.PacketType;
import com.cloutteam.jarcraftinator.utils.QuickJSON;
import com.cloutteam.jarcraftinator.utils.VarData;
import org.json.simple.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PacketHandler extends Thread {

    private Socket clientSocket;
    private boolean connected;

    public PacketHandler(Socket socket){
        clientSocket = socket;
        connected = true;
    }

    @Override
    public void run() {

        DataInputStream stream = null;
        DataOutputStream output = null;

        try {
            stream = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());
        }catch(IOException ex){
            ex.printStackTrace();
        }

        while (!currentThread().isInterrupted() && !isDisconnected()) {
            try {
                while (stream.available() > 0) {
                    int packetLength = VarData.readVarInt(stream);
                    int packetID = VarData.readVarInt(stream);
                    boolean wasHandled = false;

                    JARCraftinator.log("Packet length: " + packetLength);
                    JARCraftinator.log("Packet ID: " + packetID);

                    if (packetID == PacketType.HANDSHAKE_PACKET && packetLength > 1) {
                        JARCraftinator.log("Handshake from: ", JARCraftinator.getIPAddress(clientSocket));
                        // VARINT - Protocol Version
                        JARCraftinator.log("Protocol Version: " + VarData.readVarInt(stream));
                        // STRING - Server address (size)
                        JARCraftinator.log("Server Address: " + VarData.readVarString(stream, VarData.readVarInt(stream)));
                        // USHORT - Server port
                        JARCraftinator.log("Server Port: " + stream.readUnsignedShort());
                        // VARINT - Next state
                        JARCraftinator.log("Next State: " + VarData.readVarInt(stream));
                        wasHandled = true;
                    }

                    if (packetID == PacketType.REQUEST_PACKET && packetLength == 1) {
                        // Send SLP response
                        JARCraftinator.log("Server pinged by: ", JARCraftinator.getIPAddress(clientSocket));

                        JSONObject slp = new JSONObject();
                        slp.put("version", QuickJSON.getVersionMap("1.12", 335));
                        slp.put("players", QuickJSON.players(0, 100));
                        slp.put("description", QuickJSON.description("Hello world"));
                        slp.put("favicon", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAATsElEQVR42u1aCXgUVbb+b1f13p2ks0OABEI0JCIDsgTCoGhAQUeej0UUFR7KgAMIjo6KAyo6LDMqiCL4HERxCYIgKvuigGyKsioJa0jI1lk7vXdXV9edUxXUecuMMDMSmJn7USSd6nvrnv+c85//VBXDv/hgLb2Blh7/BqClN9DS458KAEVRdDSUf1kAJk6cKLRp08a+aNEiuyiKV9PI3r59e2o4HI6l0wIdbjreoOP0Pw0Agih2oOMXkXC4H+f8quTk5LYZmZkxgsMBJSYGSe3aMp0g4tMlf+QKmRusq1tI0x76pwAgNj4+NyJJ6/s+PDU999ZbYLdYkRAbB2tiPOrpfKLJirLPd+GNxx9D5rRHUfTEU7zhxIkTdKrTFQ+A3eG4vmNu7kdjH3887rUd29D16WnQSRKinIOTWb1s8dj14nzs3rEdOc/OgI0J2DpsJKTGxmg0GEqjJWquWAD0RuOEXgUF8yevWGEKlpVh9isvoevMJ6EwBrNOQG4YeGfceCids5F+793a3x0uLzZPngJKE950+OittMzGKxIAMn5m70GDZtxVWMgOmk1I2bcP6z7bgg6j70YspUDrU+VYOmkS0qY8iMSuP6MZnKICSGv0Yd20aTCltUbFBx8+TCdeuqIAIKITmE63+JbRo8ddv2gRikWRvBmFb93H8DrsaJXdCebte/HBvBdx1aynYU5KhEieV42nH2hTUY8Nryzgjp7dWfGzc6ZTzZx1xQAgGgxmsnb5yOnTh3SYMQ3nmKBtXVFCCHkaYYMO3jfew+6tm3H1s9PBjEbodTo13Mn/gFEQkFp0FptWFvKEgv7s6IQpavgPviIAEE2meLLkk9GvvJxve+BeYncFOhUAskyWAoiLRFHyzFycqK5A1qNTIJFFBip7cWYrRY0eevrsMFkQ2LkXm1a9z5OH38EOjrivkpZuc9kDIJhM7Sj0N97x9rIc66AbENQLFM4MXI3pqAKHP4TDkx+FKzEO7e67G7KiwLN7P1Bbh2BZOXyVlQQWSAukw0ya4FTVObSePB5fF9we5ZFIMl2i8bIFQDCbO+stlo03rVqRZsjNhD7GDuIAMKE5tB1uP/aNHo/UmweA53eHTH9zu93wBoJgZjNCEQmiwqEnrpDr6lE9/1U4unVBwqhhKL7nl1wqr+xDl/nisgSAwr6fOSXl494frogLpcQi3mKB3mbVzqkgtKp2YfM9Y9Hr4amob98K4UgEAUVGmCJApKAWyO3BsAS/PwDBaIBrzVp4vz4E27h7ES0+QcS5mYeKTt5DyxVedgCQ8UMTsrLe7bbyPZPTwhBPBBZD6k4nCqBGB0nFpdg8YTJuW/ASKh0WNHmatND3RWVifAUG0gGqUVyW4XJ7Ubt4CZhBj/jRo+BzN8K7cSsCy9fU0lcG0HH0sgKAjH8wo2fPV7oue10o4kEYmQ4pZgsMVooAMsu4cz/2zn2B37F0CStlEnxBP6Kk/sIRGX4CQKUGNQJUnvBUVqFszjzE3NAXtv79EAkF0bh9B3zLVh7j4fBQyFFVEl8+JKg3mZ665vbbn8mZP4cdC3sQJpqP14lw2O2w6g0IvPUBSnZ8joGvvoyTTU5IZDCiUQ0AXzgMiThANV791/RtMSrmL0LSL0fDkNkeciCA+vdWcP/mHavoUmMhy77vrtviAAh6vUDEtrDP+AkT2k6bgpKgB02UzwbyfpLBhCSrHeUzn4dAP7MevB9na8rJzc06QAWAukD4iANUYhQpRZxbd6Bu9Sdo9euJEOId8FVVoHHRUlk6eea3JByep+rB//z6LQoAhbyJdl5Y8Owzd9hGD4cz6CPjo+RNBXHk/XaiCcceegK5d98NdMlGbb0TOr1eqwTq4HIUgRCJIZqjqj7nsvfhOnkaqb96ADoiP/eRI2hc/Fa94vbehUhk2/+3hxYDQG82x1GyfjJ40cKfKwV90BQJaUTmJmPU3M9URBSNn4rOE8ZDyk6H1+vWclswmbVdcyI+mZjeK4UhB4OoeuFVhKkNjhsyWFN+Tes3oWnFR8foUrciIpf9pX20CABU39Mo9Df94r1l17g7ZyIQjSBIBnnJ+DB5P10x4ey4yejxm0fgTotH0O/VJC3NobJmbJa45P2QSmwVlaiauwDiTf0gdu0MI6WE+63lCH55cCVNuZ/C3vfX9nLJAdBbrTlGu33joFXL2zW2jUeYylWAjA+Q8X4yM8HHUTXpEeTPfBqeJCuCPg8RHTE81XaRqoGqAzjlviJFUHfgMEoXL4X1nuFgrVMQra6G/7W35Wh1zTTK9RcuZD8/CgClm06vYx0o+lpLUZ5E4BtoUoQ+u4hOjtJRdxHG58ekpX3cf+W7CfUOI3GYjBAZ41cBUGvYmRq4n5uLPs/PhpciXab8VijMowSSYDBoAKjGqzXe+dEGnNv0Kcxj7oSRVGLg0BGqFCvrEJZG0sKfXeiefhQA2teoATn2dzokGTXuiVB/KdPhDipYc7CJG0RWJit8HYE0Oxjh1X/ReItlSFJubmHeu0ssdYaoKukgawqOQlk0wLN2O7xrPkLWjMdgMlNB04ua8erBKS1Eq7X5b8EQSl/5I1z1DRCHDoZAHpE2bkNo8+f76DLDyfjKi4nIHwcAeGnusNZT9p8NoNEvg4ykboshP8uKosoQyl1hdEwx8E8OefcpnPenaJX+9xqi2Tyubb9+izr/9zyxgVpYRmWMUzOjStgIMX31nFfQ5CxD0thRVPasMNlsxHK82fskcnSiDiI1M7Lbg5NPzQEyM8DzuiFcVwt55VoePV7yGl1mKuW79GP2XDQAlHrrHypIHrzwQAQ3PzCO2/v1YZ/PmsMT/Q3on+JmB8sCqPVGkGwX8cWZwPu071Gygu/vzVNH93T6sGFPZ81+knmjIWh37ETVuyR1uB4l034Hd6tYmPvlIZYEj1r31RKm5bnmfarvFjOk2nqcnvkHWG8tQJTy3VtcDOmDDUHSwhMp39+8WMMvGACHRSi6Kcfeaf+gCQju/ZIPeu4Z7Fv2Ngb0L8D6GU+wIekB7C8JUErI6tf5CWf4eeKFx6kjU9u2BR0mTZyYNnkMi/DIeUSba7hOUlDx4mtwdUhFtG0rmKiHdxiM2m0tNdcUynPN+/R9/7HjKH+zEHGU75x0vYsUYXjDzjJSdEPJ+AN/q/EXBEBWirGpVaw+tmnW65BTEjj/pogJtbU8/dou7Kqrc7D6toEYka3gs2Kv5q2z9ZLilYWbYbGObDPnd/cnDuwNgSnQ0ca5ct77RG7er45otT+gqToQAAISTBYYqcxpdZ6Mj1IUNHxM9fybIsSPvQuyx42mD9ch9OXRrTTlbgr5+r/H+B8FgFIvLj/L1kjqkcnz3uOBVvFMZWT/1u3I7d0bps6dkHmyAh/eORg3Z5uw+oALqbEiir3GSNu339ITeoj5rnwxnUZmam6HT56Bt8kNtxRChCqAegfHSgDEUuOjE/WqyoFMrWzlojcRtVkQc/vNkKoq4XpzhRIprfo9rTFDuyH4Dxg/BoD9hmx7faxFMGxyObijbx8WN2o4GsorYDv0De/wxMMsx55A7L0e1fMeIonO8GmFgM6btsJpk2CWIzBTLy9QDqs5TeUDUkkpJM34MPwRSVN3ZjLaTvmvKmOVdCKU7+eeXwRjXldY8q5DsKgYrjdWeBSPfwyFxZp/hOEXBIA6Yky6tRNvSrptfaWRJy95R73Hzkrq6uB8ejbvs2QhjA4H6xaXjH2PTEOnU6uxtjiMpD1foc5XiTi9EfoYGyk4A9DQhHBtDYWxF35KAbf2EKPZ+xY67CYjCQ7q+o6fQsWrS2G/4xbo09Pg3/slvKu3HCMAv29hLykAFAW9clqbdlMqiGsj7XmbWTMR0jFWu2sfxNJz0ZwZjwmqEV1MDqzt3hHt44Ddd07l9sHXM7vRhPiEVPiXr4Wz4gzsfXtAUvOfyp8vEqa1BS33raLqfSPcO/ehbu0WxNz5C4pwGb5tu3ho/zeqpH2AyM53Afb84wE4D8JvbusS+/ugpLCDOTfxVpMeYCSLcGL6czzn0YcQ1/Va5jDZ8NWwUbjTfhILHH2VtMcm6pJjklH91POodTuRNmYkFKr9fgp9L4W+Wg7VpsVIIFgpUhre/wj+cxWw/8dAhEtL4d+wMyJX1z9OIT//pzD8YgFgVqNuz1294ntvLfJyULg78roz2eXm6l2X3IUvaBpdXr2Z5R98AytcCTx56VJWMXU6Qkk2xA/oB4M9hkSUhADxQpAksFr2VAD0/hAaXn8HrFUyzL26IHDgCJHsHicPhFVJu/OnNP6CAVCHUa/reXWqcW+v9lZh1WnwtMULYIqNYSFi9PpPd3J7YjKS+vRm7V6eitMNCsqyf87ljESmT0tFTPsM7aHld8arQyU+VlWDhpeXwjKgL8TURAT2foXgnsP7qAqQpFUuStL+5ACog3T/9EHXxDynJ7b/LPYanv7kI0z1vO9oEUI7v+Bpc2YwNnwQ754CtrHBigYSS7qEONjbt0dY1fwEQJiqgZG0AL49BU/hGtj/82ZwFkVgyx4unSz7myXtJQGAUkEgEPaM6OHo9W1liDuH/ReLHzwAhtRkyM56iBlt0fDWciVx1TLddclR7CoN41z+jXAMLqCuT9aMV4j5lU/3Qdp/CLbbboRcX4fApt3BaKP775K0lwQAdegFZLRxGA4X5Npj1x0Pwzp3Nsx53Un0n1d0RGqWsmrU/nYGfm6swLGqEI7m5sOkkhuxf3DZanCfH6YunaA4axDc9XUplyLDqMT9XZL2kgGgDtIqhYM6x9xlNuiwxetA4stzYWjXRitrdr0JcTKHjvS888mn8LOq3ShvkPC5rQMipO44NTqGjDbgZZUIHyneQMXgPiK7hpYw/qIBULtSDizIbWV+0B+Ost4drSitl3A44zokzXwMxgQH9FHqB0Y8wK+eNIE5Bt6AptnzkPXtWkRICK4+4iXhA+25XlhWNpEMvFXLiRYcFwwAEV8yqdYPhnRz9OvZ3oYT5wLYfsqD/Kus2HnCD++YsaTeBquvrsC/dguXSs6h7dTxzGZ3IPD2SiSvmqddbMcJLxxWAZUuOSjJPI8APXrZA0Ce72LQ6z6ZNCC1XbJN1PR7kzeCoooAjlQF0T3DgnWnSPvPmo74tDTt/nxd4Yc8Lr8nixlwPQxGC5y/nYPsbzZot+WPlAepEjBUNEZO0ccevPn1tcsTAIPAbouzCoUTC1LtFn1zL6/eouU0s7wmhH0lPhgNDCSUsCuawtv8bhp1vKIGQumLi3n6Y5MgZqYzi2hGyf1Tke87ivJGqfnukqSgqkn+gJYbcVkCYNazx7ukW2fd2ydR8AWjmueB5rcuqB1AjUtCk0/GxmNu3qujlZ10hlDZ60aeet8Ipj6W9lfXoGbhmzx7yQIm202wcROO3zESt8Y3YNO3HiTZBQIjovjCSjeihSOXDQBqvRd1bOF9fZPG53W0M9VQ9UUDfn4GOz/1XG1QC+fT9SHIFMs3Ztvx2XEfLFMn81gKf7UFrtn9Bed7DiHr9QUMFjuEU+Uou28osh0KDpQGVG5BvU/+mgDoifOXaFEAaEMGUq3vju2XNPxnbS1wUag2G89+mED/+YIyPjnkQi1FQF6mVQvreq+Ma2nOp9UCz1j8ByaYzQg4a1G3fiundgK2kUOYf/027t/yOQoSiESPe5mNUocigIci3E4r+1sUANV4MnblgzemDMlIMMIXimq3udTQZ+yHCWrurv66EXX+CG65Jga0ee12+eZvPVFqnQU9EdzJq/N46oNjWP0aIr6rMtBU+BGPGX47M2ZlwPPG+/y6soPsaHkAreP0OFMruaQoT2jxCCBmXvmrG1OGZyYZNc+rp7W7uCoA578TiihYsb+BZK2C27vEwkJiqIyEzjeVQX7SGZ5AmTB0YK59YJlL5q4hI9D0xQGmJxXoc3u4bu1nLGbaJARn/IFfq/ew0vow4qwiTteED9G8bpfa+P8BAHn/V2PyE1/tkWFDtTty3vQfQl4dAfL8+182IJ7qeP9su+awMKm+nSd8/LgzNIPEzizCKoVAOUznU/ae9jF/p1zO+/VgkqqATpdx8Xgp4s+dYbFmgcCMamRa1hAppMVGtSQA8XFm4fS8kemO0rqQtimmJT7//htqOrz7RQOyUgzokWElAce1mr7jhC963BmeRKLmte8Wpan9E23iph7tLYZzFB3VHhmSyay93SUGAshKNkD9u92kQ1MgijpvdApNe7klAejeO9P21fBuDrgCzaGvPYI/n/hUs3kheT6vg4VlpRg1w9Uz24p9wZK68KhghP+fG5UEwrhEu/hau3iDTtUI6nqUNfASkAQWKcEIHBYdGvxR7gkqV/E/e4f/kgOgvkt4bRtz7b15iaI33FzvVQ+rme8mQFYQ4V1Pkjc1Vq+lhcoDVMf9tR55KBm/+a+s/0s65lNZtajlTr2QQGurz0bU9zSiUY08T8sKslrC+O8BUAd5aOPDBam32IyCdvdGDYCzDWFsPubGwBw7EmyilhFqhKw76inxhaPDJRkHL+AaHen4NUVER5qvvet2nuo9aPb6cjr2tjgA5KHWRG4biAS7qCFeQeLHHZT5TZ1sLIYIi7yEY5Uhvv+sfxP9PpbC2NlSm/5JAFAHRUEK5ekqg8jyW8WIjHJYK3MqIGfrJV7jkRdQBD8S/bOHn1f6+Eu9wDV0wk6hmk2/q/XORp/30+dtF7H2FTFa/DW5lh7/BqClN9DS498AtPQGWnr8CUPiAcglAFMxAAAAAElFTkSuQmCC");

                        // Write total packet length (JSON String length, String length byte length, packet ID length)
                        byte[] pid = VarData.getVarInt(0);
                        byte[] packet = VarData.packString(slp.toJSONString());

                        // Write packet length (packet ID length and packet data length)
                        output.write(VarData.getVarInt(pid.length + packet.length));
                        // Write packet ID
                        output.write(pid);
                        // Write payload
                        output.write(packet);
                        // Flush output
                        output.flush();
                        JARCraftinator.log("Server list ping complete!");

                        if (clientSocket.isConnected()) {
                            JARCraftinator.log("Client still connected so waiting for ping packet...");

                            // Ping packet //
                            VarData.readVarInt(stream); // Packet length
                            VarData.readVarInt(stream); // Packet ID
                            long payload = stream.readLong(); // Packet payload
                            JARCraftinator.log("Ping packet payload: " + payload);

                            JARCraftinator.log("Sending pong packet...");
                            // Pong packet //
                            pid = VarData.getVarInt(0x01);
                            VarData.writeVarInt(output, pid.length + 8);
                            output.write(pid);
                            output.writeLong(payload);
                            output.flush();
                            JARCraftinator.log("Sent.");
                        }

                        wasHandled = true;
                        disconnect();
                        clientSocket.close();
                        JARCraftinator.endConnection(this);
                    }

                    if(!wasHandled) {
                        System.out.println("Unknown packet type recieved from: " + JARCraftinator.getIPAddress(clientSocket) + ", type=" + packetID + ",length=" + packetLength);
                    }
                }
            }catch(IOException ex){
                disconnect();
            }
        }
    }

    public boolean isDisconnected(){
        return !connected;
    }

    public void disconnect(){
        connected = false;
    }

}
