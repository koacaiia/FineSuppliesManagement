package fine.koacaiia.finesuppliesmanagement;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class YearMonthPickerDialog extends DialogFragment {

    DatePickerDialog.OnDateSetListener listener;
    Calendar cal;
    String itemName;
    Button btnConfirm;
    Button btnCancel;
    public YearMonthPickerDialog(){
        }
    public YearMonthPickerDialog(String itemName){
        this.itemName=itemName;
    }
    public void setListener(DatePickerDialog.OnDateSetListener listener){
        this.listener=listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.year_month_picker,null);
        NumberPicker monthPicker=view.findViewById(R.id.picker_month);
        NumberPicker yearPicker=view.findViewById(R.id.picker_year);

        btnCancel=view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YearMonthPickerDialog.this.getDialog().cancel();
            }
        });
        btnConfirm=view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDateSet(null,yearPicker.getValue(),monthPicker.getValue(),0);
                YearMonthPickerDialog.this.getDialog().cancel();
                int intentMonth=monthPicker.getValue();
                Intent intent=new Intent(getContext(),MainActivity.class);
                intent.putExtra("itemName",itemName);
                intent.putExtra("month",String.valueOf(intentMonth));
                startActivity(intent);

            }
        });


        cal=Calendar.getInstance();
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH)+1  );

        int year=cal.get(Calendar.YEAR);
        yearPicker.setMinValue(2020);
        yearPicker.setMaxValue(2023);
        yearPicker.setValue(year);
        builder.setView(view);
        return builder.create();
    }
}
