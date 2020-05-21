package wbq.frame.demo;

import wbq.frame.util.thread.DefaultPoolExecutor;

/**
 * Created by Jerry on 2020/5/7 10:50
 */
public class Test {
    static final DefaultPoolExecutor pool = new DefaultPoolExecutor();

    static class Ru implements Runnable {
        final int num;

        public Ru(int n) {
            num = n;
        }

        @Override
        public void run() {
            System.out.println(String.format("Ru%d start", num));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(String.format("Ru%d finish", num));
        }
    }
    public static void main(String... args) {
        for (int i = 0; i < 10; i++) {
            pool.execute(new Ru(i));
        }
//        fun();
//        Abo a = new Abo();
//        a.start();
//        for (;;) {
//            System.out.println("");
//            if (a.isFlag()) {
//                System.out.println("AAAAAAAAA");
//                break;
//            }
//        }
    }

    static Abo fun() {
        try {
            System.out.println("try");
            return new Abo("return try");
        } catch (Throwable throwable) {
            return new Abo("return catch");
        } finally {
            System.out.println("finally");
        }
    }

    static class  Abo extends Thread {
        private boolean flag;

        Abo(String str) {
            System.out.println(str);
        }
        public boolean isFlag() {
            return flag;
        }

        @Override
        public void run() {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            flag = true;
            System.out.println("flag=" + flag);
        }
    }
}
