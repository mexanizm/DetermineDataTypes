import com.google.gson.JsonElement;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class DataTypesDetermine {

    private static final Pattern tString = Pattern.compile("^(?![0-9\\.\\,]+$)[A-Za-zа-яА-ЯїЇґҐєЄіІ\\.\\s0-9_\\-]+" , Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern tStringNum = Pattern.compile("\\d+");
    private static final Pattern tStringFNum = Pattern.compile("(\\d+\\.\\d+)||(\\d+\\,\\d+)");
    private static final Pattern tIsJson = Pattern.compile("\\{.*\\:\\{.*\\:.*\\}\\}" , Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    private static final Pattern tNumType = Pattern.compile("(?<int>\\d+)||(?<double>\\d+\\.\\d+)||(?<dobledel>\\d+\\,\\d+)");

    public static ArrayList<String> noPrimitive = new ArrayList<>(Arrays.asList("String" , "Undefined" , "json" , "JsonElement"));

    public static class ResultCastData {
        private  String type;
        private  Object value;
        private  String meta;

        ResultCastData(){}

        ResultCastData(String type, Object val){
            this.type = type;
            this.value = val;
        }

        public ResultCastData(String type , Object val , String meta){
            this.type = type;
            this.value = val;
            this.meta = meta;
        }


        public String getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        public String getMeta() {
            return meta;
        }

        private ResultCastData set(String type, Object value){
            this.type = type;
            this.value = value;
            return this;
        }

        private ResultCastData set(String type, Object value , String meta){
            this.type = type;
            this.value = value;
            this.meta = meta;
            return this;
        }

    }

    /**
     * @param  data any Object
     * @return returns ResultCastData Object { type , value , meta }
     */
    public static ResultCastData castDataTypeValues(Object data){
        if(data == null){return null;}

        if(data instanceof JsonElement){
            if(((JsonElement) data).isJsonPrimitive()){
                data = castFromJsonData((JsonElement) data);
            }else{
                return new ResultCastData("JsonElement" , data);
            }
        }else if(isJson( String.valueOf(data)) ){
            return  new ResultCastData("json" , data);
        }

        ResultCastData restypes = new ResultCastData();

        if(data instanceof String){
            String str = String.valueOf(data).trim();
            if(str.isEmpty()){
                restypes.set("String" , str);
                return restypes;
            }
            Matcher match = tString.matcher(str);
            if(match.matches()){
                restypes.set("String" , str);
            }else if (tStringNum.matcher(str).matches()){
                if(isIntInRange(str)){
                    restypes.set("int" , Integer.parseInt(str));
                }else {
                    restypes.set("long" , Long.parseLong(str));
                }
            }else{
                Matcher matcherDouble = tStringFNum.matcher(str);
                if(matcherDouble.matches()){
                    if(matcherDouble.group(1) != null){
                        restypes.set("double" , Double.parseDouble(str));
                    }else if(matcherDouble.group(2) != null){
                        restypes.set("double" , Double.parseDouble(str.replace("," , ".")));
                    }
                }else{
                    restypes.set("String" , str , "Undefined");
                }
            }
        }
        if(data instanceof Integer || data instanceof Long || data instanceof Double){
            restypes.set(getPrimitiveFromClass(data) , data);
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

    public static Object getFromNumber(Number num){
        Matcher matcher = tNumType.matcher(String.valueOf(num));
        String numStr = String.valueOf(num);
        if(matcher.matches()){
            if(matcher.group("int") != null){
                if(isIntInRange(numStr)){
                    return Integer.parseInt(numStr);
                }else{
                    return Long.parseLong(numStr);
                }
            }
            if(matcher.group("double") != null){
                return Double.parseDouble(numStr);
            }
            if(matcher.group("dobledel") != null){
               return Double.parseDouble(numStr.replace("," , "."));
            }
        }
        return null;
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

    public static Object castFromJsonData(JsonElement data){
        if(data.isJsonPrimitive()){
            if(data.getAsJsonPrimitive().isString()){
                return data.getAsJsonPrimitive().getAsString();
            }else if(data.getAsJsonPrimitive().isNumber()){
                return getFromNumber(data.getAsJsonPrimitive().getAsNumber());
            }
        }else{
            return data;
        }
        return null;
    }


}
