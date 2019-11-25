package stws.chatstocker.view.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.app.adprogressbarlib.AdCircleProgress;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import stws.chatstocker.ConstantsValues;
import stws.chatstocker.R;
import stws.chatstocker.model.ChatMessage;
import stws.chatstocker.utils.DateTimeUtils;
import stws.chatstocker.view.AudioPalyerActivity;
import stws.chatstocker.view.FullProfilePicViewrActivity;
import stws.chatstocker.view.FullscreenImageActivity;
import stws.chatstocker.view.VideoPlayerActivity;

public class ChatAppMsgAdapter extends RecyclerView.Adapter<ChatAppMsgAdapter.ChatAppMsgViewHolder> {

    private List<ChatMessage> msgDtoList = null;
    private ArrayList<ChatMessage> selectedMessageList = new ArrayList<>();
    private int rowIndex = -1;
    ItemSelectedListner itemSelectedListner;
    private boolean isGroup;
    private boolean isSelected = false;

    public ChatAppMsgAdapter(List<ChatMessage> msgDtoList, ItemSelectedListner itemSelectedListner, boolean isGroup) {
        this.msgDtoList = msgDtoList;
        this.itemSelectedListner = itemSelectedListner;
        this.isGroup = isGroup;

    }

    public List<ChatMessage> getSelectedMessageList() {
        return selectedMessageList;
    }


