package fine.koacaiia.finesuppliesmanagement;

public class MainDataList {
    String date;
    int in;
    int out;
    int stock;
    String remark;


    public MainDataList(){

    }
    public MainDataList(String date, int in, int out, int stock, String remark) {
        this.date = date;
        this.in = in;
        this.out = out;
        this.stock = stock;
        this.remark = remark;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIn() {
        return in;
    }

    public void setIn(int in) {
        this.in = in;
    }

    public int getOut() {
        return out;
    }

    public void setOut(int out) {
        this.out = out;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
