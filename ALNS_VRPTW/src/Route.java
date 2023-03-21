import java.util.ArrayList;

public class Route {
    ArrayList<Node> N = new ArrayList<>();          //定义路线由节点构成

    public double SubT;                             //路线违反时间窗
    public double Load;                             //路线中的载重量
    public double Dis;                              //路线距离

    public static void UPDis(Route R) {
        R.Dis = 0;
        for (int i = 0;i < R.N.size()-1;i++) {
            R.Dis  = R.Dis + Parameter.DisMatrix[R.N.get(i).ID][R.N.get(i+1).ID];
        }
    }

    public static void UPSubT(Route R) {
        double Arrivetime = 0;
        R.SubT = 0;  //重置时间窗时间
        for (int i = 1; i < R.N.size(); i++) {
            //到达时间=上一个节点的到达时间+上一个节点的服务时间+两点之间距离
            Arrivetime = Arrivetime + R.N.get(i - 1).ServiceTime + Parameter.DisMatrix[R.N.get(i - 1).ID][R.N.get(i).ID];
            //如果迟到，则新增违反时间
            if (Arrivetime > R.N.get(i).Duetime) {
                R.SubT = R.SubT + Arrivetime - R.N.get(i).Duetime;
            }
            //如果提前到，则到达时间=服务开始时间，并更新违反时间
            else if (Arrivetime < R.N.get(i).ReadyTime) {
                Arrivetime = R.N.get(i).ReadyTime;
            }
        }
    }
}