    @Override
    public void onBindViewHolder(ChatAppMsgViewHolder holder, int position) {
        ChatMessage msgDto = this.msgDtoList.get(position);
        if (msgDto.isSentToserver()){
            if (msgDto.getType().equals("image")){
                holder.imgTickSingleImage.setVisibility(View.VISIBLE);
                holder.imgTickDoubleImage.setVisibility(View.VISIBLE);
            }
            else if (msgDto.getType().equals("text")) {
                holder.imgTickDouble.setVisibility(View.VISIBLE);
                holder.imgTickSingle.setVisibility(View.VISIBLE);
            }
            else if (msgDto.getType().equals("audio")){
                holder.imgTickDoubleAudio.setVisibility(View.VISIBLE);
                holder.imgTickDoubleAudio.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.imgTickDouble.setVisibility(View.GONE);
            holder.imgTickSingleImage.setVisibility(View.VISIBLE);
            holder.imgTickSingleAudio.setVisibility(View.VISIBLE);
//            holder.imgTickSingle.setVisibility(View.GONE);
        }

        String cuurentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        holder.parentLayout.setBackgroundColor(msgDtoList.get(position).isSelected() ? Color.GRAY : Color.TRANSPARENT);
        // If the message is a received message.
        if (msgDto.getProgressValue() == 100)
            holder.pgb_progress.setVisibility(View.GONE);
        else {
            holder.pgb_progress.setVisibility(View.VISIBLE);
            holder.pgb_progress.setAdProgress(msgDto.getProgressValue());
        }
        if (isGroup) {
            holder.tvUserNameRight.setVisibility(View.VISIBLE);
            holder.tvUserNameLeft.setVisibility(View.VISIBLE);
        } else {
            holder.tvUserNameRight.setVisibility(View.GONE);
            holder.tvUserNameLeft.setVisibility(View.GONE);
        }
        if (!msgDto.getFrom().equals(cuurentUserId)) {
            // Show received message in left linearlayout.
            if (msgDto.getType().equals("image") || msgDto.getType().equals("video")) {
                holder.rightImgLayout.setVisibility(View.VISIBLE);
                holder.rightAudioLayout.setVisibility(View.GONE);
                holder.leftAudioLayout.setVisibility(View.GONE);
                holder.leftMsgLayout.setVisibility(LinearLayout.GONE);
                Glide.with(holder.leftImgLayout.getContext()).load(msgDto.getMsg()).into(holder.imgFileRight);
            } else if (msgDto.getType().equals("audio")) {
                holder.rightAudioLayout.setVisibility(View.VISIBLE);
                holder.leftAudioLayout.setVisibility(View.GONE);
                holder.rightImgLayout.setVisibility(View.GONE);
                holder.leftMsgLayout.setVisibility(LinearLayout.GONE);
//                holder.imgFileRight.setImageResource(R.drawable.audio_file);
//                Glide.with(holder.leftImgLayout.getContext()).load(convertAudiotoThumbnail(Uri.parse(msgDto.getMsg()))).into(holder.imgFileRight);
            } else if (msgDto.getType().equals("text"))  {
                holder.rightAudioLayout.setVisibility(View.GONE);
                holder.leftAudioLayout.setVisibility(View.GONE);
                holder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);
                holder.leftMsgTextView.setText(msgDto.getMsg());
                holder.tvUserNameLeft.setText(msgDto.getSenderName());
                // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
                // Otherwise each iteview's distance is too big.
                holder.leftDate.setText(DateTimeUtils.Companion.convertMillisecondtodate(Long.parseLong(msgDto.getDate()), "hh:mm a"));
            }
            holder.rightMsgLayout.setVisibility(LinearLayout.GONE);
        }
        // If the message is a sent message.
        else {
            if (msgDto.getType().equals("image") || msgDto.getType().equals("video")) {
                holder.rightAudioLayout.setVisibility(View.GONE);
                holder.leftAudioLayout.setVisibility(View.GONE);
                holder.leftImgLayout.setVisibility(View.VISIBLE);
                holder.rightMsgLayout.setVisibility(LinearLayout.GONE);
                Glide.with(holder.rightImgLayout.getContext()).load(msgDto.getMsg()).into(holder.imgFileLeft) ;
            } else if (msgDto.getType().equals("audio")) {
                holder.rightAudioLayout.setVisibility(View.GONE);
                holder.leftAudioLayout.setVisibility(View.VISIBLE);
                holder.leftImgLayout.setVisibility(View.GONE);
                holder.rightMsgLayout.setVisibility(LinearLayout.GONE);
                holder.imgFileLeft.setImageResource(R.drawable.audio_recording_pause);

//                Glide.with(holder.rightImgLayout.getContext()).load(convertAudiotoThumbnail(Uri.parse(msgDto.getMsg()))).into(holder.imgFileLeft);
            } else {
                holder.rightAudioLayout.setVisibility(View.GONE);
                holder.leftAudioLayout.setVisibility(View.GONE);
                // Show sent message in right linearlayout.
                holder.rightMsgLayout.setVisibility(LinearLayout.VISIBLE);
                holder.rightMsgTextView.setText(msgDto.getMsg());
                holder.tvUserNameRight.setText(msgDto.getSenderName());
                holder.rightDate.setText(DateTimeUtils.Companion.convertMillisecondtodate(Long.parseLong(msgDto.getDate()), "hh:mm a"));
                // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
                // Otherwise each iteview's distance is too big.
            }
            holder.leftMsgLayout.setVisibility(LinearLayout.GONE);

        }
//        holder.imgTickDoubleImage.bringToFront();
//        holder.imgTickSingleImage.bringToFront();


    }

