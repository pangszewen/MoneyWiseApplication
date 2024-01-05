package com.example.moneywise.login_register;

public class FAQ {
    String id;
    String question;
    String answer;

    public FAQ(){}

    public FAQ(String id,String question,String answer){
        this.id=id;
        this.question=question;
        this.answer=answer;
    }

    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }
}
