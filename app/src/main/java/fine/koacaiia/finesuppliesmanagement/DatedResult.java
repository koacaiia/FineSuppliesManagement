package fine.koacaiia.finesuppliesmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DatedResult extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView recyclerViewAll;
    FirebaseDatabase database;
    ArrayList<MainDataList> list;
    ArrayList<MainDataList> datedResultList;
    ArrayList<MainDataList> usingPatternList;
    MainDataListAdapter adapter;
    MainDataListAdapter adapterAll;
    String depotName;
    String itemName;
    static SharedPreferences sharedPreferences;
    static private final String SHARE_NAME="SHARE_DEPOT";

    Button btnDateSelect,btnDateStart,btnDateEnd;
    String strDateStart,strDateEnd, strSortDate;

    ArrayList<String> consigneeList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dated_result);



        sharedPreferences=getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
        depotName=sharedPreferences.getString("depotName",null);
        itemName=getIntent().getStringExtra("itemName");


        recyclerView=findViewById(R.id.d_recyclerView_date);
        recyclerViewAll=findViewById(R.id.d_recyclerView_alldate);

        btnDateStart=findViewById(R.id.btn_date_start);
        btnDateEnd=findViewById(R.id.btn_date_end);

        Calendar calendar=Calendar.getInstance();
        String year=String.valueOf(calendar.get(Calendar.YEAR));
        String month="";
        if(calendar.get(Calendar.MONTH)+1<10){
            month="0"+(calendar.get(Calendar.MONTH)+1);
        }else{
            String.valueOf(calendar.get(Calendar.MONTH)+1);
        }
        String day="";
        if(calendar.get(Calendar.DAY_OF_MONTH)<10){
            day="0"+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        }else{
            day=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        }


        strDateStart=month+"월"+"01일";
        btnDateStart.setText(strDateStart);

        strDateEnd=month+"월"+day+"일";
        btnDateEnd.setText(strDateEnd   );

        LinearLayoutManager manager=new LinearLayoutManager(this);
        LinearLayoutManager managerAll=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerViewAll.setLayoutManager(managerAll);
        list=new ArrayList<MainDataList>();
        database=FirebaseDatabase.getInstance();
