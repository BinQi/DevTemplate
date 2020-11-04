package wbq.frame.demo;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }

        @NonNull
        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append('[');
            ListNode node = this;
            while (node != null) {
                b.append(node.val).append(',');
                node = node.next;
            }
            b.setCharAt(b.length() - 1, ']');
            return b.toString();
        }

        static ListNode create(int[] values) {
            ListNode head = null, tail = null;
            for (int val : values) {
                if (null == head) {
                    head = new ListNode(val);
                    tail = head;
                } else {
                    tail.next = new ListNode(val);
                    tail = tail.next;
                }
            }
            return head;
        }
    }
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        if (null == l1) {
            return l2;
        } else if (null == l2){
            return l1;
        }
        int in = 0;
        final ListNode result = l1;
        while (l1 != null) {
            if (null == l2 && in == 0) {
                break;
            }
            l1.val += (l2 != null ? l2.val : 0) + in;
            in = l1.val / 10;
            l1.val %= 10;
            if (l1.next == null) {
                l1.next = in > 0 ? addTwoNumbers(l2 != null ? l2.next : null, new ListNode(in)) : (l2 != null ? l2.next : null);
                break;
            }
            l1 = l1.next;
            if (l2 != null) {
                l2 = l2.next;
            }
        }
        return result;
    }
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(target - nums[i])) {
                return new int[]{i, map.get(target - nums[i])};
            } else {
                map.put(nums[i], i);
            }
        }
        return null;
    }

    public String convert(String s, int numRows) {
        if (1 == numRows) {
            return s;
        }
        final String[] ret = new String[Math.min(numRows, s.length())];
        Arrays.fill(ret, "");
        int row = 0;
        boolean goDown = true;
        for (int i = 0; i < s.length(); i++) {
            ret[row] += s.charAt(i);
            if (0 == row || numRows - 1 == row) {
                goDown = 0 == row;
            }
            row += goDown ? 1 : -1;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ret.length; i++) {
            sb.append(ret[i]);
        }
        return sb.toString();
    }

    public static boolean isPalindrome(int x) {
        if (x < 0) {
            return false;
        } else if ( x < 10) {
            return true;
        }
        for (int revertHalf = 0; ; ) {
            revertHalf = revertHalf * 10 + x % 10;
            if (0 == revertHalf) {
                return false;
            }
            x /= 10;
            if (x == revertHalf || (x > 9 && x / 10 == revertHalf)) {
                return true;
            } else if (revertHalf > x) {
                return false;
            }
        }
    }

    public static int lengthOfLongestSubstring(String s) {
        String sub = "";
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            final char target = s.charAt(i);
            final int found = tmp.indexOf(String.valueOf(target));
            if (found > -1) {
                if (tmp .length() > sub.length()) {
                    sub = tmp.toString();
                }
                tmp.delete(0, found + 1);
                tmp.append(target);
            } else {
                tmp.append(target);
            }
        }
        if (tmp .length() > sub.length()) {
            sub = tmp.toString();
        }
        return sub.length();
    }

    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
        final int length = nums1.length + nums2.length;
        final int[] nums = new int[length];
        int index1 = 0, index2 = 0;
        for (int i = 0; i < length; i++) {
            if (index1 >= nums1.length && index2 < nums2.length) {
                System.arraycopy(nums2, index2, nums, i, length - i);
                break;
            } else if (index2 >= nums2.length && index1 < nums1.length) {
                System.arraycopy(nums1, index1, nums, i, length - i);
                break;
            }
            if (nums1[index1] < nums2[index2]) {
                nums[i] = nums1[index1];
                index1++;
            } else {
                nums[i] = nums2[index2];
                index2++;
            }
        }
        return length % 2 == 0 ? (nums[length / 2] + nums[length / 2 - 1]) / 2d : nums[length / 2];
    }

    public static String longestPalindrome(String s) {
        final int length = s.length();
        String longest = "", tmp, tmp1;
        for (int i = 0; i < s.length(); i++) {
            tmp = "";
            tmp1 = "" + s.charAt(i);
            boolean odd = true, pair = true;
            for (int j = 1; ; j++) {
                //偶数
                if (pair && i + j < length && i - j + 1 > -1 && s.charAt(i + j) == s.charAt(i - j + 1)) {
                    tmp = s.charAt(i + j) + tmp + s.charAt(i + j);
                } else {
                    pair = false;
                }

                //奇数
                if (odd && i - j > -1 && i + j < length && s.charAt(i + j) == s.charAt(i - j)) {
                    tmp1 = s.charAt(i - j) + tmp1 + s.charAt(i - j);
                } else {
                    odd = false;
                }
                if (!odd && !pair) {
                    break;
                }
            }
            if (tmp.length() > longest.length()) {
                longest = tmp;
            }
            if (tmp1.length() > longest.length()) {
                longest = tmp1;
            }
        }
        return longest;
    }

    public static int myAtoi(String str) {
        str = str != null ? str.trim() : "";
        if (str.length() == 0) {
            return 0;
        }
        int num = 0;
        final boolean negative = str.charAt(0) == '-' ? true : false;
        boolean isOutRange =  false, invalid = false;
        int i = negative ? 1 :  (str.charAt(0) == '+' ? 1 : 0);
        if (i < str.length() && !Character.isDigit(str.charAt(i))) {
            return 0;
        }
        int limit = -Integer.MAX_VALUE;
        if (negative) {
            limit = Integer.MIN_VALUE;
        }
        int multmin = limit / 10;
        for (; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                int digit = Character.digit(str.charAt(i), 10);
                if (num < multmin) {
                    return negative ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                }
                num *= 10;
                if (num < limit + digit) {
                    return negative ? Integer.MIN_VALUE : Integer.MAX_VALUE;
                }
                num -= digit;
            } else {
                break;
            }
        }
        return negative ? num : -num;
    }

    public static int mapRoman(char c) {
        switch (c) {
            case 'I':
                return 1;
            case 'V':
                return 5;
            case 'X':
                return 10;
            case 'L':
                return 50;
            case 'C':
                return 100;
            case 'D':
                return 500;
            case 'M':
                return 1000;
            default:
                return 0;
        }
    }

    public static int romanToInt(String s) {
        int result = 0, length = s.length();
        for (int i = 0; i < length; i++) {
            int v = mapRoman(s.charAt(i));
            int v1 = i + 1 < length ? mapRoman(s.charAt(i + 1)) : 0;
            if (v < v1) {
                result += v1 - v;
                i++;
            } else {
                result += v;
            }
        }
        return result;
    }

    public static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if (null == l1) {
            return l2;
        } else if (null == l2) {
            return l1;
        }
        ListNode result = null, tail = null, tmp;
        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                tmp = l1;
                l1 = l1.next;
            } else {
                tmp = l2;
                l2 = l2.next;
            }
            if (null == result) {
                result = tmp;
                tail = tmp;
            } else {
                tail.next = tmp;
                tail = tail.next;
            }
        }
        if (tail != null) {
            tail.next = l1 != null ? l1 : l2;
        }
        return result;
    }

    public static char getMatch(char c) {
        switch (c) {
            case '(':
                return ')';
            case '{':
                return '}';
            case '[':
                return ']';
        }
        return 'a';
    }
    public static boolean isValid(String s) {
        if (null == s || s.length() == 0) {
            return true;
        }
        final int[] stack = new int[s.length() / 2];
        int peekIndex = -1;
        for (int i = 0; i < s.length(); i++) {
            if (peekIndex != -1
                    && getMatch(s.charAt(stack[peekIndex])) == s.charAt(i)) {
                peekIndex--;
            } else {
                peekIndex++;
                if (peekIndex >= stack.length) {
                    return false;
                }
                stack[peekIndex] = i;
            }
        }
        return peekIndex < 0;
    }

    public static int longestCommonSubsequence(String text1, String text2) {
        return longest(text1, 0, text2, 0);
    }

    public static int longest(String text1, int index1, String text2, int index2) {
        if (index1 >= text1.length() || index2 >= text2.length()) {
            return 0;
        }
        if (text1.charAt(index1) == text2.charAt(index2)) {
            return 1 + longest(text1, index1 + 1, text2, index2 + 1);
        }
        int n1 = longest(text1, index1, text2, index2 + 1);
        int n2 = longest(text1, index1 + 1, text2, index2);
        return Math.max(n1, n2);
    }

    public static void main(String... args) {
        int[] nums1 = new int[]{1, 2, 4};
        int[] nums2 = new int[]{0};
//        System.out.println("result=" + longestPalindrome("babad"));
        System.out.println("result=" +  longestCommonSubsequence("pmjghexybyrgzczy", "hafcdqbgncrcbihkd"));

        List<Integer> a = new ArrayList<>();
        List<Integer> b = new ArrayList<>();
        a.add(1);
        a.add(2);
        a.add(3);
        a.add(4);
        a.add(5);

        b.add(9);
        b.add(6);

        System.out.println(a.removeAll(b));
        for (int i = 0; i < a.size(); i++) {
            System.out.println(a.get(i));
        }
//        pool.execute(new Ru(i));
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
