package demo3;

import java.util.Stack;

/**
 * Created by jet on 2017/7/14.
 */
public class Solution {
    public int evalRPN(String[] tokens) {
        Stack<Integer> stack = new Stack<Integer>();

            for(int i=0;i<tokens.length;i++){
                try{
                    int num = Integer.parseInt(tokens[i]);
                    stack.push(num);
            }catch(Exception e){
                int a = stack.pop();
                int b = stack.pop();
                stack.push(getBind(a,b,tokens[i].charAt(0)));
            }

       }
       return stack.pop();
    }

    private int getBind(int a, int b, char token) {
        switch(token){
            case '+': return a+b;
            case '-': return b-a;
            case '*': return a*b;
            case '/': return b/a;
            default: return 0;

        }


    }
}