    @Override
    public ChatAppMsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_chat_app_item_view, parent, false);
        return new ChatAppMsgViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (msgDtoList == null) {
            msgDtoList = new ArrayList<ChatMessage>();
        }
        return msgDtoList.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftMsgLayout, parentLayout;
        AdCircleProgress pgb_progress;
        LinearLayout rightMsgLayout;
        RelativeLayout rightImgLayout;
        ConstraintLayout leftImgLayout;
        ConstraintLayout rightAudioLayout, leftAudioLayout;

        TextView leftMsgTextView, leftDate, tvUserNameLeft;

        TextView rightMsgTextView, rightDate, tvUserNameRight;
        ImageView imgFileLeft, imgFileRight,imgTickSingle,imgTickDouble,imgTickSingleImage,imgTickDoubleImage,imgTickSingleAudio,imgTickDoubleAudio;

        public ChatAppMsgViewHolder(View itemView) {
            super(itemView);

            if (itemView != null) {
                leftMsgLayout = (LinearLayout) itemView.findViewById(R.id.chat_left_msg_layout);
                parentLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);
                rightMsgLayout = (LinearLayout) itemView.findViewById(R.id.chat_right_msg_layout);
                leftMsgTextView = (TextView) itemView.findViewById(R.id.chat_left_msg_text_view);
                rightMsgTextView = (TextView) itemView.findViewById(R.id.chat_right_msg_text_view);
                rightDate = itemView.findViewById(R.id.chatRightdate);
                leftDate = itemView.findViewById(R.id.chatLeftdate);
                imgTickDouble= itemView.findViewById(R.id.imgTickDouble);
                imgTickSingle= itemView.findViewById(R.id.imgTickSingle);
                imgTickDoubleAudio= itemView.findViewById(R.id.imgTickDoubleAudio);
                imgTickDoubleImage= itemView.findViewById(R.id.imgTickDoubleImage);
                imgTickSingleAudio= itemView.findViewById(R.id.imgTickSingleAudio);
                imgTickSingleImage= itemView.findViewById(R.id.imgTickSingleImage);
                leftImgLayout = itemView.findViewById(R.id.left_img_layout);
                rightImgLayout = itemView.findViewById(R.id.right_img_layout);
                imgFileLeft = itemView.findViewById(R.id.imgFileLeft);
                imgFileRight = itemView.findViewById(R.id.imgFileRight);
                tvUserNameRight = itemView.findViewById(R.id.tvUserNameRight);
                tvUserNameLeft = itemView.findViewById(R.id.tvUserNameLeft);
                leftAudioLayout = itemView.findViewById(R.id.left_audio_layout);
                rightAudioLayout = itemView.findViewById(R.id.right_audio_layout);
                pgb_progress = itemView.findViewById(R.id.pgb_progress);
                imgFileLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isSelected) {
                            selectImage();
                            return;
                        }
                        if (msgDtoList.get(getAdapterPosition()).getType().equals("image")) {
                            Intent intent = new Intent(v.getContext(), FullProfilePicViewrActivity.class);
                            intent.putExtra(ConstantsValues.KEY_ISFROM_CHAT, true);
                            intent.putExtra(ConstantsValues.KEY_FILE_URL, msgDtoList.get(getAdapterPosition()).getMsg());
                            v.getContext().startActivity(intent);
                        } else if (msgDtoList.get(getAdapterPosition()).getType().equals("video")) {
                            Intent intent = new Intent(v.getContext(), VideoPlayerActivity.class);
                            intent.putExtra(ConstantsValues.KEY_ISFROM_CHAT, true);
                            intent.putExtra(ConstantsValues.KEY_FILE_URL, msgDtoList.get(getAdapterPosition()).getMsg());
                            v.getContext().startActivity(intent);
                        } else {
                            Intent intent = new Intent(v.getContext(), AudioPalyerActivity.class);
                            intent.putExtra(ConstantsValues.KEY_PATH, msgDtoList.get(getAdapterPosition()).getMsg());
                            intent.putExtra(ConstantsValues.KEY_ISFROM_CHAT, true);
                            v.getContext().startActivity(intent);
                        }
                    }
                });

                imgFileRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isSelected) {
                            selectImage();
                            return;
                        }
                        if (msgDtoList.get(getAdapterPosition()).getType().equals("image")) {
                            Intent intent = new Intent(v.getContext(), FullProfilePicViewrActivity.class);
                            intent.putExtra(ConstantsValues.KEY_FILE_URL, msgDtoList.get(getAdapterPosition()).getMsg());
                            intent.putExtra(ConstantsValues.KEY_ISFROM_CHAT, true);
                            v.getContext().startActivity(intent);
                        } else if (msgDtoList.get(getAdapterPosition()).getType().equals("video")) {
                            Intent intent = new Intent(v.getContext(), VideoPlayerActivity.class);
                            intent.putExtra(ConstantsValues.KEY_FILE_URL, msgDtoList.get(getAdapterPosition()).getMsg());
                            intent.putExtra(ConstantsValues.KEY_ISFROM_CHAT, true);
                            v.getContext().startActivity(intent);
                        } else {
                            Intent intent = new Intent(v.getContext(), AudioPalyerActivity.class);
                            intent.putExtra(ConstantsValues.KEY_FILE_URL, msgDtoList.get(getAdapterPosition()).getMsg());
                            intent.putExtra(ConstantsValues.KEY_ISFROM_CHAT, true);
                            v.getContext().startActivity(intent);
                        }
                    }
                });
                leftAudioLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isSelected) {
                            selectImage();
                            return;
                        }
                        Intent intent = new Intent(v.getContext(), AudioPalyerActivity.class);
                        intent.putExtra(ConstantsValues.KEY_PATH, msgDtoList.get(getAdapterPosition()).getMsg());
                        intent.putExtra(ConstantsValues.KEY_ISFROM_CHAT, true);
                        v.getContext().startActivity(intent);
                    }
                });
                rightAudioLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isSelected) {
                            selectImage();
                            return;
                        }
                        Intent intent = new Intent(v.getContext(), AudioPalyerActivity.class);
                        intent.putExtra(ConstantsValues.KEY_PATH, msgDtoList.get(getAdapterPosition()).getMsg());
                        intent.putExtra(ConstantsValues.KEY_ISFROM_CHAT, true);
                        v.getContext().startActivity(intent);
                    }
                });
                rightMsgLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isSelected) {
                            selectImage();
                            return;
                        }

                    }
                });
                leftMsgLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isSelected) {
                            selectImage();
                            return;
                        }

                    }
                });

            }
            initLongClick();
        }

        private void initLongClick() {
            imgFileRight.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    selectImage();
//                    msgDtoList.get(getAdapterPosition()).setSelected(!msgDtoList.get(getAdapterPosition()).isSelected());
//                    parentLayout.setBackgroundColor(msgDtoList.get(getAdapterPosition()).isSelected() ? Color.GRAY : Color.TRANSPARENT);
////                notifyDataSetChanged();
//                    if (msgDtoList.get(getAdapterPosition()).isSelected()) {
//                        selectedMessageList.add(msgDtoList.get(getAdapterPosition()));
//                        itemSelectedListner.onItemSelected(selectedMessageList);
//                    } else {
//                        selectedMessageList.remove(msgDtoList.get(getAdapterPosition()));
//                        itemSelectedListner.onItemSelected(selectedMessageList);
//                    }
//                    if (selectedMessageList.size() > 0)
//                        isSelected = true;
//                    else
//                        isSelected = false;
                    return true;
                }
            });
            imgFileLeft.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    selectImage();
