package com.teacher.TeacherAM.ui.gallery;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teacher.TeacherAM.R;

public class subject_Viewholder extends RecyclerView.ViewHolder {

    private TextView sub_n;

    public subject_Viewholder(@NonNull View itemView) {
        super(itemView);

        sub_n = itemView.findViewById(R.id.sub_n);
    }
    public void setData(String b_n, Context context, Button delete_item, Dialog delete_dialog, Button cancel_dialog, String currentUserId) {
        sub_n.setText(b_n);
    }
}
