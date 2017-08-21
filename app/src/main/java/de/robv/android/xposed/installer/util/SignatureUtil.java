package de.robv.android.xposed.installer.util;

import org.apache.commons.codec.binary.Hex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by lvyonggang on 2017/3/28.
 *  请求签名
 */

public class SignatureUtil {

    private static String TAG="SignatureUtil";

    private static final String AND="&";
    private static final String SPACE="\n";

    private static final String secret="3AWNpJhVMPgIUx3WejmfpCKnUDvgqobn"; //私钥

    /**
     * @function： 对请求的类型及参数，生成一个签名
     * methodType : 请求类型。 get/post/delete等六种
     * uri: 请求地址
     * dataMap: 键值对数据
     * secret: 私钥
     *
     */
    public static String getSignature(String methodType , String uri ,HashMap<String,String>dataMap){
        //1.key全部小写排序,把键值对数据分对用&连接起来
         String value= getSortHashMap(dataMap);
        //2.把methodType uri 及第2步结果用\n连接起来
        String catValue= methodType+"\n"+uri+"\n"+value;
        //3.使用sha256对第2步的结果加密
        String signature = sha256Util(secret , catValue);
        return signature;
    }

    /**
     * @function: 对hash键全部小些并排序，在用&把每一个键值对链接起来
     * @param formHashMap : 哈希数据
     *  返回组装后的结果
     */
    public static String getSortHashMap(HashMap<String,String>formHashMap){
        //1.先对fromHashMap的key小写
        HashMap<String, String> smallMap = new HashMap<>();
        for (Map.Entry<String,String> temp: formHashMap.entrySet()){
            String key= temp.getKey().toLowerCase();
            String value = temp.getValue();
            smallMap.put(key,value);
        }
        //2.对key排序
        Collection<String> keyset= smallMap.keySet();
        ArrayList<String> list = new ArrayList<String>(keyset);
        Collections.sort(list);
        //3.构造排序后的顺序
        HashMap<String, String> resultMap = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        for (int i= 0 ; i<list.size() ;i++){
            String key = list.get(i);
            String value = smallMap.get(key);
            builder.append(key).append("=").append(value);
            if (i<list.size()-1){
                builder.append(AND);
            }
        }
        return builder.toString();
    }

    /**
     *  使用sha256对数据加密
     *  data : 需要加密的值
     *  key ：加密的私钥
     */
    public static String sha256Util(String key, String data){
        String result = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            result = new String(Hex.encodeHex(sha256_HMAC.doFinal(data.getBytes("UTF-8"))));
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
