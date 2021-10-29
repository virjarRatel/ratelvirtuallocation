package top.littlerich.virtuallocation.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.virjar.ratel.virtuallocation.R;

import java.util.ArrayList;
import java.util.List;

public class PoiAdapter extends RecyclerView.Adapter<PoiAdapter.ViewHolder> {
    List<PoiInfo> mListData = new ArrayList<PoiInfo>();
    private PoiClickListener poiClickListener;

    public PoiAdapter(PoiClickListener poiClickListener) {
        this.poiClickListener = poiClickListener;
    }

    public interface PoiClickListener {
        void onPoiClick(PoiInfo poiInfo);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poi_info, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object tag = view.getTag();
                if (tag instanceof PoiInfo) {
                    poiClickListener.onPoiClick((PoiInfo) tag);
                }
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PoiInfo myAppInfo = mListData.get(position);
        holder.tx_name.setText(myAppInfo.name);
        holder.tx_addr.setText(myAppInfo.getAddress());
        LatLng location = myAppInfo.getLocation();
        String latLngStr = "";
        if (location != null) {
            latLngStr = location.latitude + "," + location.longitude;
        }
        holder.tx_lat_lng.setText(latLngStr);
        holder.myPoiInfo = myAppInfo;

        holder.itemView.setTag(myAppInfo);
    }

    @Override
    public int getItemCount() {
        return mListData != null ? mListData.size() : 0;
    }

    public void setData(List<PoiInfo> myAppInfos) {
        this.mListData = myAppInfos;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView tx_name;
        TextView tx_addr;
        TextView tx_lat_lng;
        PoiInfo myPoiInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            tx_name = itemView.findViewById(R.id.tv_poi_name);
            tx_addr = itemView.findViewById(R.id.tv_poi_addr);
            tx_lat_lng = itemView.findViewById(R.id.tv_poi_latlng);
        }
    }
}
