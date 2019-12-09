/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bean;

/**
 *
 * @author User
 */
public class MobileAccount {

    private int id;
    private String mac_wifi;
    private String nick;

    public MobileAccount() {
    }

    public MobileAccount(String mac_wifi, String nick) {
        setMac_wifi(mac_wifi);
        this.nick = nick;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the mac_wifi
     */
    public String getMac_wifi() {
        return mac_wifi;
    }

    /**
     * @param mac_wifi the mac_wifi to set
     */
    public void setMac_wifi(String mac_wifi) {
        mac_wifi = mac_wifi.replaceAll(":", "");
        char[] chars = mac_wifi.toCharArray();
        String nString = "";
        for (int x = 0; x < chars.length; x++) {
            if (x % 2 == 1 && x < 10) {
                nString += chars[x] + ":";
            } else {
                nString += chars[x];
            }
        }
        this.mac_wifi = nString;
    }

    /**
     * @return the nick
     */
    public String getNick() {
        return nick;
    }

    /**
     * @param nick the nick to set
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    public String toString() {
        return this.mac_wifi + "      " + this.nick;
    }
}
