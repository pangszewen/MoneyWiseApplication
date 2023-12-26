package com.example.moneywise.forum;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class DeleteTopic {
    Firebase_Forum firebase = new Firebase_Forum();

    public interface ConfirmationDialogCallback{
        void onConfirmation(boolean status);
    }
    public void showDeleteConfirmationDialog(Context context, ForumTopic topicToDelete, ConfirmationDialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete " + topicToDelete.getSubject() + " ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onConfirmation(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onConfirmation(false);
                    }
                })
                .show();
    }


    public void deleteTopic(Context context, ForumTopic deleteTopic) {
        firebase.deleteTopic(deleteTopic.getTopicID(), new Firebase_Forum.DeleteTopicCallback() {
            @Override
            public void onDeleteTopic(boolean status) {
                if (status) {
                    Toast.makeText(context, "Topic deleted successfully", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Please refresh", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context, "Topic failed to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
