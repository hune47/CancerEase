package com.graduation.CancerEaseProj.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.graduation.CancerEaseProj.Models.ChatGroup;
import com.graduation.CancerEaseProj.R;
import java.util.List;

public class ChatGroupsAdapter extends RecyclerView.Adapter<ChatGroupsAdapter.ViewHolder> {
    private Context context;
    private List<ChatGroup> chatGroups;
    private OnClickListener onClickListener;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView chatImage;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title_chat);
            chatImage = view.findViewById(R.id.image_chat);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (onClickListener != null && position != RecyclerView.NO_POSITION){
                        onClickListener.onClick(chatGroups.get(position));
                    }
                }
            });
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public ChatGroupsAdapter(Context context, List<ChatGroup> chatGroups) {
        this.context = context;
        this.chatGroups = chatGroups;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
        return new ViewHolder(itemView);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ChatGroup currentChatGroup = chatGroups.get(position);
        holder.title.setText(currentChatGroup.getName());
        int path = 0;
        try {
            path = context.getResources().getIdentifier(currentChatGroup.getImage(), "drawable", context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.chatImage.setImageResource(path);

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int getItemCount() {
        return chatGroups.size();
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
    public void setOnClickListener(OnClickListener clickListener){
        this.onClickListener = clickListener;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public interface OnClickListener {
        void onClick(ChatGroup chatGroup);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
