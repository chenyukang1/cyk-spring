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
