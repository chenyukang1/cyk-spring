/*
 * Copyright (c) 2015-2025，千寻位置网络有限公司版权所有。
 *
 * 时空智能 共创数字中国（厘米级定位 | 毫米级感知 | 纳秒级授时）
 */
package com.cyk.spring.aop;

/**
 * The class Main
 *
 * @author yukang.chen
 * @date 2025/5/28
 */
public class Main {
    static boolean ready = false;
    static int number = 0;
    public static void main(String[] args) {
        Thread writer = new Thread(() -> {
            number = 42;
            ready = true;  // 写完了，希望另一个线程能看到
        });

        Thread reader = new Thread(() -> {
            while (true) {
                if (ready) {
                    System.out.println(number); // 理论上应该打印42，但可能打印0！
                    break;
                }
            }
        });

        reader.start();
        writer.start();

    }
}
