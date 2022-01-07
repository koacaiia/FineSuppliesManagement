package fine.koacaiia.finesuppliesmanagement;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainDataListAdapter extends RecyclerView.Adapter<MainDataListAdapter.ListViewHolder> {


    public interface MainDataClickListener{
        void itemOnClick(MainDataListAdapter.ListViewHolder holder,View v,int pos);
    }

    MainDataClickListener listener;
    ArrayList<MainDataList> list;
    String sortingName;
    ArrayList<OutDataList> mList;

    public MainDataListAdapter(ArrayList<MainDataList> list) {
        this.list=list;
    }
    public MainDataListAdapter(ArrayList<MainDataList> list,MainDataClickListener listener) {
        this.list = list;
        this.listener=listener;
    }
    public MainDataListAdapter(ArrayList<OutDataList> list,String sortingName){
        this.mList=list;
        this.sortingName=sortingName;
    }

    

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.in_out_stock_list,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
            holder.date.setText(list.get(position).getDate());
            holder.in.setText(String.valueOf(list.get(position).getIn()));
            holder.out.setText(String.valueOf(list.get(position).getOut()));
            holder.stock.setText(String.valueOf(list.get(position).getStock()));
            holder.remark.setText(String.valueOf(list.get( position).getRemark()));
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView in;
        TextView out;
        TextView stock;
        TextView remark;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.date=itemView.findViewById(R.id.in_out_stock_date);
            this.in=itemView.findViewById(R.id.in_out_stock_in);
            this.out=itemView.findViewById(R.id.in_out_stock_out);
            this.stock=itemView.findViewById(R.id.in_out_stock_stock);
            this.remark=itemView.findViewById(R.id.in_out_stock_remark);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(listener!=null){
                        listener.itemOnClick(ListViewHolder.this,v,getAdapterPosition());
                    }

                }
            });
        }
    }
}
