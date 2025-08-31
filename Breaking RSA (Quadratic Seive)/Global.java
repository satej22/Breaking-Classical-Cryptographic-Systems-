import java.util.Arrays;

public class Global {

    static long startTime = System.currentTimeMillis();

    public static String breaker = " | ";

    public static void print(Object o){
        float timediff = (float) ((System.currentTimeMillis() - startTime) / 1000.0);
        String time = String.valueOf(timediff);
        System.out.println(time +": "+ o.toString());
    }

    public static void print(Object o, Object o1 ){
        float timediff = (float) ((System.currentTimeMillis() - startTime) / 1000.0);
        String time = String.valueOf(timediff);
        System.out.println(time +": "+ o.toString() + breaker + o1.toString());
    }

    public static void print(Object o, Object o1, Object o2  ){
        float timediff = (float) ((System.currentTimeMillis() - startTime) / 1000.0);
        String time = String.valueOf(timediff);
        System.out.println(time +": "+ o.toString() + breaker + o1.toString() +breaker + o2.toString());
    }

    public static void print(Object o, Object o1, Object o2 , Object o3 ){
        float timediff = (float) ((System.currentTimeMillis() - startTime) / 1000.0);
        String time = String.valueOf(timediff);
        System.out.println(time +": "+ o.toString() + breaker+ o1.toString() + breaker+ o2.toString()+ breaker+ o1.toString());
    }

    public static void print(Object[] o){
        float timediff = (float) ((System.currentTimeMillis() - startTime) / 1000.0);
        String time = String.valueOf(timediff);
        System.out.println(time +": "+ Arrays.toString(o));
    }
}