//                    msgDtoList.get(getAdapterPosition()).setSelected(!msgDtoList.get(getAdapterPosition()).isSelected());
//                    parentLayout.setBackgroundColor(msgDtoList.get(getAdapterPosition()).isSelected() ? Color.GRAY : Color.TRANSPARENT);
////                notifyDataSetChanged();
//                    if (msgDtoList.get(getAdapterPosition()).isSelected()) {
//                        selectedMessageList.add(msgDtoList.get(getAdapterPosition()));
//                        itemSelectedListner.onItemSelected(selectedMessageList);
//                    } else {
//                        selectedMessageList.remove(msgDtoList.get(getAdapterPosition()));
//                        itemSelectedListner.onItemSelected(selectedMessageList);
//                    }
//                    if (selectedMessageList.size() > 0)
//                        isSelected = true;
//                    else
//                        isSelected = false;
                    return true;
                }
            });
            leftMsgLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    selectImage();
//                    msgDtoList.get(getAdapterPosition()).setSelected(!msgDtoList.get(getAdapterPosition()).isSelected());
//                    parentLayout.setBackgroundColor(msgDtoList.get(getAdapterPosition()).isSelected() ? Color.GRAY : Color.TRANSPARENT);
////                notifyDataSetChanged();
//                    if (msgDtoList.get(getAdapterPosition()).isSelected()) {
//                        selectedMessageList.add(msgDtoList.get(getAdapterPosition()));
//                        itemSelectedListner.onItemSelected(selectedMessageList);
//                    } else {
//                        selectedMessageList.remove(msgDtoList.get(getAdapterPosition()));
//                        itemSelectedListner.onItemSelected(selectedMessageList);
//                    }
//                    if (selectedMessageList.size() > 0)
//                        isSelected = true;
//                    else
//                        isSelected = false;
                    return true;
                }
            });
            rightMsgLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    selectImage();
