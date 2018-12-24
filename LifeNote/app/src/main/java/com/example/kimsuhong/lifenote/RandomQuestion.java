package com.example.kimsuhong.lifenote;

import java.util.ArrayList;
import java.util.Random;

public class RandomQuestion {
    private ArrayList<String> randomQuestion = new ArrayList<String>();
    public RandomQuestion(){
        init();
    }
    public RandomQuestion(String question){
        init();
        randomQuestion.add(question);
    }
    public void init(){
        randomQuestion.clear();
        randomQuestion.add("가장 추억에 남는 소풍이 언제인가요?"); randomQuestion.add("30년전 나와 지금의 나는  변하였나요?"); randomQuestion.add("현재 읽고 있는 글이나 책이 있다면 어떤 건가요?");
    }
    public void addQuestion(String question){
        randomQuestion.add(question);
    }
    public String getQuestion(){
        int idx = (new Random()).nextInt(randomQuestion.size());
        String value = randomQuestion.get(idx);
        randomQuestion.remove(idx); // 나중에 중복되서 나오지 않게 질문 삭제
        return value;
    }
}
