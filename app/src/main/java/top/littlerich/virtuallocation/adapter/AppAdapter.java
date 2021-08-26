package top.littlerich.virtuallocation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.virjar.ratel.virtuallocation.R;

import java.util.ArrayList;
import java.util.List;

import top.littlerich.virtuallocation.common.AppApplication;
import top.littlerich.virtuallocation.model.MyAppInfo;

/**
 * Created by xuqingfu on 2017/4/24.
 */

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    List<MyAppInfo> mListData = new ArrayList<MyAppInfo>();

    public AppAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_info, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.cbAppSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppApplication.setAppMock(viewHolder.myAppInfo.getPkgName(), isChecked);
            }
        });


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyAppInfo myAppInfo = mListData.get(position);
        holder.iv_app_icon.setImageDrawable(myAppInfo.getImage());
        holder.tx_app_name.setText(myAppInfo.getAppName());
        holder.tx_app_pkg.setText(myAppInfo.getPkgName());
        holder.myAppInfo = myAppInfo;
        holder.cbAppSelect.setChecked(
                AppApplication.getAppMockStatus(myAppInfo.getPkgName())
        );
    }

    @Override
    public int getItemCount() {
        return mListData != null ? mListData.size() : 0;
    }

    public void setData(List<MyAppInfo> myAppInfos) {
        this.mListData = myAppInfos;
        notifyDataSetChanged();
    }

    /*




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        MyAppInfo myAppInfo = mListData.get(position);
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_app_info, null);
            mViewHolder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
            mViewHolder.tx_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.iv_app_icon.setImageDrawable(myAppInfo.getImage());
        mViewHolder.tx_app_name.setText(myAppInfo.getAppName());
        return convertView;
    }*/

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_app_icon;
        TextView tx_app_name;
        TextView tx_app_pkg;
        CheckBox cbAppSelect;
        MyAppInfo myAppInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_app_icon = itemView.findViewById(R.id.iv_app_icon);
            tx_app_name = itemView.findViewById(R.id.tv_app_name);
            tx_app_pkg = itemView.findViewById(R.id.tv_app_pkg);
            cbAppSelect = itemView.findViewById(R.id.cb_app_select);
        }
    }
}
