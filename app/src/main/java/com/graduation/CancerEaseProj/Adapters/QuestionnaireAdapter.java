package com.graduation.CancerEaseProj.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.graduation.CancerEaseProj.Models.Answer;
import com.graduation.CancerEaseProj.Models.Question;
import com.graduation.CancerEaseProj.R;

import java.util.HashMap;
import java.util.List;

public class QuestionnaireAdapter extends RecyclerView.Adapter<QuestionnaireAdapter.ViewHolder> {
    private Context context;
    private List<Question> questions;
    private List<Answer> answers;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionText, questionNoTxt;
        RadioButton choiceRdo1, choiceRdo2, choiceRdo3, choiceRdo4, choiceRdo5;

        public ViewHolder(View view) {
            super(view);
            questionText = view.findViewById(R.id.question_txt);
            questionNoTxt = view.findViewById(R.id.question_no_txt);
            choiceRdo1 = view.findViewById(R.id.choice_rdo_1);
            choiceRdo2 = view.findViewById(R.id.choice_rdo_2);
            choiceRdo3 = view.findViewById(R.id.choice_rdo_3);
            choiceRdo4 = view.findViewById(R.id.choice_rdo_4);
            choiceRdo5 = view.findViewById(R.id.choice_rdo_5);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public QuestionnaireAdapter(Context context, List<Question> questions, List<Answer> answers) {
        this.context = context;
        this.questions = questions;
        this.answers = answers;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new ViewHolder(itemView);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Question currentQuestion = questions.get(position);
        final Answer answer = answers.get(position);
        holder.questionText.setText(currentQuestion.getQuestion());
        holder.questionNoTxt.setText(String.valueOf(currentQuestion.getId()));

        holder.choiceRdo1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                answer.setQuestion_id(currentQuestion.getId());
                if (holder.choiceRdo1.isChecked()){
                    answer.setAnswer_no(1);
                }else if (holder.choiceRdo2.isChecked()) {
                    answer.setAnswer_no(2);
                }else if (holder.choiceRdo3.isChecked()) {
                    answer.setAnswer_no(3);
                }else if (holder.choiceRdo4.isChecked()) {
                    answer.setAnswer_no(4);
                }else if (holder.choiceRdo5.isChecked()) {
                    answer.setAnswer_no(5);
                }
                Log.i("answer", ""+answer.getAnswer_no());
        }
        });
        holder.choiceRdo2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                answer.setQuestion_id(currentQuestion.getId());
                if (holder.choiceRdo1.isChecked()){
                    answer.setAnswer_no(1);
                }else if (holder.choiceRdo2.isChecked()) {
                    answer.setAnswer_no(2);
                }else if (holder.choiceRdo3.isChecked()) {
                    answer.setAnswer_no(3);
                }else if (holder.choiceRdo4.isChecked()) {
                    answer.setAnswer_no(4);
                }else if (holder.choiceRdo5.isChecked()) {
                    answer.setAnswer_no(5);
                }
                Log.i("answer", ""+answer.getAnswer_no());
            }
        });
        holder.choiceRdo3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                answer.setQuestion_id(currentQuestion.getId());
                if (holder.choiceRdo1.isChecked()){
                    answer.setAnswer_no(1);
                }else if (holder.choiceRdo2.isChecked()) {
                    answer.setAnswer_no(2);
                }else if (holder.choiceRdo3.isChecked()) {
                    answer.setAnswer_no(3);
                }else if (holder.choiceRdo4.isChecked()) {
                    answer.setAnswer_no(4);
                }else if (holder.choiceRdo5.isChecked()) {
                    answer.setAnswer_no(5);
                }
                Log.i("answer", ""+answer.getAnswer_no());
            }
        });
        holder.choiceRdo4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                answer.setQuestion_id(currentQuestion.getId());
                if (holder.choiceRdo1.isChecked()){
                    answer.setAnswer_no(1);
                }else if (holder.choiceRdo2.isChecked()) {
                    answer.setAnswer_no(2);
                }else if (holder.choiceRdo3.isChecked()) {
                    answer.setAnswer_no(3);
                }else if (holder.choiceRdo4.isChecked()) {
                    answer.setAnswer_no(4);
                }else if (holder.choiceRdo5.isChecked()) {
                    answer.setAnswer_no(5);
                }
                Log.i("answer", ""+answer.getAnswer_no());
            }
        });
        holder.choiceRdo5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                answer.setQuestion_id(currentQuestion.getId());
                if (holder.choiceRdo1.isChecked()){
                    answer.setAnswer_no(1);
                }else if (holder.choiceRdo2.isChecked()) {
                    answer.setAnswer_no(2);
                }else if (holder.choiceRdo3.isChecked()) {
                    answer.setAnswer_no(3);
                }else if (holder.choiceRdo4.isChecked()) {
                    answer.setAnswer_no(4);
                }else if (holder.choiceRdo5.isChecked()) {
                    answer.setAnswer_no(5);
                }
                Log.i("answer", ""+answer.getAnswer_no());
            }
        });


    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int getItemCount() {
        return questions.size();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public long getItemId(int position) {
        return position;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
