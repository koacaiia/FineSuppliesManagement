package fine.koacaiia.finesuppliesmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsingPatternResult extends AppCompatActivity {
    RecyclerView recyclerViewPattern;
    ArrayList<OutDataList> patternList;
    OutDataListAdapter patternAdapter;

    RecyclerView recyclerViewUsingName;
    ArrayList<OutDataList> usingNameList;
    OutDataListAdapter usingAdapter;

    FirebaseDatabase firebaseDatabase;

    String itemName;
    static SharedPreferences sharedPreferences;
    static private final String SHARE_NAME="SHARE_DEPOT";
    String depotName;
    int totalSum;

    TextView usingDateTextView;
    TextView usingNameTextView;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_using_pattern_result);

        sharedPreferences=getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
        depotName=sharedPreferences.getString("depotName",null);

        itemName=getIntent().getStringExtra("itemName");
        recyclerViewPattern=findViewById(R.id.u_recyclerView_date);
        LinearLayoutManager managerPattern=new LinearLayoutManager(this);
        recyclerViewPattern.setLayoutManager(managerPattern);
        firebaseDatabase=FirebaseDatabase.getInstance();
        patternList=new ArrayList<>();
        usingNameList=new ArrayList<>();
        getDataList();
        patternAdapter=new OutDataListAdapter(patternList);
        recyclerViewPattern.setAdapter(patternAdapter);
        patternAdapter.notifyDataSetChanged();

        recyclerViewUsingName=findViewById(R.id.u_recyclerView_usingpattern);
        LinearLayoutManager managerUsing=new LinearLayoutManager(this);
        recyclerViewUsingName.setLayoutManager(managerUsing);
        usingAdapter=new OutDataListAdapter(usingNameList);
        recyclerViewUsingName.setAdapter(usingAdapter);
        usingAdapter.notifyDataSetChanged();

        usingDateTextView=findViewById(R.id.Utitle);
        usingDateTextView.setText(itemName+"_ 일별 수불상황");

        usingNameTextView=findViewById(R.id.usingTitle);
        usingNameTextView.setText(itemName+"_ 사용처별 수불상황");

    }

    private void getDataList() {
        DatedResult datedResult=new DatedResult();
        int untilDate=datedResult.untilDateAll();

        ArrayList<String> usingNameArrayList=new ArrayList<>();
        DatabaseReference databaseReference=firebaseDatabase.getReference("SuppliesManagement/"+depotName+"/Log/"+itemName+"/");
        ArrayList<OutDataList> list=new ArrayList<>();
        totalSum=0;
        ValueEventListener listener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    OutDataList mList=data.getValue(OutDataList.class);
                    patternList.add(mList);
                    totalSum=totalSum+mList.getOut();
                    //사용처,누적사용량,일평균사용량,%
                    String usingName=mList.getEtc();
                    usingNameArrayList.add(usingName);
                    Log.i("duatjsrb","usingNameArrayList:"+usingName);
                }
                ArrayList<String> usingArr=new ArrayList<>();
                Log.i("duatjsrb","usingNameArrayList Size:"+usingNameArrayList.size());
                Log.i("totalSum","totalSum"+totalSum);
                String[] nameArrayList=usingNameArrayList.toArray(new String[usingNameArrayList.size()]);
                usingNameArrayList.clear();
                for(String item:nameArrayList){
                    if(! usingNameArrayList.contains(item)){
                        usingNameArrayList.add(item);
                        Log.i("duatjsrb6610","contains item:"+item);
                    }
                    }
                Log.i("duatjsrb1","Sort contains List:"+usingNameArrayList.size());
                for(int i=0;i<usingNameArrayList.size();i++){
                    String usingName = usingNameArrayList.get(i);
                    int outSum=0;
                    Double outAvr = null;
                    Double outPercent=null;
                    for(int k=0;k<patternList.size();k++){
                        if(usingName.equals(patternList.get(k).getEtc())){
//                            OutDataList list=new OutDataList(usingName,"",k,"","");
//                            usingNameList.add(list);
                            int out=patternList.get(k).getOut();
                            outSum=outSum+out;
                            Double untilDateD=Double.valueOf(untilDate);
                            outAvr=(Double)(outSum/untilDateD);
                            Double totalSumD=Double.valueOf(totalSum);
                            outPercent=(Double)(outSum/totalSumD)*100;
                        }

                    }
                    OutDataList list=new OutDataList(usingName,String.format("%.2f",outPercent),outSum,"",
                            String.format("%.2f",outAvr));
                    usingNameList.add(list);
                }

                patternAdapter.notifyDataSetChanged();
                usingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(listener);
    }
}