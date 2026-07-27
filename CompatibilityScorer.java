import java.util.HashMap;
import java.util.Map;

public class CompatibilityScorer {

    // The "Camelot wheel" is a system DJs use to label every musical key with
    // a simple code like "8B". Two songs are easy to blend if their codes are
    // identical, share the same number, or sit right next to each other.
    private static final Map<String, String> CAMELOT = new HashMap<>();
    static {
        CAMELOT.put("Abm", "1A"); CAMELOT.put("B",   "1B");
        CAMELOT.put("Ebm", "2A"); CAMELOT.put("F#",  "2B");
        CAMELOT.put("Bbm", "3A"); CAMELOT.put("Db",  "3B");
        CAMELOT.put("Fm",  "4A"); CAMELOT.put("Ab",  "4B");
        CAMELOT.put("Cm",  "5A"); CAMELOT.put("Eb",  "5B");
        CAMELOT.put("Gm",  "6A"); CAMELOT.put("Bb",  "6B");
        CAMELOT.put("Dm",  "7A"); CAMELOT.put("F",   "7B");
        CAMELOT.put("Am",  "8A"); CAMELOT.put("C",   "8B");
        CAMELOT.put("Em",  "9A"); CAMELOT.put("G",   "9B");
        CAMELOT.put("Bm",  "10A"); CAMELOT.put("D",  "10B");
        CAMELOT.put("F#m", "11A"); CAMELOT.put("A",  "11B");
        CAMELOT.put("Dbm", "12A"); CAMELOT.put("E",  "12B");
    }

    // Combine a tempo score and a key score into one number from 0 (bad match) to 100 (great match).
    public static double score(double tempo1, String key1, double tempo2, String key2) {
        double tempoPoints = tempoScore(tempo1, tempo2);
        double keyPoints = keyScore(key1, key2);
        return (tempoPoints + keyPoints) / 2.0;
    }

    private static double tempoScore(double t1, double t2) {
        // Songs also mix well if one tempo is exactly double the other
        // (a 90 BPM beat lines up naturally with a 180 BPM track), so we
        // check all three possibilities and keep whichever gap is smallest.
        double gap = Math.min(Math.abs(t1 - t2),
                     Math.min(Math.abs(t1 - t2 * 2), Math.abs(t1 * 2 - t2)));
        return Math.max(0, 100 - (gap * 5));
    }

    private static double keyScore(String key1, String key2) {
        String pos1 = CAMELOT.get(key1);
        String pos2 = CAMELOT.get(key2);
        if (pos1 == null || pos2 == null) return 50; // key not recognised, stay neutral

        if (pos1.equals(pos2)) return 100; // exact same key

        int num1 = Integer.parseInt(pos1.replaceAll("[AB]", ""));
        int num2 = Integer.parseInt(pos2.replaceAll("[AB]", ""));
        char letter1 = pos1.charAt(pos1.length() - 1);
        char letter2 = pos2.charAt(pos2.length() - 1);

        if (num1 == num2) return 90; // relative major/minor pair - blends beautifully

        int distance = Math.min(Math.abs(num1 - num2), 12 - Math.abs(num1 - num2));
        if (distance == 1 && letter1 == letter2) return 80; // neighbours on the wheel
        if (distance == 1) return 60;

        return 30; // far apart so risky to mix
    }

    // A tiny built-in test so we can test to see if it works
    public static void main(String[] args) {
        System.out.println("Am @120 vs C @122   -> " + score(120, "Am", 122, "C"));
        System.out.println("C @120 vs F# @174   -> " + score(120, "C", 174, "F#"));
        System.out.println("Em @128 vs Am @126  -> " + score(128, "Em", 126, "Am"));
    }
}