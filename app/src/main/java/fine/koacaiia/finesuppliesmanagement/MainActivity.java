package fine.koacaiia.finesuppliesmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements MainDataListAdapter.MainDataClickListener {
    RecyclerView recyclerView;
    ArrayList<MainDataList> list;
    ArrayList<MainDataList> sortStockList;
    MainDataListAdapter adapter;
    FirebaseDatabase database;
//    FloatingActionButton fltBtnPutItems;
//    FloatingActionButton fltBtnSearchItems;
    Button btnPut;
    Button btnSearch;
    ArrayList<DepotNameList> depotList;
    String depotName;
    String nickName;

    ArrayList<String> itemList=new ArrayList<>();

    static private final String SHARE_NAME="SHARE_DEPOT";
    static SharedPreferences sharedPreference;
    static SharedPreferences.Editor sharedPreferencesEditor;

    InputMethodManager imm;
    TextView txtItemName;
    Button btnDate;

    String itemName;
    String targetDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreference=getSharedPreferences(SHARE_NAME,MODE_PRIVATE);
        depotName=sharedPreference.getString("depotName",null);
        if(depotName==null){
            Toast.makeText(this,"????????? ????????? ?????? ?????? ????????????.",Toast.LENGTH_LONG).show();
            dialogPutDepotName();
        }else{
            Intent intent=getIntent();
            if(intent.getStringExtra("month")==null){
               targetDate=new SimpleDateFormat("yyyy???MM???dd???").format(new Date()).substring(5,7);
            }else{
                targetDate=intent.getStringExtra("month");
            }
            nickName=sharedPreference.getString("nickName","Fine_Staff");
//            Toast.makeText(this,"Depart Name:"+depotName+"_"+"Staff Name:"+nickName,Toast.LENGTH_SHORT).show();
            recyclerView=findViewById(R.id.recyclerView);
            LinearLayoutManager manager=new LinearLayoutManager(this);
            recyclerView.setLayoutManager(manager);
            database=FirebaseDatabase.getInstance();
            list=new ArrayList<>();
            sortStockList=new ArrayList<>();
//        alertDialogPutData();
//            sortItems();
            txtItemName=findViewById(R.id.txtItemName);
            btnDate=findViewById(R.id.in_out_stock_date);
            if(intent.getStringExtra("month")==null){
                targetDate=new SimpleDateFormat("yyyy???MM???dd???").format(new Date()).substring(5,7);
                sortItems();
            }else{
                itemName=intent.getStringExtra("itemName");
                targetDate=intent.getStringExtra("month");
                int intTargetDate=Integer.parseInt(targetDate);
                if(intTargetDate<10){
                    targetDate="0"+targetDate;
                }
                getData();
            }
            adapter=new MainDataListAdapter(sortStockList,this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            btnSearch=findViewById(R.id.button2);
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogSortItems();
                }
            });

            btnDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String itemName=txtItemName.getText().toString();
                    if(itemName==null||itemName.equals("")){
                        Toast.makeText(getApplicationContext(),"???????????? ?????? ????????? ?????? ?????? ?????? ????????????.",Toast.LENGTH_SHORT).show();
                        dialogSortItems();
                    }else{
                        sortItemsByMonth();
                    }

                }
            });
            btnPut=findViewById(R.id.button);
            btnPut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogPutData();
                }
            });
        }

    }

    private void dialogPutDepotName() {
        View view=getLayoutInflater().inflate(R.layout.user_reg,null);
        EditText editText=view.findViewById(R.id.user_reg_edit);
        Button btnReg=view.findViewById(R.id.user_reg_button);
        TextView txtReg=view.findViewById(R.id.user_reg_txtDepot);

        ArrayList<String> depotSort=new ArrayList<>();
        depotSort.add("1??????(02010810)");
        depotSort.add("2??????(02010027)");
        depotSort.add("?????????");
        depotSort.add("???????????????");
        depotSort.add("?????????????????????(A???)");
        depotSort.add("?????????????????????(B???)");
        depotSort.add("R&A ?????????");
        depotSort.add("?????? ????????? ?????? ?????????");


        String[] arrDepotSort=depotSort.toArray(new String[depotSort.size()]);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(arrDepotSort,0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                depotName=arrDepotSort[which];
                txtReg.setText(depotName);

            }
        });
        AlertDialog dialog=builder.create();
        dialog.setView(view);
              dialog.show();
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickName=editText.getText().toString();
                confirmDialogPutDepotName();
                dialog.dismiss();
            }
        });

    }

    private void confirmDialogPutDepotName() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setTitle("????????? ??????")
                .setMessage("Department Name:"+depotName+"\n"+"Staff Name:"+ nickName +"\n"+"????????? ????????? ?????? ?????????."+"\n"+
                        "?????????????????? Confirm ????????? ?????? ????????????.")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPreferencesEditor=sharedPreference.edit();
                        sharedPreferencesEditor.putString("depotName",depotName);
                        sharedPreferencesEditor.putString("nickName", nickName);
                        sharedPreferencesEditor.apply();
                        Toast.makeText(getApplicationContext(),"Depart_Name:"+depotName+"\n"+"Staff_Name:"+nickName+"\n"+"??? ?????? ???????????????.",
                                Toast.LENGTH_LONG).show();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void alertDialogPutData() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        EditText editText=new EditText(this);
        
        builder.setTitle("???????????? ?????????")
                .setMessage("???????????? ????????? ???????????? ???????????? ?????? ????????????.")
                .setView(editText)
                .setPositiveButton("???????????? ??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String itemName=editText.getText().toString();
                                PutItemName(itemName);

                            }
                        }
                )
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        getData();
                    }
                })


                .show();

    }

    private void itemReset(String date) {

        DatabaseReference databaseReference=database.getReference("SuppliesManagement/"+depotName+"/"+itemName+"/"+date);
        MainDataList list=new MainDataList(date,0,0,0,"");
        databaseReference.setValue(list);
        getData();

    }

    private void PutItemName(String itemName) {
        for(int i=0;i<365;i++){
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy???MM???dd???");
            Date dateG=null;
            try {
                dateG=sdf.parse("2021???01???01???");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar= Calendar.getInstance();
            calendar.setTime(dateG);
            calendar.add(Calendar.DAY_OF_MONTH,i);
            String date=sdf.format(calendar.getTime());

            MainDataList data=new MainDataList(date,0,0,0,"");
            DatabaseReference databaseReference=database.getReference("SuppliesManagement/"+depotName+"/"+itemName+"/"+date);
            databaseReference.setValue(data);
            this.itemName=itemName;

        }
        putItemNameParent(itemName);
        getData();
    }

    private void putItemNameParent(String itemName) {
        DatabaseReference databaseReference=database.getReference("SuppliesManagement/"+depotName+"/"+"ItemName/"+itemName);
        ItemNames itemNames=new ItemNames(itemName);
        databaseReference.setValue(itemNames);
    }

    public void getData() {
        list.clear();
        sortStockList.clear();
        txtItemName.setText(itemName);
        btnDate.setText(targetDate+" ???");
        Toast.makeText(getApplicationContext(),"itemName"+itemName+"/targetDate:"+targetDate,Toast.LENGTH_SHORT).show();
        DatabaseReference databaseReference=database.getReference("SuppliesManagement/"+depotName+"/"+itemName);
        ValueEventListener listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot data:snapshot.getChildren()){
                   MainDataList mList=data.getValue(MainDataList.class);
//                   String dataMonth=mList.getDate().substring(5,7);
//                   if(dataMonth.equals(targetDate)){
//                   list.add(mList);}
                   list.add(mList);
               }
               int listSize=list.size();
               int totalIn=list.get(0).getIn();
               int totalOut=list.get(0).getOut();
               int totalStock=0;
               for(int i=0;i<listSize;i++){
                   int in,out,stock;

                   String date=list.get(i).getDate();
                   String month=date.substring(5,7);
                   String remark=list.get(i).getRemark();
                   in=list.get(i).getIn();
                   out=list.get(i).getOut();
                   if(i==0){
                       stock=in-out;
                   }else{
                       stock=(list.get(i-1).getStock()+in)-out;
                   }
                   totalIn=totalIn+in;
                   totalOut=totalOut+out;
                   totalStock=totalIn-totalOut;

                   MainDataList dataList=new MainDataList(date,in,out,totalStock,remark);
                   DatabaseReference databaseReferenceSort=
                           database.getReference("SuppliesManagement/"+depotName+"/"+itemName+"/"+date);
                   databaseReferenceSort.setValue(dataList);

                   if(month.equals(targetDate)){

                       sortStockList.add(dataList);
                   }
               }
               adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(listener);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_account:
               dialogPutDepotName();
                break;
            case R.id.action_account_help:
                Toast.makeText(this,"Not Yet",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_account_search:
                dialogResultSelect();
        }
        return true;
    }

    private void dialogResultSelect() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setTitle("???????????? ???????????? ")
                .setMessage("????????? ??????:??? ????????? ????????? ??????"+"\n"+"??????????????? ??????:"+itemName+"??? ?????? ???????????????")
                .setPositiveButton("????????? ??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(MainActivity.this,DatedResult.class);
                        intent.putExtra("itemName",itemName);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("??????????????? ??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(MainActivity.this,UsingPatternResult.class);
                        intent.putExtra("itemName",itemName);
                        startActivity(intent);

                    }
                })
                .show();
    }

    public void sortItems(){

        DatabaseReference databaseReference=database.getReference("SuppliesManagement/"+depotName+"/ItemName");
        ValueEventListener listener=new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    ItemNames mList=data.getValue(ItemNames.class);
                    String itemName=mList.getItemName();
                    itemList.add(itemName);
                }
                dialogSortItems();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addValueEventListener(listener);
    }

    private void dialogSortItems() {
        String[] arrItems=itemList.toArray(new String[itemList.size()]);
        int itemListSize=itemList.size();
        if(itemListSize==0){
           AlertDialog.Builder builders=new AlertDialog.Builder(this);
           builders.setTitle("???????????? ?????????")
                   .setMessage("??????????????? ?????? 1??? ?????? ?????? ????????????.???????????? ????????? ????????? ???????????? ?????? ?????? ?????? ??????,Confirm ??????????????? ?????? ?????? ????????? ???????????????")
                   .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           alertDialogPutData();
                       }
                   })
                   .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {

                       }
                   })
                   .show();
        }else{
            AlertDialog.Builder builder=new AlertDialog.Builder(this);

            builder.setSingleChoiceItems(arrItems,0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    itemName=arrItems[which];
                    getData();

                    dialog.dismiss();

                }
            });

            AlertDialog dialog=builder.create();
            dialog.show();
        }

    }

    private void sortItemsByMonth() {
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            }
        };

        YearMonthPickerDialog yearMonthPickerDialog=new YearMonthPickerDialog(itemName);

        yearMonthPickerDialog.setListener(dateSetListener);
        yearMonthPickerDialog.show(getSupportFragmentManager(),"YearMonthPicker");
    }

    @Override
    public void itemOnClick(MainDataListAdapter.ListViewHolder holder, View v, int pos) {
      AlertDialog.Builder builder=new AlertDialog.Builder(this);
      EditText editText=new EditText(this);

      editText.setInputType(InputType.TYPE_CLASS_NUMBER);

      int in=sortStockList.get(pos).getIn();
      int out=sortStockList.get(pos).getOut();
      String date=sortStockList.get(pos).getDate();
      builder.setTitle("???,?????? ?????? ?????? ???")
              .setMessage(itemName+":"+"\n"+date+"  ???????????? :"+in+"\n"+date+"  ???????????? :"+out+"\n"+
                              "??? ?????? ???,?????? ????????? ????????? ?????????????????? ?????? ????????????.")
              .setView(editText)
              .setPositiveButton("???????????? ??????", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      
                        String strQty=editText.getText().toString();

                        if(strQty.equals("")){
                            Toast.makeText(getApplicationContext(),"????????? ?????? ????????????.!",Toast.LENGTH_SHORT).show();
                        }else{
                            itemPutData(date,in,out+Integer.parseInt(strQty));
                            Log.i("koacaiia","date:"+date+"/in:"+in+"/listout:"+out+"/inputOut"+strQty);
                            dialogItemPutData(strQty);

                        }

                  }
              })
              .setNegativeButton("???????????? ??????", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                      String strQty=editText.getText().toString();
                      if(strQty.equals("")){
                          Toast.makeText(getApplicationContext(),"????????? ?????? ????????????.!",Toast.LENGTH_SHORT).show();
                      }else{
                          itemPutData(date,in+Integer.parseInt(strQty),out);
//                          getData();
                      }

                  }
              })
              .setNeutralButton("?????? ?????????", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    itemReset(date);
                  }
              })
              .show();

    }

    private void dialogItemPutData(String strQty) {
        View view = getLayoutInflater().inflate(R.layout.usingname_outdata_clicked, null);

        Button btnManager = view.findViewById(R.id.manager);
        btnManager.setText(depotName + "\n" + "?????? ??????");
        Button btnWmanager = view.findViewById(R.id.wManager);
        btnWmanager.setText(depotName + "\n" + "?????? ????????????");
        Button btnMoutsourcing = view.findViewById(R.id.mOutsourcing);
        Button btnWoutsourcing = view.findViewById(R.id.wOutsourcing);
        Button btnOcompany = view.findViewById(R.id.outCompany);
        Button btnequip = view.findViewById(R.id.equipment);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = 700;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(params);

        Button.OnClickListener listener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usingName = null;
                switch (v.getId()) {
                    case R.id.manager:
                        usingName=btnManager.getText().toString();
                        break;
                    case R.id.wManager:
                        usingName= btnWmanager.getText().toString();
                        break;
                    case R.id.mOutsourcing:
                        usingName=btnMoutsourcing.getText().toString();
                        break;
                    case R.id.wOutsourcing:
                        usingName=btnWoutsourcing.getText().toString();
                        break;
                    case R.id.outCompany:
                        usingName=btnOcompany.getText().toString();
                        break;
                    case R.id.equipment:
                        usingName=btnequip.getText().toString();
                        break;
                }
                itemPutLogData(strQty,usingName);

                alertDialog.dismiss();
            }

        };
        btnManager.setOnClickListener(listener);
        btnWmanager.setOnClickListener(listener);
        btnMoutsourcing.setOnClickListener(listener);
        btnWoutsourcing.setOnClickListener(listener);
        btnOcompany.setOnClickListener(listener);
        btnequip.setOnClickListener(listener);
    }

    private void itemPutLogData(String strQty,String usingName) {
        String timeStamp=String.valueOf(System.currentTimeMillis());
        String time=new SimpleDateFormat("HH???mm???").format(new Date());
        String date=new SimpleDateFormat("yyyy???MM???dd???").format(new Date());
        DatabaseReference databaseReference=
                database.getReference("SuppliesManagement/"+depotName+"/Log/"+itemName+"/"+date+"_"+timeStamp);
        OutDataList data=new OutDataList(date,nickName,Integer.parseInt(strQty),time,nickName);
        databaseReference.setValue(data);
//        getData();
    }

    private void itemPutData(String date, int in, int out) {
        DatabaseReference databaseReference=database.getReference("SuppliesManagement/"+depotName+"/"+itemName+"/"+date);
        Log.i("koacaiia","outDate:"+out);
        MainDataList data=new MainDataList(date,in,out,0,"");
        databaseReference.setValue(data);
        getData();

    }
}