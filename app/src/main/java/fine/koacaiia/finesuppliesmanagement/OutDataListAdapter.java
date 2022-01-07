package fine.koacaiia.finesuppliesmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OutDataListAdapter extends RecyclerView.Adapter<OutDataListAdapter.ViewHolder> {
    ArrayList<OutDataList> list;

    public OutDataListAdapter(ArrayList<OutDataList> list){
        this.list=list;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.using_data_list,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.date.setText(list.get(position).getDate()+"\n"+list.get(position).getTimeStamp());
        holder.out.setText(String.valueOf(list.get(position).getOut()));
        holder.usingName.setText(list.get(position).getEtc());
        holder.nickName.setText(list.get(position).getNickName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView out;
        TextView usingName;
        TextView nickName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.date=itemView.findViewById(R.id.Udate);
            this.out=itemView.findViewById(R.id.Uout);
            this.usingName=itemView.findViewById(R.id.UusingName);
            this.nickName=itemView.findViewById(R.id.Udepot);
        }
    }
}
