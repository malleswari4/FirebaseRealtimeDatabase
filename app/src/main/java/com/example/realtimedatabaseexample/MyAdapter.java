package com.example.realtimedatabaseexample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.Holdview> {
    Context ct;
    ArrayList<MyModel> list;
    DatabaseReference reference;

    public MyAdapter(Context ct, ArrayList<MyModel> list) {
        this.ct = ct;
        this.list = list;
    }

    @NonNull
    @Override
    public MyAdapter.Holdview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holdview(LayoutInflater.from(ct).inflate(R.layout.row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.Holdview holder, final int position) {
        holder.textName.setText(list.get(position).getName());
        holder.textRoll.setText(list.get(position).getRoll());
        holder.texNum.setText(list.get(position).getNumber());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //code to delete the data from the
                reference = FirebaseDatabase.getInstance().getReference("Data");
                reference.child(list.get(position).getRoll()).removeValue();
                Toast.makeText(ct, "Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(ct);
                ViewGroup group=v.findViewById(R.id.content);
                View view =LayoutInflater.from(ct).inflate(R.layout.updatelayout,group,false);
                builder.setView(view);
                builder.setCancelable(false);
                final EditText sname=view.findViewById(R.id.etname);
                EditText sroll=view.findViewById(R.id.etroll);
                final EditText snum=view.findViewById(R.id.etnum);
                sname.setText(list.get(position).getName());
                sroll.setText(list.get(position).getRoll());
                sroll.setEnabled(false);
                snum.setText(list.get(position).getNumber());
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reference=FirebaseDatabase.getInstance().getReference();
                        Query query =reference.child("Data").orderByChild("roll").equalTo(list.get(position).getRoll());
                        final HashMap<String,Object> map=new HashMap<>();
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    map.put("name",sname.getText().toString());
                                    map.put("number",snum.getText().toString());
                                    dataSnapshot.getRef().updateChildren(map);
                                    Toast.makeText(ct, "Data updated", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class Holdview extends RecyclerView.ViewHolder {
        TextView textName,textRoll,texNum;
        ImageView edit,delete;
        public Holdview(@NonNull View itemView) {
            super(itemView);
            textName=itemView.findViewById(R.id.tvname);
            textRoll=itemView.findViewById(R.id.tvroll);
            texNum=itemView.findViewById(R.id.tvnum);
            edit=itemView.findViewById(R.id.edit);
            delete=itemView.findViewById(R.id.delete);
        }
    }
}
