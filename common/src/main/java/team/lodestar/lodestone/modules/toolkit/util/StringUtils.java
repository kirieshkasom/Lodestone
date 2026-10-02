package team.lodestar.lodestone.modules.toolkit.util;

public class StringUtils {

    /**
     * Converts a normal integer to a string of roman numerals.
     * @param number The input integer
     * @return A String consisting of roman numbers
     */
    public static String intToRoman(int number) {
        String[] sym = {"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D", "CM", "M"};
        int[] val = {1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000};
        StringBuilder res = new StringBuilder();
        int idx = val.length - 1;
        while(number > 0 && idx >= 0){
            if(number < val[idx]) idx -= 1;
            else{
                number -= val[idx];
                res.append(sym[idx]);
            }
        }
        return res.toString();
    }
}
