/*
 * @author: GuYouda
 * @date: 2018/3/20
 * @time: 23:31
 * @des: ModifierList 常用操作封装工具类
 *       主要提供一些OpenJava原始ModifierList中没有的操作
 */

package mujava.op.concurrence.util;

import openjava.ptree.ModifierList;

import java.util.HashMap;

public class ModifierListUtil {

    private static HashMap<String, Integer> modifierMap = new HashMap<>();

    static {
        modifierMap.put("public", ModifierList.PUBLIC);
        modifierMap.put("protected", ModifierList.PROTECTED);
        modifierMap.put("private", ModifierList.PRIVATE);
        modifierMap.put("static", ModifierList.STATIC);
        modifierMap.put("final", ModifierList.FINAL);
        modifierMap.put("synchronized", ModifierList.SYNCHRONIZED);
        modifierMap.put("volatile", ModifierList.VOLATILE);
        modifierMap.put("transient", ModifierList.TRANSIENT);
        modifierMap.put("native", ModifierList.NATIVE);
        modifierMap.put("abstract", ModifierList.ABSTRACT);
    }

    /**
     * 由于原始API未提供删除modifier的方法，故提供此方法
     *
     * @param modifier        modifier eg. static, public
     * @param oldModifierList 原始ModifierList
     * @return 删除指定Modifier 的 ModifierList
     */
    public static ModifierList removeModifier(String modifier, ModifierList oldModifierList) {
        ModifierList newModifierList = new ModifierList();

        String oldModifierListStr = oldModifierList.toString();
        String[] modifierListArr = oldModifierListStr.split(" ");

        for (String aModifierListStr : modifierListArr) {

            if (!modifier.equals(aModifierListStr)) {
                int modifierCode = modifierMap.get(aModifierListStr);
                newModifierList.add(modifierCode);
            }
        }
        return newModifierList;

    }

}