//        getList(strDateStart,strDateEnd);
        getConsigneeList();

        adapter=new MainDataListAdapter(datedResultList);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        adapterAll=new MainDataListAdapter(usingPatternList);
        recyclerViewAll.setAdapter(adapterAll);
        adapterAll.notifyDataSetChanged();

        btnDateSelect=findViewById(R.id.btn_dateselect);
        btnDateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int untilDate=Integer.parseInt(String.valueOf(untilDatePart()));
                getConsigneeList();
            }
        });
        

        View.OnClickListener btnListener= new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch(v.getId()){
                    case R.id.btn_date_start:
                        strSortDate="검색 시작일";
                        sortDate(strSortDate);
                        break;
                    case R.id.btn_date_end:
                        strSortDate="검색 종료일";
                        sortDate(strSortDate);
                        break;              
                       
                }
                
            }
        };
        btnDateStart.setOnClickListener(btnListener);
        btnDateEnd.setOnClickListener(btnListener);


    }

    private void getConsigneeList() {

        datedResultList=new ArrayList<>();
        datedResultList.clear();
        usingPatternList=new ArrayList<>();
        usingPatternList.clear();
        DatabaseReference databaseReference=database.getReference("SuppliesManagement/"+depotName+"/ItemName");
        ValueEventListener listener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    ItemNames mList=data.getValue(ItemNames.class);
                    String itemName=mList.getItemName();
                    Log.i("koacaiia","ItemName:"+itemName);
//                    consigneeList.add(itemName);
                    getDateResultDate(itemName);
                    
                    getUsingPatternDate(itemName);
                   }
               }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(listener);

    }

    private void getUsingPatternDate(String itemName) {


        DatabaseReference databaseReference=database.getReference("SuppliesManagement/"+depotName+"/"+itemName);

        ValueEventListener listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                    MainDataList mList=data.getValue(MainDataList.class);
                    list.add(mList );
                }
                int in=0;
                int out=0;
                int stock=0;
                Log.i("koacaiia","usingPatternList.size()"+list.size());
                for(int i=0;i<list.size();i++){
                    in=in+list.get(i).getIn();
                    out=out+list.get(i).getOut();
                    stock=in-out;
                }

                Double dOut=Double.valueOf(out);
                Double dAvr=dOut/untilDateAll();
                String strOutAvr=String.format("%.3f",dAvr);
                MainDataList mList=new MainDataList(itemName,in,out,stock,strOutAvr);
                usingPatternList.add(mList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        databaseReference.addListenerForSingleValueEvent(listener);
    }

    private void getDateResultDate(String itemName) {

        DatabaseReference databaseReference=database.getReference("SuppliesManagement/"+depotName+"/"+itemName);
        ValueEventListener listener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                    MainDataList mList=data.getValue(MainDataList.class);
                    list.add(mList);
                }
                int in=0;
                int out=0;
                int stock=0;
                for(int i=0;i<list.size();i++){
                    in=in+list.get(i).getIn();
                    out=out+list.get(i).getOut();
                    stock=in-out;
                    }
                Double dOut=Double.valueOf(out);
                Double dAvr=dOut/untilDatePart();
                String strAvr=String.format("%.3f",dAvr);

                MainDataList mList=new MainDataList(itemName,in,out,stock,strAvr);
                datedResultList.add(mList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        strDateStart="2021년"+btnDateStart.getText().toString();
        strDateEnd="2021년"+btnDateEnd.getText().toString();
        Query dateSortItem=databaseReference.orderByChild("date").startAt(strDateStart).endAt(strDateEnd);
        dateSortItem.addListenerForSingleValueEvent(listener);
        Log.i("koacaiib","strDateStart++:"+strDateStart);
    }

    private void sortDate(String strSortDate) {
        DatePickerFragment datePicker=new DatePickerFragment("");
        datePicker.show(getSupportFragmentManager(),"datePicker");
        Toast.makeText(this,strSortDate+"을 설정합니다.",Toast.LENGTH_SHORT).show();

    }


    private void getList(String dateStart,String dateEnd) {
        DatabaseReference databaseReference=database.getReference("SuppliesManagement/"+depotName+"/"+itemName);
        ValueEventListener listener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    MainDataList mList=data.getValue(MainDataList.class);
                    list.add(mList);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        Query sortDate=databaseReference.orderByChild("date").startAt("2021년"+strDateStart).endAt("2021년"+strDateEnd);
        sortDate.addListenerForSingleValueEvent(listener);


    }

    public void processDatePickerResult(int year, int month, int dayOfMonth) {

        String pickDate;
        String startDate,endDate;
        if(month<10){
            startDate="0"+String.valueOf(month+1);
        }else{
            startDate=String.valueOf(month+1);
        }

        if(dayOfMonth<10){
            endDate="0"+(dayOfMonth);
        }else{
            endDate=Integer.toString(dayOfMonth);
        }


        pickDate=startDate+"월"+endDate+"일";
        Toast.makeText(getApplicationContext(),pickDate+" 을 "+strSortDate+" 로 설정",Toast.LENGTH_SHORT).show();
        switch(strSortDate){
            case "검색 시작일":
                btnDateStart.setText(pickDate);

                break;
            case "검색 종료일":
              btnDateEnd.setText(pickDate);
                break;

        }
    }

    public int untilDateAll(){
        Date today=new Date();
        Calendar cal=Calendar.getInstance();
        cal.setTime(today);
        Calendar cal2=Calendar.getInstance();
        cal2.set(2021,00,01);
        int count=0;
        while(!cal2.after(cal)){
            count++;
            cal2.add(Calendar.DATE,1);
        }
        return count;
    }

    public long untilDatePart(){
        String dateStart=btnDateStart.getText().toString();
        String dateEnd=btnDateEnd.getText().toString();
        Date dateS = new Date();
        Date dateE=new Date();
        try {
            dateS=new SimpleDateFormat("MM월dd일").parse(dateStart);
            dateE=new SimpleDateFormat("MM월dd일").parse(dateEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("koacaiia","Date dateS:"+dateS+"/String dateS:"+dateStart);
        Calendar calS=Calendar.getInstance();
        calS.setTime(dateS);
        Calendar calE=Calendar.getInstance();
        calE.setTime(dateE);
        long diffDay=((calE.getTimeInMillis()-calS.getTimeInMillis())/1000/(24*60*60))+1;
        return diffDay;
    }
}