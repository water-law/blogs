package com.example.demo.col;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author duwei
 * @date 2021/5/10
 */
public class Test {

    public static void main(String[] args) {

        List<String> list = new ArrayList<>(12);
        list.add("");

        list = new LinkedList<String>();
        list.add("");

        list = new Vector<>();
        list.add("");

        Stack<String> stack = new Stack<>();

        Set<String> set = new HashSet<>();
        set.add("");

        set = new TreeSet<>();
        set.add("");

        Queue<String> queue = new LinkedList<>();
        // qq
        Hashtable<String, String> table = new Hashtable<>();
        table.put("null", null);

        HashMap<String, String> map = new HashMap<>();
        map.put(null, null);
    }

}
