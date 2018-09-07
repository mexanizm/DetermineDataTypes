import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class DataTypesDetermine {

    private static final Pattern tString = Pattern.compile("^(?![0-9\\.\\,]+$)[A-Za-zа-яА-ЯїЇґҐєЄіІ\\.\\s0-9_\\-]+" , Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern tStringNum = Pattern.compile("\\d+");
    private static final Pattern tStringFNum = Pattern.compile("(\\d+\\.\\d+)||(\\d+\\,\\d+)");
    private static final Pattern tIsJson = Pattern.compile("\\{.*\\:\\{.*\\:.*\\}\\}" , Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    private static DataTypesDetermine instance = null;

    public static DataTypesDetermine getNewInstance(){
        return instance = new DataTypesDetermine();
    }

    public static DataTypesDetermine getInstance(){
        return instance;
    }

    /**
     * @param  data any Object
     * @return returns array [ typeName , valueCastToType ]
     */
    public ArrayList<Serializable> castDataTypeValues(Object data){
        if(data == null){return null;}
        ArrayList<Serializable> restypes = new ArrayList<Serializable>();

        if(isJson( String.valueOf(data)) ){
            restypes.add("json");
            restypes.add(String.valueOf(data));
        }

        if(data instanceof String){
            String str = String.valueOf(data).trim();
            Matcher match = tString.matcher(str);
            if(match.matches()){
                restypes.add("String");
                restypes.add(str);
            }else if (tStringNum.matcher(str).matches()){
                if(isIntInRange(str)){
                    restypes.add("int");
                    restypes.add(Integer.parseInt(str));
                }else {
                    restypes.add("Long");
                    restypes.add(str);
                }
            }else{
                Matcher matcherDouble = tStringFNum.matcher(str);
                if(matcherDouble.matches()){
                    if(matcherDouble.group(1) != null){
                        restypes.add("double");
                        restypes.add(Double.parseDouble(str));
                    }else if(matcherDouble.group(2) != null){
                        restypes.add("double");
                        restypes.add(str.replace("," , "."));
                    }
                }else{
                    restypes.add("Undefined");
                    restypes.add( str );
                }
            }
        }
        if(data instanceof Integer || data instanceof Long || data instanceof Double){
            String primitiveName = "";
            Class typeClass = data.getClass();
            switch (typeClass.getSimpleName()){
                case "Integer":
                    primitiveName = "int";
                    break;
                case "Double":
                    primitiveName = "double";
                    break;
                case "Long":
                    primitiveName = "long";
                    break;
            }
            restypes.add(primitiveName);
            restypes.add((Serializable) data);
        }
        return restypes;
    }

    public static boolean isNumString(String str) {
        return str != null && str.length() > 0 &&
                IntStream.range(0, str.length()).allMatch(i -> i == 0 && (str.charAt(i) == '-' || str.charAt(i) == '+')
                        || Character.isDigit(str.charAt(i)));
    }

    public static boolean isIntInRange(String str){
        return (str != null && isNumString(str)) && (
                Long.parseLong(str) <= Integer.MAX_VALUE && Long.parseLong(str) >= Integer.MIN_VALUE
        );
    }

    public static boolean isJson(String str){
        return tIsJson.matcher(str).matches();
    }

}
