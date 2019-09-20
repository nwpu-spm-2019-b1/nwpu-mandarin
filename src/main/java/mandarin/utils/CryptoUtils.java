package mandarin.utils;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;

import java.nio.charset.StandardCharsets;

public class CryptoUtils {
    private static SodiumLibrary SODIUM;

    public interface SodiumLibrary extends Library {

        public static SodiumLibrary INSTANCE = null;

        int crypto_pwhash_str(byte[] hashedPassword,
                              byte[] password,
                              long passwordLen,
                              long opslimit,
                              NativeLong memlimit);

        int crypto_pwhash_str_verify(byte[] hashedPassword,
                                     byte[] password,
                                     long passwordLen);

        int sodium_init();

        int crypto_pwhash_strbytes();

        long crypto_pwhash_opslimit_interactive();

        NativeLong crypto_pwhash_memlimit_interactive();

        long crypto_pwhash_opslimit_moderate();

        NativeLong crypto_pwhash_memlimit_moderate();

        long crypto_pwhash_opslimit_sensitive();

        NativeLong crypto_pwhash_memlimit_sensitive();
    }

    static {
        if (Platform.isWindows()) {
            SODIUM = Native.load("libsodium", SodiumLibrary.class);
        } else {
            SODIUM = Native.load("sodium", SodiumLibrary.class);
        }
        if (SODIUM.sodium_init() != 0) {
            throw new RuntimeException("Error initializing libsodium");
        }
        System.out.println(String.format("%s-%s", System.getProperty("os.name"), System.getProperty("os.arch")));
    }

    public static boolean verifyPassword(String password, String hash) {
        byte[] hashBytes = hash.getBytes(StandardCharsets.US_ASCII);
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        int retcode = SODIUM.crypto_pwhash_str_verify(hashBytes, passwordBytes, passwordBytes.length);
        return retcode == 0;
    }

    public static String hashPassword(String password) {
        byte[] out = new byte[SODIUM.crypto_pwhash_strbytes()];
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        int retcode = SODIUM.crypto_pwhash_str(out,
                passwordBytes, passwordBytes.length,
                SODIUM.crypto_pwhash_opslimit_interactive(),
                SODIUM.crypto_pwhash_memlimit_interactive());
        if (retcode != 0) {
            throw new RuntimeException("libsodium crypto_pwhash_str failed, returned " + retcode + ", expected 0");
        }
        String outString = new String(out, StandardCharsets.UTF_8);
        return outString.substring(0, outString.indexOf(0));
    }
}