//                    msgDtoList.get(getAdapterPosition()).setSelected(!msgDtoList.get(getAdapterPosition()).isSelected());
//                    parentLayout.setBackgroundColor(msgDtoList.get(getAdapterPosition()).isSelected() ? Color.GRAY : Color.TRANSPARENT);
//                    if (msgDtoList.get(getAdapterPosition()).isSelected()) {
//                        selectedMessageList.add(msgDtoList.get(getAdapterPosition()));
//                        itemSelectedListner.onItemSelected(selectedMessageList);
//                    } else {
//                        selectedMessageList.remove(msgDtoList.get(getAdapterPosition()));
//                        itemSelectedListner.onItemSelected(selectedMessageList);
//                    }
//                    if (selectedMessageList.size() > 0)
//                        isSelected = true;
//                    else
//                        isSelected = false;
                    //                notifyDataSetChanged();
                    return true;
                }
            });
            leftAudioLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    selectImage();
//                    msgDtoList.get(getAdapterPosition()).setSelected(!msgDtoList.get(getAdapterPosition()).isSelected());
//                    parentLayout.setBackgroundColor(msgDtoList.get(getAdapterPosition()).isSelected() ? Color.GRAY : Color.TRANSPARENT);
//                    if (msgDtoList.get(getAdapterPosition()).isSelected()) {
//                        selectedMessageList.add(msgDtoList.get(getAdapterPosition()));
//                        itemSelectedListner.onItemSelected(selectedMessageList);
//                    } else {
//                        selectedMessageList.remove(msgDtoList.get(getAdapterPosition()));
//                        itemSelectedListner.onItemSelected(selectedMessageList);
//                    }
//                    if (selectedMessageList.size() > 0)
//                        isSelected = true;
//                    else
//                        isSelected = false;
                    //                notifyDataSetChanged();
                    return true;
                }
            });
            rightAudioLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    selectImage();
//                    msgDtoList.get(getAdapterPosition()).setSelected(!msgDtoList.get(getAdapterPosition()).isSelected());
//                    parentLayout.setBackgroundColor(msgDtoList.get(getAdapterPosition()).isSelected() ? Color.GRAY : Color.TRANSPARENT);
//                    if (msgDtoList.get(getAdapterPosition()).isSelected()) {
//                        selectedMessageList.add(msgDtoList.get(getAdapterPosition()));
//                        itemSelectedListner.onItemSelected(selectedMessageList);
//                    } else {
//                        selectedMessageList.remove(msgDtoList.get(getAdapterPosition()));
//                        itemSelectedListner.onItemSelected(selectedMessageList);
//                    }
//                    if (selectedMessageList.size() > 0)
//                        isSelected = true;
//                    else
//                        isSelected = false;
                    //                notifyDataSetChanged();
                    return true;
                }
            });
        }

        public void selectImage() {
//            if (isSelected) {
//            msgDtoList.get(getAdapterPosition()).setSelected(!msgDtoList.get(getAdapterPosition()).isSelected());

            if (!msgDtoList.get(getAdapterPosition()).isSelected()) {
                msgDtoList.get(getAdapterPosition()).setSelected(true);
                selectedMessageList.add(msgDtoList.get(getAdapterPosition()));
                notifyDataSetChanged();
//                parentLayout.setBackgroundColor( Color.GRAY );
                itemSelectedListner.onItemSelected(selectedMessageList);
            } else {
                msgDtoList.get(getAdapterPosition()).setSelected(false);
                selectedMessageList.remove(msgDtoList.get(getAdapterPosition()));
                notifyDataSetChanged();
//                parentLayout.setBackgroundColor( Color.TRANSPARENT);
                itemSelectedListner.onItemSelected(selectedMessageList);
            }


            if (selectedMessageList.size()>=1)
            isSelected=true;
            else
                isSelected=false;
        }

//        }
    }

    public interface ItemSelectedListner {
        public void onItemSelected(ArrayList<ChatMessage> list);

    }


}