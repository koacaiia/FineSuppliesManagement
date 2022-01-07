package fine.koacaiia.finesuppliesmanagement;

public class OutDataList {
    String date,etc,timeStamp,nickName;
    int out;
    public OutDataList(){

    }
    public OutDataList(String date, String etc, int parseInt, String timeStamp, String nickName) {
        this.date=date;
        this.etc=etc;
        this.out=parseInt;
        this.timeStamp=timeStamp;
        this.nickName=nickName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getOut() {
        return out;
    }

    public void setOut(int out) {
        this.out = out;
    }
}
