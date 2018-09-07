import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class DataTypesDetermine {

    private static final Pattern tString = Pattern.compile("^(?![0-9\\.\\,]+$)[A-Za-zа-яА-ЯїЇґҐєЄіІ\\.\\s0-9_\\-]+" , Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern tStringNum = Pattern.compile("\\d+");
    private static final Pattern tStringFNum = Pattern.compile("(\\d+\\.\\d+)||(\\d+\\,\\d+)");
    private static final Pattern tIsJson = Pattern.compile("\\{.*\\:\\{.*\\:.*\\}\\}" , Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public static ArrayList<String> noPrimitive = new ArrayList<>(Arrays.asList("String" , "Undefined" , "json"));

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
            return restypes;
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
                    restypes.add("long");
                    restypes.add(Long.parseLong(str));
                }
            }else{
                Matcher matcherDouble = tStringFNum.matcher(str);
                if(matcherDouble.matches()){
                    if(matcherDouble.group(1) != null){
                        restypes.add("double");
                        restypes.add(Double.parseDouble(str));
                    }else if(matcherDouble.group(2) != null){
                        restypes.add("double");
                        restypes.add(Double.parseDouble(str.replace("," , ".")));
                    }
                }else{
                    restypes.add("Undefined");
                    restypes.add( str );
                }
            }
        }
        if(data instanceof Integer || data instanceof Long || data instanceof Double){
            restypes.add(getPrimitiveFromClass(data));
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

    public static String getPrimitiveFromClass(Object object){
        Class<?> cls = object.getClass();
        String primitiveName = null;
        if(!noPrimitive.contains(cls.getSimpleName())) {
            try {
                Field fi = cls.getField("TYPE");
                primitiveName = String.valueOf(fi.get(cls));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return primitiveName;
    }

}
