package com.bohra.voicerecorderjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.Audio_VH> {

    private File[] files;
    private TimeFromater timeFromater;
    //to use the interface inside viewholder
    private onItemClick onItemClick;

    //Create a constructor where we will recivie all the list of audio file in Array
    public AudioListAdapter(File[] files,onItemClick onItemClick){
        //now we are assigning all the values using the adapter from audioListFragment
        this.files = files;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public Audio_VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_list_item,parent,false);

        //inatlizing the TimeFormater
        timeFromater = new TimeFromater();

        return new Audio_VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Audio_VH holder, int position) {

        //setting the file name
        holder.auidoName.setText(files[position].getName());
        //setting the date by passing the long value then formatting them
        holder.audioDate.setText(timeFromater.formatTime(files[position].lastModified()));

    }

    @Override
    public int getItemCount() {
        // it will total number of item in our array
        return files.length;
    }

    public class Audio_VH extends RecyclerView.ViewHolder {
        private ImageView audioLogo;
        private TextView auidoName;
        private TextView audioDate;

        public Audio_VH(@NonNull View itemView) {
            super(itemView);

            audioLogo = itemView.findViewById(R.id.audioLogo);
            auidoName = itemView.findViewById(R.id.auidoName);
            audioDate = itemView.findViewById(R.id.audioDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //here we are getting the positon of item where we click
                    onItemClick.onClickItemListner(files[getAdapterPosition()],getAdapterPosition());
                }
            });

        }
    }

    // creating the interface to handel onClickListner
    public interface onItemClick{
        //Creating a method where we are getting the single file where user has clicked and the position
        void onClickItemListner(File file,int position);
    }
}
