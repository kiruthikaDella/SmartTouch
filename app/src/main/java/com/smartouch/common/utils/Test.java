package com.smartouch.common.utils;

import com.smartouch.AppDelegate;
import com.smartouch.R;
import com.smartouch.adapters.faqadapter.AnswerModel;
import com.smartouch.adapters.faqadapter.QuestionModel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Jignesh Dangar on 15-04-2021.
 */
public class Test {

    public static List<QuestionModel> makeQuestion() {
        return Arrays.asList(Que1(),Que2(),Que3(),Que4());
    }

    public static QuestionModel Que1() {
        return new QuestionModel(AppDelegate.instance.getString(R.string.que1), makeRockArtists());
    }

    public static QuestionModel Que2() {
        return new QuestionModel(AppDelegate.instance.getString(R.string.que2), makeRockArtists());
    }

    public static QuestionModel Que3() {
        return new QuestionModel(AppDelegate.instance.getString(R.string.que3), makeRockArtists());
    }

    public static QuestionModel Que4() {
        return new QuestionModel(AppDelegate.instance.getString(R.string.que4), makeRockArtists());
    }

    public static List<AnswerModel> makeRockArtists() {
        AnswerModel queen = new AnswerModel(AppDelegate.instance.getString(R.string.title_text_answer), true);
        return Arrays.asList(queen);
    }
}
