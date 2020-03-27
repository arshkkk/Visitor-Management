package com.example.visitormanagement.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visitormanagement.Booking_Meeting;
import com.example.visitormanagement.Person.Host;
import com.example.visitormanagement.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HostAdapter extends RecyclerView.Adapter<HostAdapter.HostViewHolder>
{
    private List<Host> HostList;
    private Context context;



    public HostAdapter (List<Host> HostList, Context context)
    {
        this.HostList = HostList;
        this.context = context;
    }



    public class HostViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView photo;
        public TextView name, email;
        public CardView root;

        public HostViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.photoOfVisitor);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.emailOfHost);
            root = itemView.findViewById(R.id.root);

        }


        public void setPhoto(String url){
            Picasso.get().load(url).into(this.photo);
        }
    }



    @NonNull
    @Override
    public HostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.host_card, viewGroup, false);

        return new HostViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final HostViewHolder HostViewHolder, int i)
    {
        final Host host = HostList.get(i);

        HostViewHolder.name.setText(host.getFullName());
        HostViewHolder.email.setText(host.getEmail());
        HostViewHolder.setPhoto(host.getPhoto());

        HostViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(HostViewHolder.root, host.getUid());
            }
        });

    }

    @Override
    public int getItemCount() {
        return HostList.size();
    }

    //Popup Menu on Cards
    public boolean showMenu(View anchor, String uid) {
        PopupMenu popup = new PopupMenu(this.context, anchor);
        popup.getMenuInflater().inflate(R.menu.custom_menu, popup.getMenu());

        final String hostId= uid;
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                Intent intent = new Intent(context, Booking_Meeting.class);
                intent.putExtra("hostId",hostId);
                context.startActivity(intent);
                return true;
            }
        });

        popup.show();
        return true;
    }
}