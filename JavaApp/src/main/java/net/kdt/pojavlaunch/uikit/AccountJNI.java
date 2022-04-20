package net.kdt.pojavlaunch.uikit;

import java.net.UnknownHostException;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.authenticator.microsoft.MicrosoftAuthTask;
import net.kdt.pojavlaunch.value.MinecraftAccount;

public class AccountJNI {
    public static final int TYPE_SELECTACC = 0;
    public static final int TYPE_MICROSOFT = 1;
    public static final int TYPE_OFFLINE = 2;
    
    public static volatile MinecraftAccount CURRENT_ACCOUNT;

    // Call back about account credentials for login
    public static String loginAccount(int type,
        String data // One of:
        // Local username
        // Microsoft token
    ) {
        try {
            switch (type) {
                case TYPE_SELECTACC:
                    CURRENT_ACCOUNT = MinecraftAccount.load(data);
                    try {
                        if (CURRENT_ACCOUNT.accessToken.length() > 5) {
                            CURRENT_ACCOUNT = new MicrosoftAuthTask().run("true", CURRENT_ACCOUNT.msaRefreshToken);
                        }
                        CURRENT_ACCOUNT = MinecraftAccount.load(data);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        // ignore, since it's likely that the user is offline
                    }
                    return (CURRENT_ACCOUNT.accessToken.equals("0") ? "-" : "$") + CURRENT_ACCOUNT.username;
                
                case TYPE_MICROSOFT:
                    CURRENT_ACCOUNT = new MicrosoftAuthTask().run("false", data);
                    return "$" + CURRENT_ACCOUNT.username;
                
                case TYPE_OFFLINE:
                    CURRENT_ACCOUNT = new MinecraftAccount();
                    CURRENT_ACCOUNT.username = data;
                    CURRENT_ACCOUNT.save();
                    return "-" + CURRENT_ACCOUNT.username;
            }
        } catch (Throwable th) {
            Tools.showError(th);
        }
        
        return null;
    }
    
    static {
        // System.loadLibrary("pojavexec");
    }
}
