package stws.chatstocker.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import stws.chatstocker.R;
import stws.chatstocker.model.ChatMessage;
import stws.chatstocker.utils.DateTimeUtils;

public class ChatAppMsgAdapter extends RecyclerView.Adapter<ChatAppMsgAdapter.ChatAppMsgViewHolder> {

    private List<ChatMessage> msgDtoList = null;

    public ChatAppMsgAdapter(List<ChatMessage> msgDtoList) {
        this.msgDtoList = msgDtoList;
    }

    @Override
    public void onBindViewHolder(ChatAppMsgViewHolder holder, int position) {
        ChatMessage msgDto = this.msgDtoList.get(position);
        String cuurentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // If the message is a received message.
        if(!msgDto.getFrom().equals(cuurentUserId))
        {
            // Show received message in left linearlayout.
            if (msgDto.getType().equals("image")) {
                holder.rightImgLayout.setVisibility(View.VISIBLE);
                holder.leftMsgLayout.setVisibility(LinearLayout.GONE);
                Glide.with(holder.leftImgLayout.getContext()).load(msgDto.getMsg()).into(holder.imgFileRight);
            }
            else {
                holder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);
                holder.leftMsgTextView.setText(msgDto.getMsg());
                // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
                // Otherwise each iteview's distance is too big.
                holder.leftDate.setText(DateTimeUtils.Companion.convertMillisecondtodate(Long.parseLong(msgDto.getDate()), "hh:mm a"));
            }
            holder.rightMsgLayout.setVisibility(LinearLayout.GONE);
        }
        // If the message is a sent message.
        else
        {
            if (msgDto.getType().equals("image")) {
                holder.leftImgLayout.setVisibility(View.VISIBLE);
                holder.rightMsgLayout.setVisibility(LinearLayout.GONE);
                Glide.with(holder.rightImgLayout.getContext()).load(msgDto.getMsg()).into(holder.imgFileLeft);
            }
            else {
                // Show sent message in right linearlayout.
                holder.rightMsgLayout.setVisibility(LinearLayout.VISIBLE);
                holder.rightMsgTextView.setText(msgDto.getMsg());
                holder.rightDate.setText(DateTimeUtils.Companion.convertMillisecondtodate(Long.parseLong(msgDto.getDate()), "hh:mm a"));
                // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
                // Otherwise each iteview's distance is too big.
            }
            holder.leftMsgLayout.setVisibility(LinearLayout.GONE);
        }
    }

    @Override
    public ChatAppMsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_chat_app_item_view, parent, false);
        return new ChatAppMsgViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if(msgDtoList==null)
        {
            msgDtoList = new ArrayList<ChatMessage>();
        }
        return msgDtoList.size();
    }

    public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftMsgLayout,leftImgLayout;

        LinearLayout rightMsgLayout,rightImgLayout;

        TextView leftMsgTextView,leftDate;

        TextView rightMsgTextView,rightDate;
        ImageView imgFileLeft,imgFileRight;
        public ChatAppMsgViewHolder(View itemView) {
            super(itemView);

            if(itemView!=null) {
                leftMsgLayout = (LinearLayout) itemView.findViewById(R.id.chat_left_msg_layout);
                rightMsgLayout = (LinearLayout) itemView.findViewById(R.id.chat_right_msg_layout);
                leftMsgTextView = (TextView) itemView.findViewById(R.id.chat_left_msg_text_view);
                rightMsgTextView = (TextView) itemView.findViewById(R.id.chat_right_msg_text_view);
                rightDate=itemView.findViewById(R.id.chatRightdate);
                leftDate=itemView.findViewById(R.id.chatLeftdate);
                leftImgLayout=itemView.findViewById(R.id.left_img_layout);
                rightImgLayout=itemView.findViewById(R.id.right_img_layout);
                imgFileLeft=itemView.findViewById(R.id.imgFileLeft);
                imgFileRight=itemView.findViewById(R.id.imgFileRight);
            }
        }
    }
}