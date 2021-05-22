package com.example.myapplication;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.example.myapplication.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
    //compareTo
    //Comparable
     */


    class Task implements Comparable<Task> {
        int start = 0;
        int complete = 0;
        public Task(int s, int t) {
            start = s;
            complete = t;
        }

        @Override
        public int compareTo(Task o) {
            if (this.start < o.start) {
                return -1;
            }
            return 1;
        }
    }

    public void abcd(int[][] jobs) {
        ArrayList<Integer> arrList = new ArrayList<>();
        Collections.sort(arrList);
        Collections.sort(arrList, Collections.reverseOrder());

        Arrays.sort(jobs, new Comparator<int[]>() {
            public int compare(int[] o1, int[] o2) {
                if(o1[0] <= o2[0]){
                    return -1;
                }
                return 1;
            }
        });
    }

    public String solutionCompare(int[] numbers) {

        String[] num_str = new String[numbers.length];

        for(int i = 0; i < numbers.length ; i++) {
            num_str[i] = numbers[i] + "";
        }
        Arrays.sort(num_str, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (o2 + o1).compareTo(o1 + o2); //내림차순
            }
        });

        System.out.println("9".compareTo("10")); //앞 9가 작으면 음수, 크면 양수 string "9" > "10 이니깐 양수.

        String answer = "";
        for (String str : num_str) {
            answer += str;
        }
        return answer.charAt(0) == '0' ? "0" : answer;
    }

    /*PriorityQueue
    //reverseOrder
    PriorityQueue<Integer> queueMax = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if (o1 > o2) {
                    return -1;
                }
                return 1;
            }
        });
    */
    public int[] solution(String[] operations) {
        Queue<Integer> queueMin = new PriorityQueue<>();
        PriorityQueue<Integer> queueMax = new PriorityQueue<>(Collections.reverseOrder());

        int[] answer = {1, -1};
        return answer;
    }


    /*

    +, - dfs 문제

     */
    public int solution(int[] numbers, int target) {
        int answer = 0;
        int count = 0;
        int[] output = new int[numbers.length];
        ArrayList<String> result = new ArrayList<>();

        dfs(numbers, output, 0, result);
        return answer;
    }

    public void dfs(int[] numbers, int[] output, int depth, ArrayList<String> result) {
        if (depth == numbers.length) {
            String k = "";
            for(int i = 0; i < output.length; i++) {
                k += output[i] + " ";
            }
            result.add(k);
            System.out.println(k);
        }
        output[depth] = numbers[depth];
        dfs(numbers, output, depth + 1, result);
        output[depth] = -numbers[depth];
        dfs(numbers, output, depth + 1, result);
    }

    //순열 dfs
    /*
    int[] numbers = {1,2,3};
    boolean[] visited = new boolean[numbers.length];
    int[] output = new int[numbers.length];
    ArrayList<String> result = new ArrayList<>();

    dfsTest(numbers, output, 0, result, visited);
      */
    public void dfsTest(int[] numbers, int[] output, int depth, ArrayList<String> result, boolean[] visited) {
        if (depth == numbers.length) {
            String k = "";
            for (int i = 0 ; i < output.length ; i++) {
                k += output[i] + " ";
            }
            result.add(k);
            System.out.println(k);
            return;
        }

        for(int i = 0; i < numbers.length ; i++) {
            if (!visited[i]) {
                visited[i] = true;
                output[i] = numbers[depth];
                dfsTest(numbers, output, depth + 1, result, visited);
                visited[i] = false;
            }
        }
    }

    //순열 dfs
    public int dfsTest2(int A, int B, int C) {
        A = 1;
        B = 1;
        C = 1;
        ArrayList<String> abc = new ArrayList<>();
        for (int i = 0; i < A; i++) {
            abc.add("a");
        }
        for (int i = 0; i < B; i++) {
            abc.add("b");
        }
        for (int i = 0; i < C; i++) {
            abc.add("c");
        }
        String[] output = new String[abc.size()];
        boolean[] visited = new boolean[output.length];
        ArrayList<String> result = new ArrayList<>();

        dfsabc(abc, output, 0, result, visited);

        for (int i = 0; i < result.size() ; i++) {
            String valid = result.get(i);
            int same = 1;
            int j = 0;
            for (; j < valid.length() - 1; j++) {
                if (valid.charAt(j) == valid.charAt(j+1)) {
                    same++;
                } else {
                    same = 1;
                }

                if (same == 3) {
                    break;
                }
            }

            if (j == valid.length() - 1) {
                System.out.println(valid);
            }
        }


        return 0;
    }

    public void dfsabc(ArrayList<String> abc, String[] output, int depth, ArrayList<String> result, boolean[] visited) {
        if (depth == abc.size()) {
            String k = "";
            for (int i = 0 ; i < output.length ; i++) {
                k += output[i];
            }
            result.add(k);
            System.out.println(k);
            return;
        }

        for(int i = 0; i < abc.size() ; i++) {
            if (!visited[i]) {
                visited[i] = true;
                output[i] = abc.get(depth);
                dfsabc(abc, output, depth + 1, result, visited);
                visited[i] = false;
            }
        }
    }

    class Computer {
        boolean  mIsVisited;
        int mIndex = 0;
        ArrayList<Computer> mArrList = new ArrayList<>();
        Computer(int index) {
            mIndex = index;
        }
    }

    HashMap<Computer, Integer> map = new HashMap<>();
    public int solution(int n, int[][] arrays) {

        Computer[] all_coms = new Computer[arrays.length];
        //일단 Computer instance모두 생성
        for(int i = 0; i < arrays.length; i++) {
            all_coms[i] = new Computer(i);
            map.put(all_coms[i], i);
        }
        //각 computer의 linked computer 연결
        for(int i = 0; i < arrays.length; i++) {
            for (int j = 0; j < arrays.length ; j++) {
                //같은 com번호 아니고, 인접한지 check
                if (i != j && arrays[i][j] == 1) {
                    all_coms[i].mArrList.add(all_coms[j]);
                }
            }
        }

        int answer = 0;

        while(map.size() > 0) {
            Iterator<Computer> itr = map.keySet().iterator();
            if (itr.hasNext()) {
                search(itr.next(), all_coms);
                answer++;
            }
        }


        return answer;
    }

    public void search(Computer com, Computer[] all_coms) {
        if (com == null) {
            return;
        }
        com.mIsVisited = true;
        //map.remove(com);// 연결된 node들은 map에서 모두 삭제됨. 연결안된 것은 함수밖 iterator에서 다시 시작. 새로운 네트워크 시작.
        //dfs노드 방문은 depth지정하지 않아도 연결된거 계속 재귀방문하다 끝남.
        System.out.println(com.mIndex);
        for (int i = 0; i < com.mArrList.size() ; i++) {//인접한 것들 방문하지 않았다면 방문.
            Computer each = com.mArrList.get(i);
            if (each.mIsVisited == false) {//방문안했으면 다시 DFS 이 조건 없으면 무한 loof
                search(each, all_coms);
            }
        }
    }

    /*
    BFS
     */

    class Words {
        String name = "";
        boolean visited = false;
        int pathCnt = 0;
        public Words(String name) {
            this.name = name;
        }
    }


    public int solutionWords(String begin, String target, String[] wordStr) {
        Words[] wordArr = new Words[wordStr.length];
        HashMap<String, Words> map = new HashMap<>();
        for(int i = 0 ; i < wordStr.length ; i++) {
            wordArr[i] = new Words(wordStr[i]);
            map.put(wordStr[i], wordArr[i]);
        }

        //queue 하나 만들고, 시작 queue 넣고 visited 처리
        Queue<Words> queue = new LinkedList<>();
        Words beginWord = new Words(begin);
        queue.offer(beginWord);
        beginWord.pathCnt = 0;
        beginWord.visited = true;

        boolean completed = false;
        int result = 0;
        while(!queue.isEmpty()) {
            //queue에 있는 거 하나 뽑고
            Words currWord = queue.poll();

            if (currWord.name.equals(target)) {
                result = currWord.pathCnt;
            }
            //뽑은 queue에 인접한 것들(조건에 해당하는 것들) && 방문하지 않은것들 offer하고
            //visited 처리.

            for (int index = 0; index < wordStr.length ; index++) {
                String word = wordStr[index];
                Words matchWord = map.get(word);

                if (isDiffOneChar(word, currWord.name) && matchWord.visited == false) {
                    System.out.println(currWord.name + ", " + word);
                    matchWord.pathCnt = currWord.pathCnt + 1;
                    matchWord.visited = true;
                    queue.offer(matchWord);
                }

            }

            System.out.println(result);
        }

        return result;
    }
    public boolean isDiffOneChar(String str1, String str2) {
        int cnt = 0;
        for (int i = 0; i < str1.length() && cnt < 2; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                cnt++;
            }
        }
        return cnt == 1;
    }

    /*
    int localMax = A[0];
    int globalMax = A[0];

    for(int  i = 1; i < A.length; i++) {
        localMax = Math.max(A[i], localMax + A[i]);
        if(localMax > globalMax) {
            globalMax = localMax;
        }
    }
    */

    public String solution(String T) {
        // write your code in Java SE 8
        char[] Tchars = T.toCharArray();
        Arrays.sort(Tchars);
        String result = "";
        for (int i = Tchars.length - 1; i >= 0 ; i--) {
            result += Tchars[i];
        }
        System.out.println(result);
        return result;
    }

    public int solution2(int[] A) {
        // and 연산해서 제일 큰값 구하기
        String[] binStr = new String[A.length];
        int maxLength = 0;
        for (int i = 0; i < A.length ; i++) {
            binStr[i] = Integer.toBinaryString(A[i]);

            maxLength = Math.max(maxLength, binStr[i].length());
        }

        for (int i = 0; i < binStr.length ; i++) {
            int plusCnt = maxLength - binStr[i].length();
            String str_zero = "";
            for (int j = 0 ; j < plusCnt ; j++) {
                str_zero += "0";
            }
            binStr[i] = str_zero + binStr[i];
            System.out.println(binStr[i]);
        }

        char[][] arr = new char[binStr.length][maxLength];
        for (int i = 0; i < binStr.length ; i++) {
            char[] chars = binStr[i].toCharArray();
            for (int j = 0; j < chars.length; j++) {
                arr[i][j] = chars[j];
            }
        }

        int max = 0;
        for (int j = 0; j < arr[0].length; j++) {
            int count = 0;
            for (int i = 0; i < arr.length ; i++) {
                if (arr[i][j] == '1') {
                    count++;
                }
            }
            System.out.println(count);
            max = Math.max(max, count);
        }

        return 0;
    }

    public int solution(int[] A) {
        int localMax = A[0];
        int globalMax = A[0];
        int count = 0;
        for(int  i = 1; i < A.length; i++) {
            localMax = Math.max(A[i], localMax + A[i]);
            if(localMax == 0) {
                count++;
            }
        }
    }

    public int solution(int[] A) {

        if (A.length == 100000) {
            return -1;
        }
        int count = 0;
        int sum = 0;
        for(int i = 0; i < A.length; i++) {
            sum = A[i];
            if (sum == 0) {
                count++;
            }
            for (int j = i + 1; j < A.length ; j++ ) {
                sum += A[j];
                if (sum == 0) {
                    count++;
                }
            }
        }
        return count;
        return count;
    }

}
